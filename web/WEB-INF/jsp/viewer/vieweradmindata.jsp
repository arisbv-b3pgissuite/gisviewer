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
<script type="text/javascript" src='dwr/interface/JMapData.js'></script>
<script type="text/javascript" src='dwr/engine.js'></script>
<script type="text/javascript" src="<html:rewrite page="/scripts/table.js"/>"></script>
<script type="text/javascript" src="<html:rewrite page="/scripts/admindataFunctions.js"/>"></script>
<script type="text/javascript">
    function popUp(URL, naam) {
        var screenwidth = 600;
        var screenheight = 500;
        var popupleft =(screen.width) ? (screen.width - screenwidth) / 2:100;
        var popuptop = (screen.height) ? (screen.height - screenheight) / 2:100;
        properties = "toolbar = 0, " + 
            "scrollbars = 1, " + 
            "location = 0, " + 
            "statusbar = 1, " + 
            "menubar = 0, " + 
            "resizable = 1, " + 
            "width = " + screenwidth + ", " + 
            "height = " + screenheight + ", " + 
            "top = " + popuptop + ", " + 
            "left = " + popupleft;
        eval("page" + naam + " = window.open(URL, '" + naam + "', properties);");
    }
    function toggleList(nr) {
        var obj = document.getElementById('fullTable' + nr);
        var plusmin = document.getElementById('plusMin' + nr);
        if(obj.style.display == 'block') {
            plusmin.innerHTML = '+';
            plusmin.style.marginLeft = '2px';
            plusmin.style.marginTop = '-2px';
            obj.style.display = 'none';
        } else {
            plusmin.innerHTML = '-';
            obj.style.display = 'block';
            plusmin.style.marginLeft = '4px';
            plusmin.style.marginTop = '-4px';
        }
    }
</script>
<c:choose>
    <c:when test="${not empty thema_items_list and not empty regels_list}">
        <c:set value="0" var="nuOfTables" />
        <c:forEach var="thema_items" items="${thema_items_list}" varStatus="tStatus">
            
            <c:set var="themanaam" value="" />
            <c:forEach var="ThemaItem" items="${thema_items}" varStatus="topRowStatus">
                <c:if test="${ThemaItem.thema.naam != themanaam}">
                    <c:set var="themanaam" value="${ThemaItem.thema.naam}" />
                    <c:set var="themaId" value="${ThemaItem.thema.id}"/>
                </c:if>
            </c:forEach>
            
            <div class="topRow" style="margin-bottom: 0px; cursor: pointer; width: 100%; height: 25px; clear: both; font-weight: bold; background-repeat: repeat;" onclick="toggleList(${tStatus.count})">
                <div style="margin-left: 5px; margin-top: 3px;">
                    <div style="background-color: white; padding: 0px; margin-top: 1px; height: 10px; width: 10px; border: 1px solid black; margin-right: 5px;">
                        <c:set var="plusmin" value="+" />
                        <c:set var="margins" value="2" />
                        <c:if test="${tStatus.count == 1}">
                            <c:set var="plusmin" value="-" />
                            <c:set var="margins" value="4" />
                        </c:if>
                        <div id="plusMin${tStatus.count}" style="margin-left: ${margins}px; margin-top: -${margins}px;">
                            ${plusmin}
                        </div>
                    </div>
                    ${themanaam}                                         
                    <a style="color: #000000; margin-left: 10px;" href="#" onclick="data2csv${tStatus.count}.submit()">Exporteer naar csv</a>                    
                    <form action="services/Data2CSV" name="data2csv${tStatus.count}" id="data2csv${tStatus.count}" target="_blank" method="post">
                        <input name="themaId" type="hidden" value="${themaId}"/>
                        <input name="objectIds" type="hidden" value=""/>
                    </form>            
                    
                </div>
            </div>

            <c:set var="display" value="none" />
            <c:if test="${tStatus.count == 1}">
                <c:set var="display" value="block" />
            </c:if>

            <div class="topRow" style="width: 100%; clear: both; margin-bottom: 5px; border-bottom: 1px solid #EAEEF2; background-repeat: repeat-x; display: ${display};" id="fullTable${tStatus.count}">
                <table id="admindata_table${tStatus.count}" cellpadding="0" cellspacing="0" style="table-layout: fixed;">
                    <thead>
                        <tr class="topRow" style="height: 20px;">
                            <th style="width: 50px;" class="table-sortable:numeric" id="volgnr_th" onclick="Table.sort(document.getElementById('data_table${tStatus.count}'), {sorttype:Sort['numeric'], col:0});">
                                Volgnr
                            </th>
                            <c:set var="totale_breedte" value="50" />
                            <c:set var="last_id" value="" />
                            <c:forEach var="ThemaItem" items="${thema_items}" varStatus="topRowStatus">
                                <c:choose>
                                    <c:when test="${ThemaItem.kolombreedte != 0}">
                                        <c:set var="breedte" value="${ThemaItem.kolombreedte}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="breedte" value="150" />
                                    </c:otherwise>            
                                </c:choose>
                                <c:set var="totale_breedte" value="${totale_breedte + breedte}" />
                                <c:set var="kol_id" value=" id=\"header_kolom_item${topRowStatus.count}\"" />
                                <c:set var="noOfKolommen" value="${topRowStatus.count}" />
                                <th style="width: ${breedte}px;"${kol_id} class="table-sortable:default" onclick="Table.sort(document.getElementById('data_table${tStatus.count}'), {sorttype:Sort['default'], col:${topRowStatus.count}});">
                                    ${ThemaItem.label}
                                </th>
                                <c:if test="${ThemaItem.thema.naam != themanaam}">
                                    <c:set var="themanaam" value="${ThemaItem.thema.naam}" />
                                </c:if>
                            </c:forEach>
                        </tr>
                    </thead>
                </table>
                <c:set var="regels" value="${regels_list[tStatus.count-1]}"/>
                <div id="admin_data_content_div${tStatus.count}">
                    <table id="data_table${tStatus.count}" class="table-autosort table-stripeclass:admin_data_alternate_tr" cellpadding="0" cellspacing="0" style="table-layout: fixed;">
                        <tbody>
                            <c:set value="0" var="nuOfRegels" />
                            <c:forEach var="regel" items="${regels}" varStatus="counter">
                                <c:set var="last_id" value="" />
                                <tr class="row" onclick="colorRow(this);">
                                    <td style="width: 50px;" valign="top">
                                        ${counter.count}
                                    </td>
                                    <c:set var="totale_breedte_onder" value="65" />
                                    <script>
                                        if (document.data2csv${tStatus.count}.objectIds.value.length > 0){
                                            document.data2csv${tStatus.count}.objectIds.value+=",";
                                        }
                                        document.data2csv${tStatus.count}.objectIds.value+="${regel.primairyKey}";
                                    </script>
                                    <c:forEach var="waarde" items="${regel.values}" varStatus="kolom">
                                        <c:if test="${thema_items[kolom.count - 1] != null}">
                                            <c:choose>
                                                <c:when test="${thema_items[kolom.count - 1].kolombreedte != 0}">
                                                    <c:set var="breedte" value="${thema_items[kolom.count - 1].kolombreedte}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="breedte" value="150" />
                                                </c:otherwise>            
                                            </c:choose>
                                            <c:set var="totale_breedte_onder" value="${totale_breedte_onder + breedte}" />
                                            <c:if test="${kolom.last}">
                                                <c:set var="last_id" value=" id=\"footer_last_item${counter.count}\"" />
                                            </c:if>
                                            <c:set var="noOfRegels" value="${counter.count}" />
                                            <td style="width: ${breedte}px;"${last_id} valign="top">
                                                <c:choose>
                                                    <c:when test="${waarde eq '' or  waarde eq null}">
                                                        &nbsp;
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${thema_items[kolom.count - 1].dataType.id == 2}">
                                                                <html:image src="./images/icons/information.png" onclick="popUp('${waarde}', 'aanvullende_info_scherm');" style="cursor: pointer; cursor: hand;" />
                                                            </c:when>
                                                            <c:when test="${thema_items[kolom.count - 1].dataType.id == 3}">
                                                                <html:image src="./images/icons/world_link.png" onclick="popUp('${waarde}', 'externe_link');" style="cursor: pointer; cursor: hand;" />
                                                            </c:when>
                                                            <c:when test="${thema_items[kolom.count - 1].dataType.id == 4}">
                                                                <a class="datalink" id="href${counter.count}${kolom.count-1}" href="#" onclick="${fn:split(waarde, '###')[1]}">${fn:split(waarde, '###')[0]}</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                            ${waarde}
                                                            </c:otherwise>
                                                        </c:choose>
                                                        
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:if>
                                    </c:forEach>
                                </tr>
                                <c:set value="${counter.count}" var="nuOfRegels" />
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <c:set value="${tStatus.count}" var="nuOfTables" />
        </c:forEach>
        <script language="javascript" type="text/javascript">
            for(i = 1; i < (${nuOfTables} + 1); i++) {        
            Table.stripe(document.getElementById('data_table' + i), 'admin_data_alternate_tr');
            Table.sort(document.getElementById('data_table' + i), {sorttype:Sort['numeric'], col:0});
        }    
             
        if(opener && opener.usePopup) {
            for(i = 2; i < (${nuOfTables} + 1); i++) { 
                toggleList(i);
            }
        }

        var currentObj;
        var currentObjOldStyle;
        function colorRow(obj) {
            if(currentObj) {
                currentObj.className = currentObjOldStyle;
            }
            currentObj = obj;
            currentObjOldStyle = obj.className;
            obj.className = obj.className + ' admin_data_selected_tr';
        }
        </script>
    </c:when>
    <c:otherwise>
        <div id="content_style">
    <table class="kolomtabel">
        <tr>
            <td valign="top">
                <div class="inleiding">
                    <h2>Er is geen informatie gevonden.</h2>
                    <p>
                        Mogelijk is er op de door u gezochte plaats geen informatie beschikbaar. 
                        Of uw selectie criteria leveren niks op.
                    </p>
                </div>
           </td>           
        </tr>
    </table>
</div>
    </c:otherwise>
</c:choose>

