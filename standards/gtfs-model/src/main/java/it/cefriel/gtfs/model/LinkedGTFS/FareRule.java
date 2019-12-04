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
@RdfsClass("gtfs:FareRule")
public final class FareRule implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }
    
    private int id;

    @DataField(pos = 1, columnName="fare_id")
    @RdfProperty(propertyName = "gtfs:fareClass")
    private String fareId;
    
    @DataField(pos = 2, columnName="route_id")
    @RdfProperty(propertyName = "gtfs:route")
    private String routeId;    

    @DataField(pos = 3, columnName="origin_id")
    @RdfProperty(propertyName = "gtfs:originZone")
    private String originId;

    @DataField(pos = 4, columnName="destination_id")
    @RdfProperty(propertyName = "gtfs:destinationZone")
    private String destinationId;

    @DataField(pos = 5, columnName="contains_id")
    @RdfProperty(propertyName = "gtfs:zone")
    private String containsId;

    private Route route;
    private FareAttribute fare;
    
    public FareRule() {

    }

    public FareRule(FareRule fr) {
        this.id = fr.id;
        this.fare = fr.fare;
        this.route = fr.route;
        this.originId = fr.originId;
        this.destinationId = fr.destinationId;
        this.containsId = fr.containsId;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getContainsId() {
        return containsId;
    }

    public void setContainsId(String containsId) {
        this.containsId = containsId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public FareAttribute getFare() {
        return fare;
    }

    public void setFare(FareAttribute fare) {
        this.fare = fare;
    }

    
    public String getFareId() {
        return fareId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
    }

    public String toString() {
        return "<FareRule " + getId() + ">";
    }
}
