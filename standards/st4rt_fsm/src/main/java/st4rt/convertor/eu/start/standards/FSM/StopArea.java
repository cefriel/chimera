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
 * <p>Java class for StopArea complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StopArea">
 *   &lt;complexContent>
 *     &lt;extension base="{http://domainmodel.pts_fsm.org/2015/10/29/common}FSM.ID">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="VehicleAccess" type="{http://domainmodel.pts_fsm.org/2015/10/29/infrastructure}VehicleAccess" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="StopAreaId" type="{http://domainmodel.pts_fsm.org/2015/10/29/common}MasterData.GUID" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopArea", namespace = "http://domainmodel.pts_fsm.org/2015/10/29/infrastructure", propOrder = {
    "vehicleAccess"
})
public class StopArea
    extends FSMID
{

    @XmlElement(name = "VehicleAccess")
    protected List<VehicleAccess> vehicleAccess;
    @XmlAttribute(name = "StopAreaId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String stopAreaId;

    /**
     * Gets the value of the vehicleAccess property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehicleAccess property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicleAccess().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VehicleAccess }
     * 
     * 
     */
    public List<VehicleAccess> getVehicleAccess() {
        if (vehicleAccess == null) {
            vehicleAccess = new ArrayList<VehicleAccess>();
        }
        return this.vehicleAccess;
    }

    /**
     * Gets the value of the stopAreaId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopAreaId() {
        return stopAreaId;
    }

    /**
     * Sets the value of the stopAreaId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopAreaId(String value) {
        this.stopAreaId = value;
    }

}