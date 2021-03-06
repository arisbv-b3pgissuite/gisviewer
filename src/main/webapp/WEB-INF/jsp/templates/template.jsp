<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%--
B3P Gisviewer is an extension to Flamingo MapComponents making
it a complete webbased GIS viewer and configuration tool that
works in cooperation with B3P Kaartenbalie.

Copyright 2006 - 2013 B3Partners BV

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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@include file="/WEB-INF/jsp/taglibs.jsp" %>

<!--[if lt IE 7]> <html class="lt-ie9 lt-ie8 lt-ie7" lang="nl"> <![endif]-->
<!--[if IE 7]> <html class="lt-ie9 lt-ie8" lang="nl"> <![endif]-->
<!--[if IE 8]> <html class="lt-ie9" lang="nl"> <![endif]-->
<!--[if gt IE 8]><!--> <html lang="nl"> <!--<![endif]-->
    <head>
        <%@include file="/WEB-INF/jsp/metatags.jsp" %>
        
        <title><tiles:insert name='title'/> - GIS Viewer</title>

        <%-- Themes selection --%>
        <c:choose>
            <c:when test="${empty theme}">
                <%-- Fallback to B3P style --%>
                <link href="<html:rewrite page='/styles/gisviewer_base.css' module=''/>" rel="stylesheet" type="text/css">
            </c:when>
            <c:otherwise>
                <%-- Select theme style --%>
                <link href="<html:rewrite page='/themes/${theme}/styles/gisviewer_base.css' module=''/>" rel="stylesheet" type="text/css">
            </c:otherwise>
        </c:choose>
        <c:if test="${!empty cmsPage}">
            <link href="<html:rewrite page='/cmsstyle/${cmsPage.id}' module=''/>" rel="stylesheet" type="text/css">
        </c:if>

        <script type="text/javascript" src="<html:rewrite page='/scripts/validation.jsp' module=''/>"></script>
        <script type="text/javascript" src="<html:rewrite page='/scripts/lib/jquery-1.3.2.min.js' module=''/>"></script>
        <script type="text/javascript" src="<html:rewrite page='/scripts/lib/jquery-ui-1.8.10.custom.min.js' module=''/>"></script>
        <c:choose>
            <c:when test="${not empty param.debug}">
                <script type="text/javascript" src="<html:rewrite page='/scripts/commonfunctions.js' module=''/>"></script>
                <script type="text/javascript" src="<html:rewrite page='/scripts/components/Component.js'/>"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="<html:rewrite page='/scripts/commonfunctions-min.js' module=''/>"></script>
            </c:otherwise>
        </c:choose>
        
        <meta name="HandheldFriendly" content="True">
        <meta name="MobileOptimized" content="width=device-width; height=device-height; user-scalable=no; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, maximum-scale=1.0, minimum-scale=1.0">
        <meta http-equiv="cleartype" content="on">

        <script type="text/javascript">
            var gisviewerurls = {
                'mapicon': '<html:rewrite page='/images/icons/map.png' module='' />',
                'listicon': '<html:rewrite page='/images/icons/search_list.png' module='' />'
            };
            B3PGissuite.commons.attachOnload(B3PGissuite.commons.checkLocation);
        </script>
    </head>
    <body class="homebody">
        <div id="wrapper">
            <div id="header"><div id="header_content"><tiles:insert attribute="menu" /></div></div>
            <div id="content_normal">
                <div id="content">
                    <tiles:insert attribute="content" />
                </div>
            </div>
        </div>
        <div id="footer">
            <tiles:insert attribute="footer" />
        </div>

        <tiles:insert definition="googleAnalytics"/>
    </body>
</html>
