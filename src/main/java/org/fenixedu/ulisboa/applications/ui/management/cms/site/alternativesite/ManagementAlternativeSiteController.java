package org.fenixedu.ulisboa.applications.ui.management.cms.site.alternativesite;

import java.io.File;
import java.io.IOException;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.applications.dto.management.cms.site.alternativesite.ManagementAlternativeSitesParametersBean;
import org.fenixedu.ulisboa.applications.services.cms.site.alternativesite.AlternativeSitesImporter;
import org.fenixedu.ulisboa.applications.services.cms.site.alternativesite.AlternativeSitesImporter.ProcessResult;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsBaseController;
import org.fenixedu.ulisboa.applications.ui.FenixeduULisboaApplicationsController;
import org.fenixedu.ulisboa.applications.util.ULisboaApplicationsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduULisboaApplicationsController.class,
        title = "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.title",
        accessGroup = "#eLearningManagers")
@RequestMapping(ManagementAlternativeSiteController.CONTROLLER_URL)
public class ManagementAlternativeSiteController extends FenixeduULisboaApplicationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-applications/management/cms/site/alternativesite";
    private static final String JSP_PATH = CONTROLLER_URL;

    @Autowired
    private AlternativeSitesImporter importer;

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        final ExecutionInterval executionInterval = ExecutionInterval.findFirstCurrentChild(null);
        final ManagementAlternativeSitesParametersBean bean = new ManagementAlternativeSitesParametersBean();
        bean.setExecutionSemester(executionInterval);

        return manage(model, redirectAttributes, bean);
    }

    private static final String _MANAGE_URI = "/manage";
    public static final String MANAGE_URL = CONTROLLER_URL + _MANAGE_URI;

    @RequestMapping(value = _MANAGE_URI)
    public String manage(Model model, RedirectAttributes redirectAttributes,
            @RequestParam("bean") ManagementAlternativeSitesParametersBean bean) {
        setParametersBean(bean, model);

        return jspPage("manage");
    }

    private static final String _UPLOAD_FILE_URI = "/upload";
    public static final String UPLOAD_FILE_URL = CONTROLLER_URL + _UPLOAD_FILE_URI;

    @RequestMapping(value = _UPLOAD_FILE_URI, headers = "content-type=multipart/*", method = RequestMethod.POST)
    public String manage(@RequestParam("bean") ManagementAlternativeSitesParametersBean bean,
            @RequestParam(value = "inputFile") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        if (bean.getExecutionSemester() != null && file != null && !file.isEmpty()) {
            try {
                File temporaryFile = File.createTempFile(getClass().getSimpleName(), "");
                temporaryFile.deleteOnExit();
                file.transferTo(temporaryFile);

                ProcessResult result = importer.processFile(temporaryFile, bean.getExecutionSemester());
                if (result.hasFailed()) {
                    addProcessFileErrorMessage(model);
                } else {
                    if (result.getErrorMessages().isEmpty()) {
                        addInfoMessage(ULisboaApplicationsUtil.bundle(
                                "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.fileProcessedWithoutErrors"),
                                model);
                    } else {
                        addInfoMessage(ULisboaApplicationsUtil.bundle(
                                "label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.fileProcessedWithErrors"),
                                model);
                    }

                    for (String infoMessage : result.getInfoMessages()) {
                        addInfoMessage(infoMessage, model);
                    }

                    for (String errorMessage : result.getErrorMessages()) {
                        addErrorMessage(errorMessage, model);
                    }
                }

                temporaryFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
                addProcessFileErrorMessage(model);
            }
        }

        return manage(model, redirectAttributes, bean);
    }

    private void setParametersBean(ManagementAlternativeSitesParametersBean bean, Model model) {
        model.addAttribute("beanJson", getBeanJson(bean));
        model.addAttribute("bean", bean);
    }

    private void addProcessFileErrorMessage(Model model) {
        addErrorMessage(ULisboaApplicationsUtil
                .bundle("label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.process.failure"), model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }
}
