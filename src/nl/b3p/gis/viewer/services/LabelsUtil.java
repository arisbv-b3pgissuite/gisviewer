package nl.b3p.gis.viewer.services;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import nl.b3p.gis.viewer.GetViewerDataDigitreeAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ophalen labels bij extra kolommen. In de viewer worden deze
 * labels getoond in de aanvullende info i.p.v. extra1, extra2...
 * @author Boy de Wit
**/
public class LabelsUtil {
    private static final Log log = LogFactory.getLog(GetViewerDataDigitreeAction.class);

    /* Instellen in web.xml */
    private static String JDBC_URL = null;;
    private static String DB_USER = null;
    private static String DB_PASSW = null;
    private static String TABLE = null;

    static public void getExtraLabels(HttpServletRequest request) {
        
        JDBC_URL = ConfigServlet.getJdbcUrlGisdata();
        DB_USER = ConfigServlet.getDatabaseUserName();
        DB_PASSW = ConfigServlet.getDatabasePassword();
        TABLE = ConfigServlet.getDatabaseLabelTable();
        
        /* ophalen ingelogd projectid */
        Principal user = request.getUserPrincipal();

        GisPrincipal gp = (GisPrincipal) user;
        String projectid = gp.getSp().getOrganizationCode();

        StringBuilder query = new StringBuilder();

        query.append("SELECT extra1, extra2, extra3, extra4, extra5, extra6," +
                " extra7, extra8, extra9, extra10 FROM ");

        query.append(TABLE);

        query.append(" WHERE projectid = \'");
        query.append(projectid);
        query.append("\'");

        Map labels = new HashMap();

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSW);

            PreparedStatement statement = conn.prepareStatement(query.toString());

            try {
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    labels.put("extra1", rs.getString("extra1"));
                    labels.put("extra2", rs.getString("extra2"));
                    labels.put("extra3", rs.getString("extra3"));
                    labels.put("extra4", rs.getString("extra4"));
                    labels.put("extra5", rs.getString("extra5"));
                    labels.put("extra6", rs.getString("extra6"));
                    labels.put("extra7", rs.getString("extra7"));
                    labels.put("extra8", rs.getString("extra8"));
                    labels.put("extra9", rs.getString("extra9"));
                    labels.put("extra10", rs.getString("extra10"));

                }
            } finally {
                statement.close();
            }

        } catch (SQLException ex) {
            log.error("", ex);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                log.error("", ex);
            }
        }

        /* Controleren of alle labels wel ingevult zijn en anders er weer extra velden van maken. */
        if (labels.get("extra1") == null || labels.get("extra1").equals("")) {
            labels.put("extra1", "extra1");
        }

        if (labels.get("extra2") == null || labels.get("extra2").equals("")) {
            labels.put("extra2", "extra2");
        }

        if (labels.get("extra3") == null || labels.get("extra3").equals("")) {
            labels.put("extra3", "extra3");
        }

        if (labels.get("extra4") == null || labels.get("extra4").equals("")) {
            labels.put("extra4", "extra4");
        }

        if (labels.get("extra5") == null || labels.get("extra5").equals("")) {
            labels.put("extra5", "extra5");
        }

        if (labels.get("extra6") == null || labels.get("extra6").equals("")) {
            labels.put("extra6", "extra6");
        }

        if (labels.get("extra7") == null || labels.get("extra7").equals("")) {
            labels.put("extra7", "extra7");
        }

        if (labels.get("extra8") == null || labels.get("extra8").equals("")) {
            labels.put("extra8", "extra8");
        }

        if (labels.get("extra9") == null || labels.get("extra9").equals("")) {
            labels.put("extra9", "extra9");
        }

        if (labels.get("extra10") == null || labels.get("extra10").equals("")) {
            labels.put("extra10", "extra10");
        }

        request.setAttribute("labels", labels);
    }
}
