//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:43:51 AM CEST 
//


package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for CouplingKey complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CouplingKey">
 *   &lt;complexContent>
 *     &lt;extension base="{http://domainmodel.pts_fsm.org/2015/10/29/common}FSM.ID">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="OfferCouplingKey" type="{http://domainmodel.pts_fsm.org/2015/10/29/offering}OfferCouplingKey" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="FulfilmentCouplingKey" type="{http://domainmodel.pts_fsm.org/2015/10/29/offering}TicketCouplingKey" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CouplingKey", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/offering", propOrder = {
    "offerCouplingKey",
    "fulfilmentCouplingKey"
})
public class CouplingKey
    extends FSMID
{

    @XmlElement(name = "OfferCouplingKey")
    protected List<OfferCouplingKey> offerCouplingKey;
    @XmlElement(name = "FulfilmentCouplingKey")
    protected List<TicketCouplingKey> fulfilmentCouplingKey;

    /**
     * Gets the value of the offerCouplingKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the offerCouplingKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOfferCouplingKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OfferCouplingKey }
     * 
     * 
     */
    public List<OfferCouplingKey> getOfferCouplingKey() {
        if (offerCouplingKey == null) {
            offerCouplingKey = new ArrayList<OfferCouplingKey>();
        }
        return this.offerCouplingKey;
    }

    /**
     * Gets the value of the fulfilmentCouplingKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fulfilmentCouplingKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFulfilmentCouplingKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TicketCouplingKey }
     * 
     * 
     */
    public List<TicketCouplingKey> getFulfilmentCouplingKey() {
        if (fulfilmentCouplingKey == null) {
            fulfilmentCouplingKey = new ArrayList<TicketCouplingKey>();
        }
        return this.fulfilmentCouplingKey;
    }

}