package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class Trip {

    @DataField(pos = 1, columnName="route_id")
    private String route_id;

    @DataField(pos = 2, columnName="service_id")
    private String serviceId;
    
    @DataField(pos = 3, columnName="trip_id")
    private String tripId;

    @DataField(pos = 4, columnName="trip_headsign")
    private String tripHeadsign;
    
    @DataField(pos = 5, columnName="trip_short_name")
    private String tripShortName;
    
    @DataField(pos = 6, columnName="direction_id")
    private String directionId;

    @DataField(pos = 7, columnName="block_id")
    private String blockId;

    @DataField(pos = 8, columnName="shape_id")
    private String shapeId;

    @DataField(pos = 9, columnName="wheelchair_accessible")
    private int wheelchairAccessible = 0;
    
    /**
     * 0 = unknown / unspecified, 1 = bikes allowed, 2 = bikes NOT allowed
     */
    @DataField(pos = 10, columnName="bikes_allowed")
    private int bikesAllowed = 0;

    private Route route;
    
    public Trip() {

    }

    public Trip(Trip obj) {
        this.route_id = obj.route_id;
        this.route = obj.route;
        this.serviceId = obj.serviceId;
        this.tripShortName = obj.tripShortName;
        this.tripHeadsign = obj.tripHeadsign;
        this.directionId = obj.directionId;
        this.blockId = obj.blockId;
        this.shapeId = obj.shapeId;
        this.wheelchairAccessible = obj.wheelchairAccessible;
        this.bikesAllowed = obj.bikesAllowed;
    }

    public String getId() {
        return route_id;
    }

    public void setId(String id) {
        this.route_id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public void setTripShortName(String tripShortName) {
        this.tripShortName = tripShortName;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public String getDirectionId() {
        return directionId;
    }

    public void setDirectionId(String directionId) {
        this.directionId = directionId;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public void setWheelchairAccessible(int wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public int getWheelchairAccessible() {
        return wheelchairAccessible;
    }

    /**
     * @return 0 = unknown / unspecified, 1 = bikes allowed, 2 = bikes NOT allowed
     */
    public int getBikesAllowed() {
        return bikesAllowed;
    }

    /**
     * @param bikesAllowed 0 = unknown / unspecified, 1 = bikes allowed, 2 = bikes
     *          NOT allowed
     */
    public void setBikesAllowed(int bikesAllowed) {
        this.bikesAllowed = bikesAllowed;
    }

    public String toString() {
        return "<Trip " + getId() + ">";
    }

}
