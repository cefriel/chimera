package it.cefriel.gtfs.model.LinkedGTFS;

import javax.xml.bind.annotation.XmlTransient;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.SupportsRdfId;
import st4rt.convertor.empire.annotation.SupportsRdfIdImpl;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
@Namespaces({"gtfs", "http://vocab.gtfs.org/terms#",
			"dct", "http://purl.org/dc/terms/",
			"dcat",	"http://www.w3.org/ns/dcat#",
			"xsd", "http://www.w3.org/2001/XMLSchema#",
			"rdfs",	"http://www.w3.org/2000/01/rdf-schema#",
			"foaf",	"http://xmlns.com/foaf/0.1/",
			"schema", "http://schema.org/",
			"geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"})
@RdfsClass("gtfs:StopTime")
public final class StopTime implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

	public static final int MISSING_VALUE = -999;

	private Trip trip;

	private Stop stop;

	private int id;

	@DataField(pos = 1, columnName="trip_id")
	@RdfProperty(propertyName = "gtfs:trip")
	private String tripId;

	@DataField(pos = 2, columnName="arrival_time")
	@RdfProperty(propertyName = "gtfs:arrivalTime")
	private String arrivalTime;

	@DataField(pos = 3, columnName="departure_time")
	@RdfProperty(propertyName = "gtfs:departureTime")
	private String departureTime;

	@DataField(pos = 4, columnName="stop_id")
	@RdfProperty(propertyName = "gtfs:stop")
	private String stop_id;

	@DataField(pos = 5, columnName="stop_sequence")
	@RdfProperty(propertyName = "gtfs:stopSequence")
	private String stopSequence;

	@DataField(pos = 6, columnName="stop_headsign")
	@RdfProperty(propertyName = "gtfs:headsign")
	private String stopHeadsign;

	@DataField(pos = 7, columnName="pickup_type")
	@RdfProperty(propertyName = "gtfs:pickupTipe")
	private int pickupType;

	@DataField(pos = 8, columnName="drop_off_type")
	@RdfProperty(propertyName = "gtfs:dropOffType")
	private int dropOffType;

	@DataField(pos = 9, columnName="shape_dist_traveled")
	@RdfProperty(propertyName = "gtfs:distanceTraveled")
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
