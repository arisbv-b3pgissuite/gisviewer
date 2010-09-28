var plannen = new Object();
var bestemmingen = new Object();
var selectedPlan=null;

var plantypeAttribuutNaam="typeplan";
var planStatusAttribuutNaam="planstatus";
var tekstAttribuutNaam="documenten";

/*De select velden*/
var eigenaarSelectName="eigenaarselect";
var planSelectName="planselect";
var plantypeSelectName="plantypeselect";
var statusSelectName="statusselect";

var eigenaarSelect=document.getElementById(eigenaarSelectName);
var planSelect=document.getElementById(planSelectName);
var plantypeSelect=document.getElementById(plantypeSelectName);
var statusSelect=document.getElementById(statusSelectName);

/* de geconfigureerde planselectie id's staan als volgt in db
 * 3,1 waarbij eerste id voor eigenaren is en tweede voor plannen */
var planIds = planSelectieIds.split(",");

var planEigenaarId = planIds[0];
var planId = planIds[1];

/*Hier begint het zoeken:*/
JZoeker.zoek(new Array(planEigenaarId),"*",0,handleGetEigenaar);

function handleGetEigenaar(list){
    eigenaarSelect.disabled=false;
    if (list!=null && list.length > 0){
        //eigenaarselect
        dwr.util.removeAllOptions(eigenaarSelectName);
        dwr.util.addOptions(eigenaarSelectName,list,"id","label");
    }
}

/*Als er een eigenaar is gekozen.*/
function eigenaarchanged(element){
    if (element.value!=""){
        dwr.util.removeAllOptions(plantypeSelectName);
        dwr.util.removeAllOptions(statusSelectName)
        dwr.util.removeAllOptions(planSelectName);

        dwr.util.addOptions(plantypeSelectName,[ "Bezig met ophalen..."]);
        dwr.util.addOptions(statusSelectName,[ "Bezig met ophalen..."]);
        dwr.util.addOptions(planSelectName,[ "Bezig met ophalen..."]);

        JZoeker.zoek(new Array(planId),element.value,0,handleGetPlannen);
        //geen nieuwe eigenaar kiezen tijdens de zoek opdracht
        eigenaarSelect.disabled=true;
        setSelectedPlan(null);
    }
}

function handleGetPlannen(list){
    
    //klaar met zoeken dus eigenaar veld weer aan.
    eigenaarSelect.disabled=false;
    dwr.util.removeAllOptions(planSelectName);
    dwr.util.addOptions(planSelectName,list,"id","label");
    plannen = new Object();
    //als niks gevonden dan tekstje tonen
    if (list==undefined || list.length==0){
        dwr.util.addOptions(planSelectName,[ "Geen plannen gevonden"]);
    }else{
        plannen=list;
    }
    //update de typeselect filter en statusselect filter
    updateTypeSelect();
    updateStatusSelect();
}
/*Update select boxen*/
function updateTypeSelect(){
    dwr.util.removeAllOptions(plantypeSelectName);
    var typen= getDistinctFromPlannen(plantypeAttribuutNaam);
    dwr.util.addOptions(plantypeSelectName,[{
        naam:"Selecteer een plantype...",
        waarde:""
    }],'waarde','naam');
    dwr.util.addOptions(plantypeSelectName,typen);
}

function updateStatusSelect(){
    dwr.util.removeAllOptions(statusSelectName);
    //als er al een type is geselecteerd, dan filteren.
    var filteredPlannen=plannen;
    if (plantypeSelect.value.length>0){
        filteredPlannen=filterPlannen(plantypeAttribuutNaam,plantypeSelect.value, filteredPlannen);
    }
    //alleen de statusen van de gefilterde plannen
    var statussen= getDistinctFromPlannen(planStatusAttribuutNaam,filteredPlannen);
    dwr.util.addOptions(statusSelectName,[{
        naam:"Selecteer een planstatus...",
        waarde:""
    }],'waarde','naam');
    dwr.util.addOptions(statusSelectName,statussen);
}
function updatePlanSelect(){
    dwr.util.removeAllOptions(planSelectName);
    var filteredPlannen=plannen;
    if (plantypeSelect.value.length>0){
        filteredPlannen=filterPlannen(plantypeAttribuutNaam,plantypeSelect.value, filteredPlannen);
    }
    if (statusSelect.value.length>0){
        filteredPlannen=filterPlannen(planStatusAttribuutNaam,statusSelect.value, filteredPlannen);
    }
    dwr.util.addOptions(planSelectName,filteredPlannen,"id","label");
    if (filteredPlannen==undefined || filteredPlannen.length==0){
        dwr.util.addOptions(planSelectName,[ "Geen plannen gevonden"]);
    }
}
/***
     *onchange events:
     */
function plantypechanged(element){
    updateStatusSelect();
    updatePlanSelect();
    setSelectedPlan(null);
}
function statuschanged(element){
    updatePlanSelect(element.value);
    setSelectedPlan(null);
}
function planchanged(element){
    if (element.value!=""){
        var plan;
        var zoekConfigId;
        for (var i=0; i < plannen.length; i++){
            if (plannen[i].id == element.value) {                
                plan=plannen[i];
                break;
            }
        }
        if (plan){
            setSelectedPlan(plan);

            var ext = new Object();

            ext.minx=plan.minx;
            ext.miny=plan.miny;
            ext.maxx=plan.maxx;
            ext.maxy=plan.maxy;

            flamingoController.getMap("map1").moveToExtent(ext);
        }
    }
}
/*Haalt een lijst met mogelijke waarden op met de meegegeven attribuutnaam uit de plannen*/
function getDistinctFromPlannen(attribuutnaam,plannenArray){
    if(plannenArray==undefined){
        plannenArray=plannen;
    }
 
    var typen = new Array();
    for (var i=0; i < plannenArray.length; i++){
        var attributen = plannenArray[i].attributen;

        for (var e=0; e <attributen.length; e++){
            if(attributen[e].attribuutnaam==attribuutnaam){
                if (!arrayContains(typen,attributen[e].waarde)){
                    typen.push(attributen[e].waarde);
                }
            }
        }
    }
    return typen;
}
function filterPlannen(attribuutType,value,plannenArray){
    if(plannenArray==undefined){
        plannenArray=plannen;
    }
    var filteredPlannen=new Array();
    for (var i=0; i < plannenArray.length; i++){
        var attributen=plannenArray[i].attributen;
        for (var e=0; e <attributen.length; e++){
            if(attributen[e].attribuutnaam==attribuutType){
                if (value==attributen[e].waarde){
                    filteredPlannen.push(plannenArray[i]);
                }
            }
        }
    }
    return filteredPlannen;
}

function setSelectedPlan(plan){
    selectedPlan=plan;
    if (plan==null){
        document.getElementById("selectedPlan").innerHTML="Geen plan geselecteerd";
    }else{
        //commentaar tool zichtbaar maken:
        document.getElementById("selectedPlan").innerHTML=plan.id;
    }
}

/*
De zoekconfiguratie wordt op 2 manieren gebruikt. Als echte zoekactie,
maar ook om opzoeklijstjes te vormen. In beide gevallen worden zoveel mogelijk
zoekvelden vooringevuld op basis van resultaatvelden van vorige zoekacties.

Zoekvelden die geen waarde hebben worden bij een normale zoekactie opgevraagd
bij de gebruiker middels een geschikte control die bij het zoekveld gedefinieerd
is.

Bij het maken van opzoeklijstjes worden de onbekende zoekvelden gevuld met een
wildkaart(*), waarmee alle mogelijkheden worden opgehaald (het opzoeklijstje).
Het is vooralsnog niet mogelijk te filteren op unieke records via het
daadwerkelijke datastore request, dus de zoeker filtert bij opzoeklijstjes
achteraf de unieke velden (klopt het we dit dus altijd gaan bij wildcard
zoekacties???). In de toekomst wordt aan de zoekconfiguratie een caching
mechanisme toegevoegd, waardoor de traagheid van WFS bij grote datasets
omzeild kan worden.

Na het uitvoeren van een zoekconfiguratie (plus eventuele extra zoekacties
voor de opzoeklijstjes) wordt gecontroleerd of de zoekactie een parent-
zoekactie heeft (dus niet child zoals nu). Hierna begint het weer van voor
af aan.
 */

/*
    document.write('<div id="searchConfigurationsContainer">&nbsp;</div>')
    document.write('<div id="searchInputFieldsContainer">&nbsp;</div>')
*/

//var zoekconfiguraties = [{"id":1,"zoekVelden":[{"id":1,"attribuutnaam":"fid","label":"Plannen","type":0,"naam":""}],"featureType":"app:Plangebied","resultaatVelden":[{"id":1,"attribuutnaam":"naam","label":"plan naam","type":2,"naam":"plannaam"},{"id":2,"attribuutnaam":"identificatie","label":"plan id","type":1,"naam":"planid"},{"id":3,"attribuutnaam":"verwijzingNaarTekst","label":"documenten","type":0,"naam":"documenten"},{"id":4,"attribuutnaam":"typePlan","label":"plantype","type":0,"naam":"plantype"},{"id":5,"attribuutnaam":"planstatus","label":"planstatus","type":0,"naam":"planstatus"},{"id":6,"attribuutnaam":"geometrie","label":"geometry","type":3,"naam":"geometry"}],"bron":{"id":1,"naam":"nlrpp","volgorde":1,"url":"http://afnemers.ruimtelijkeplannen.nl/afnemers/services?Version=1.0.0"},"naam":"iets"}];


function createSearchConfigurations(){
    var container=$j("#searchConfigurationsContainer");
    if (zoekconfiguraties!=null) {

        var selectbox = $j('<select></select>');
        selectbox.attr("id", "searchSelect");
        selectbox.change(function() {
            searchConfigurationsSelectChanged($j(this));
        });

        selectbox.append($j('<option></option>').html("Maak uw keuze ..."));

        for (var i=0; i < zoekconfiguraties.length; i++){
            if(showZoekConfiguratie(zoekconfiguraties[i])){
                selectbox.append($j('<option></option>').html(zoekconfiguraties[i].naam).val(i));
            }
        }

        container.append("<strong>Zoek op</strong><br />");
        container.append(selectbox);
    } else {
        container.html("Geen zoekingangen geconfigureerd.");
    }
}

// Roept dmv ajax een java functie aan die de coordinaten zoekt met de ingevulde zoekwaarden.
function performSearch() {
    $j("#searchResults").html("Een ogenblik geduld, de zoek opdracht wordt uitgevoerd.....");
    var waarde=new Array();
    var zoekVelden=zoekconfiguraties[currentSearchSelectId].zoekVelden;
    for(var i=0; i<zoekVelden.length; i++){
        waarde[i]=$j("#"+zoekVelden[i].attribuutnaam).val();
    }
    showLoading();

    JZoeker.zoek(zoekconfiguraties[currentSearchSelectId].id,waarde,maxResults,searchCallBack);
}

function handleZoekResultaat(searchResultId){
    var searchResult=foundValues[searchResultId];

    //zoom naar het gevonden object.(als er een bbox is)
    if (searchResult.minx)
        moveToExtent(searchResult.minx, searchResult.miny, searchResult.maxx, searchResult.maxy);

    //kijk of de zoekconfiguratie waarmee de zoekopdracht is gedaan een ouder heeft.
    var zoekConfiguratie=searchResult.zoekConfiguratie;
    var parentZc = zoekConfiguratie.parentZoekConfiguratie;
    if (parentZc == null){
        return false;
     }

    // $j("#searchResults").html("<br /><strong>Verder zoeken op " + parentZc.naam + "</strong>");
    if (parentZc.zoekVelden==undefined || parentZc.zoekVelden.length==0){
        alert("Geen zoekvelden geconfigureerd voor zoekconfiguratie parent met id: "+parentZc.id);
        return false;
    }

    for (var i=0; i < zoekconfiguraties.length; i++){
        if(zoekconfiguraties[i].id == parentZc.id) currentSearchSelectId = i;
    }

    // Doe de volgende zoekopdracht
    var zoekStrings = createZoekStringsFromZoekResultaten(parentZc, searchResult);
    //
    // toon de gevonden invoervelden en creeer inputboxen voor de strings met
    // een * want die moeten nog ingevuld worden.
    fillSearchDiv($j("#searchInputFieldsContainer"), parentZc.zoekVelden, zoekStrings);

    return false;
}

// Maak een volgende zoekopdracht voor de ouder.
// vergelijk de gevondenAttributen met de zoekvelden van het kind.
// Als het type gelijk is van beide vul dan de gevonden waarde in voor het zoekveld.
function createZoekStringsFromZoekResultaten(zc, zoekResultaten) {
    var newZoekStrings= new Array();
    if(typeof zc === 'undefined' || !zc) return newZoekStrings;
    for (var i=0; i < zc.zoekVelden.length; i++){
        // * wordt evt later dmv van inputvelden ingevuld.
        newZoekStrings[i] = "*";
        for (var b=0; b < zoekResultaten.attributen.length;  b++){
            var searchedAttribuut=zoekResultaten.attributen[b];
            if (zc.zoekVelden[i].attribuutnaam == searchedAttribuut.attribuutnaam) {
                newZoekStrings[i]=searchedAttribuut.waarde;
                break;
            }
            if (zc.zoekVelden[i].attribuutnaam == searchedAttribuut.label) {
                newZoekStrings[i]=searchedAttribuut.waarde;
                break;
            }
        }
    }
    return newZoekStrings;
}

function createZoekStringsFromZoekVelden(zc, zoekVelden, zoekStrings) {
    var newZoekStrings= new Array();
    if(typeof zc === 'undefined' || !zc) return newZoekStrings;

    for (var i=0; i < zc.zoekVelden.length; i++){
        // * wordt evt later dmv van inputvelden ingevuld.
        newZoekStrings[i] = "*";
        if(zoekStrings) {
            for (var b=0; b < zoekVelden.length;  b++){
                var searchedAttribuut=zoekVelden[b];
                if (zc.zoekVelden[i].attribuutnaam == searchedAttribuut.attribuutnaam && zoekStrings[b]) {
                    newZoekStrings[i]=zoekStrings[b];
                    break;
                }
                if (zc.zoekVelden[i].label == searchedAttribuut.attribuutnaam && zoekStrings[b]) {
                    newZoekStrings[i]=zoekStrings[b];
                    break;
                }
            }
        }
    }
    return newZoekStrings;
}

// De callback functie van het zoeken
// @param values = de gevonden lijst met waarden.
var foundValues=null;
function searchCallBack(values){
    hideLoading();

    foundValues=values;
    var searchResults=$j("#searchResults");

    if (values==null || values.length == 0) {
	searchResults.html("<br /><strong>Er zijn geen resultaten gevonden!</strong>");
        return;
    }

    // Controleer of de bbox groter is dan de minimale bbox van de zoeker
    for (var i=0; i < values.length; i++){
        values[i]=getBboxMinSize2(values[i]);
    }
    if (values.length==1) {
        handleZoekResultaat(0);
	return;
    }

    var ollist = $j("<ol></ol>");
    for (var j = 0; j < values.length; j++){
        (function(tmp){
            var li = $j('<li></li>');
            var link = $j('<a></a>').attr("href", "#").html(values[tmp].label).click(function() {
                handleZoekResultaat(tmp);
            });
            ollist.append(li.append(link));
        })(j);
    }
    searchResults.empty().append(ollist);

}


function getBboxMinSize2(feature){
    if ((Number(feature.maxx-feature.minx) < minBboxZoeken)){
        var addX=Number((minBboxZoeken-(feature.maxx-feature.minx))/2);
        var addY=Number((minBboxZoeken-(feature.maxy-feature.miny))/2);
        feature.minx=Number(feature.minx-addX);
        feature.maxx=Number(Number(feature.maxx)+Number(addX));
        feature.miny=Number(feature.miny-addY);
        feature.maxy=Number(Number(feature.maxy)+Number(addY));
    }
    return feature;
}

var currentSearchSelectId = "";
function searchConfigurationsSelectChanged(element){
    var container=$j("#searchInputFieldsContainer");

    if (currentSearchSelectId == element.val()){
        return;
    } else if(element.val()==""){
        currentSearchSelectId = "";
        container.html("");
        return;
    }
    currentSearchSelectId=element.val();

    var zc = zoekconfiguraties[currentSearchSelectId];
    var zoekVelden=zc.zoekVelden;
    fillSearchDiv(container, zoekVelden, null);
}

function fillSearchDiv(container, zoekVelden, zoekStrings) {
    if (!zoekVelden){
            container.html("Geen zoekvelden");
            return container;
    }
    if (zoekStrings && zoekStrings.length!=zoekVelden.length){
            container.html("lengte van zoekvelden en te zoeken strings komt niet overeen");
            return container;
    }

    container.empty();
    for (var i=0; i < zoekVelden.length; i++){
        var zoekVeld=zoekVelden[i];
        if (zoekVeld.type==3){
            // Bepaalde typen moeten niet getoond worden zoals: Geometry (3)
            continue;
        }

        var zoekString = "*";
        if (zoekStrings) {
            zoekString = zoekStrings[i];
        }

        container.append('<strong>'+zoekVelden[i].label+':</strong><br />');
        var inputfield;
        if (zoekVeld.inputType == 1 && zoekVeld.inputZoekConfiguratie) {

            inputfield = $j('<select></select>').attr({
                id: zoekVeld.attribuutnaam, //'searchField_ ' + zoekVeld.id,
                name: zoekVeld.attribuutnaam,
                size: zoekVeld.inputSize,
                disabled: "disabled"
            });
            inputfield.append($j('<option></option>').html("Bezig met laden..."));
            container.append(inputfield).append('<br /><br />');

            //option lijst ophalen
            var optionZcId = zoekVeld.inputZoekConfiguratie;
            var optionListZc;
            for (k=0; k < zoekconfiguraties.length; k++){
                if(zoekconfiguraties[k].id == optionZcId) optionListZc = zoekconfiguraties[k];
            }
            var optionListStrings = createZoekStringsFromZoekVelden(optionListZc, zoekVelden, zoekStrings);
            var ida = new Array(1);
            ida[0] = optionListZc.id;
            JZoeker.zoek(ida, optionListStrings, 0, handleZoekVeldinputList);

        } else {

            inputfield = $j('<input type="text" />');
            inputfield.attr({
                id: zoekVeld.attribuutnaam, //'searchField_' + zoekVeld.id,
                name: zoekVeld.attribuutnaam,
                maxlength: zoekVeld.inputSize
            }).keyup(function(ev) {
                performSearchOnEnterKey(ev);
            });
            container.append(inputfield).append('<br /><br />');

         }

        if (zoekString != "*") {
            inputfield.val(zoekString);
        }
    }
    
    container.append($j('<input type="button" />').attr("value", " Zoek ").addClass("knop").click(function() {
        performSearch();
    }));

    $j("#searchResults").empty();
    return container;
}

function handleZoekVeldinputList(list){
    if (list!=null && list.length > 0){
        var controlElementName;
        var zc = zoekconfiguraties[currentSearchSelectId];
        var optionListZc = list[0].zoekConfiguratie;
        for (var i=0; i < zc.zoekVelden.length; i++) {
            var zoekVeld=zc.zoekVelden[i];
            if (zoekVeld.inputZoekConfiguratie == optionListZc.id) {
                // controlElementName="searchField_"+zoekVeld.id;
                controlElementName = zoekVeld.attribuutnaam;
            }
        }

        // hier lijst nog filteren, zodat alleen unieke waarden erin staan
        var controlElement=document.getElementById(controlElementName);
        $j(controlElement).removeAttr("disabled");
        dwr.util.removeAllOptions(controlElementName);
        dwr.util.addOptions(controlElementName,list,"id","label");
    }
}

function performSearchOnEnterKey(ev){
    var sourceEvent;
    if(ev)			//Moz
    {
        sourceEvent= ev.target;
    }

    if(window.event)	//IE
    {
        sourceEvent=window.event.srcElement;
    }
    var keycode;
    if(ev)			//Moz
    {
        keycode= ev.keyCode;
    }
    if(window.event)	//IE
    {
        keycode = window.event.keyCode;
    }
    if (keycode==13){
        performSearch();
    }
}

function showZoekConfiguratie(zoekconfiguratie){
    var visibleIds = zoekConfigIds.split(",");
    for (var i=0; i < visibleIds.length; i++){
        if (zoekconfiguratie.id == visibleIds[i]){
            return true;
        }
    }
    return false;
}