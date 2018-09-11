package org.fenixedu.ulisboa.applications.services.teacher.calendar;

import java.util.Comparator;

import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.util.icalendar.EventBean;

public class TeacherLessonCalendarReport {

    public static final Comparator<TeacherLessonCalendarReport> COMPARATOR_BY_BEGIN =
            (x, y) -> x.getEvent().getBegin().compareTo(y.getEvent().getEnd());

    private final Lesson lesson;

    private final EventBean event;

    public TeacherLessonCalendarReport(Lesson lesson, EventBean event) {
        this.lesson = lesson;
        this.event = event;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public EventBean getEvent() {
        return event;
    }

}
