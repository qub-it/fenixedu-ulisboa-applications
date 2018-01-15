package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "org.fenixedu.ulisboa.applications", bundles = "FenixeduULisboaApplicationsResources")
public class FenixeduULisboaApplicationsSpringConfiguration {
    public final static String BUNDLE = "resources/FenixeduULisboaApplicationsResources";
}
