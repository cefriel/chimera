//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:43:51 AM CEST 
//


package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Fulfilment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Fulfilment">
 *   &lt;complexContent>
 *     &lt;extension base="{http://domainmodel.pts_fsm.org/2015/10/29/common}FSM.ID">
 *       &lt;sequence>
 *         &lt;element name="FulfilmentStatus" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}GenericStatus"/>
 *         &lt;element name="PassengerRefsList" type="{http://www.w3.org/2001/XMLSchema}IDREFS" minOccurs="0"/>
 *         &lt;element name="SegmentRefsList" type="{http://www.w3.org/2001/XMLSchema}IDREFS" minOccurs="0"/>
 *         &lt;element name="CarrierServiceItemRefsList" type="{http://www.w3.org/2001/XMLSchema}IDREFS" minOccurs="0"/>
 *         &lt;element name="FulfilmentInformation" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}AdditionalEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Fulfilment", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/fulfilment", propOrder = {
    "fulfilmentStatus",
    "passengerRefsList",
    "segmentRefsList",
    "carrierServiceItemRefsList",
    "fulfilmentInformation"
})
public class Fulfilment
    extends FSMID
{

    @XmlElement(name = "FulfilmentStatus", required = true)
    protected GenericStatus fulfilmentStatus;
    @XmlList
    @XmlElement(name = "PassengerRefsList")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> passengerRefsList;
    @XmlList
    @XmlElement(name = "SegmentRefsList")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> segmentRefsList;
    @XmlList
    @XmlElement(name = "CarrierServiceItemRefsList")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> carrierServiceItemRefsList;
    @XmlElement(name = "FulfilmentInformation")
    protected List<AdditionalEntry> fulfilmentInformation;

    /**
     * Gets the value of the fulfilmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link GenericStatus }
     *     
     */
    public GenericStatus getFulfilmentStatus() {
        return fulfilmentStatus;
    }

    /**
     * Sets the value of the fulfilmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericStatus }
     *     
     */
    public void setFulfilmentStatus(GenericStatus value) {
        this.fulfilmentStatus = value;
    }

    /**
     * Gets the value of the passengerRefsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passengerRefsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassengerRefsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPassengerRefsList() {
        if (passengerRefsList == null) {
            passengerRefsList = new ArrayList<Object>();
        }
        return this.passengerRefsList;
    }

    /**
     * Gets the value of the segmentRefsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the segmentRefsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSegmentRefsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getSegmentRefsList() {
        if (segmentRefsList == null) {
            segmentRefsList = new ArrayList<Object>();
        }
        return this.segmentRefsList;
    }

    /**
     * Gets the value of the carrierServiceItemRefsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the carrierServiceItemRefsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCarrierServiceItemRefsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getCarrierServiceItemRefsList() {
        if (carrierServiceItemRefsList == null) {
            carrierServiceItemRefsList = new ArrayList<Object>();
        }
        return this.carrierServiceItemRefsList;
    }

    /**
     * Gets the value of the fulfilmentInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fulfilmentInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFulfilmentInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalEntry }
     * 
     * 
     */
    public List<AdditionalEntry> getFulfilmentInformation() {
        if (fulfilmentInformation == null) {
            fulfilmentInformation = new ArrayList<AdditionalEntry>();
        }
        return this.fulfilmentInformation;
    }

}