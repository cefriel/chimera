package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class FareRule {

    
    private int id;

    @DataField(pos = 1, columnName="fare_id")
    private String fareId;
    
    @DataField(pos = 2, columnName="route_id")
    private String routeId;    

    @DataField(pos = 3, columnName="origin_id")
    private String originId;

    @DataField(pos = 4, columnName="destination_id")
    private String destinationId;

    @DataField(pos = 5, columnName="contains_id")
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
