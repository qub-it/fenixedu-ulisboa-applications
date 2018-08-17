package org.fenixedu.ulisboa.applications.services.teacher.calendar;

import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.LessonInstance;
import org.fenixedu.academic.domain.Teacher;

import com.google.common.collect.Sets;

public class TeacherLessonCalendarService {

    private ExecutionSemester executionSemester;
    private Teacher teacher;

    public TeacherLessonCalendarService(Teacher teacher, ExecutionSemester executionSemester) {
        this.teacher = teacher;
        this.executionSemester = executionSemester;
    }

    public Collection<LessonInstance> getLessonInstances() {
        return buildSearchUniverse();
    }

    private Collection<LessonInstance> buildSearchUniverse() {

        if (teacher == null || executionSemester == null) {
            return Sets.newHashSet();
        }

        return this.teacher.getProfessorships(this.executionSemester).stream()
                .flatMap(p -> p.getAssociatedShiftProfessorshipSet().stream()).map(sp -> sp.getShift())
                .flatMap(s -> s.getAssociatedLessonsSet().stream()).flatMap(l -> l.getLessonInstancesSet().stream())
                .sorted(LessonInstance.COMPARATOR_BY_BEGIN_DATE_TIME).collect(Collectors.toList());
    }

}
