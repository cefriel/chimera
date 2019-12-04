package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class StopTime {

    public static final int MISSING_VALUE = -999;

    private Trip trip;

    private Stop stop;


    private int id;

    @DataField(pos = 1, columnName="trip_id")
    private String tripId;

    @DataField(pos = 2, columnName="arrival_time")
    private String arrivalTime;

    @DataField(pos = 3, columnName="departure_time")
    private String departureTime;

    @DataField(pos = 4, columnName="stop_id")
    private String stop_id;

    @DataField(pos = 5, columnName="stop_sequence")
    private String stopSequence;

    @DataField(pos = 6, columnName="stop_headsign")
    private String stopHeadsign;

    @DataField(pos = 7, columnName="pickup_type")
    private int pickupType;

    @DataField(pos = 8, columnName="drop_off_type")
    private int dropOffType;

    @DataField(pos = 9, columnName="shape_dist_traveled")
    private double shapeDistTraveled = MISSING_VALUE;

    @DataField(pos = 10, columnName="timepoint")
    private int timepoint = MISSING_VALUE;  

    /** Support track extension */
    private String track;

    public StopTime() {

    }

    public StopTime(StopTime st) {
        this.arrivalTime = st.arrivalTime;
        this.departureTime = st.departureTime;
        this.dropOffType = st.dropOffType;
        this.id = st.id;
        this.pickupType = st.pickupType;
        this.shapeDistTraveled = st.shapeDistTraveled;
        this.stop = st.stop;
        this.stopHeadsign = st.stopHeadsign;
        this.stopSequence = st.stopSequence;
        this.timepoint = st.timepoint;
        this.trip = st.trip;
        this.track = st.track;
    }
    
    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(String stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    public int getPickupType() {
        return pickupType;
    }

    public void setPickupType(int pickupType) {
        this.pickupType = pickupType;
    }

    public int getDropOffType() {
        return dropOffType;
    }

    public void setDropOffType(int dropOffType) {
        this.dropOffType = dropOffType;
    }

    public double getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    public void setShapeDistTraveled(double shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }

    public int getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(int timepoint) {
        this.timepoint = timepoint;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }


}
