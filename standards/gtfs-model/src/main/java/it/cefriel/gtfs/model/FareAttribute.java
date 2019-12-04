package it.cefriel.gtfs.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator=",", crlf="UNIX",skipFirstLine=true)
public final class FareAttribute {

    private static final int MISSING_VALUE = -999;

    @DataField(pos = 1, columnName="fare_id")
    private String fareId;

    @DataField(pos = 2, columnName="price")
    private float price;

    @DataField(pos = 3, columnName="currency_type")
    private String currencyType;

    @DataField(pos = 4, columnName="payment_method")
    private int paymentMethod;

    @DataField(pos = 5, columnName="transfers")
    private int transfers = MISSING_VALUE;

    @DataField(pos = 6, columnName="agency_id")
    private String agencyId;
    
    @DataField(pos = 7, columnName="transfer_duration")
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
