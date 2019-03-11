package org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;

public class TeacherLessonCalendarParametersBean implements IBean {

    private ExecutionSemester executionSemester;
    private Teacher teacher;

    private List<TupleDataSourceBean> executionSemestersDataSource;
    private List<TupleDataSourceBean> teachersDataSource;

    public TeacherLessonCalendarParametersBean(ExecutionSemesterProviderType executionSemesterProviderType,
            TeacherProviderType teacherProviderType) {
        updateData(executionSemesterProviderType, teacherProviderType);
    }

    public void updateData(ExecutionSemesterProviderType executionSemesterProviderType, TeacherProviderType teacherProviderType) {

        updateExecutionSemestersDataSource(executionSemesterProviderType);

        updateTeachersDataSource(teacherProviderType);

    }

    private void updateExecutionSemestersDataSource(ExecutionSemesterProviderType executionSemesterProviderType) {
        switch (executionSemesterProviderType) {
        case INCLUDE_ALL:
            this.executionSemestersDataSource = Bennu.getInstance().getExecutionPeriodsSet().stream()
                    .sorted(ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.reversed())
                    .map(x -> new TupleDataSourceBean(x.getExternalId(), x.getQualifiedName())).collect(Collectors.toList());
            break;

        case CURRENT_EXECUTION_YEAR:
            this.executionSemestersDataSource = ExecutionYear.readCurrentExecutionYear().getExecutionPeriodsSet().stream()
                    .map(x -> new TupleDataSourceBean(x.getExternalId(), x.getQualifiedName())).collect(Collectors.toList());
            break;
        }
    }

    private void updateTeachersDataSource(TeacherProviderType teacherProviderType) {
        switch (teacherProviderType) {
        case INCLUDE_ALL_WITH_AUTHORIZATION:
            if (this.executionSemester != null) {
                this.teachersDataSource = this.executionSemester.getTeacherAuthorizationSet().stream().map(ta -> ta.getTeacher())
                        .distinct().map(t -> new TupleDataSourceBean(t.getExternalId(), t.getPerson().getDisplayName()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
            }
            break;

        case CURRENT_AUTHENTICATED_TEACHER:
            this.teachersDataSource = Stream.of(Authenticate.getUser().getPerson().getTeacher())
                    .map(t -> new TupleDataSourceBean(t.getExternalId(), t.getPerson().getDisplayName()))
                    .collect(Collectors.toList());
            break;
        }

        if (this.teacher != null
                && this.teachersDataSource.stream().noneMatch(t -> t.getId().equals(this.teacher.getExternalId()))) {
            this.teacher = null;
        }
    }

    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

    public void setExecutionSemester(ExecutionSemester executionSemester) {
        this.executionSemester = executionSemester;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<TupleDataSourceBean> getExecutionSemestersDataSource() {
        return executionSemestersDataSource;
    }

    public List<TupleDataSourceBean> getTeachersDataSource() {
        return teachersDataSource;
    }

    public boolean hasAllRequiredFieldsFilled() {
        return this.executionSemester != null && this.teacher != null;
    }

    public enum ExecutionSemesterProviderType {
        INCLUDE_ALL, CURRENT_EXECUTION_YEAR;
    }

    public enum TeacherProviderType {
        INCLUDE_ALL_WITH_AUTHORIZATION, CURRENT_AUTHENTICATED_TEACHER;
    }
}