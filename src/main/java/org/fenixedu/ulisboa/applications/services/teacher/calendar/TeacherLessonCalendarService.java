package org.fenixedu.ulisboa.applications.services.teacher.calendar;

import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.Teacher;

import com.google.common.collect.Sets;

public class TeacherLessonCalendarService {

    private final ExecutionInterval executionSemester;
    private final Teacher teacher;

    public TeacherLessonCalendarService(ExecutionInterval executionInterval, Teacher teacher) {
        this.teacher = teacher;
        this.executionSemester = executionInterval;
    }

    public Collection<TeacherLessonCalendarReport> getLessonEvents() {
        return buildSearchUniverse();
    }

    private Collection<TeacherLessonCalendarReport> buildSearchUniverse() {

        if (executionSemester == null || teacher == null) {
            return Sets.newHashSet();
        }

        return this.teacher.getProfessorships(this.executionSemester).stream()
                .flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream()).map(sp -> sp.getShift())
                .flatMap(s -> s.getAssociatedLessonsSet().stream())
                .flatMap(l -> l.getAllLessonsEvents().stream().map(e -> new TeacherLessonCalendarReport(l, e)))
                .sorted(TeacherLessonCalendarReport.COMPARATOR_BY_BEGIN).collect(Collectors.toList());
    }

}
