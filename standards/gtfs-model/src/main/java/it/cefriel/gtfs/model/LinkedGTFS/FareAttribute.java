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
@RdfsClass("gtfs:FareClass")
public final class FareAttribute implements SupportsRdfId {

    @XmlTransient
    private SupportsRdfId mIdSupport = new SupportsRdfIdImpl();

    public RdfKey getRdfId() {
        return mIdSupport.getRdfId();
    }

    public void setRdfId(RdfKey theId) {
        mIdSupport.setRdfId(theId);
    }

    private static final int MISSING_VALUE = -999;

    @DataField(pos = 1, columnName="fare_id")
    private String fareId;

    @DataField(pos = 2, columnName="price")
    @RdfProperty(propertyName = "schema:price")
    private float price;

    @DataField(pos = 3, columnName="currency_type")
    @RdfProperty(propertyName = "schema:priceCurrency")
    private String currencyType;

    @DataField(pos = 4, columnName="payment_method")
    @RdfProperty(propertyName = "gtfs:paymentMethod")
    private int paymentMethod;

    @DataField(pos = 5, columnName="transfers")
    @RdfProperty(propertyName = "gtfs:transfers")
    private int transfers = MISSING_VALUE;

    @DataField(pos = 6, columnName="agency_id")
    private String agencyId;
    
    @DataField(pos = 7, columnName="transfer_duration")
    @RdfProperty(propertyName = "gtfs:transferExpiryTime")
    private int transferDuration = MISSING_VALUE;


    public FareAttribute() {

    }

    public FareAttribute(FareAttribute fa) {
        this.fareId = fa.fareId;
        this.price = fa.price;
        this.currencyType = fa.currencyType;
        this.paymentMethod = fa.paymentMethod;
        this.transfers = fa.transfers;
        this.transferDuration = fa.transferDuration;
        
    }

    public String getFareId() {
        return fareId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getTransfers() {
        return transfers;
    }

    public void setTransfers(int transfers) {
        this.transfers = transfers;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public int getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(int transferDuration) {
        this.transferDuration = transferDuration;
    }


}
