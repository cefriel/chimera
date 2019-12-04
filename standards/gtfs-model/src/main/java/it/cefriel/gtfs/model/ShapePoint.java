package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class ShapePoint implements Comparable<ShapePoint> {

    public static final double MISSING_VALUE = -999;

    @DataField(pos = 1, columnName="shape_id")
    private String shapeId;

    @DataField(pos = 2, columnName="shape_pt_lat")
    private double lat;

    @DataField(pos = 3, columnName="shape_pt_lon")
    private double lon;

    @DataField(pos = 4, columnName="shape_pt_sequence")
    private int sequence;

    @DataField(pos = 5, columnName="shape_dist_traveled")
    private double distTraveled = MISSING_VALUE;


    public ShapePoint() {

    }

    public ShapePoint(ShapePoint shapePoint) {
        this.shapeId = shapePoint.shapeId;
        this.sequence = shapePoint.sequence;
        this.distTraveled = shapePoint.distTraveled;
        this.lat = shapePoint.lat;
        this.lon = shapePoint.lon;
    }


    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public double getDistTraveled() {
        return distTraveled;
    }

    public void setDistTraveled(double distTraveled) {
        this.distTraveled = distTraveled;
    }

    public int compareTo(ShapePoint o) {
        return this.getSequence() - o.getSequence();
    }
}
