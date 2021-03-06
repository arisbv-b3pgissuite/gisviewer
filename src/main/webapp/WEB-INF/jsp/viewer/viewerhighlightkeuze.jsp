<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@include file="/WEB-INF/jsp/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html:html>
    <head>
        <%@include file="/WEB-INF/jsp/metatags.jsp" %>

        <title>B3P GIS Viewer</title>
        <script type="text/javascript">
            function getParent(){
                if (window.opener){
                    return window.opener;
                }else if (window.parent){
                    return window.parent;
                }else{
                    alert("No parent found");
                    return null;
                }
            }
        </script>
        <style type="text/css">
            body {
                color: #000000;
                font-family: Arial, sans-serif;
                font-size: 8pt;
            }
            h2 {
                color: #196299;
            }
            a {
                color: #1962A0;
                text-decoration: underline;
                font-weight: bold;
            }
            a:hover {
                text-decoration: none;
            }
        </style>
    </head>
    <body>
        <h2>Meerdere kaartlagen actief</h2>
        <p>Kies de kaartlaag met het object die u wilt selecteren.</p>
        <script type="text/javascript">
            var parentViewerComponent = getParent().B3PGissuite.get('ViewerComponent'),
                highlightLayers = parentViewerComponent.getHighlightLayers();
            for (var i=0; i < highlightLayers.length; i++) {
                var item = highlightLayers[i];
                var link = "<a href='#' onclick='parentViewerComponent.handlePopupValue("+item.id+"); getParent().B3PGissuite.commons.closeiFramePopup();'>" + item.title +"</a>";
                document.write("<p>"+link +"</p>");
            }
        </script>
    </body>
</html:html>