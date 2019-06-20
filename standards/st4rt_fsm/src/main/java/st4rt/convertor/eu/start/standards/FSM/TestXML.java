package st4rt.convertor.eu.start.standards.FSM;

import javax.xml.bind.*;
import java.io.File;

public class TestXML {
	public static void main(String[] args) {
		//<unmarshall>
//		try {
			test1();
			//			<1>
//			File file = new File("src/main/java/jaxb/input/FMS-Booking_PreBookRequest_DirectBooking.xml");
//			JAXBContext jaxbContext = JAXBContext.newInstance(PreBookRequest.class);
//
//			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//			JAXBElement<PreBookRequest> tmp = (JAXBElement<PreBookRequest>) jaxbUnmarshaller.unmarshal(file);
//			PreBookRequest preBookReq = tmp.getValue();
//			//			System.out.println(preBookReq);
//			//			System.out.println(preBookReq.getCaller().get(0).getSimpleAddress().toString());
//			Segment s1;
//			s1 = preBookReq.getDirectBookingParameters().getItinerary().get(0).getSegment().get(0);
//			//			List<RouteLink> rls = new ArrayList<RouteLink>();
//			//			rls = s1.getRouteLinks();//here we have access to the routeLinks.
			//		</1>
			//</unmarshall>
			//<marshall>
			//		PreBookRequest request = new PreBookRequest();
			////		request.setGUID("testGUID");
			//		  try {
			//
			//			File file = new File("src/main/java/jaxb/input/marshalledFMS-Booking_PreBookRequest_DirectBooking.xml");
			//			JAXBContext jaxbContext = JAXBContext.newInstance(PreBookRequest.class);
			//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			//
			//			// output pretty printed
			//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			//
			//			jaxbMarshaller.marshal(request, file);
			//			jaxbMarshaller.marshal(request, System.out);
			//
			//		      } catch (JAXBException e) {
			//			e.printStackTrace();
			//		      }

			//		Segment request = new Segment();
			//		request.setGUID("testGUID");
			//<2>
			//			File file1 = new File("src/main/java/jaxb/input/marshalledFMS-Booking_PreBookRequest_DirectBooking.xml");
			//			JAXBContext jaxbContext1 = JAXBContext.newInstance(Segment.class);
			//			Marshaller jaxbMarshaller = jaxbContext1.createMarshaller();
			//
			//			// output pretty printed
			//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			//
			//			jaxbMarshaller.marshal(s1, file1);
			//			jaxbMarshaller.marshal(s1, System.out);
			//</2>
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}

		//</marshall>
	}

	private static void test1() {
		try {
//			File file = new File("src/main/java/jaxb/input/FSMM1.xml");FSM PreBookRequest
			File file = new File("src/main/java/jaxb/input/FSMPreBookRequest.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(PreBookRequest.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<PreBookRequest> tmp = (JAXBElement<PreBookRequest>) jaxbUnmarshaller.unmarshal(file);
			PreBookRequest preBookReq = tmp.getValue();
				System.out.println(preBookReq);
//				System.out.println(preBookReq.getCaller().get(0).getSimpleAddress().toString());
//			Segment s1;
//			s1 = preBookReq.getDirectBookingParameters().getItinerary().get(0).getSegment().get(0);
				Itinerary s1 = preBookReq.getDirectBookingParameters().getItinerary().get(0);
			File file1 = new File("src/main/java/jaxb/input/marshalledFSMM1.xml");
//			JAXBContext jaxbContext1 = JAXBContext.newInstance(Segment.class);
			JAXBContext jaxbContext1 = JAXBContext.newInstance(Itinerary.class);
			Marshaller jaxbMarshaller = jaxbContext1.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(s1, file1);
			jaxbMarshaller.marshal(s1, System.out);
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
