<%@include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:if test="${!fn:startsWith(pageContext.request.requestURL, \"https\")}">
    <!-- Google analytics -->
    <!--
        <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
        </script>
        <script type="text/javascript">
            _uacct = "UA-2873163-1";
            urchinTracker();
        </script>
    -->
    
    <script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
    </script>
    <script type="text/javascript">
    var pageTracker = _gat._getTracker("UA-2873163-1");
    pageTracker._initData();
    pageTracker._trackPageview();
    </script>
</c:if>
