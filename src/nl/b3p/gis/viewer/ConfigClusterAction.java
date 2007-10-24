/*
 * ConfigThemaAction.java
 *
 * Created on 13 oktober 2007, 19:08
 *
 */

package nl.b3p.gis.viewer;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.b3p.commons.services.FormUtils;
import nl.b3p.gis.viewer.db.Clusters;
import nl.b3p.gis.viewer.services.HibernateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.hibernate.Session;

/**
 *
 * @author Chris
 */
public class ConfigClusterAction extends ViewerCrudAction {
    
    private static final Log log = LogFactory.getLog(ConfigThemaAction.class);
    
    protected Clusters getCluster(DynaValidatorForm form, boolean createNew) {
        Integer id = FormUtils.StringToInteger(form.getString("clusterID"));
        Clusters c = null;
        if(id == null && createNew)
            c = new Clusters();
        else if(id != null) {
            Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
            c = (Clusters)sess.get(Clusters.class, id);
        }
        return c;
    }
    
    protected Clusters getFirstCluster() {
        Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
        List cs = sess.createQuery("from Clusters order by naam").setMaxResults(1).list();
        if (cs!=null && cs.size()>0) {
            return (Clusters) cs.get(0);
        }
        return null;
    }
    
    protected void createLists(DynaValidatorForm form, HttpServletRequest request) throws Exception {
        super.createLists(form, request);
        Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
        request.setAttribute("allClusters", sess.createQuery("from Clusters where id<>9 order by naam").list());
    }
    
    public ActionForward unspecified(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Clusters c = getCluster(dynaForm, false);
        if (c==null)
            c = getFirstCluster();
        populateClustersForm(c, dynaForm, request);
        return super.unspecified(mapping, dynaForm, request, response);
    }
    
    public ActionForward edit(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Clusters c = getCluster(dynaForm, false);
        if (c==null)
            c = getFirstCluster();
        populateClustersForm(c, dynaForm, request);
        return super.edit(mapping, dynaForm, request, response);
    }
    
    public ActionForward save(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        if (!isTokenValid(request)) {
            prepareMethod(dynaForm, request, EDIT, LIST);
            addAlternateMessage(mapping, request, TOKEN_ERROR_KEY);
            return getAlternateForward(mapping, request);
        }
        
        // nieuwe default actie op delete zetten
        Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
        
        ActionErrors errors = dynaForm.validate(mapping, request);
        if(!errors.isEmpty()) {
            addMessages(request, errors);
            prepareMethod(dynaForm, request, EDIT, LIST);
            addAlternateMessage(mapping, request, VALIDATION_ERROR_KEY);
            return getAlternateForward(mapping, request);
        }
        
        Clusters c = getCluster(dynaForm, true);
        if (c==null) {
            prepareMethod(dynaForm, request, LIST, EDIT);
            addAlternateMessage(mapping, request, NOTFOUND_ERROR_KEY);
            return getAlternateForward(mapping, request);
        }
        
        populateClustersObject(dynaForm, c, request);
        
        sess.saveOrUpdate(c);
        sess.flush();
        
        /* Indien we input bijvoorbeeld herformatteren oid laad het dynaForm met
         * de waardes uit de database.
         */
        sess.refresh(c);
        populateClustersForm(c, dynaForm, request);
        
        return super.save(mapping, dynaForm, request, response);
    }
    
    public ActionForward delete(ActionMapping mapping, DynaValidatorForm dynaForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        if (!isTokenValid(request)) {
            prepareMethod(dynaForm, request, EDIT, LIST);
            addAlternateMessage(mapping, request, TOKEN_ERROR_KEY);
            return getAlternateForward(mapping, request);
        }
        
        // nieuwe default actie op delete zetten
        Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
        
        Clusters c = getCluster(dynaForm, false);
        if (c==null) {
            prepareMethod(dynaForm, request, LIST, EDIT);
            addAlternateMessage(mapping, request, NOTFOUND_ERROR_KEY);
            return getAlternateForward(mapping, request);
        }
        
        sess.delete(c);
        sess.flush();
        
        return super.delete(mapping, dynaForm, request, response);
    }
    
    
    private void populateClustersForm(Clusters c, DynaValidatorForm dynaForm, HttpServletRequest request) {
        if (c==null)
            return;
        
        dynaForm.set("clusterID", Integer.toString(c.getId()));
        dynaForm.set("naam", c.getNaam());
        dynaForm.set("omschrijving", c.getOmschrijving());
        String val = "";
        if (c.getParent()!=null)
            val = Integer.toString(c.getParent().getId());
        dynaForm.set("parentID", val);
    }
    
    private void populateClustersObject(DynaValidatorForm dynaForm, Clusters c, HttpServletRequest request) {
        
        c.setNaam(FormUtils.nullIfEmpty(dynaForm.getString("naam")));
        c.setOmschrijving(FormUtils.nullIfEmpty(dynaForm.getString("omschrijving")));
        
        Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
        int mId=0;
        try {
            mId = Integer.parseInt(dynaForm.getString("parentID"));
        } catch (NumberFormatException ex) {
            log.error("Illegal parent id", ex);
        }
        Clusters m = (Clusters)sess.get(Clusters.class, new Integer(mId));
        c.setParent(m);
    }
 }