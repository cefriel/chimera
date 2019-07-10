package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.SupportsRdfIdImpl;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
@Namespaces({"gtfs", "http://vocab.gtfs.org/terms#"
			"dct", "http://purl.org/dc/terms/"
			"dcat",	"http://www.w3.org/ns/dcat#"
			"xsd", "http://www.w3.org/2001/XMLSchema#"
			"rdfs",	"http://www.w3.org/2000/01/rdf-schema#"
			"foaf",	"http://xmlns.com/foaf/0.1/"
			"schema", "http://schema.org/"
			"geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"})
@RdfsClass("gtfs:ShapePoint")
public final class ShapePoint implements Comparable<ShapePoint> implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    public static final double MISSING_VALUE = -999;

    @DataField(pos = 1, columnName="shape_id")
    @RdfProperty(propertyName = "gtfs:shape")
    private String shapeId;

    @DataField(pos = 2, columnName="shape_pt_lat")
    @RdfProperty(propertyName = "geo:lat")
    private double lat;

    @DataField(pos = 3, columnName="shape_pt_lon")
    @RdfProperty(propertyName = "geo:long")
    private double lon;

    @DataField(pos = 4, columnName="shape_pt_sequence")
    @RdfProperty(propertyName = "gtfs:pointSequence")
    private int sequence;

    @DataField(pos = 5, columnName="shape_dist_traveled")
    @RdfProperty(propertyName = "gtfs:distanceTraveled")
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
