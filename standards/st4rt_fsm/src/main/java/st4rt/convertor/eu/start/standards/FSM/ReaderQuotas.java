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


/**
 * <p>Java class for ReaderQuotas complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReaderQuotas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="MaxArrayLength" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="MaxBytesPerRead" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="MaxDepth" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="MaxStringContentLength" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReaderQuotas", propOrder = {
    "maxArrayLength",
    "maxBytesPerRead",
    "maxDepth",
    "maxStringContentLength"
})
public class ReaderQuotas {

    @XmlElement(name = "MaxArrayLength")
    protected Long maxArrayLength;
    @XmlElement(name = "MaxBytesPerRead")
    protected Long maxBytesPerRead;
    @XmlElement(name = "MaxDepth")
    protected Long maxDepth;
    @XmlElement(name = "MaxStringContentLength")
    protected Long maxStringContentLength;

    /**
     * Gets the value of the maxArrayLength property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxArrayLength() {
        return maxArrayLength;
    }

    /**
     * Sets the value of the maxArrayLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxArrayLength(Long value) {
        this.maxArrayLength = value;
    }

    /**
     * Gets the value of the maxBytesPerRead property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxBytesPerRead() {
        return maxBytesPerRead;
    }

    /**
     * Sets the value of the maxBytesPerRead property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxBytesPerRead(Long value) {
        this.maxBytesPerRead = value;
    }

    /**
     * Gets the value of the maxDepth property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the value of the maxDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxDepth(Long value) {
        this.maxDepth = value;
    }

    /**
     * Gets the value of the maxStringContentLength property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxStringContentLength() {
        return maxStringContentLength;
    }

    /**
     * Sets the value of the maxStringContentLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxStringContentLength(Long value) {
        this.maxStringContentLength = value;
    }

}