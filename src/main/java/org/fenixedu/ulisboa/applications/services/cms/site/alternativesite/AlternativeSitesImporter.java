package org.fenixedu.ulisboa.applications.services.cms.site.alternativesite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.applications.util.ULisboaApplicationsUtil;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;

@Service
public class AlternativeSitesImporter {

    private static int NUMBER_REQUIRED_COLUMS = 3;
    private static int COLUMN_DEGREE_CODE = 0;
    private static int COLUMN_DEGREE_CURRICULAR_PLAN_NAME = 1;
    private static int COLUMN_COMPETENCE_COURSE_CODE = 2;
    private static int COLUMN_ALTERNATIVE_SITE_LINK = 3;

    public class ProcessResult {

        private List<String> infoMessages = new ArrayList<>();
        private List<String> errorMessages = new ArrayList<>();

        private boolean processFailed = false;

        public void addInfoMessage(String message, String... args) {
            infoMessages.add(ULisboaApplicationsUtil.bundle(message, args));
        }

        public void addErrorMessage(String message, String... args) {
            errorMessages.add(ULisboaApplicationsUtil.bundle(message, args));
        }

        protected void reportFailure() {
            processFailed = true;
        }

        public boolean hasFailed() {
            return processFailed;
        }

        public List<String> getInfoMessages() {
            return infoMessages;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }

    }

    @Atomic
    public ProcessResult processFile(File file, ExecutionInterval executionInterval) {
        ProcessResult result = new ProcessResult();

        processFile(file, executionInterval, result);

        return result;
    }

    private void processFile(File file, ExecutionInterval executionInterval, ProcessResult result) {
        Workbook wb = null;

        try {
            wb = WorkbookFactory.create(file);

            final Sheet sheet = wb.getSheetAt(0);
            processSheet(sheet, executionInterval, result);

        } catch (final IOException | InvalidFormatException e) {
            e.printStackTrace();
            result.reportFailure();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    result.reportFailure();
                }
            }
        }
    }

    private void processSheet(Sheet sheet, ExecutionInterval executionInterval, ProcessResult result) {
        Iterator<Row> rowIterator = sheet.rowIterator();

        // Skip header row
        rowIterator.next();

        while (rowIterator.hasNext()) {
            final Row row = rowIterator.next();
            processRow(row, executionInterval, result);
        }
    }

    private void processRow(Row row, ExecutionInterval executionInterval, ProcessResult result) {
        if (row.getPhysicalNumberOfCells() >= NUMBER_REQUIRED_COLUMS) {
            Degree degree = getDegree(row, result);
            if (degree == null) {
                return;
            }

            DegreeCurricularPlan degreeCurricularPlan = getDegreeCurricularPlan(row, degree, result);
            if (degreeCurricularPlan == null) {
                return;
            }

            ExecutionCourse executionCourse = getExecutionCourse(row, degreeCurricularPlan, executionInterval, result);
            if (executionCourse == null) {
                return;
            }

            if (executionCourse.getSite() == null) {
                result.addErrorMessage(
                        "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.noSiteConfigured",
                        String.valueOf(row.getRowNum() + 1), executionInterval.getQualifiedName(), degreeCurricularPlan.getName(),
                        executionCourse.getCode());
                return;
            }

            String alternativeSiteLink =
                    row.getCell(COLUMN_ALTERNATIVE_SITE_LINK, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();

            if (StringUtils.isEmpty(alternativeSiteLink)) {
                alternativeSiteLink = null;
            }

            executionCourse.getSite().setAlternativeSite(alternativeSiteLink);
        } else {
            result.addErrorMessage(
                    "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.invalidLine",
                    String.valueOf(row.getRowNum() + 1));
        }
    }

    private Degree getDegree(Row row, ProcessResult result) {
        String degreeCode = getCellValueAsString(row, row.getCell(COLUMN_DEGREE_CODE), result);
        if (degreeCode != null) {
            Degree degree = Degree.find(degreeCode);
            if (degree == null) {
                result.addErrorMessage(
                        "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.unknownDegree",
                        String.valueOf(row.getRowNum() + 1), degreeCode);
                return null;
            }
            return degree;
        }
        return null;
    }

    private DegreeCurricularPlan getDegreeCurricularPlan(Row row, Degree degree, ProcessResult result) {
        String degreeCurricularPlanName = getCellValueAsString(row, row.getCell(COLUMN_DEGREE_CURRICULAR_PLAN_NAME), result);
        if (degreeCurricularPlanName != null) {
            DegreeCurricularPlan degreeCurricularPlan =
                    findDegreeCurricularPlan(degree, new LocalizedString(Locale.getDefault(), degreeCurricularPlanName));
            if (degreeCurricularPlan == null) {
                result.addErrorMessage(
                        "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.unknownDegreeCurricularPlan",
                        String.valueOf(row.getRowNum() + 1), degree.getPresentationName(), degreeCurricularPlanName);
                return null;
            }
            return degreeCurricularPlan;
        }
        return null;
    }

    private static DegreeCurricularPlan findDegreeCurricularPlan(final Degree degree, final LocalizedString name) {
        String dcpName = name.getContent(Locale.getDefault());
        for (DegreeCurricularPlan dcp : degree.getDegreeCurricularPlansSet()) {
            if (dcp.getName().equalsIgnoreCase(dcpName)) {
                return dcp;
            }
        }
        return null;
    }

    private ExecutionCourse getExecutionCourse(Row row, DegreeCurricularPlan degreeCurricularPlan,
            ExecutionInterval executionInterval, ProcessResult result) {
        String competenceCourseCode = getCellValueAsString(row, row.getCell(COLUMN_COMPETENCE_COURSE_CODE), result);
        if (competenceCourseCode != null) {
            Optional<ExecutionCourse> executionCourseOptional =
                    degreeCurricularPlan.getExecutionCourses(executionInterval).stream()
                            .filter(ec -> Objects.equals(ec.getCode(), competenceCourseCode)).findFirst();

            if (!executionCourseOptional.isPresent()) {
                result.addErrorMessage(
                        "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.unknownExecutionCourse",
                        String.valueOf(row.getRowNum() + 1), executionInterval.getQualifiedName(), degreeCurricularPlan.getName(),
                        competenceCourseCode);
                return null;
            }
            return executionCourseOptional.get();
        }
        return null;
    }

    private String getCellValueAsString(Row row, Cell cell, ProcessResult result) {
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK || cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        } else {
            result.addErrorMessage(
                    "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.invalidCell",
                    String.valueOf(row.getRowNum() + 1), String.valueOf(cell.getColumnIndex() + 1));
            return null;
        }
    }

}
