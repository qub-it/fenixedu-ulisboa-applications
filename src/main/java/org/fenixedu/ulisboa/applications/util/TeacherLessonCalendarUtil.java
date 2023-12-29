package org.fenixedu.ulisboa.applications.util;

import java.util.Collection;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarReport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TeacherLessonCalendarUtil {

    public static String getJsonLessonEvents(Collection<TeacherLessonCalendarReport> collection) {

        final JsonArray result = new JsonArray();

        for (final TeacherLessonCalendarReport lessonEventReport : collection) {
            final JsonObject event = new JsonObject();

            event.addProperty("start", lessonEventReport.getEvent().getBegin().toString());
            event.addProperty("end", lessonEventReport.getEvent().getEnd().toString());

            final Shift lessonShift = lessonEventReport.getLesson().getShift();
            final JsonObject shiftJSON = new JsonObject();
            shiftJSON.addProperty("name", lessonShift.getName());
            shiftJSON.addProperty("typeInitials", lessonShift.getCourseLoadType().getInitials().getContent());
            shiftJSON.addProperty("type", lessonShift.getCourseLoadType().getName().getContent());
            event.add("shift", shiftJSON);

            final ExecutionCourse lessonExecutionCourse = lessonEventReport.getLesson().getExecutionCourse();
            final JsonObject executionCourseJSON = new JsonObject();
            executionCourseJSON.addProperty("id", lessonExecutionCourse.getExternalId());
            executionCourseJSON.addProperty("name", lessonExecutionCourse.getNameI18N().getContent());
            executionCourseJSON.addProperty("initials", lessonExecutionCourse.getSigla());
            executionCourseJSON.addProperty("code", lessonExecutionCourse.getCode());
            executionCourseJSON.addProperty("url", lessonExecutionCourse.getSiteUrl());
            event.add("executionCourse", executionCourseJSON);

            final Space space = lessonEventReport.getLesson().getSala();
            if (space != null) {
                final JsonObject spaceJSON = new JsonObject();
                spaceJSON.addProperty("name", space.getName());
                spaceJSON.addProperty("presentationName", space.getPresentationName());
                event.add("space", spaceJSON);
            }

            event.addProperty("title", lessonExecutionCourse.getSigla() + " (" + lessonExecutionCourse.getCode() + " - "
                    + lessonShift.getName() + " " + lessonShift.getCourseLoadType().getName().getContent() + ")");

            result.add(event);
        }
        return result.toString();
    }

}
