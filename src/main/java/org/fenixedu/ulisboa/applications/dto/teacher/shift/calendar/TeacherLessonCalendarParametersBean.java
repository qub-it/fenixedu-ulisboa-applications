package org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;

public class TeacherLessonCalendarParametersBean implements IBean {

    private ExecutionSemester executionSemester;
    private List<TupleDataSourceBean> executionSemestersDataSource;

    public TeacherLessonCalendarParametersBean() {
        updateData();
    }

    public void updateData() {

        this.executionSemestersDataSource = Stream.of(1, 2)
                .map(x -> Objects.requireNonNull(ExecutionYear.readCurrentExecutionYear()).getExecutionSemesterFor(x))
                .map(x -> new TupleDataSourceBean(x.getExternalId(), x.getQualifiedName())).collect(Collectors.toList());
    }

    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

    public void setExecutionSemester(ExecutionSemester executionSemester) {
        this.executionSemester = executionSemester;
    }

    public List<TupleDataSourceBean> getExecutionSemestersDataSource() {
        return executionSemestersDataSource;
    }
}