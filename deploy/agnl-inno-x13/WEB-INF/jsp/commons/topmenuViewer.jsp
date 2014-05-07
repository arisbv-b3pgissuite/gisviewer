<%--
B3P Gisviewer is an extension to Flamingo MapComponents making
it a complete webbased GIS viewer and configuration tool that
works in cooperation with B3P Kaartenbalie.

Copyright 2006, 2007, 2008 B3Partners BV

This file is part of B3P Gisviewer.

B3P Gisviewer is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

B3P Gisviewer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with B3P Gisviewer.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page isELIgnored="false"%>

<img width="940" height="90" alt="AgentschapNL Innovatie" src="<html:rewrite page='/images/agentschapnl_logo.gif' module=''/>" class="logo_boven" />

<div id="duurzaamheidbalk_viewer">
    <img alt="Duurzaamheid" src="<html:rewrite page='/images/duurzaamheid_header.png' module=''/>" />
</div>

<div id="topmenu">
    <c:set var="requestURI" value="${fn:split(requestScope['javax.servlet.forward.request_uri'], '/')}" />
    <c:set var="requestJSP" value="${requestURI[fn:length(requestURI) - 1]}" />
    <c:set var="kaartid" value="${param['id']}"/>
    <c:set var="appCode" value="${param['appCode']}"/>

    <c:set var="stijlklasse" value="menulink" />
    <c:if test="${requestJSP eq 'index.do' or requestJSP eq 'indexlist.do' or requestJSP eq ''}">
        <c:set var="stijlklasse" value="activemenulink" />
    </c:if>
    <html:link page="/indexlist.do?appCode=${appCode}" styleClass="${stijlklasse}" module=""><fmt:message key="commons.topmenu.home"/></html:link>
	
	<c:set var="stijlklasse" value="menulink" />
    <html:link page="/viewer.do?appCode=${appCode}" target="_new" styleClass="${stijlklasse}" module="">
        &#155; Viewer
    </html:link>
	
    <a href="http://www.agentschapnl.nl/nl/node/445061" class="menulink" target="_blank">&#155; Contact</a>

    <div id="menulinks_onder"></div>
    <div id="menulinks_whitespaceonder"></div>
</div>