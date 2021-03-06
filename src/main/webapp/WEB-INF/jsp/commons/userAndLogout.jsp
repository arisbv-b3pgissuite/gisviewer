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

<c:choose>
    <c:when test="${pageContext.request.remoteUser != null}">
        <script type="text/javascript">
            function logout() {
                var kburl = '${kburl}';
                var logoutLocation = '/kaartenbalie/logout.do'
                if (kburl!='') {
                    var pos = kburl.lastIndexOf("services");
                    if (pos>=0) {
                        logoutLocation = kburl.substring(0,pos) + "logout.do";
                    }
                }
                lof = document.getElementById('logoutframe');
                lof.src=logoutLocation;
                
                location.href = '<html:rewrite page="/logout.do?cmsPageId=${cmsPageId}&appCode=${appCode}" module=""/>';
            };
        </script>
        <div id="logoutvak" style="display: none;">
            <iframe src="" id="logoutframe" name="logoutframe"></iframe>
        </div>

        <c:set var="userName" value="${pageContext.request.remoteUser}" />

        <!-- Tekst ingelogd als alleen tonen als de gebruiker niet anoniem is -->
        <c:if test="${userName != 'anoniem'}">
            <fmt:message key="commons.userandlogout.ingelogdals"/> 
            <c:if test="${not empty appName}">
                <c:out value="${appName}"/>
            </c:if>
            
            (<c:out value="${pageContext.request.remoteUser}"/>)
        </c:if>
            
        <a href="#"  onclick="javascript:logout();"><fmt:message key="commons.userandlogout.uitloggen"/></a>
    </c:when>
    <c:otherwise>
        <html:link page="/login.do" module=""><fmt:message key="commons.userandlogout.inloggen"/></html:link>
    </c:otherwise>
</c:choose>
