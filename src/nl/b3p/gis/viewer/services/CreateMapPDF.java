/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.b3p.gis.viewer.services;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.b3p.commons.services.FormUtils;
import nl.b3p.ogc.utils.OGCRequest;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Roy
 */
public class CreateMapPDF extends HttpServlet {
    private static final int RTIMEOUT = 20000;
    private static final String host = AuthScope.ANY_HOST; // "localhost";
    private static final int port = AuthScope.ANY_PORT;
    
    private static final Log log = LogFactory.getLog(CreateMapPDF.class);
    private static final String METADATA_TITLE = "Kaart export B3p Gisviewer";
    private static final String METADATA_AUTHOR = "B3p Gisviewer";
    private static final String OUTPUT_PDF = "PDF";
    private static final String OUTPUT_HTML = "HTML";
    private static final String OUTPUT_RTF = "RTF";

    private static final int MAXSIZE=2048;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String title = FormUtils.nullIfEmpty(request.getParameter("title"));
        if (title == null) {
            title = "Export kaart";
        }
        /**
         * Haal alle form properties op.
         */
        String remark = FormUtils.nullIfEmpty(request.getParameter("remark"));
        String mapUrl = FormUtils.nullIfEmpty(request.getParameter("mapUrl"));
        String pageSize = FormUtils.nullIfEmpty(request.getParameter("pageSize"));
        boolean landscape = new Boolean(request.getParameter("landscape")).booleanValue();
        String outputType = FormUtils.nullIfEmpty(request.getParameter("outputType"));
        int imageSize=0;
        if (FormUtils.nullIfEmpty(request.getParameter("imageSize"))!=null){
            try{
                imageSize= Integer.parseInt(FormUtils.nullIfEmpty(request.getParameter("imageSize")));
            }catch(NumberFormatException nfe){
                imageSize=0;
            }
        }
        //return error als mapUrl null is.
        if (mapUrl == null || mapUrl.length() == 0) {
            throw new ServletException("Geen kaart om te plaatsen in de pdf.");
        }
        OGCRequest ogcr= new OGCRequest(mapUrl);
        Document doc = null;
        try {
            DocWriter dw = null;
            doc = createDocument(pageSize, landscape);
            String filename=title;
            //Maak writer set response headers en maak de filenaam.
            if (outputType.equalsIgnoreCase(OUTPUT_PDF)) {
                dw = PdfWriter.getInstance(doc, response.getOutputStream());
                response.setContentType("application/pdf");    
                filename+=".pdf";
            } else if (outputType.equalsIgnoreCase(OUTPUT_RTF)) {
                dw = RtfWriter2.getInstance(doc, response.getOutputStream());
                response.setContentType("application/rtf");
                filename+=".rtf";
            } else {
                dw = HtmlWriter.getInstance(doc, response.getOutputStream());
                response.setContentType("text/html");
                filename+=".html";
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\";");
            
            doc.open();
            setDocumentMetadata(doc);
            //set title
            Paragraph titleParagraph = new Paragraph(title);
            titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(titleParagraph); 
            /*
             Vergroot het plaatje naar de imagesize die mee gegeven is.
             als die waarde leeg is doe dan de MAXSIZE waarde.
             */
            int width=0;
            int height=0;
            width=Integer.parseInt(ogcr.getParameter(ogcr.WMS_PARAM_WIDTH));
            height=Integer.parseInt(ogcr.getParameter(ogcr.WMS_PARAM_HEIGHT));
            if (imageSize==0)
                imageSize=MAXSIZE;
            float factor=0;
            if (width >= height){
                factor= new Float(imageSize).floatValue()/width;
            }else{
                factor= new Float(imageSize).floatValue()/height;
            }
            width= new Double(Math.floor(width*factor)).intValue();
            height= new Double(Math.floor(height*factor)).intValue();
            ogcr.addOrReplaceParameter(ogcr.WMS_PARAM_WIDTH, ""+width);
            ogcr.addOrReplaceParameter(ogcr.WMS_PARAM_HEIGHT, ""+height);
            try {
                String url=ogcr.getUrl();
                Image map=getImage(url, request);
                map.setAlignment(Image.ALIGN_CENTER);
                /*zorg er voor dat het plaatje binnen de marging van het document komt.
                 */
                float imageWidth=doc.getPageSize().getWidth()-doc.leftMargin()-doc.rightMargin();
                float imageHeight=doc.getPageSize().getHeight()-doc.topMargin()-doc.bottomMargin();
                //als pagina gedraaid is en er is een opmerking gegeven maak het plaatje minder hoog
                //zodat er ruimte is voor de opmerking
                if (landscape && remark!=null){
                    imageWidth=imageWidth-100;
                } 
                map.scaleToFit(imageWidth,imageHeight);
                doc.add(map);
            } catch (Exception ex) {
                log.error("Kan kaart image niet toevoegen.", ex);
                doc.add(new Phrase("Kan kaart image niet toevoegen."));
            }
            if (remark != null) {
                doc.add(new Phrase(remark));
            }
        } catch (DocumentException de) {
            log.error("Fout bij het maken van een document. Reden: ", de);
            throw new ServletException(de);
        } finally {
            doc.close();
        }
    }

    public Document createDocument(String pageSize, boolean landscape) {
        Rectangle ps = PageSize.A4;
        if (pageSize != null) {
            ps = PageSize.getRectangle(pageSize);
        }
        Document doc = null;
        if (landscape) {
            doc = new Document(ps.rotate());
        } else {
            doc = new Document(ps);
        }
        return doc;
    }
    
    private Image getImage(String mapUrl,HttpServletRequest request) throws IOException, Exception{
        Image image=null;
        if (request.getUserPrincipal() instanceof GisPrincipal) {
            GisPrincipal gp = (GisPrincipal) request.getUserPrincipal();
            HttpClient client = new HttpClient();
            client.getParams().setAuthenticationPreemptive(true);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(RTIMEOUT);

            Credentials defaultcreds = new UsernamePasswordCredentials(gp.getName(),gp.getPassword());
            AuthScope authScope = new AuthScope(host, port);
            client.getState().setCredentials(authScope, defaultcreds);

            GetMethod method = new GetMethod(mapUrl);
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                log.error("Host: " + mapUrl + " error: " + method.getStatusLine().getReasonPhrase());
                throw new Exception("Host: " + mapUrl + " error: " + method.getStatusLine().getReasonPhrase());
            }
            image= Image.getInstance(method.getResponseBody());
        }else{
            image = Image.getInstance(new URL(mapUrl));
        }
        return image;
    }
    /**
     *Voeg de metadata toe aan het document. 
     */
    private void setDocumentMetadata(Document doc) {
        doc.addTitle(METADATA_TITLE);
        doc.addAuthor(METADATA_AUTHOR);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
