package org.fenixedu.ulisboa.applications.services.teacher.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.util.icalendar.ClassEventBean;
import org.fenixedu.academic.domain.util.icalendar.EventBean;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

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
                .flatMap(l -> getAllLessonsEvents(l).stream().map(e -> new TeacherLessonCalendarReport(l, e)))
                .sorted(TeacherLessonCalendarReport.COMPARATOR_BY_BEGIN).collect(Collectors.toList());
    }

    private static List<EventBean> getAllLessonsEvents(Lesson lesson) {
        List<EventBean> result = new ArrayList<>();
        for (YearMonthDay aDay : lesson.getAllLessonDates()) {
            DateTime beginDate = aDay.toLocalDate().toDateTime(lesson.getBeginHourMinuteSecond().toLocalTime());
            DateTime endDate = aDay.toLocalDate().toDateTime(lesson.getEndHourMinuteSecond().toLocalTime());
            result.add(new ClassEventBean(beginDate, endDate, false, Set.of(), null, null, lesson.getShift()));
        }
        return result;
    }

}
