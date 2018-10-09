package org.fenixedu.ulisboa.applications.ui.management.teacher.calendar;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.ui.struts.action.resourceAllocationManager.ExecutionPeriodDA;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.servlet.BennuPortalDispatcher;
import org.fenixedu.bennu.struts.portal.RenderersAnnotationProcessor;
import org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarReport;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarService;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsBaseController;
import org.fenixedu.ulisboa.applications.ui.teacher.calendar.TeacherLessonCalendarController;
import org.fenixedu.ulisboa.applications.util.TeacherLessonCalendarUtil;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component("org.fenixedu.ulisboa.applications.ui.management.teacher.calendar")
@RequestMapping(ManagementTeacherLessonCalendarController.CONTROLLER_URL)
public class ManagementTeacherLessonCalendarController extends FenixeduULisboaApplicationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-applications/management/teacher/teacherlessoncalendar";
    private static final String TEACHER_LESSON_CALENDAR_JSP_PATH = TeacherLessonCalendarController.CONTROLLER_URL.substring(1);

    @ModelAttribute
    private void setFunctionalityContext(final Model model, final HttpServletRequest request) {

        final Functionality functionality = RenderersAnnotationProcessor.getFunctionalityForType(ExecutionPeriodDA.class);
        final MenuFunctionality menuItem =
                MenuFunctionality.findFunctionality(functionality.getProvider(), functionality.getKey());
        if (menuItem == null || !menuItem.isAvailableForCurrentUser()) {
            throw AuthorizationException.unauthorized();
        }

        BennuPortalDispatcher.selectFunctionality(request, menuItem);
    }

    private static final String _SCHEDULE_URI = "/";
    public static final String SCHEDULE_URL = CONTROLLER_URL + _SCHEDULE_URI;

    @RequestMapping(value = _SCHEDULE_URI + "{teacherOid}/{executionSemesterOid}", method = RequestMethod.GET)
    public String schedule(@PathVariable("teacherOid") final Teacher teacher,
            @PathVariable("executionSemesterOid") final ExecutionSemester executionSemester, final Model model) {
        final TeacherLessonCalendarParametersBean bean = new TeacherLessonCalendarParametersBean(teacher, executionSemester);
        setParametersBean(bean, model);
        setJsonEvents(TeacherLessonCalendarUtil.getJsonLessonEvents(getTeacherLessonEvents(bean)), model);

        model.addAttribute("showForm", false);

        return teacherLessonCalendarJspPage("teacherlessoncalendar");
    }

    private void setParametersBean(TeacherLessonCalendarParametersBean bean, Model model) {
        model.addAttribute("beanJson", getBeanJson(bean));
        model.addAttribute("bean", bean);
    }

    private String teacherLessonCalendarJspPage(final String page) {
        return TEACHER_LESSON_CALENDAR_JSP_PATH + "/" + page;
    }

    private void setJsonEvents(String events, Model model) {
        model.addAttribute("events", events);
    }

    private Collection<TeacherLessonCalendarReport> getTeacherLessonEvents(final TeacherLessonCalendarParametersBean bean) {

        final ExecutionSemester currentExecutionSemester = bean.getExecutionSemester();
        final Teacher currentTeacher = bean.getTeacher();

        final TeacherLessonCalendarService service = new TeacherLessonCalendarService(currentTeacher, currentExecutionSemester);

        return service.getLessonEvents();
    }
}
