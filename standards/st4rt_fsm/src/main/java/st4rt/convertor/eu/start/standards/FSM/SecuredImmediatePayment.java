//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:43:51 AM CEST 
//


package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 			
 * 
 * <p>Java class for SecuredImmediatePayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecuredImmediatePayment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="PaymentCardHolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CurrencyId" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}MasterData.GUID"/>
 *         &lt;element name="PostalAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalSecurePaymentInfo" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}AdditionalEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecuredImmediatePayment", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/payment", propOrder = {
    "amount",
    "paymentCardHolderName",
    "currencyId",
    "postalAddress",
    "additionalSecurePaymentInfo"
})
@XmlSeeAlso({
    SecuredThreeDImmediatePayment.class,
    SecuredPCIDSSImmediatePayment.class
})
public class SecuredImmediatePayment {

    @XmlElement(name = "Amount", required = true)
    protected BigDecimal amount;
    @XmlElement(name = "PaymentCardHolderName", required = true)
    protected String paymentCardHolderName;
    @XmlElement(name = "CurrencyId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String currencyId;
    @XmlElement(name = "PostalAddress")
    protected String postalAddress;
    @XmlElement(name = "AdditionalSecurePaymentInfo")
    protected List<AdditionalEntry> additionalSecurePaymentInfo;

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * Gets the value of the paymentCardHolderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentCardHolderName() {
        return paymentCardHolderName;
    }

    /**
     * Sets the value of the paymentCardHolderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentCardHolderName(String value) {
        this.paymentCardHolderName = value;
    }

    /**
     * Gets the value of the currencyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyId() {
        return currencyId;
    }

    /**
     * Sets the value of the currencyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyId(String value) {
        this.currencyId = value;
    }

    /**
     * Gets the value of the postalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalAddress() {
        return postalAddress;
    }

    /**
     * Sets the value of the postalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalAddress(String value) {
        this.postalAddress = value;
    }

    /**
     * Gets the value of the additionalSecurePaymentInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalSecurePaymentInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalSecurePaymentInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalEntry }
     * 
     * 
     */
    public List<AdditionalEntry> getAdditionalSecurePaymentInfo() {
        if (additionalSecurePaymentInfo == null) {
            additionalSecurePaymentInfo = new ArrayList<AdditionalEntry>();
        }
        return this.additionalSecurePaymentInfo;
    }

}