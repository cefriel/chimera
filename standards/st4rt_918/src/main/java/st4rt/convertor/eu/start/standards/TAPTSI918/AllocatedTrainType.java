//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:44:46 AM CEST 
//


package st4rt.convertor.eu.start.standards.TAPTSI918;

import st4rt.convertor.empire.annotation.Link;
import st4rt.convertor.empire.annotation.RdfProperty;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * All data describing the allocated train and the allocated relation on that train.
 * 
 * <p>Java class for AllocatedTrainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AllocatedTrainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DepartureStationName" type="{http://www.uic-asso.fr/xml/passenger/02}StationNameType"/>
 *         &lt;element name="ArrivalStationName" type="{http://www.uic-asso.fr/xml/passenger/02}StationNameType"/>
 *         &lt;element name="ServiceBrand" type="{http://www.uic-asso.fr/xml/passenger/02}ServiceBrandInformationType"/>
 *         &lt;element name="Category" type="{http://www.uic-asso.fr/xml/passenger/02}TrainCategory918Type" minOccurs="0"/>
 *         &lt;element name="Train" type="{http://www.uic-asso.fr/xml/passenger/02}TrainNumberType"/>
 *         &lt;element name="DepartureDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ArrivalDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllocatedTrainType", propOrder = {
    "departureStationName",
    "arrivalStationName",
    "serviceBrand",
    "category",
    "train",
    "departureDateTime",
    "arrivalDateTime"
})
public class AllocatedTrainType {

    @XmlElement(name = "DepartureStationName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @RdfProperty(links = {
            @Link(propertyName = "travel:hasItineraryOffer", nodeType = Link.NodeType.Shared, sharedID = "IDforItineraryOffer"),
            @Link(propertyName = "st4rt:hasItinerary", nodeType = Link.NodeType.Shared, sharedID = "IDforItinerary"),
            @Link(propertyName = "mobility:hasTravelEpisode", nodeType = Link.NodeType.Shared, sharedID = "IDforTravelEpisode"),
            @Link(propertyName = "st4rt:hasOrigin", nodeType = Link.NodeType.Shared, sharedID = "IDforOrigin")},
            propertyName = "infrastructure:hasStopPlaceName")
    protected String departureStationName;
    @XmlElement(name = "ArrivalStationName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @RdfProperty(links = {
            @Link(propertyName = "travel:hasItineraryOffer", nodeType = Link.NodeType.Shared, sharedID = "IDforItineraryOffer"),
            @Link(propertyName = "st4rt:hasItinerary", nodeType = Link.NodeType.Shared, sharedID = "IDforItinerary"),
            @Link(propertyName = "mobility:hasTravelEpisode", nodeType = Link.NodeType.Shared, sharedID = "IDforTravelEpisode"),
            @Link(propertyName = "st4rt:hasDestination", nodeType = Link.NodeType.Shared, sharedID = "IDforDestination")},
            propertyName = "infrastructure:hasStopPlaceName")
    protected String arrivalStationName;
    @XmlElement(name = "ServiceBrand", required = true)
    protected ServiceBrandInformationType serviceBrand;
    @XmlElement(name = "Category")
    protected TrainCategory918Type category;
    @XmlElement(name = "Train", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @RdfProperty(links = {
            @Link(propertyName = "travel:hasItineraryOffer", nodeType = Link.NodeType.Shared, sharedID = "IDforItineraryOffer"),
            @Link(propertyName = "st4rt:hasItinerary", nodeType = Link.NodeType.Shared, sharedID = "IDforItinerary"),
            @Link(propertyName = "mobility:hasTravelEpisode", nodeType = Link.NodeType.Shared, sharedID = "IDforTravelEpisode"),
            @Link(propertyName = "travel:isForTransportationService", nodeType = Link.NodeType.Shared, sharedID = "IDforTransportationService"),
            @Link(propertyName = "transport:hasEquipment", nodeType = Link.NodeType.Shared, sharedID = "IDforEquipment")},
            propertyName = "transport:hasTransportationServiceCode")
    protected String train;
    @XmlElement(name = "DepartureDateTime", required = true)
    @XmlSchemaType(name = "dateTime")
    @RdfProperty(links = {
            @Link(propertyName = "travel:hasItineraryOffer", nodeType = Link.NodeType.Shared, sharedID = "IDforItineraryOffer"),
            @Link(propertyName = "st4rt:hasItinerary", nodeType = Link.NodeType.Shared, sharedID = "IDforItinerary"),
            @Link(propertyName = "mobility:hasTravelEpisode", nodeType = Link.NodeType.Shared, sharedID = "IDforTravelEpisode"),
            @Link(propertyName = "st4rt:hasOrigin", nodeType = Link.NodeType.Shared, sharedID = "IDforOrigin")},
            propertyName = "st4rt:hasEffectiveDepartureTime")
    protected XMLGregorianCalendar departureDateTime;
    @XmlElement(name = "ArrivalDateTime", required = true)
    @XmlSchemaType(name = "dateTime")
    @RdfProperty(links = {
            @Link(propertyName = "travel:hasItineraryOffer", nodeType = Link.NodeType.Shared, sharedID = "IDforItineraryOffer"),
            @Link(propertyName = "st4rt:hasItinerary", nodeType = Link.NodeType.Shared, sharedID = "IDforItinerary"),
            @Link(propertyName = "mobility:hasTravelEpisode", nodeType = Link.NodeType.Shared, sharedID = "IDforTravelEpisode"),
            @Link(propertyName = "st4rt:hasDestination", nodeType = Link.NodeType.Shared, sharedID = "IDforDestination")},
            propertyName = "st4rt:hasArrivalTime")
    protected XMLGregorianCalendar arrivalDateTime;

    /**
     * Gets the value of the departureStationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureStationName() {
        return departureStationName;
    }

    /**
     * Sets the value of the departureStationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureStationName(String value) {
        this.departureStationName = value;
    }

    /**
     * Gets the value of the arrivalStationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalStationName() {
        return arrivalStationName;
    }

    /**
     * Sets the value of the arrivalStationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalStationName(String value) {
        this.arrivalStationName = value;
    }

    /**
     * Gets the value of the serviceBrand property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceBrandInformationType }
     *     
     */
    public ServiceBrandInformationType getServiceBrand() {
        return serviceBrand;
    }

    /**
     * Sets the value of the serviceBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceBrandInformationType }
     *     
     */
    public void setServiceBrand(ServiceBrandInformationType value) {
        this.serviceBrand = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link TrainCategory918Type }
     *     
     */
    public TrainCategory918Type getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrainCategory918Type }
     *     
     */
    public void setCategory(TrainCategory918Type value) {
        this.category = value;
    }

    /**
     * Gets the value of the train property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrain() {
        return train;
    }

    /**
     * Sets the value of the train property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrain(String value) {
        this.train = value;
    }

    /**
     * Gets the value of the departureDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDepartureDateTime() {
        return departureDateTime;
    }

    /**
     * Sets the value of the departureDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDepartureDateTime(XMLGregorianCalendar value) {
        this.departureDateTime = value;
    }

    /**
     * Gets the value of the arrivalDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArrivalDateTime() {
        return arrivalDateTime;
    }

    /**
     * Sets the value of the arrivalDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArrivalDateTime(XMLGregorianCalendar value) {
        this.arrivalDateTime = value;
    }

}