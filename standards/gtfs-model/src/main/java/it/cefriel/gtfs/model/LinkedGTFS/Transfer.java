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
@RdfsClass("gtfs:TransferRule")
public final class Transfer implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    private static final int MISSING_VALUE = -999;

    @DataField(pos = 1, columnName="from_stop_id")
    @RdfProperty(propertyName = "gtfs:originStop")
    private String fromStopId;

    @DataField(pos = 2, columnName="to_stop_id")
    @RdfProperty(propertyName = "gtfs:destinationStop")
    private String toStopId;

    @DataField(pos = 3, columnName="transfer_type")
    @RdfProperty(propertyName = "gtfs:transferType")
    private int transferType;

    @DataField(pos = 4, columnName="min_transfer_time")
    @RdfProperty(propertyName = "gtfs:minimumTransferTime")
    private int minTransferTime = MISSING_VALUE;

    private Route fromRoute;

    private Trip fromTrip;

    private Stop toStop;

    private Stop fromStop;

    private Route toRoute;

    private Trip toTrip;

    public String toString() {
        return "<Transfer " + getFromStopId() + " to "+ getToStopId()+ ">";
    }

    public String getFromStopId() {
        return fromStopId;
    }

    public void setFromStopId(String fromStopId) {
        this.fromStopId = fromStopId;
    }

    public String getToStopId() {
        return toStopId;
    }

    public void setToStopId(String toStopId) {
        this.toStopId = toStopId;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getMinTransferTime() {
        return minTransferTime;
    }

    public void setMinTransferTime(int minTransferTime) {
        this.minTransferTime = minTransferTime;
    }

    public Route getFromRoute() {
        return fromRoute;
    }

    public void setFromRoute(Route fromRoute) {
        this.fromRoute = fromRoute;
    }

    public Trip getFromTrip() {
        return fromTrip;
    }

    public void setFromTrip(Trip fromTrip) {
        this.fromTrip = fromTrip;
    }

    public Stop getToStop() {
        return toStop;
    }

    public void setToStop(Stop toStop) {
        this.toStop = toStop;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public void setFromStop(Stop fromStop) {
        this.fromStop = fromStop;
    }

    public Route getToRoute() {
        return toRoute;
    }

    public void setToRoute(Route toRoute) {
        this.toRoute = toRoute;
    }

    public Trip getToTrip() {
        return toTrip;
    }

    public void setToTrip(Trip toTrip) {
        this.toTrip = toTrip;
    }
    
    
}
