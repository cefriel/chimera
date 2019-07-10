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
@RdfsClass("gtfs:Route")
public final class Route implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }
	
	private static final int MISSING_VALUE = -999;

	@DataField(pos = 1, columnName="route_id")
	private String id;

	@DataField(pos = 2, columnName="agency_id")
	@RdfProperty(propertyName = "gtfs:agency")
	private String agency;

	@DataField(pos = 3, columnName="route_short_name")
	@RdfProperty(propertyName = "gtfs:shortName")
	private String shortName;

	@DataField(pos = 4, columnName="route_long_name")
	@RdfProperty(propertyName = "gtfs:longName")
	private String longName;

	@DataField(pos = 5, columnName="route_desc")
	@RdfProperty(propertyName = "dct:description")
	private String desc;

	@DataField(pos = 6, columnName="route_type")
	@RdfProperty(propertyName = "gtfs:routeType")
	private int type;

	@DataField(pos = 7, columnName="route_url")
	@RdfProperty(propertyName = "gtfs:routeUrl")
	private String url;

	@DataField(pos = 8, columnName="route_color")
	@RdfProperty(propertyName = "gtfs:color")
	private String color;

	@DataField(pos = 9, columnName="route_text_color")
	@RdfProperty(propertyName = "gtfs:textColor")
	private String textColor;

	@DataField(pos = 10, columnName="route_sort_order")
	private int sortOrder = MISSING_VALUE;

	@Deprecated
	private int routeBikesAllowed = 0;

	/**
	 * 0 = unknown / unspecified, 1 = bikes allowed, 2 = bikes NOT allowed
	 */
	private int bikesAllowed = 0;

	private String brandingUrl;

	public Route() {

	}

	public Route(Route r) {
		this.id = r.id;
		this.agency = r.agency;
		this.shortName = r.shortName;
		this.longName = r.longName;
		this.desc = r.desc;
		this.type = r.type;
		this.url = r.url;
		this.color = r.color;
		this.textColor = r.textColor;
		this.bikesAllowed = r.bikesAllowed;
		this.sortOrder = r.sortOrder;
		this.brandingUrl = r.brandingUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	@Deprecated
	public int getRouteBikesAllowed() {
		return routeBikesAllowed;
	}

	@Deprecated
	public void setRouteBikesAllowed(int routeBikesAllowed) {
		this.routeBikesAllowed = routeBikesAllowed;
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

	public boolean isSortOrderSet() {
		return sortOrder != MISSING_VALUE;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getBrandingUrl() {
		return brandingUrl;
	}

	public void setBrandingUrl(String brandingUrl) {
		this.brandingUrl = brandingUrl;
	}

	@Override
	public String toString() {
		return "<Route " + id + " " + shortName + ">";
	}
}
