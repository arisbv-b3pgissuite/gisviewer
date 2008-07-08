<%@include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page isELIgnored="false"%>
<script type="text/javascript" src='dwr/interface/JMapData.js'></script>
<script type="text/javascript" src='dwr/engine.js'></script>
<script type="text/javascript" src="<html:rewrite page="/scripts/table.js"/>"></script>
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
    function setAttributeValue(element, themaid, keyName, keyValue, attributeName, attributeValue, eenheid){
        // Leeg -> Ja
        // Nee -> Ja
        // Ja -> Nee        
        var oldValue = element.innerHTML; // Nu wordt er gegeken naar wat de waarde is die in de link staat, deze wordt gebruikt, niet attributeValue
        var newValue = 'Nee';
        if(oldValue == 'Leeg' || oldValue == 'Nee' || oldValue == 'Nieuw')
            newValue = 'Ja';
        JMapData.setAttributeValue(element.id, themaid, keyName, keyValue, attributeName, attributeValue, newValue, handleSetAttribute);
    }
    function handleSetAttribute(str){
        document.getElementById(str[0]).innerHTML=str[1];
    }
    function berekenOppervlakte(element, themaid, kolomnaam,value,eenheid){        
        JMapData.getArea(element.id,themaid,kolomnaam,value,eenheid,handleGetArea);
    }
    function handleGetArea(str){
        document.getElementById(str[0]).innerHTML=str[1];
    }
</script>
<c:choose>
    <c:when test="${not empty thema_items_list and not empty regels_list}">
        <c:set value="0" var="nuOfTables" />
        <c:forEach var="thema_items" items="${thema_items_list}" varStatus="tStatus">
            <div style="width: 100%; clear: both; margin-bottom: 5px; border-bottom: 1px solid Black;">
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
                            </c:forEach>
                        </tr>
                    </thead>
                </table>
                <c:set var="regels" value="${regels_list[tStatus.count-1]}"/>            
                <div class="admin_data_content_div" id="admin_data_content_div${tStatus.count}">
                    <table id="data_table${tStatus.count}" class="table-autosort table-stripeclass:admin_data_alternate_tr" cellpadding="0" cellspacing="0" style="table-layout: fixed;">
                        <tbody>
                            <c:set value="0" var="nuOfRegels" />
                            <c:forEach var="regel" items="${regels}" varStatus="counter">
                                <c:set var="last_id" value="" />
                                <tr class="row" onclick="colorRow(this);">
                                    <td style="width: 50px;" valign="top">
                                        ${counter.count}
                                    </td>
                                    <c:set var="totale_breedte_onder" value="50" />
                                    <c:forEach var="waarde" items="${regel}" varStatus="kolom">
                                        
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
                                                                <%--a class="datalink" id="href${counter.count}${kolom.count-1}" href="#" onclick="${waarde}"><html:image src="./images/icons/information.png"/> </a--%>
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
                <script type="text/javascript">
                    if(document.getElementById('admin_data_content_div${tStatus.count}').scrollHeight > 50) document.getElementById('admin_data_content_div${tStatus.count}').style.width = (document.getElementById('admindata_table${tStatus.count}').offsetWidth + 15) + 'px';
                    else document.getElementById('admin_data_content_div${tStatus.count}').style.width = document.getElementById('admindata_table${tStatus.count}').offsetWidth + 'px';
                </script>
            </div>
            <c:set value="${tStatus.count}" var="nuOfTables" />
        </c:forEach>
        <script language="javascript" type="text/javascript">
            for(i = 1; i < (${nuOfTables} + 1); i++) {        
                Table.stripe(document.getElementById('data_table' + i), 'admin_data_alternate_tr');
                Table.sort(document.getElementById('data_table' + i), {sorttype:Sort['numeric'], col:0});
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
        Er is geen admin data gevonden!
    </c:otherwise>
</c:choose>

