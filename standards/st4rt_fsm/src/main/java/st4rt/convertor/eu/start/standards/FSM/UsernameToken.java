//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.28 at 11:43:51 AM CEST 
//


package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for UsernameToken complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UsernameToken">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="UsernameTokenPolicy" type="{http://servicemodel.pts_fsm.org/2015/10/29/fsm.common.headers}UsernameTokenPolicy" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Password" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "UsernameToken", propOrder = {
    "usernameTokenPolicy"
})
public class UsernameToken {

    @XmlElement(name = "UsernameTokenPolicy")
    protected UsernameTokenPolicy usernameTokenPolicy;
    @XmlAttribute(name = "Username")
    protected String username;
    @XmlAttribute(name = "Password")
    protected String password;
    @XmlAttribute(name = "IncludeToken")
    protected String includeToken;
    @XmlAttribute(name = "Issuer")
    protected String issuer;
    @XmlAttribute(name = "IssuerName")
    protected String issuerName;
    @XmlAttribute(name = "Claims")
    protected String claims;

    /**
     * Gets the value of the usernameTokenPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link UsernameTokenPolicy }
     *     
     */
    public UsernameTokenPolicy getUsernameTokenPolicy() {
        return usernameTokenPolicy;
    }

    /**
     * Sets the value of the usernameTokenPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsernameTokenPolicy }
     *     
     */
    public void setUsernameTokenPolicy(UsernameTokenPolicy value) {
        this.usernameTokenPolicy = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
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