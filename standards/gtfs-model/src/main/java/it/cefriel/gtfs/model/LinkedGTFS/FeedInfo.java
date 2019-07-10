package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.SupportsRdfIdImpl;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
@Namespaces({"gtfs", "http://vocab.gtfs.org/terms#"
					"dct", "http://purl.org/dc/terms/"
					"dcat",	"http://www.w3.org/ns/dcat#"
					"xsd", "http://www.w3.org/2001/XMLSchema#"
					"rdfs",	"http://www.w3.org/2000/01/rdf-schema#"
					"foaf",	"http://xmlns.com/foaf/0.1/"
					"schema", "http://schema.org/"
					"geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"})
@RdfsClass("gtfs:Feed")
public final class FeedInfo implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    private int id;

    @DataField(pos = 1, columnName="feed_publisher_name")
    @RdfProperty(propertyName = "gtfs:publisher")
    private String publisherName;

    @DataField(pos = 2, columnName="feed_publisher_url")
    private String publisherUrl;

    @DataField(pos = 3, columnName="feed_lang")
    @RdfProperty(propertyName = "gtfs:language")
    private String lang;

    @DataField(pos = 4, columnName="feed_start_date")
    @RdfProperty(propertyName = "dct:issued")
    private String startDate;

    @DataField(pos = 5, columnName="feed_end_date")
    @RdfProperty(propertyName = "dct:modified")
    private String endDate;

    @DataField(pos = 6, columnName="feed_version")
    @RdfProperty(propertyName = "schema:version")
    private String version;

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /****
     * {@link IdentityBean} Interface
     ****/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
