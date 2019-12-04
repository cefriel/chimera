package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class Stop {

  private static final int MISSING_VALUE = -999;

  @DataField(pos = 1, columnName="stop_id")
  private String id;

  @DataField(pos = 2, columnName="stop_code")
  private String code;
  
  @DataField(pos = 3, columnName="stop_name")
  private String name;

  @DataField(pos = 4, columnName="stop_desc")
  private String desc;
  
  @DataField(pos = 5, columnName="stop_lat")
  private double lat;

  @DataField(pos = 6, columnName="stop_lon")
  private double lon;

  @DataField(pos = 7, columnName="zone_id")
  private String zoneId;

  @DataField(pos = 8, columnName="stop_url")
  private String url;
  
  @DataField(pos = 9, columnName="location_type")
  private int locationType = 0;
  
  @DataField(pos = 10, columnName="parent_station")
  private String parentStation;

  @DataField(pos = 11, columnName="stop_timezone")
  private String timezone;
  
  @DataField(pos = 12, columnName="wheelchair_boarding")
  private int wheelchairBoarding = 0;

  
  private String direction;

  private int vehicleType = MISSING_VALUE;

  private String platformCode;

  public Stop() {

  }

  public Stop(Stop obj) {
    this.id = obj.id;
    this.code = obj.code;
    this.name = obj.name;
    this.desc = obj.desc;
    this.lat = obj.lat;
    this.lon = obj.lon;
    this.zoneId = obj.zoneId;
    this.url = obj.url;
    this.locationType = obj.locationType;
    this.parentStation = obj.parentStation;
    this.wheelchairBoarding = obj.wheelchairBoarding;
    this.direction = obj.direction;
    this.timezone = obj.timezone;
    this.vehicleType = obj.vehicleType;
    this.platformCode = obj.platformCode;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public String getZoneId() {
    return zoneId;
  }

  public void setZoneId(String zoneId) {
    this.zoneId = zoneId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getLocationType() {
    return locationType;
  }

  public void setLocationType(int locationType) {
    this.locationType = locationType;
  }

  public String getParentStation() {
    return parentStation;
  }

  public void setParentStation(String parentStation) {
    this.parentStation = parentStation;
  }

  @Override
  public String toString() {
    return "<Stop " + this.id + ">";
  }

  public void setWheelchairBoarding(int wheelchairBoarding) {
    this.wheelchairBoarding = wheelchairBoarding;
  }

  public int getWheelchairBoarding() {
    return wheelchairBoarding;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
  
  public boolean isVehicleTypeSet() {
    return vehicleType != MISSING_VALUE;
  }

  public int getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(int vehicleType) {
    this.vehicleType = vehicleType;
  }
  
  public void clearVehicleType() {
    vehicleType = MISSING_VALUE;
  }

  public String getPlatformCode() {
    return platformCode;
  }

  public void setPlatformCode(String platformCode) {
    this.platformCode = platformCode;
  }
}
