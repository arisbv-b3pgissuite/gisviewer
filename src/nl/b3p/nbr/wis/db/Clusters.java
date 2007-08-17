/**
 * @(#)Clusters.java
 * @author Chris van Lith
 * @version 1.00 2007/01/16
 *
 * Purpose: een bean klasse die de verschillende properties van een Clusters opslaat en weer kan tonen.
 *
 * @copyright 2007 All rights reserved. B3Partners
 */

package nl.b3p.nbr.wis.db;

import java.util.Set;

public class Clusters {
    
    private int id;
    private String naam;
    private String omschrijving;
    private Clusters parent;
    private Set children;
    private Set themas;
    
    /** Creates a new instance of Clusters */
    public Clusters() {
    }
    
    /** 
     * Return het ID van de Clusters.
     *
     * @return int ID van de clusters
     */
    // <editor-fold defaultstate="" desc="public int getId()">
    public int getId() {
        return id;
    }
    // </editor-fold>
    
    /** 
     * Set het ID van de Clusters.
     *
     * @param id int id
     */
    // <editor-fold defaultstate="" desc="public void setId(int id)">
    public void setId(int id) {
        this.id = id;
    }
    // </editor-fold>
    
    /** 
     * Return de naam van het cluster.
     *
     * @return String met de naam.
     */
    // <editor-fold defaultstate="" desc="public String getNaam()">
    public String getNaam() {
        return naam;
    }
    // </editor-fold>
    
    /** 
     * Set de naam van het cluster.
     *
     * @param naam String met de naam van het cluster.
     */
    // <editor-fold defaultstate="" desc="public void setNaam(String naam)">
    public void setNaam(String naam) {
        this.naam = naam;
    }
    // </editor-fold>
    
    /** 
     * Return de omschrijving van het cluster.
     *
     * @return String met de omschrijving van het cluster.
     */
    // <editor-fold defaultstate="" desc="public String getOmschrijving()">
    public String getOmschrijving() {
        return omschrijving;
    }
    // </editor-fold>
    
    /** 
     * Set de omschrijving van het cluster.
     *
     * @param omschrijving String met de omschrijving van het cluster.
     */
    // <editor-fold defaultstate="" desc="public void setOmschrijving(String omschrijving)">
    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }
    // </editor-fold>
    
    /** 
     * Return de children van dit cluster.
     *
     * @return Set met alle children van dit cluster.
     *
     * @see Set
     */
    // <editor-fold defaultstate="" desc="public String getChildren()">
    public Set getChildren() {
        return children;
    }
    // </editor-fold>
    
    /** 
     * Set de children van dit cluster.
     *
     * @param children Set children met de children van dit cluster.
     *
     * @see Set
     */
    // <editor-fold defaultstate="" desc="public void setChildren(Set children)">
    public void setChildren(Set children) {
        this.children = children;
    }
    // </editor-fold>
    
    /** 
     * Return de themas van dit cluster.
     *
     * @return Set met alle themas van dit cluster.
     *
     * @see Set
     */
    // <editor-fold defaultstate="" desc="public String getThemas()">
    public Set getThemas() {
        return themas;
    }
    // </editor-fold>
    
    /** 
     * Set de themas van dit cluster.
     *
     * @param themas Set children met de themas van dit cluster.
     *
     * @see Set
     */
    // <editor-fold defaultstate="" desc="public void setThemas(Set themas)">
    public void setThemas(Set themas) {
        this.themas = themas;
    }
    // </editor-fold>
    
    /** 
     * Return de parent van dit cluster.
     *
     * @return Clusters met de parent van dit Cluster.
     */
    // <editor-fold defaultstate="" desc="public String getParent()">
    public Clusters getParent() {
        return parent;
    }
    // </editor-fold>
    
    /** 
     * Set de parent van dit cluster.
     *
     * @param parent Clusters met de parent van dit cluster.
     */
    // <editor-fold defaultstate="" desc="public void setParent(Clusters parent)">
    public void setParent(Clusters parent) {
        this.parent = parent;
    }
    // </editor-fold>
}
