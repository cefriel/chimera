package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class FeedInfo {

    private int id;

    @DataField(pos = 1, columnName="feed_publisher_name")
    private String publisherName;

    @DataField(pos = 2, columnName="feed_publisher_url")
    private String publisherUrl;

    @DataField(pos = 3, columnName="feed_lang")
    private String lang;

    @DataField(pos = 4, columnName="feed_start_date")
    private String startDate;

    @DataField(pos = 5, columnName="feed_end_date")
    private String endDate;

    @DataField(pos = 6, columnName="feed_version")
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
