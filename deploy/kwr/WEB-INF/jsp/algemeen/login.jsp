<%@include file="/WEB-INF/jsp/taglibs.jsp" %>

<div class="infobalk">
    <div class="infobalk_description"><fmt:message key="algemeen.login.infobalk"/></div>
    <div class="infobalk_actions"><tiles:insert name="loginblock"/></div>
</div>

<tiles:insert definition="actionMessages"/>

<!-- Loop door tekstblokken heen -->
<c:forEach var="tb" varStatus="status" items="${tekstBlokken}">
    <div class="content_block">
        <div class="content_title"><c:out value="${tb.titel}"/></div>

        <!-- Indien toonUrl aangevinkt is dan inhoud van url in iFrame tonen -->
        <c:if test="${tb.toonUrl}">
            <iframe class="iframe_tekstblok" id="iframe_${tb.titel}" name="iframe_${tb.titel}" frameborder="0" src="${tb.url}"></iframe>
        </c:if>

        <!-- Anders gewoon de tekst tonen van tekstblok -->
        <c:if test="${!tb.toonUrl}">
        <div class="inleiding_body">
            ${tb.tekst}

            <c:if test="${!empty tb.url}">
            Meer informatie: <a href="${tb.url}" target="_new">${tb.url}</a>
            </c:if>

            <c:if test="${tb.toonUrl}">
                <iframe id="iframe_${tb.titel}" name="iframe_${tb.titel}" frameborder="0" src="${tb.url}"></iframe>
            </c:if>
        </div>
        </c:if>
    </div>
</c:forEach>

<div class="content_block">

<div class="content_title">Inloggen</div>

<div class="inleiding_body">
<form id="loginForm" action="j_security_check" method="POST">
<table>
    <tr>
        <td style="width: 145px;"><fmt:message key="algemeen.login.gebruikersnaam"/></td>
        <td><input class="inputfield" type="text" name="j_username" size="36"></td>
    </tr>
    <tr>
        <td><fmt:message key="algemeen.login.wachtwoord"/></td>
        <td><input class="inputfield" type="password" name="j_password" size="36"></td>
    </tr>
    <tr>
        <td><fmt:message key="algemeen.login.code"/></td>
        <td><input class="inputfield" type="text" name="j_code" size="36"></td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td><input class="inlogbutton" type="submit" id="loginsubmit" value="<fmt:message key="algemeen.login.login"/>"></td>
    </tr>
</table>
</form>
    </div>
</div>

<script type="text/javascript">    
    document.forms.loginForm.j_username.focus();
    (function($) {
        var $link = $('<a href="#" class="menulink" style="float: left;">Inloggen</a>').click(function(event) {
            $('#loginForm').submit();
            event.preventDefault();
        });
        $('.inputfield').keyup(function(e) {
            if(e.keyCode == 13) { // Enter key
                $('#loginForm').submit();
            }
        });
        $('#loginsubmit').hide().parent().append($link);
    })(jQuery);
</script>