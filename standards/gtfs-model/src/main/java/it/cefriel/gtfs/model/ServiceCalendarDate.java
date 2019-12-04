package it.cefriel.gtfs.model;

import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;


@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class ServiceCalendarDate {

  public static final int EXCEPTION_TYPE_ADD = 1;

  public static final int EXCEPTION_TYPE_REMOVE = 2;

  @DataField(pos = 1, columnName="service_id")
  private String serviceId;

  @DataField(pos = 2, columnName="date", pattern = "yyyyMMdd")
  private Date date;

  @DataField(pos = 3, columnName="exception_type")
  private int exceptionType;

  public ServiceCalendarDate() {

  }

  public ServiceCalendarDate(ServiceCalendarDate obj) {
    this.serviceId = obj.serviceId;
    this.date = obj.date;
    this.exceptionType = obj.exceptionType;
  }



  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getExceptionType() {
    return exceptionType;
  }

  public void setExceptionType(int exceptionType) {
    this.exceptionType = exceptionType;
  }

  @Override
  public String toString() {
    return "<CalendarDate serviceId=" + this.serviceId + " date=" + this.date
        + " exception=" + this.exceptionType + ">";
  }
}
