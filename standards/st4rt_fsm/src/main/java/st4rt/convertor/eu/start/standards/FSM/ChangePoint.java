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
 * <p>Java class for ChangePoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChangePoint">
 *   &lt;complexContent>
 *     &lt;extension base="{http://domainmodel.pts_fsm.org/2015/10/29/common}FSM.ID">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="ConnectionTime" type="{http://domainmodel.pts_fsm.org/2015/10/29/connection}ConnectionTime" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="LinkPath" type="{http://domainmodel.pts_fsm.org/2015/10/29/connection}LinkPath" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="StopPlaceName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangePoint", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/connection", propOrder = {
    "connectionTime",
    "linkPath"
})
public class ChangePoint
    extends FSMID
{

    @XmlElement(name = "ConnectionTime")
    protected List<ConnectionTime> connectionTime;
    @XmlElement(name = "LinkPath")
    protected List<LinkPath> linkPath;
    @XmlAttribute(name = "StopPlaceName")
    protected String stopPlaceName;

    /**
     * Gets the value of the connectionTime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connectionTime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnectionTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConnectionTime }
     * 
     * 
     */
    public List<ConnectionTime> getConnectionTime() {
        if (connectionTime == null) {
            connectionTime = new ArrayList<ConnectionTime>();
        }
        return this.connectionTime;
    }

    /**
     * Gets the value of the linkPath property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkPath property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkPath().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkPath }
     * 
     * 
     */
    public List<LinkPath> getLinkPath() {
        if (linkPath == null) {
            linkPath = new ArrayList<LinkPath>();
        }
        return this.linkPath;
    }

    /**
     * Gets the value of the stopPlaceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopPlaceName() {
        return stopPlaceName;
    }

    /**
     * Sets the value of the stopPlaceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopPlaceName(String value) {
        this.stopPlaceName = value;
    }

}