package nl.b3p.gis.digitree.print;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="info")
@XmlType(propOrder = {"titel","datum","imageUrl","bbox","opmerking","kwaliteit","legendUrls","scale","projectid"})
public class PrintInfo {
    private String titel;
    private String datum;
    private String imageUrl;
    private String bbox;
    private String opmerking;
    private int kwaliteit;
    private List<String> legendUrls;
    private Integer scale;
    private String projectid;

    public PrintInfo() {
    }    

    @XmlElement(name="titel")
    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    @XmlElement(name="datum")
    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    @XmlElement(name="imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @XmlElement(name="bbox")
    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    @XmlElement(name="opmerking")
    public String getOpmerking() {
        return opmerking;
    }

    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }

    @XmlElement(name="kwaliteit")
    public int getKwaliteit() {
        return kwaliteit;
    }

    public void setKwaliteit(int kwaliteit) {
        this.kwaliteit = kwaliteit;
    }

    @XmlElement(name="legendUrls")
    public List<String> getLegendUrls() {
        return legendUrls;
    }

    public void setLegendUrls(List<String> legendUrls) {
        this.legendUrls = legendUrls;
    }

    @XmlElement(name="scale")
    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    @XmlElement(name="projectid")
    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }
}