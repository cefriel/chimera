package it.cefriel.gtfs.model;

import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class ServiceCalendar {

    @DataField(pos = 1, columnName="service_id")
    private String serviceId;

    @DataField(pos = 2, columnName="monday")
    private int monday;

    @DataField(pos = 3, columnName="tuesday")
    private int tuesday;

    @DataField(pos = 4, columnName="wednesday")
    private int wednesday;

    @DataField(pos = 5, columnName="thursday")
    private int thursday;

    @DataField(pos = 6, columnName="friday")
    private int friday;

    @DataField(pos = 7, columnName="saturday")
    private int saturday;

    @DataField(pos = 8, columnName="sunday")
    private int sunday;

    @DataField(pos = 9, columnName="start_date", pattern = "yyyyMMdd")
    private Date startDate;

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
