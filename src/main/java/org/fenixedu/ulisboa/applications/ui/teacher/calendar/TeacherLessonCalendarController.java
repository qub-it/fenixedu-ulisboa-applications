package org.fenixedu.ulisboa.applications.ui.teacher.calendar;

import java.util.Collection;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarReport;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarService;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsBaseController;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SpringFunctionality(app = FenixeduULisboaApplicationsController.class,
        title = "label.org.fenixedu.ulisboa.applications.teacher.teacherLessonCalendar", accessGroup = "logged")
@RequestMapping(TeacherLessonCalendarController.CONTROLLER_URL)
public class TeacherLessonCalendarController extends FenixeduULisboaApplicationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-applications/teacher/teacherlessoncalendar";
    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        final ExecutionSemester executionSemester = ExecutionSemester.readActualExecutionSemester();
        final TeacherLessonCalendarParametersBean bean = new TeacherLessonCalendarParametersBean();
        bean.setExecutionSemester(executionSemester);

        return home(model, redirectAttributes, bean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String home(Model model, RedirectAttributes redirectAttributes,
            @RequestParam("bean") TeacherLessonCalendarParametersBean bean) {
        setParametersBean(bean, model);
        setJsonEvents(getJsonLessonEvents(getTeacherLessonEvents(bean)), model);
        return jspPage("teacherlessoncalendar");
    }

    private void setParametersBean(TeacherLessonCalendarParametersBean bean, Model model) {
        model.addAttribute("beanJson", getBeanJson(bean));
        model.addAttribute("bean", bean);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    private void setJsonEvents(String events, Model model) {
        model.addAttribute("events", events);
    }

    private String getJsonLessonEvents(Collection<TeacherLessonCalendarReport> collection) {

        final JsonArray result = new JsonArray();

        for (final TeacherLessonCalendarReport lessonEventReport : collection) {
            final JsonObject event = new JsonObject();

            event.addProperty("start", lessonEventReport.getEvent().getBegin().toString());
            event.addProperty("end", lessonEventReport.getEvent().getEnd().toString());

            final Shift lessonShift = lessonEventReport.getLesson().getShift();
            final JsonObject shiftJSON = new JsonObject();
            shiftJSON.addProperty("name", lessonShift.getNome());
            shiftJSON.addProperty("typeInitials", lessonShift.getTypes().iterator().next().getSiglaTipoAula());
            shiftJSON.addProperty("type", lessonShift.getTypes().iterator().next().getFullNameTipoAula());
            event.add("shift", shiftJSON);

            final ExecutionCourse lessonExecutionCourse = lessonEventReport.getLesson().getExecutionCourse();
            final JsonObject executionCourseJSON = new JsonObject();
            executionCourseJSON.addProperty("id", lessonExecutionCourse.getExternalId());
            executionCourseJSON.addProperty("name", lessonExecutionCourse.getNameI18N().getContent());
            executionCourseJSON.addProperty("initials", lessonExecutionCourse.getSigla());
            executionCourseJSON.addProperty("code", lessonExecutionCourse.getCode());
            executionCourseJSON.addProperty("url", lessonExecutionCourse.getSiteUrl());
            event.add("executionCourse", executionCourseJSON);

            event.addProperty("title", lessonExecutionCourse.getSigla() + " (" + lessonExecutionCourse.getCode() + " - "
                    + lessonShift.getNome() + " " + lessonShift.getTypes().iterator().next().getSiglaTipoAula() + ")");

            result.add(event);
        }
        return result.toString();
    }

    private Collection<TeacherLessonCalendarReport> getTeacherLessonEvents(final TeacherLessonCalendarParametersBean bean) {

        final ExecutionSemester currentExecutionSemester = bean.getExecutionSemester();
        final Teacher currentTeacher = Authenticate.getUser().getPerson().getTeacher();

        final TeacherLessonCalendarService service = new TeacherLessonCalendarService(currentTeacher, currentExecutionSemester);

        return service.getLessonEvents();
    }
}
