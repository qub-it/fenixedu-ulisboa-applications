package org.fenixedu.ulisboa.applications.ui.teacher.calendar;

import java.util.Collection;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean;
import org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean.ExecutionSemesterProviderType;
import org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean.TeacherProviderType;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarReport;
import org.fenixedu.ulisboa.applications.services.teacher.calendar.TeacherLessonCalendarService;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsBaseController;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsController;
import org.fenixedu.ulisboa.applications.util.TeacherLessonCalendarUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduULisboaApplicationsController.class,
        title = "label.org.fenixedu.ulisboa.applications.teacher.teacherLessonCalendar", accessGroup = "logged")
@RequestMapping(TeacherLessonCalendarController.CONTROLLER_URL)
public class TeacherLessonCalendarController extends FenixeduULisboaApplicationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-applications/teacher/teacherlessoncalendar";
    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private static final ExecutionSemesterProviderType EXECUTION_SEMESTER_PROVIDER_TYPE =
            ExecutionSemesterProviderType.CURRENT_EXECUTION_YEAR;
    private static final TeacherProviderType TEACHER_PROVIDER_TYPE = TeacherProviderType.CURRENT_AUTHENTICATED_TEACHER;

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        final ExecutionInterval executionInterval = ExecutionInterval.findFirstCurrentChild(null);
        final TeacherLessonCalendarParametersBean bean =
                new TeacherLessonCalendarParametersBean(EXECUTION_SEMESTER_PROVIDER_TYPE, TEACHER_PROVIDER_TYPE);
        bean.setExecutionSemester(executionInterval);

        return home(model, redirectAttributes, bean);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String home(Model model, RedirectAttributes redirectAttributes,
            @RequestParam("bean") TeacherLessonCalendarParametersBean bean) {
        bean.setTeacher(Authenticate.getUser().getPerson().getTeacher());
        bean.updateData(EXECUTION_SEMESTER_PROVIDER_TYPE, TEACHER_PROVIDER_TYPE);
        setParametersBean(bean, model);
        setJsonEvents(TeacherLessonCalendarUtil.getJsonLessonEvents(getTeacherLessonEvents(bean)), model);

        model.addAttribute("showTeacherSelector", false);

        model.addAttribute("controllerUrl", CONTROLLER_URL);

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

    private Collection<TeacherLessonCalendarReport> getTeacherLessonEvents(final TeacherLessonCalendarParametersBean bean) {

        final ExecutionInterval currentExecutionSemester = bean.getExecutionSemester();
        final Teacher currentTeacher = bean.getTeacher();

        final TeacherLessonCalendarService service = new TeacherLessonCalendarService(currentExecutionSemester, currentTeacher);

        return service.getLessonEvents();
    }
}
