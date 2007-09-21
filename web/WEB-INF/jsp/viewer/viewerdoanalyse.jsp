<%@include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page isELIgnored="false"%>
<html>
    <head>
        <title>Analyse Resultaat</title>
        <link href="styles/main.css" rel="stylesheet" type="text/css">
        <link href="styles/viewer.css" rel="stylesheet" type="text/css">
        <style>
            td, select {
            font-size: 8pt;
            }
            
            select {
            margin-top: 5px;
            margin-bottom: 5px;
            margin-left: 5px;
            }
            
            .zoek_knop {
            margin-top: 5px;
            margin-left: 5px;
            }
            
            .thema_label {
            margin-left: 1px;
            font-weight: bold;
            font-style: italic;
            }
            
            .thema_object {
            margin-left: 5px;
            }
            
            .optie {
            float: left;
            clear: both;
            }
            
            #object_opties, #waarde_opties {
            margin-left: 20px;
            clear: both;
            float: left;
            }
        </style>
        <script type="text/javascript">
        function showDiv(id) {
            document.getElementById(id).style.display = 'block';
            if(id == 'object_opties') document.getElementById('waarde_opties').style.display = 'none';
            if(id == 'waarde_opties') document.getElementById('object_opties').style.display = 'none';
        }
        </script>
    </head>
    <body>
        <h1>Analyse Resultaat</h1>
    </body>
</html>