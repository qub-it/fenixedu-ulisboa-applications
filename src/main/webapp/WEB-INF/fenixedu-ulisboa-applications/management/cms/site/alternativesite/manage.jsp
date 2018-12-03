<%@ page import="org.fenixedu.ulisboa.applications.ui.management.cms.site.alternativesite.ManagementAlternativeSiteController" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../../../commons/angularInclude.jsp"/>

<script type="text/javascript">

    angular.module('managementAlternativeSiteApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).
            controller('ManagementAlternativeSiteController', [
                '$scope', '$timeout', '$http', function ($scope, $timeout, $http) {

                    $scope.object = ${beanJson};
                    $scope.form = {};
                    $scope.form.object = $scope.object;

                }]);

</script>

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.title" />
        <small></small>
    </h1>
</div>

<%-- MESSAGES --%>
<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">
    
        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign"
                    aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">
   
        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>

<%-- FORM --%>
<form method="post" enctype="multipart/form-data" class="form-horizontal" id="paramsForm"
	  action="${pageContext.request.contextPath}<%= ManagementAlternativeSiteController.UPLOAD_FILE_URL %>"
	  name="form" ng-app="managementAlternativeSiteApp"
	  ng-controller="ManagementAlternativeSiteController" novalidate>

	<input name="bean" type="hidden" value="{{ object }}"/>
	
	<div class="alert alert-info" role="alert">
		<p><spring:message code="label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.uploadFile.help.allowedExtensions"></spring:message></p>
		<p><spring:message code="label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.uploadFile.help.format"></spring:message></p>
    </div>

	<div class="form-group row">
		<div class="col-sm-1 control-label">
			<spring:message code="label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.executionSemester"/>
		</div>
		<div class="col-sm-6">
			<select id="executionSemesterSelect" class="form-control"
					ng-model="object.executionSemester"
					ng-options="executionSemester.id as executionSemester.text for executionSemester in object.executionSemestersDataSource"
					required>
			</select>
		</div>
	</div>
	
	<div class="form-group row">
		<div class="col-sm-1 control-label">
			<spring:message code="label.org.fenixedu.ulisboa.applications.management.cms.site.alternativesite.uploadFile"/>
		</div>
		<div class="col-sm-6">
			<input type="file" class="form-control" name="inputFile" accept=".xls,.xlsx" required />
		</div>
    </div>
	
	<div class="form-group row">
		<div class="col-sm-offset-1 col-sm-10">
	       <input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />" />
	    </div>
	</div>
</form>