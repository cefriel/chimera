//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:44:46 AM CEST 
//


package st4rt.convertor.eu.start.standards.TAPTSI918;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * The time ranges to load and unload a vehicle on a car carriage train.
 * 
 * <p>Java class for VehicleLoadingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehicleLoadingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BeginOfLoading" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="EndOfLoading" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="BeginOfUnloading" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="EndOfUnloading" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehicleLoadingType", propOrder = {
    "beginOfLoading",
    "endOfLoading",
    "beginOfUnloading",
    "endOfUnloading"
})
public class VehicleLoadingType {

    @XmlElement(name = "BeginOfLoading", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar beginOfLoading;
    @XmlElement(name = "EndOfLoading", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endOfLoading;
    @XmlElement(name = "BeginOfUnloading", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar beginOfUnloading;
    @XmlElement(name = "EndOfUnloading", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endOfUnloading;

    /**
     * Gets the value of the beginOfLoading property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBeginOfLoading() {
        return beginOfLoading;
    }

    /**
     * Sets the value of the beginOfLoading property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBeginOfLoading(XMLGregorianCalendar value) {
        this.beginOfLoading = value;
    }

    /**
     * Gets the value of the endOfLoading property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndOfLoading() {
        return endOfLoading;
    }

    /**
     * Sets the value of the endOfLoading property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndOfLoading(XMLGregorianCalendar value) {
        this.endOfLoading = value;
    }

    /**
     * Gets the value of the beginOfUnloading property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBeginOfUnloading() {
        return beginOfUnloading;
    }

    /**
     * Sets the value of the beginOfUnloading property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBeginOfUnloading(XMLGregorianCalendar value) {
        this.beginOfUnloading = value;
    }

    /**
     * Gets the value of the endOfUnloading property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndOfUnloading() {
        return endOfUnloading;
    }

    /**
     * Sets the value of the endOfUnloading property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndOfUnloading(XMLGregorianCalendar value) {
        this.endOfUnloading = value;
    }

}