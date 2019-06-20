package st4rt.convertor.eu.start.standards.TAPTSI918;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ReadXML {
	public static void main(String[] args) {

		try {

			File file = new File("src/main/java/jaxb/input/918-request.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(ReservationRequest.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ReservationRequest resReq = (ReservationRequest) jaxbUnmarshaller.unmarshal(file);
			System.out.println(resReq);
			System.out.println(resReq.getClass().toString());
			System.out.println(resReq.getRequestor().getTerminal().getCountry().toString());

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}
}
