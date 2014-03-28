/*
 * B3P Gisviewer is an extension to Flamingo MapComponents making
 * it a complete webbased GIS viewer and configuration tool that
 * works in cooperation with B3P Kaartenbalie.
 *
 * Copyright 2006, 2007, 2008 B3Partners BV
 * 
 * This file is part of B3P Gisviewer.
 * 
 * B3P Gisviewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * B3P Gisviewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with B3P Gisviewer.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.b3p.gis.digitree.print;

import java.io.OutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.b3p.combineimages.CombineImagesServlet;
import nl.b3p.commons.services.FormUtils;
import nl.b3p.commons.struts.ExtendedMethodProperties;
import nl.b3p.gis.digitree.print.PrintInfo;
import nl.b3p.gis.digitree.print.PrintServlet;
import nl.b3p.gis.viewer.services.GisPrincipal;
import nl.b3p.gis.viewer.struts.BaseHibernateAction;
import nl.b3p.imagetool.CombineImageSettings;
import nl.b3p.imagetool.CombineImageUrl;
import nl.b3p.imagetool.CombineImagesHandler;
import nl.b3p.ogc.utils.OGCRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

public class PrintAction extends BaseHibernateAction {

    private static final Log logFile = LogFactory.getLog(PrintAction.class);
    protected static final String PRINT = "print";
    protected static final String IMAGE = "image";
    private static final String METADATA_TITLE = "Kaartexport B3P Gisviewer";
    private static final String METADATA_AUTHOR = "B3P Gisviewer";
    private static final String OUTPUT_PDF_PRINT = "PDF_PRINT";
    private static final String OUTPUT_PDF = "PDF";
    private static final String OUTPUT_RTF = "RTF";
    private static final int MAXSIZE = 2048;
    private static String logoPath = null;
    private static String extraImagePath = null;
    private static String disclaimer = null;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy", new Locale("NL"));
    private static float footerHeight = 25;
    private static boolean addFooter = true;
    private static int maxResponseTime = 30000;
    
    private static Integer DEFAULT_PPI = 72;

    @Override
    protected Map getActionMethodPropertiesMap() {
        Map map = new HashMap();

        ExtendedMethodProperties hibProp = null;

        hibProp = new ExtendedMethodProperties(PRINT);
        hibProp.setDefaultForwardName(SUCCESS);
        hibProp.setAlternateForwardName(FAILURE);
        map.put(PRINT, hibProp);

        hibProp = new ExtendedMethodProperties(IMAGE);
        hibProp.setDefaultForwardName(SUCCESS);
        hibProp.setAlternateForwardName(FAILURE);
        map.put(IMAGE, hibProp);

        return map;

    }

    public ActionForward unspecified(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CombineImageSettings settings = getCombineImageSettings(request);

        String imageId = CombineImagesServlet.uniqueName("");
        request.getSession().setAttribute(imageId, settings);
        dynaForm.set("imageId", imageId);
        
        /* Legenda items klaarzetten voor jsp. Deze legenda urls zijn door de viewer.js 
         via een formulier gesubmit. In de getCombineImageSettings worden deze gesplit
         en in een Map<laag naam, legenda url> settings gestopt. De gebruiker kan in
         het printvoorbeeld nog kiezen welke legenda plaatjes in de print moeten komen. */
        request.getSession().setAttribute("legendItems", settings.getLegendMap());

        Integer currentScale = calcCurrentScale(settings);
        dynaForm.set("scale", currentScale);

        return mapping.findForward(SUCCESS);
    }

    public ActionForward image(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = null;
        String password = null;

        GisPrincipal gp = GisPrincipal.getGisPrincipal(request);
        if (gp != null) {
            username = gp.getName();
            password = gp.getPassword();
        }

        CombineImageSettings settings = null;
        String imageId = request.getParameter("imageId");

        if (imageId != null && request.getSession().getAttribute(imageId) != null) {
            settings = (CombineImageSettings) request.getSession().getAttribute(imageId);

            response.setContentType(settings.getMimeType());
            response.setHeader("Content-Disposition", "attachment; filename=\"printvoorbeeld.png\";");
            response.setDateHeader("Expires", System.currentTimeMillis() + (1000 * 60 * 60 * 24));

            String keepAlive = request.getParameter("keepAlive");
            if (keepAlive == null || keepAlive.length() == 0) {
                request.getSession().removeAttribute(imageId);
            }
        }

        if (settings == null) {
            logFile.error("No settings for image found");
            this.addAlternateMessage(mapping, request, null, "No settings for image found");
            return this.getAlternateForward(mapping, request);
        }

        OutputStream os = null;
        try {
            //response.setContentType("text/html;charset=UTF-8");
            os = response.getOutputStream();
            CombineImagesHandler.combineImage(response.getOutputStream(), settings,
                    settings.getMimeType(), maxResponseTime, username, password);
        } finally {
            os.close();
        }

        return null;
    }

    private Integer calcCurrentScale(CombineImageSettings settings) {
        Double newMapWidth = settings.getBbox().getMaxx() - settings.getBbox().getMinx();
        Double scale = newMapWidth / (settings.getWidth() * 0.00028);

        return scale.intValue();
    }

    private String calculateBboxForScale(CombineImageSettings settings, Integer scale) {
        Integer mapWidth = settings.getWidth();
        Integer mapHeight = settings.getHeight();

        /* Calculate new width in map units assuming a default 
         * pixel on screen of 0.28mm */
        Double newMapWidth = Math.ceil(scale * (mapWidth * 0.00028));
        Double newMapHeight = Math.ceil(scale * (mapHeight * 0.00028));

        double minx = settings.getBbox().getMinx();
        double miny = settings.getBbox().getMiny();
        double maxx = settings.getBbox().getMaxx();
        double maxy = settings.getBbox().getMaxy();

        /* Calculate center of current bounding box */
        double centerX = (maxx - minx) / 2 + minx;
        double centerY = (maxy - miny) / 2 + miny;

        /* Calculate new bounding box for scale */
        String newMinX = Double.toString(centerX - (newMapWidth / 2));
        String newMaxX = Double.toString(centerX + (newMapWidth / 2));
        String newMinY = Double.toString(centerY - (newMapHeight / 2));
        String newMaxY = Double.toString(centerY + (newMapHeight / 2));

        return newMinX + "," + newMinY + "," + newMaxX + "," + newMaxY;
    }

    private float calcPixelSizeForResolution(Integer ppi) {
        float ratio = (float) 1 / ppi;
        float pixelSize = (float) ratio * 25.4f;

        return (float) pixelSize / 1000;
    }

    /* Paper sizes in mm
     * A0 841 � 1189, A1 594 x 841, A2 420 � 594, A3 297 � 420, A4 210 � 297
     */
    private Double convertPaperFormatToInches(String format, boolean landscape) {
        Double oneMmInchUnit = 0.0394; // 1mm = 0.0394 inch

        if (format.equals("A0") && !landscape) {
            return 841 * oneMmInchUnit;
        } else if (format.equals("A0") && landscape) {
            return 1189 * oneMmInchUnit;
        } else if (format.equals("A1") && !landscape) {
            return 594 * oneMmInchUnit;
        } else if (format.equals("A1") && landscape) {
            return 841 * oneMmInchUnit;
        } else if (format.equals("A2") && !landscape) {
            return 420 * oneMmInchUnit;
        } else if (format.equals("A2") && landscape) {
            return 594 * oneMmInchUnit;
        } else if (format.equals("A3") && !landscape) {
            return 297 * oneMmInchUnit;
        } else if (format.equals("A3") && landscape) {
            return 420 * oneMmInchUnit;
        } else if (format.equals("A4") && !landscape) {
            return 210 * oneMmInchUnit;
        } else if (format.equals("A4") && landscape) {
            return 297 * oneMmInchUnit;
        }

        return null;
    }

    private Integer calcNewMapWidthFromPPI(Integer ppi, Double paperWidthInInches) {
        Double w = Math.ceil(ppi * paperWidthInInches);

        return w.intValue();
    }

    /**
     * Calculate new scale given a ppi, map width and paper size
     *
     * @see
     * http://www.britishideas.com/2009/09/22/map-scales-and-printing-with-mapnik/
    *
     */
    private Integer calcScaleForHigherPPI(Integer ppi, Integer mapWidthInMeters,
            Double paperWidthInInches) {

        float pixelSize = calcPixelSizeForResolution(ppi);
        Integer newMapWidth = calcNewMapWidthFromPPI(ppi, paperWidthInInches);

        Double schaal = Math.ceil(mapWidthInMeters / (newMapWidth * pixelSize));

        return schaal.intValue();
    }

    public ActionForward print(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        /* ophalen form waardes */
        String title = FormUtils.nullIfEmpty(dynaForm.getString("title"));
        String imageId = FormUtils.nullIfEmpty(dynaForm.getString("imageId"));
        String pageSize = FormUtils.nullIfEmpty(dynaForm.getString("pageSize"));
        boolean landscape = Boolean.valueOf(dynaForm.getString("landscape")).booleanValue();
        String outputType = FormUtils.nullIfEmpty(dynaForm.getString("outputType"));
        String remark = FormUtils.nullIfEmpty(dynaForm.getString("remark"));
        
        String strPPI = FormUtils.nullIfEmpty(dynaForm.getString("ppi"));
        
        Principal user = request.getUserPrincipal();
        GisPrincipal gp = (GisPrincipal) user;
        String projectid = gp.getSp().getOrganizationCode();
        
        Integer PPI = DEFAULT_PPI;
        if (strPPI != null) {
             PPI = new Integer(strPPI);
        }        
        
        /* huidige CombineImageSettings ophalen */
        CombineImageSettings settings = (CombineImageSettings) request.getSession().getAttribute(imageId);

        /* bbox klaarzetten voor xsl */
        String minx = Double.toString(settings.getBbox().getMinx());
        String miny = Double.toString(settings.getBbox().getMiny());
        String maxx = Double.toString(settings.getBbox().getMaxx());
        String maxy = Double.toString(settings.getBbox().getMaxy());
        String bbox = minx + "," + miny + "," + maxx + "," + maxy;

        /* nu */
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("d MMMMM yyyy", new Locale("NL"));

        String imageUrl = createImageUrl(request);

        /* template keuze */
        String template = null;

        if (landscape && pageSize.equals("A4")) {
            template = PrintServlet.xsl_A4_Liggend;
        } else if (!landscape && pageSize.equals("A4")) {
            template = PrintServlet.xsl_A4_Staand;
        } else if (landscape && pageSize.equals("A3")) {
            template = PrintServlet.xsl_A3_Liggend;
        } else if (!landscape && pageSize.equals("A3")) {
            template = PrintServlet.xsl_A3_Staand;
        } else if (landscape && pageSize.equals("A2")) {
            template = PrintServlet.xsl_A2_Liggend;
        } else if (!landscape && pageSize.equals("A2")) {
            template = PrintServlet.xsl_A2_Staand;
        } else if (landscape && pageSize.equals("A1")) {
            template = PrintServlet.xsl_A1_Liggend;
        } else if (!landscape && pageSize.equals("A1")) {
            template = PrintServlet.xsl_A1_Staand;
        } else if (landscape && pageSize.equals("A0")) {
            template = PrintServlet.xsl_A0_Liggend;
        } else if (!landscape && pageSize.equals("A0")) {
            template = PrintServlet.xsl_A0_Staand;
        } else {
            template = PrintServlet.xsl_A4_Liggend;
        }

        /* nieuw (xml) Object voor gebruik met fop */
        PrintInfo info = new PrintInfo();
        info.setTitel(title);
        info.setDatum(df.format(now));
        info.setImageUrl(imageUrl);
        info.setBbox(bbox);
        info.setOpmerking(remark);
        info.setProjectid(projectid);

        /* Indien schaal ingevuld in printvoorbeeld de bbox opnieuw berekenen. */
        Integer currentScale = calcCurrentScale(settings);
        String oldBBox = calculateBboxForScale(settings, currentScale);

        Integer newScale = (Integer) dynaForm.get("scale");
        String newBbox = null;
        if (newScale != null && newScale > 0) {
            newBbox = calculateBboxForScale(settings, newScale);
            info.setBbox(newBbox);
            settings.setBbox(newBbox);
            info.setScale(newScale);
        }

        /* Test voor grotere print resoluties en papier formaten a0, a1 en a2 */
        Double paperInches = convertPaperFormatToInches(pageSize, landscape);

        Integer newWidthPx = calcNewMapWidthFromPPI(PPI, paperInches);
        
        info.setKwaliteit(newWidthPx);
         
        /*
        double maxX = settings.getBbox().getMaxx();
        double minX = settings.getBbox().getMinx();

        Double bla = Math.ceil(maxX - minX);
        Integer currentWidth = bla.intValue();

        Integer schaalEnzo = calcScaleForHigherPPI(PPI, currentWidth, paperInches);
        logFile.debug("NEW SCALE: " + schaalEnzo);

        if (schaalEnzo != null & schaalEnzo > 0) {
            newBbox = calculateBboxForScale(settings, schaalEnzo);
            logFile.debug("NEW BBOX: " + newBbox);

            settings.setBbox(newBbox);
            info.setBbox(newBbox);
            info.setScale(schaalEnzo);
            info.setKwaliteit(newWidthPx);
        }
        */

        /* Legenda urls klaarzetten. Hier worden de aangevinkte laagnamen
         vergeleken met de keys die al klaargezet waren in de 
         Map<laag naam, legenda url> settings. De bijbehornede legenda url wordt
         in een List gestopt en doorgegeven aan de xsl. */
        String[] arr = (String[]) dynaForm.get("legendItems");
        List<String> legendUrlsList = new ArrayList<String>();
        if (arr != null && arr.length > 0) {
            Map legendMap = settings.getLegendMap();

            for (int i = 0; i < arr.length; i++) {
                String keyStr = arr[i];

                if (legendMap != null && legendMap.containsKey(keyStr)) {
                    String url = (String) legendMap.get(keyStr);
                    legendUrlsList.add(url);
                }
            }
        }

        info.setLegendUrls(legendUrlsList);

        /* doorgeven mimetype en template */
        String mimeType = null;

        if (outputType != null && outputType.equals(OUTPUT_PDF) || outputType.equals(OUTPUT_PDF_PRINT)) {
            mimeType = MimeConstants.MIME_PDF;
        } else if (outputType != null && outputType.equals(OUTPUT_RTF)) {
            mimeType = MimeConstants.MIME_RTF;
        } else {
            mimeType = MimeConstants.MIME_PDF;
        }

        /* add javascript print dialog to pdf ? */
        boolean addJavascript = false;
        if (outputType != null && outputType.equals(OUTPUT_PDF_PRINT)) {
            addJavascript = true;
        }

        /* Maak de output */
        PrintServlet.setSettings(settings);
        PrintServlet.createOutput(info, mimeType, template, addJavascript, response);

        return null;
    }

    private String createImageUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();

        int lastIndex = requestUrl.lastIndexOf("/");

        String basePart = requestUrl.substring(0, lastIndex);
        String servletPart = "/services/PrintServlet?";
        String imageUrl = basePart + servletPart;

        return imageUrl;
    }

    private CombineImageSettings getCombineImageSettings(HttpServletRequest request) throws Exception {
        String url = FormUtils.nullIfEmpty(request.getParameter("urls"));
        String wkt = FormUtils.nullIfEmpty(request.getParameter("wkts"));
        String tilings = FormUtils.nullIfEmpty(request.getParameter("tilings"));
        String mapsizes = FormUtils.nullIfEmpty(request.getParameter("mapsizes"));
        String legendUrls = FormUtils.nullIfEmpty(request.getParameter("legendUrls"));

        CombineImageSettings settings = new CombineImageSettings();

        String[] urls = null;
        if (url != null) {
            logFile.debug("Urls: " + url);
            urls = url.split(";");
            settings.setUrls(urls);
        }
        String[] wkts = null;
        if (wkt != null) {
            logFile.debug("WKT: " + wkt);
            wkts = wkt.split(";");
            settings.setWktGeoms(wkts);
        }

        Map legendMap = new HashMap();
        if (legendUrls != null) {
            logFile.debug("legendUrls: " + legendUrls);
            String[] arr = legendUrls.split(";");

            for (int i = 0; i < arr.length; i++) {
                String[] legendUrlsArr = arr[i].split("#");
                legendMap.put(legendUrlsArr[0], legendUrlsArr[1]);
            }

            settings.setLegendMap(legendMap);
        }

        /* Tiling settings van POST form:
         * bbox, resolutions, tileWidth, tileHeight, serviceUrl */
        String[] tilingSettings = null;
        if (tilings != null) {
            tilingSettings = tilings.split(";");

            if (tilingSettings != null && tilingSettings.length == 5) {
                settings.setTilingBbox(tilingSettings[0]);
                settings.setTilingResolutions(tilingSettings[1]);
                settings.setTilingTileWidth(new Integer(tilingSettings[2]));
                settings.setTilingTileHeight(new Integer(tilingSettings[3]));
                settings.setTilingServiceUrl(tilingSettings[4]);
            }
        }

        if (tilingSettings != null && tilingSettings.length < 5) {
            throw new Exception("Er zijn niet voldoende parameters voor printen tiling.");
        }

        if ((urls == null && tilingSettings == null) || (urls != null && urls.length == 0)) {
            throw new Exception("Er zijn geen verzoeken naar plaatjes gevonden.");
        }

        String reqWidth = request.getParameter(OGCRequest.WMS_PARAM_WIDTH);
        String reqHeight = request.getParameter(OGCRequest.WMS_PARAM_HEIGHT);
        String reqBbox = request.getParameter(OGCRequest.WMS_PARAM_BBOX);

        if (reqWidth != null && reqHeight != null && reqBbox != null) {
            // gebruik info uit request
            settings.setWidth(new Integer(reqWidth));
            settings.setHeight(new Integer(reqHeight));
            settings.setBbox(reqBbox);
        } else if (urls != null) {
            // bereken width, height en bbox uit eerste url,
            // want dit zal voor alle urls hetzelfde moeten zijn
            String url1 = urls[0];
            OGCRequest ogcr = new OGCRequest(url1);
            int width = Integer.parseInt(ogcr.getParameter(OGCRequest.WMS_PARAM_WIDTH));
            int height = Integer.parseInt(ogcr.getParameter(OGCRequest.WMS_PARAM_HEIGHT));
            String bbox = ogcr.getParameter(OGCRequest.WMS_PARAM_BBOX);
            settings.setWidth(width);
            settings.setHeight(height);
            settings.setBbox(bbox);
        }

        /* Indien geen wms url beschikbaar om width en height uit te halen pad dan
         * waardes uit post formulier direct van controller opgehaald */
        String[] mapSizeSettings = null;
        if (urls == null && mapsizes != null) {
            mapSizeSettings = mapsizes.split(";");

            if (mapSizeSettings != null && mapSizeSettings.length == 3) {
                settings.setWidth(new Integer(mapSizeSettings[0]));
                settings.setHeight(new Integer(mapSizeSettings[1]));
                settings.setBbox(mapSizeSettings[2]);

                /* TODO: Kijken of dit netter kan. Nu wordt er in de createmappdf.js
                 * een imageSize uit url[0] &width gehaald. Alleen werkt dit niet als
                 * er geen gewone wms url aanwezig is. Dus als er alleen een tiling laag
                 * aan staat. */
                urls = new String[1];
                urls[0] = tilingSettings[4] + "&width=" + mapSizeSettings[0];
                settings.setUrls(urls);
            }
        }

        String mimeType = FormUtils.nullIfEmpty(request.getParameter(OGCRequest.WMS_PARAM_FORMAT));
        if (mimeType != null && !mimeType.equals("")) {
            settings.setMimeType(mimeType);
        }

        return settings;
    }
}