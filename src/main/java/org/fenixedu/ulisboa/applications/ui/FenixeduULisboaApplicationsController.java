package org.fenixedu.ulisboa.applications.ui;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/fenixedu-ulisboa-applications")
@SpringApplication(group = "logged", path = "fenixedu-ulisboa-applications",
        title = "label.org.fenixedu.ulisboa.applications.title.FenixeduULisboaApplications")
public class FenixeduULisboaApplicationsController {

}
