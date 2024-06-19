package org.fenixedu.ulisboa.applications.services.cms.site.alternativesite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.qubit.terra.framework.tools.excel.ExcelUtil;
import com.qubit.terra.framework.tools.excel.SheetProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

    private static class ExecutionIntervalSheetProcessor extends SheetProcessor {
        ProcessResult result;
        ExecutionInterval executionInterval;


        public ExecutionIntervalSheetProcessor(ProcessResult processResult, ExecutionInterval executionInterval) {
            super();
            this.result = processResult;
            this.executionInterval = executionInterval;
            setRowProcessor(row -> {
                if (row.getPhysicalNumberOfCells() >= NUMBER_REQUIRED_COLUMS) {
                    Degree degree = getDegree(row, result);
                    if (degree == null) {
                        return;
                    }

                    DegreeCurricularPlan degreeCurricularPlan = getDegreeCurricularPlan(row, degree, this.result);
                    if (degreeCurricularPlan == null) {
                        return;
                    }

                    ExecutionCourse executionCourse = getExecutionCourse(row, degreeCurricularPlan, this.executionInterval, this.result);
                    if (executionCourse == null) {
                        return;
                    }

                    if (executionCourse.getSite() == null) {
                        this.result.addErrorMessage(
                                "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.noSiteConfigured",
                                String.valueOf(row.getRowNum() + 1), this.executionInterval.getQualifiedName(), degreeCurricularPlan.getName(),
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
                    this.result.addErrorMessage(
                            "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.invalidLine",
                            String.valueOf(row.getRowNum() + 1));
                }
            });

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
    }
    private void processFile(File file, ExecutionInterval executionInterval, ProcessResult result) {
        try {
            ExcelUtil.importExcel(file, new ExecutionIntervalSheetProcessor(result, executionInterval));
        } catch (Throwable t) {
            result.reportFailure();
        }
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



    private static String getCellValueAsString(Row row, Cell cell, ProcessResult result) {
        String value = null;
        try {
            value = ExcelUtil.getCellValueAsString(row, cell.getColumnIndex());
        } catch (Throwable t) {
            result.addErrorMessage(
                    "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.error.invalidCell",
                    String.valueOf(row.getRowNum() + 1), String.valueOf(cell.getColumnIndex() + 1), t.getMessage());
        }
        return value;
    }

}
