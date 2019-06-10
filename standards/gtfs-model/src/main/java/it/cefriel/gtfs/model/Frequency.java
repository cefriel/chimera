package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class Frequency {

    private int id;
    
    @DataField(pos = 1, columnName="trip_id")
    private String tripId;

    @DataField(pos = 2, columnName="start_time")
    private String startTime;

    @DataField(pos = 3, columnName="end_time")
    private String endTime;

    @DataField(pos = 4, columnName="headway_secs")
    private int headwaySecs;

    @DataField(pos = 5, columnName="exact_times")
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
