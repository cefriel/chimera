package it.cefriel.gtfs.model.LinkedGTFS;

import java.util.Date;
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
@RdfsClass("gtfs:CalendarDateRule")
public final class ServiceCalendarDate implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    public static final int EXCEPTION_TYPE_ADD = 1;

    public static final int EXCEPTION_TYPE_REMOVE = 2;

	@DataField(pos = 1, columnName="service_id")
	private String serviceId;
	
	@DataField(pos = 2, columnName="date", pattern = "yyyyMMdd")
	@RdfProperty(propertyName = "dct:date")
	private Date date;
	
	@DataField(pos = 3, columnName="exception_type")
	@RdfProperty(propertyName = "gtfs:dateAddition")
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
