package org.fenixedu.ulisboa.applications.dto.management.cms.site.alternativesite;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;

public class ManagementAlternativeSitesParametersBean implements IBean {

    private ExecutionInterval executionSemester;

    private List<TupleDataSourceBean> executionSemestersDataSource;

    public ManagementAlternativeSitesParametersBean() {
        updateData();
    }

    public void updateData() {

        updateExecutionSemestersDataSource();

    }

    private void updateExecutionSemestersDataSource() {
        this.executionSemestersDataSource = Bennu.getInstance().getExecutionPeriodsSet().stream()
                .sorted(ExecutionInterval.COMPARATOR_BY_BEGIN_DATE.reversed())
                .map(x -> new TupleDataSourceBean(x.getExternalId(), x.getQualifiedName())).collect(Collectors.toList());
    }

    public ExecutionInterval getExecutionSemester() {
        return executionSemester;
    }

    public void setExecutionSemester(ExecutionInterval executionInterval) {
        this.executionSemester = executionInterval;
    }

    public List<TupleDataSourceBean> getExecutionSemestersDataSource() {
        return executionSemestersDataSource;
    }

}
