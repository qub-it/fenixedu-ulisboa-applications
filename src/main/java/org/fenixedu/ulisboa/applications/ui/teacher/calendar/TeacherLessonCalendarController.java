package org.fenixedu.ulisboa.applications.ui.teacher.calendar;

import java.util.Collection;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean;
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
        setJsonEvents(TeacherLessonCalendarUtil.getJsonLessonEvents(getTeacherLessonEvents(bean)), model);

        model.addAttribute("showForm", true);

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

        final ExecutionSemester currentExecutionSemester = bean.getExecutionSemester();
        final Teacher currentTeacher = Authenticate.getUser().getPerson().getTeacher();

        final TeacherLessonCalendarService service = new TeacherLessonCalendarService(currentTeacher, currentExecutionSemester);

        return service.getLessonEvents();
    }
}
