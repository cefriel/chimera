//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:43:51 AM CEST 
//


package st4rt.convertor.eu.start.standards.FSM;

import st4rt.convertor.empire.annotation.RdfProperty;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for OfferParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OfferParameters">
 *   &lt;complexContent>
 *     &lt;extension base="{http://domainmodel.pts_fsm.org/2015/10/29/common}FSM.ID">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="Itinerary" type="{http://domainmodel.pts_fsm.org/2015/10/29/journeyplan}Itinerary" maxOccurs="unbounded"/>
 *         &lt;element name="Passenger" type="{http://domainmodel.pts_fsm.org/2015/10/29/passenger}PassengerData" maxOccurs="unbounded"/>
 *         &lt;element name="SegmentPassengerBinding" type="{http://domainmodel.pts_fsm.org/2015/10/29/offering}SegmentPassengerBinding" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OfferParameters", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/offering", propOrder = {
    "itinerary",
    "passenger",
    "segmentPassengerBinding"
})
@XmlSeeAlso({
    DirectBookingParameters.class,
    OfferingParameters.class
})
public class OfferParameters
    extends FSMID
{

    @XmlElement(name = "Itinerary")
    @RdfProperty(propertyName = "st4rt:hasItinerary")
    protected List<Itinerary> itinerary;
    
    @XmlElement(name = "Passenger")
    @RdfProperty(propertyName = "st4rt:hasPassenger")
    protected List<PassengerData> passenger;
    
    @XmlElement(name = "SegmentPassengerBinding")
    protected List<SegmentPassengerBinding> segmentPassengerBinding;

    /**
     * Gets the value of the itinerary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itinerary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItinerary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Itinerary }
     * 
     * 
     */
    public List<Itinerary> getItinerary() {
        if (itinerary == null) {
            itinerary = new ArrayList<Itinerary>();
        }
        return this.itinerary;
    }

    /**
     * Gets the value of the passenger property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passenger property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassenger().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PassengerData }
     * 
     * 
     */
    public List<PassengerData> getPassenger() {
        if (passenger == null) {
            passenger = new ArrayList<PassengerData>();
        }
        return this.passenger;
    }

    /**
     * Gets the value of the segmentPassengerBinding property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the segmentPassengerBinding property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSegmentPassengerBinding().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SegmentPassengerBinding }
     * 
     * 
     */
    public List<SegmentPassengerBinding> getSegmentPassengerBinding() {
        if (segmentPassengerBinding == null) {
            segmentPassengerBinding = new ArrayList<SegmentPassengerBinding>();
        }
        return this.segmentPassengerBinding;
    }

}