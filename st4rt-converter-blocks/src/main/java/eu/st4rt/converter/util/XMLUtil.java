package eu.st4rt.converter.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.st4rt.converter.exception.InvalidXMLInputException;
import eu.st4rt.converter.exception.RdfIdSupportException;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class XMLUtil {

    public static <T> T convertXMLToObject(InputStream input, Class<T> outputClazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(outputClazz);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (T) JAXBIntrospector.getValue(jaxbUnmarshaller.unmarshal(input));
    }

    public static String convertObjectToXML(Object obj, Class outputClazz) throws RdfIdSupportException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(outputClazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter stringWriter = new StringWriter();

        jaxbMarshaller.marshal(obj, stringWriter);

        return stringWriter.toString();
    }

    public static SOAPBody getSOAPBody(String inputSoap) throws SOAPException, IOException {
        InputStream is = new ByteArrayInputStream(inputSoap.getBytes());
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
        is.close();
        return request.getSOAPBody();
    }

    public static String covertSOAPBodyToString(SOAPBody soapBody) throws TransformerException, SOAPException {
        Document doc = soapBody.extractContentAsDocument();
        DOMSource source = new DOMSource(doc);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        return stringResult.toString();
    }

    public static Document getDocument(String xmlString) throws InvalidXMLInputException, SOAPException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(xmlString.getBytes()));
    }

    public static String findRootElementName(String xmlString) throws InvalidXMLInputException, SOAPException, IOException, SAXException, ParserConfigurationException {
        Document doc = getDocument(xmlString);
        return findRootElementName(doc);
    }

    public static String findRootElementName(Document document) throws InvalidXMLInputException {
        org.w3c.dom.Element rootElement = document.getDocumentElement();

        if (rootElement == null)
            throw new InvalidXMLInputException("ST4RT convertor - XML root element name is empty.");

        String tagName = rootElement.getTagName();

        if (tagName.contains(":"))
            tagName = tagName.contains(":") ? tagName.substring(tagName.indexOf(":") + 1, tagName.length()) : tagName;

        return tagName;
    }
}
