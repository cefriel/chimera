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
 * <p>Java class for EndpointBehavior complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EndpointBehavior">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="MaxNumberOfItemsInObjectgraph" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ReaderQuotas" type="{http://servicemodel.pts_fsm.org/2015/10/29/fsm.common.headers}ReaderQuotas" minOccurs="0"/>
 *         &lt;element name="ClientCertificate" type="{http://servicemodel.pts_fsm.org/2015/10/29/fsm.common.headers}ClientCertificate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EndpointBehavior", propOrder = {
    "maxNumberOfItemsInObjectgraph",
    "readerQuotas",
    "clientCertificate"
})
public class EndpointBehavior {

    @XmlElement(name = "MaxNumberOfItemsInObjectgraph")
    protected Long maxNumberOfItemsInObjectgraph;
    @XmlElement(name = "ReaderQuotas")
    protected ReaderQuotas readerQuotas;
    @XmlElement(name = "ClientCertificate")
    protected ClientCertificate clientCertificate;

    /**
     * Gets the value of the maxNumberOfItemsInObjectgraph property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxNumberOfItemsInObjectgraph() {
        return maxNumberOfItemsInObjectgraph;
    }

    /**
     * Sets the value of the maxNumberOfItemsInObjectgraph property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxNumberOfItemsInObjectgraph(Long value) {
        this.maxNumberOfItemsInObjectgraph = value;
    }

    /**
     * Gets the value of the readerQuotas property.
     * 
     * @return
     *     possible object is
     *     {@link ReaderQuotas }
     *     
     */
    public ReaderQuotas getReaderQuotas() {
        return readerQuotas;
    }

    /**
     * Sets the value of the readerQuotas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReaderQuotas }
     *     
     */
    public void setReaderQuotas(ReaderQuotas value) {
        this.readerQuotas = value;
    }

    /**
     * Gets the value of the clientCertificate property.
     * 
     * @return
     *     possible object is
     *     {@link ClientCertificate }
     *     
     */
    public ClientCertificate getClientCertificate() {
        return clientCertificate;
    }

    /**
     * Sets the value of the clientCertificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientCertificate }
     *     
     */
    public void setClientCertificate(ClientCertificate value) {
        this.clientCertificate = value;
    }

}