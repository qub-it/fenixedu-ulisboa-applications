<jsp:useBean id="bean" scope="request"
			 type="org.fenixedu.ulisboa.applications.dto.teacher.shift.calendar.TeacherLessonCalendarParametersBean"/>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="org.fenixedu.ulisboa.applications.ui.teacher.calendar.TeacherLessonCalendarController" %>

<jsp:include page="../../commons/angularInclude.jsp"/>

<spring:url var="staticUrl" value="/themes/fenixedu-learning-theme/static"/>
<spring:url var="calendarUrl" value="/static/fenixedu-ulisboa-applications"/>

<link href='${calendarUrl}/css/fullcalendar.css' rel='stylesheet'/>
<link href='${calendarUrl}/css/fullcalendar.print.css' rel='stylesheet' media='print'/>
<link href='${staticUrl}/css/schedule.css' rel='stylesheet' rel='stylesheet'/>

<script src='${calendarUrl}/js/moment.min.js'></script>
<script src='${staticUrl}/js/jquery-ui.fullCalendar.custom.min.js'></script>
<script src='${calendarUrl}/js/fullcalendar.js'></script>

<script type="text/javascript">

    angular.module('teacherLessonCalendarApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).
            controller('TeacherLessonCalendarController', [
                '$scope', '$timeout', '$http', function ($scope, $timeout, $http) {

                    $scope.object = ${beanJson};
                    $scope.form = {};
                    $scope.form.object = $scope.object;

                    $scope.postBack = createAngularPostbackFunction($scope);

                    $scope.onBeanChange = function () {
                        $scope.submitForm();
                    }

                    $scope.submitForm = function () {

                        if ($scope.object.executionSemester !== null) {
                            $scope.$apply();
                            $('#paramsForm').
                                attr('action', '${pageContext.request.contextPath}${controllerUrl}/')
                            $('#paramsForm').submit();
                        }
                    }

                }]);

</script>

<style>
	a:hover {
		text-decoration: none;
	}

	a:visited {
		text-decoration: none;
	}

	a:link {
		color: inherit;
		text-decoration: underline;
	}

	a:active {
		text-decoration: underline;
	}

	.alert {
		padding: 5px;
	}
</style>

<%-- TITLE --%>
<div class="page-header">
	<h2>
		<spring:message
				code="label.org.fenixedu.ulisboa.applications.teacher.title.teacherLessonCalendar"/>
	</h2>
	<div class="alert alert-info" role="alert"><h5><strong><spring:message
			code="label.org.fenixedu.ulisboa.applications.teacher.teacherLessonCalendar.disclaimer"/></strong></h5>
	</div>
</div>

<%-- FORM --%>
<form method="post" class="form-horizontal" id="paramsForm"
	  name="form" ng-app="teacherLessonCalendarApp"
	  ng-controller="TeacherLessonCalendarController" novalidate>

	<input name="bean" type="hidden" value="{{ object }}"/>

	<div class="form-group row">
		<div class="col-sm-1 control-label">
			<spring:message code="label.org.fenixedu.ulisboa.applications.teacher.teacherLessonCalendar.semester"/>
		</div>
		<div class="col-sm-6">
			<select id="executionSemesterSelect" class="form-control" ng-change="onBeanChange()"
					ng-model="object.executionSemester"
					ng-options="executionSemester.id as executionSemester.text for executionSemester in object.executionSemestersDataSource">
			</select>
		</div>
	</div>
	
	<c:if test="${showTeacherSelector}">
		<div class="form-group row">
			<div class="col-sm-1 control-label">
				<spring:message code="label.org.fenixedu.ulisboa.applications.teacher.teacherLessonCalendar.teacher"/>
			</div>
			<div class="col-sm-6">
				<select id="teacherSelect" class="form-control" ng-change="onBeanChange()"
						ng-model="object.teacher"
						ng-options="teacher.id as teacher.text for teacher in object.teachersDataSource">
				</select>
			</div>
		</div>
	</c:if>
</form>

<c:if test="${bean.hasAllRequiredFieldsFilled()}">
	<div id="calendar"></div>
	
	<script>
	
	    var i18nDayNames = [
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.sunday"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.monday"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.tuesday"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.wednesday"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.thursday"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.friday"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.weekday.saturday"/>"];
	
	    var i18nDayNamesShort = i18nDayNames.map(function (el) {
	        return el.substr(0, 3);
	    });
	
	    var i18nMonthNames = [
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.january"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.february"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.march"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.april"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.may"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.june"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.july"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.august"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.september"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.october"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.november"/>",
	        "<spring:message code="label.org.fenixedu.ulisboa.applications.month.december"/>"];
	
	    var i18nMonthNamesShort = i18nMonthNames.map(function (el) {
	        return el.substr(0, 3);
	    });
	
	    var events = ${events};
	
	    //Each executionCourse should have an unique background color
	    const uniqueExecutionCourseCodes = Array.from(events, event => event.executionCourse.code).
	                                             filter((value, index, array) => array.indexOf(value) === index).
	                                             sort();
	    const colors = [
	        '#813537', '#A86900', '#00A800', '#0073E7', '#FFBF00', '#A800A8', '#9D9CA8', '#436659', '#297E88', '#DB112C'];
	
	    events.forEach(function (event) {
	        event.backgroundColor = colors[uniqueExecutionCourseCodes.indexOf(event.executionCourse.code) % colors.length];
	        event.borderColor = "#000000";
	    });
	
	    $(document).ready(function () {
	        $('#calendar').fullCalendar({
	            events: events,
	            nowIndicator: true,
	            defaultDate: getEventDefaultDate(),
	            defaultView: 'agendaWeek',
	            validRange: {
	                start: '${bean.executionSemester.beginLocalDate.toString()}',
	                end: '${bean.executionSemester.endLocalDate.toString()}'
	            },
	            minTime: '07:00',
	            maxTime: '24:00',
	            timeFormat: 'HH:mm',
	            slotLabelFormat: 'HH:mm',
	            allDaySlot: false,
	            firstDay: 1,
	            editable: false,
	            eventLimit: true, // allow "more" link when too many events
	            weekends: true,
	            hiddenDays: [0], //Hides Sunday
	            height: 500,
	            slotDuration: '00:30:00',
	            slotEventOverlap: false,
	            dayNames: i18nDayNames,
	            dayNamesShort: i18nDayNamesShort,
	            monthNames: i18nMonthNames,
	            monthNamesShort: i18nMonthNamesShort,
	            header: {
	                left: 'prev,next today', center: 'title', right: 'month,agendaWeek,basicDay,listMonth'
	            },
	            buttonText: {
	                today: "<spring:message code="label.org.fenixedu.ulisboa.applications.calendar.today"/>",
	                month: "<spring:message code="label.org.fenixedu.ulisboa.applications.calendar.month"/>",
	                week: "<spring:message code="label.org.fenixedu.ulisboa.applications.calendar.week"/>",
	                day: "<spring:message code="label.org.fenixedu.ulisboa.applications.calendar.day"/>",
	                listMonth: "<spring:message code="label.org.fenixedu.ulisboa.applications.calendar.listMonth"/>"
	            },
	            views: {
	                agendaWeek: {columnHeaderFormat: 'ddd DD/MM'},
	                basicDay: {columnHeaderFormat: 'dddd'},
	                listMonth: {noEventsMessage: "<spring:message code="label.org.fenixedu.ulisboa.applications.calendar.noEvents"/>"}
	            },
	
	            eventRender: function (event, element, view) {
	
	                if (view.type === "month") {
	                    renderAgendaMonthEvent(event, element);
	                }
	
	                if (view.type === "agendaWeek") {
	                    renderAgendaWeekEvent(event, element);
	                }
	
	                if (view.type === "basicDay") {
	                    renderAgendaDayEvent(event, element);
	                }
	
	                if (view.type === "listMonth") {
	                    renderListMonthEvent(event, element);
	                }
	            }
	        });
	
	        function getEventDefaultDate() {
	            <c:if test="${bean.executionSemester.current}">
	            return moment();
	            </c:if>
	            <c:if test="${not bean.executionSemester.current}">
	            return events.length > 0 ? events[0].start : null;
	            </c:if>
	        }
	
	        function renderAgendaMonthEvent(event, element) {
	            var title = urlEvent(event.executionCourse.initials, event.executionCourse.url) + " (" + event.shift.name +
	                        " - " + event.shift.typeInitials + ")";
	            element.find('.fc-title').replaceWith(title);
	            element.find('.fc-title').css("white-space", "normal");
	
	            element.popover(eventPopoverConfig(event));
	        }
	
	        function renderAgendaWeekEvent(event, element) {
	            var title = urlEvent(event.executionCourse.initials, event.executionCourse.url) + "<br/>" +
	                        event.shift.name + " - " + event.shift.typeInitials;
	            if (event.space)
	            	title = title + " (" + event.space.name + ")";
	            element.find('.fc-title').replaceWith(title);
	            element.find('.fc-content').css('padding', '2px 0 0 2px');
	            element.find('.fc-time').css("white-space", "normal");
	
	            element.popover(eventPopoverConfig(event));
	        }
	
	        function renderAgendaDayEvent(event, element) {
	            var title = [
	                "",
	                event.executionCourse.code + " - " + urlEvent(event.executionCourse.name, event.executionCourse.url),
	                event.shift.name + " - " + event.shift.type].join("<br/>");
	            if (event.space)
	            	title = title + "<br />" + event.space.presentationName;
	
	            element.find('.fc-title').replaceWith(title);
	        }
	
	        function renderListMonthEvent(event, element) {
	            var title = event.executionCourse.code + " - " +
	                        urlEvent(event.executionCourse.name, event.executionCourse.url) + " (" + event.shift.name +
	                        " - " + event.shift.type + ")";
	            if (event.space)
	            	title = title + " (" + event.space.presentationName + ")";
	            element.find(".fc-list-item-title a").replaceWith(title);
	        }
	
	        function urlEvent(eventName, url) {
	            return "<a href=\"" + url + "\" " + "target=\"_blank\">" + eventName + "</a>";
	        }
	
	        function eventPopoverConfig(event) {
	            return {
	                content: "<div>" + event.executionCourse.code + " - " + event.executionCourse.name + "</div>" + "<hr>" +
	                         "<div>" + event.shift.type + (event.space ? "<br />" + event.space.presentationName : "") + "</div>",
	                html: 'true',
	                trigger: 'hover',
	                placement: 'top',
	                container: 'body'
	            }
	        }
	    });
	</script>
</c:if>