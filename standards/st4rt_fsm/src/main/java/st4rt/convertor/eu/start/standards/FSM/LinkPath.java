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
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for LinkPath complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LinkPath">
 *   &lt;complexContent>
 *     &lt;extension base="{http://domainmodel.pts_fsm.org/2015/10/29/common}FSM.ID">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="WaitingTimeTypeId" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}MasterData.GUID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MobilityAidTypeId" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}MasterData.GUID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="NavigationPoint" type="{http://domainmodel.pts_fsm.org/2015/10/29/connection}NavigationPoint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="PathLinkTypeId" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}MasterData.GUID" />
 *       &lt;attribute name="PedestrianPaceId" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}MasterData.GUID" />
 *       &lt;attribute name="AidBookable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkPath", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/connection", propOrder = {
    "waitingTimeTypeId",
    "mobilityAidTypeId",
    "navigationPoint"
})
public class LinkPath
    extends FSMID
{

    @XmlElement(name = "WaitingTimeTypeId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected List<String> waitingTimeTypeId;
    @XmlElement(name = "MobilityAidTypeId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected List<String> mobilityAidTypeId;
    @XmlElement(name = "NavigationPoint")
    protected List<NavigationPoint> navigationPoint;
    @XmlAttribute(name = "PathLinkTypeId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pathLinkTypeId;
    @XmlAttribute(name = "PedestrianPaceId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pedestrianPaceId;
    @XmlAttribute(name = "AidBookable")
    protected Boolean aidBookable;

    /**
     * Gets the value of the waitingTimeTypeId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the waitingTimeTypeId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWaitingTimeTypeId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getWaitingTimeTypeId() {
        if (waitingTimeTypeId == null) {
            waitingTimeTypeId = new ArrayList<String>();
        }
        return this.waitingTimeTypeId;
    }

    /**
     * Gets the value of the mobilityAidTypeId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobilityAidTypeId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobilityAidTypeId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMobilityAidTypeId() {
        if (mobilityAidTypeId == null) {
            mobilityAidTypeId = new ArrayList<String>();
        }
        return this.mobilityAidTypeId;
    }

    /**
     * Gets the value of the navigationPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the navigationPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNavigationPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NavigationPoint }
     * 
     * 
     */
    public List<NavigationPoint> getNavigationPoint() {
        if (navigationPoint == null) {
            navigationPoint = new ArrayList<NavigationPoint>();
        }
        return this.navigationPoint;
    }

    /**
     * Gets the value of the pathLinkTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathLinkTypeId() {
        return pathLinkTypeId;
    }

    /**
     * Sets the value of the pathLinkTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathLinkTypeId(String value) {
        this.pathLinkTypeId = value;
    }

    /**
     * Gets the value of the pedestrianPaceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPedestrianPaceId() {
        return pedestrianPaceId;
    }

    /**
     * Sets the value of the pedestrianPaceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPedestrianPaceId(String value) {
        this.pedestrianPaceId = value;
    }

    /**
     * Gets the value of the aidBookable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAidBookable() {
        return aidBookable;
    }

    /**
     * Sets the value of the aidBookable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAidBookable(Boolean value) {
        this.aidBookable = value;
    }

}