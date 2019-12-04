
package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public class Agency {

    @DataField(pos = 1, columnName="agency_id")
    private String id;

    @DataField(pos = 2, columnName="agency_name")
    private String name;

    @DataField(pos = 3, columnName="agency_url")
    private String url;
    
    @DataField(pos = 4, columnName="agency_timezone")
    private String timezone;

    @DataField(pos = 5, columnName="agency_lang")
    private String lang;

    @DataField(pos = 6, columnName="agency_phone")
    private String phone;

    @DataField(pos = 7, columnName="agency_fare_url")
    private String fareUrl;

    @DataField(pos = 8, columnName="agency_email")
    private String email;

    public Agency() {

    }

    public Agency(Agency a) {
        this.id = a.id;
        this.name = a.name;
        this.url = a.url;
        this.timezone = a.timezone;
        this.lang = a.lang;
        this.phone = a.phone;
        this.email = a.email;
        this.fareUrl = a.fareUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFareUrl() {
        return fareUrl;
    }

    public void setFareUrl(String fareUrl) {
        this.fareUrl = fareUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return "<Agency " + this.id + ">";
    }
}
