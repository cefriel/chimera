//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:43:51 AM CEST 
//


package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for KerberosToken complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KerberosToken">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="KerberosTokenPolicy" type="{http://servicemodel.pts_fsm.org/2015/10/29/fsm.common.headers}KerberosTokenPolicy" minOccurs="0"/>
 *         &lt;element name="KerberosTokenReference" type="{http://servicemodel.pts_fsm.org/2015/10/29/fsm.common.headers}KerberosTokenReference" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="IncludeToken" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Issuer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IssuerName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Claims" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KerberosToken", propOrder = {
    "kerberosTokenPolicy",
    "kerberosTokenReference"
})
public class KerberosToken {

    @XmlElement(name = "KerberosTokenPolicy")
    protected KerberosTokenPolicy kerberosTokenPolicy;
    @XmlElement(name = "KerberosTokenReference")
    protected KerberosTokenReference kerberosTokenReference;
    @XmlAttribute(name = "IncludeToken")
    protected String includeToken;
    @XmlAttribute(name = "Issuer")
    protected String issuer;
    @XmlAttribute(name = "IssuerName")
    protected String issuerName;
    @XmlAttribute(name = "Claims")
    protected String claims;

    /**
     * Gets the value of the kerberosTokenPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link KerberosTokenPolicy }
     *     
     */
    public KerberosTokenPolicy getKerberosTokenPolicy() {
        return kerberosTokenPolicy;
    }

    /**
     * Sets the value of the kerberosTokenPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link KerberosTokenPolicy }
     *     
     */
    public void setKerberosTokenPolicy(KerberosTokenPolicy value) {
        this.kerberosTokenPolicy = value;
    }

    /**
     * Gets the value of the kerberosTokenReference property.
     * 
     * @return
     *     possible object is
     *     {@link KerberosTokenReference }
     *     
     */
    public KerberosTokenReference getKerberosTokenReference() {
        return kerberosTokenReference;
    }

    /**
     * Sets the value of the kerberosTokenReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link KerberosTokenReference }
     *     
     */
    public void setKerberosTokenReference(KerberosTokenReference value) {
        this.kerberosTokenReference = value;
    }

    /**
     * Gets the value of the includeToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeToken() {
        return includeToken;
    }

    /**
     * Sets the value of the includeToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeToken(String value) {
        this.includeToken = value;
    }

    /**
     * Gets the value of the issuer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the value of the issuer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuer(String value) {
        this.issuer = value;
    }

    /**
     * Gets the value of the issuerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * Sets the value of the issuerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerName(String value) {
        this.issuerName = value;
    }

    /**
     * Gets the value of the claims property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaims() {
        return claims;
    }

    /**
     * Sets the value of the claims property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaims(String value) {
        this.claims = value;
    }

}