package it.cefriel.gtfs.model.LinkedGTFS;

import java.util.Date;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.SupportsRdfId;
import st4rt.convertor.empire.annotation.SupportsRdfIdImpl;
import st4rt.convertor.empire.annotation.Link;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
@Namespaces({"gtfs", "http://vocab.gtfs.org/terms#",
			"dct", "http://purl.org/dc/terms/",
			"dcat",	"http://www.w3.org/ns/dcat#",
			"xsd", "http://www.w3.org/2001/XMLSchema#",
			"rdfs",	"http://www.w3.org/2000/01/rdf-schema#",
			"foaf",	"http://xmlns.com/foaf/0.1/",
			"schema", "http://schema.org/",
			"geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"})
@RdfsClass("gtfs:CalendarRule")
public final class ServiceCalendar implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    @DataField(pos = 1, columnName="service_id")
    private String serviceId;

    @DataField(pos = 2, columnName="monday")
    @RdfProperty(propertyName = "gtfs:monday")
    private int monday;

    @DataField(pos = 3, columnName="tuesday")
    @RdfProperty(propertyName = "gtfs:tuesday")
    private int tuesday;

    @DataField(pos = 4, columnName="wednesday")
    @RdfProperty(propertyName = "gtfs:wednesday")
    private int wednesday;

    @DataField(pos = 5, columnName="thursday")
    @RdfProperty(propertyName = "gtfs:thursday")
    private int thursday;

    @DataField(pos = 6, columnName="friday")
    @RdfProperty(propertyName = "gtfs:friday")
    private int friday;

    @DataField(pos = 7, columnName="saturday")
    @RdfProperty(propertyName = "gtfs:saturday")
    private int saturday;

    @DataField(pos = 8, columnName="sunday")
    @RdfProperty(propertyName = "gtfs:sunday")
    private int sunday;

    @RdfProperty(links = {
            @Link(propertyName = "dct:temporal", nodeType = Link.NodeType.Shared, sharedID = "IDforTimePeriod")},
            propertyName = "schema:startDate")
    @DataField(pos = 9, columnName="start_date", pattern = "yyyyMMdd")
    private Date startDate;
    @RdfProperty(links = {
            @Link(propertyName = "dct:temporal", nodeType = Link.NodeType.Shared, sharedID = "IDforTimePeriod")},
            propertyName = "schema:endDate")
    @DataField(pos = 10, columnName="end_date", pattern = "yyyyMMdd")
    private Date endDate;

    public ServiceCalendar() {

    }

    public ServiceCalendar(ServiceCalendar sc) {
        this.serviceId = sc.serviceId;
        this.monday = sc.monday;
        this.tuesday = sc.tuesday;
        this.wednesday = sc.wednesday;
        this.thursday = sc.thursday;
        this.friday = sc.friday;
        this.saturday = sc.saturday;
        this.sunday = sc.sunday;
        this.startDate = sc.startDate;
        this.endDate = sc.endDate;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String toString() {
        return "<ServiceCalendar " + this.serviceId + " [" + this.monday
                + this.tuesday + this.wednesday + this.thursday + this.friday
                + this.saturday + this.sunday + "]>";
    }
}
