package it.cefriel.gtfs.model.LinkedGTFS;

import javax.xml.bind.annotation.XmlTransient;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.SupportsRdfId;
import st4rt.convertor.empire.annotation.SupportsRdfIdImpl;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
@Namespaces({"gtfs", "http://vocab.gtfs.org/terms#",
			"dct", "http://purl.org/dc/terms/",
			"dcat",	"http://www.w3.org/ns/dcat#",
			"xsd", "http://www.w3.org/2001/XMLSchema#",
			"rdfs",	"http://www.w3.org/2000/01/rdf-schema#",
			"foaf",	"http://xmlns.com/foaf/0.1/",
			"schema", "http://schema.org/",
			"geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"})
@RdfsClass("gtfs:Frequency")
public final class Frequency implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    private int id;
    
    @DataField(pos = 1, columnName="trip_id")
    @RdfProperty(propertyName = "gtfs:trip")
    private String tripId;

    @DataField(pos = 2, columnName="start_time")
    @RdfProperty(propertyName = "gtfs:startTime")
    private String startTime;

    @DataField(pos = 3, columnName="end_time")
    @RdfProperty(propertyName = "gtfs:endTime")
    private String endTime;

    @DataField(pos = 4, columnName="headway_secs")
    @RdfProperty(propertyName = "gtfs:headwaySeconds")
    private int headwaySecs;

    @DataField(pos = 5, columnName="exact_times")
    @RdfProperty(propertyName = "gtfs:exactTimes")
    private int exactTimes = 0;

    private Trip trip;
    
    public Frequency() {

    }

    public Frequency(Frequency f) {
        this.id = f.id;
        this.trip = f.trip;
        this.startTime = f.startTime;
        this.endTime = f.endTime;
        this.headwaySecs = f.headwaySecs;
        this.exactTimes = f.exactTimes;
    }

    
    
    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getHeadwaySecs() {
        return headwaySecs;
    }

    public void setHeadwaySecs(int headwaySecs) {
        this.headwaySecs = headwaySecs;
    }

    public int getExactTimes() {
        return exactTimes;
    }

    public void setExactTimes(int exactTimes) {
        this.exactTimes = exactTimes;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    
}
