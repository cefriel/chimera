/*
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.netex.model;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

public class MarshalUnmarshalTest {

	private static JAXBContext jaxbContext;
	
	private static ObjectFactory factory = new ObjectFactory();

	@BeforeClass
	public static void initContext() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(PublicationDeliveryStructure.class);

	}

	@Test
	public void publicationDeliveryWithOffsetDateTime() throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();

		PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
				.withDescription(new MultilingualString().withValue("value").withLang("no").withTextIdType("")).withPublicationTimestamp(LocalDateTime.now())
				.withParticipantRef("participantRef");

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		marshaller.marshal(factory.createPublicationDelivery(publicationDelivery), byteArrayOutputStream);

		String xml = byteArrayOutputStream.toString();
		System.out.println(xml);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		@SuppressWarnings("unchecked")
		JAXBElement<PublicationDeliveryStructure> jaxbElement = (JAXBElement<PublicationDeliveryStructure>) unmarshaller
				.unmarshal(new ByteArrayInputStream(xml.getBytes()));
		PublicationDeliveryStructure actual = jaxbElement.getValue();

		System.out.println(actual.getPublicationTimestamp());
		assertThat(actual.getPublicationTimestamp()).isEqualTo(publicationDelivery.getPublicationTimestamp());
		assertThat(actual.getDescription()).isNotNull();
		assertThat(actual.getDescription().getValue()).isEqualTo(publicationDelivery.getDescription().getValue());
		assertThat(actual.getParticipantRef()).isEqualTo(publicationDelivery.getParticipantRef());
	}

	@Test
	public void unmarshalPublicationDeliveryAndVerifyDateTime() throws JAXBException {

		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<PublicationDelivery version=\"1.0\" xmlns=\"http://www.netex.org.uk/netex\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.netex.org.uk/netex ../../xsd/NeTEx_publication.xsd\">"
				+ " <PublicationTimestamp>2016-05-18T15:00:00.0+01:00</PublicationTimestamp>"
				+ " <ParticipantRef>NHR</ParticipantRef>"
				+ " <dataObjects>"
				+ "  <SiteFrame version=\"01\" id=\"nhr:sf:1\">"
				+ "   <stopPlaces>"
				+ "    <StopPlace version=\"01\" created=\"2016-04-21T09:00:00.0Z\" id=\"nhr:sp:1\">"
				+ "     <Centroid>"
				+ "      <Location srsName=\"WGS84\">"
				+ "       <Longitude>10.8577903</Longitude>"
				+ "       <Latitude>59.910579</Latitude>"
				+ "      </Location>"
				+ "     </Centroid>"
				+ "     <Name lang=\"no-NO\">Krokstien</Name>    "
				+ "     <TransportMode>bus</TransportMode>"
				+ "     <StopPlaceType>onstreetBus</StopPlaceType>"
				+ "     <quays>"
				+ "      <Quay version=\"01\" created=\"2016-04-21T09:01:00.0Z\" id=\"nhr:sp:1:q:1\">"
				+ "       <Centroid>"
				+ "        <Location srsName=\"WGS84\">"
				+ "         <Longitude>10.8577903</Longitude>"
				+ "         <Latitude>59.910579</Latitude>"
				+ "        </Location>"
				+ "       </Centroid>"
				+ "       <Covered>outdoors</Covered>"
				+ "       <Lighting>wellLit</Lighting>"
				+ "       <QuayType>busStop</QuayType>"
				+ "      </Quay>"
				+ "     </quays>"
				+ "    </StopPlace>"
				+ "   </stopPlaces>"
				+ "  </SiteFrame>"
				+ " </dataObjects>"
				+ "</PublicationDelivery>";

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		@SuppressWarnings("unchecked")
		JAXBElement<PublicationDeliveryStructure> jaxbElement = (JAXBElement<PublicationDeliveryStructure>) unmarshaller
				.unmarshal(new ByteArrayInputStream(xml.getBytes()));
		PublicationDeliveryStructure actual = jaxbElement.getValue();

		System.out.println(actual.getPublicationTimestamp());
		assertThat(actual.getPublicationTimestamp().getHour()).isEqualTo(15);
	}

	@Test
	public void timetableWithVehicleModes() throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();

		TimetableFrame timetableFrame = factory.createTimetableFrame().withVersion("any").withId("TimetableFrame")
				.withName(factory.createMultilingualString().withValue("TimetableFrame")).withVehicleModes(VehicleModeEnumeration.AIR);

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		marshaller.marshal(factory.createTimetableFrame(timetableFrame), byteArrayOutputStream);

		String xml = byteArrayOutputStream.toString();
		System.out.println(xml);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		@SuppressWarnings("unchecked")
		JAXBElement<TimetableFrame> jaxbElement = (JAXBElement<TimetableFrame>) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
		TimetableFrame actual = jaxbElement.getValue();

		assertThat(actual.getVersion()).isNotNull().isNotEmpty().isEqualTo(timetableFrame.getVersion());
		assertThat(actual.getId()).isNotNull().isNotEmpty().isEqualTo(timetableFrame.getId());
		assertThat(actual.getName().getValue()).isNotNull().isNotEmpty().isEqualTo(timetableFrame.getName().getValue());
		assertThat(actual.getVehicleModes()).isNotNull().isNotEmpty().hasSize(1);
		assertThat(actual.getVehicleModes().get(0)).isNotNull().isEqualTo(VehicleModeEnumeration.AIR);
	}

	@Test
	public void dayTypeWithPropertiesOfDay() throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();

		List<DayOfWeekEnumeration> daysOfWeek = Arrays.asList(DayOfWeekEnumeration.MONDAY, DayOfWeekEnumeration.TUESDAY, DayOfWeekEnumeration.WEDNESDAY,
				DayOfWeekEnumeration.THURSDAY, DayOfWeekEnumeration.FRIDAY);
		PropertyOfDay propertyOfDay = factory.createPropertyOfDay().withDescription(factory.createMultilingualString().withValue("PropertyOfDay"))
				.withName(factory.createMultilingualString().withValue("PropertyOfDay")).withDaysOfWeek(daysOfWeek);
		PropertiesOfDay_RelStructure propertiesOfDay = factory.createPropertiesOfDay_RelStructure().withPropertyOfDay(propertyOfDay);
		DayType dayType = factory.createDayType().withVersion("any").withId(String.format("%s:dt:weekday", "SK4488"))
				.withName(factory.createMultilingualString().withValue("Ukedager (mandag til fredag)")).withProperties(propertiesOfDay);

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		marshaller.marshal(factory.createDayType(dayType), byteArrayOutputStream);

		String xml = byteArrayOutputStream.toString();
		System.out.println(xml);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		@SuppressWarnings("unchecked")
		JAXBElement<DayType> jaxbElement = (JAXBElement<DayType>) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
		DayType actual = jaxbElement.getValue();

		assertThat(actual.getVersion()).isNotNull().isNotEmpty().isEqualTo(dayType.getVersion());
		assertThat(actual.getId()).isNotNull().isNotEmpty().isEqualTo(dayType.getId());
		assertThat(actual.getName().getValue()).isNotNull().isNotEmpty().isEqualTo(dayType.getName().getValue());
		assertThat(actual.getProperties().getPropertyOfDay().get(0).getDaysOfWeek()).isNotNull().isNotEmpty().hasSize(5)
				.hasOnlyElementsOfType(DayOfWeekEnumeration.class).hasSameElementsAs(daysOfWeek);
	}

	@Test
	public void datedCallWithLocalDate() throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();

		DatedCall datedCall = new DatedCall().withArrivalDate(LocalDateTime.now().with(ChronoField.MILLI_OF_DAY, 0));

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		marshaller.marshal(factory.createDatedCall(datedCall), byteArrayOutputStream);

		String xml = byteArrayOutputStream.toString();
		System.out.println(xml);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		JAXBElement<DatedCall> actual = (JAXBElement<DatedCall>) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

		assertThat(actual.getValue().getArrivalDate()).isEqualTo(datedCall.getArrivalDate());

	}


	@Test
	public void marshalledNamespacePrefixes() throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();

		PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure();


		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		marshaller.marshal(factory.createPublicationDelivery(publicationDeliveryStructure), byteArrayOutputStream);

		String xml = byteArrayOutputStream.toString();
		System.out.println(xml);

		assertThat(xml).as("Namespace declaration without prefix for netex").contains("xmlns=\"http://www.netex.org.uk/netex\"");
		assertThat(xml).as("<PublicationDelivery without namespace prefix").contains("<PublicationDelivery");
	}

	@Test
	public void datedCallWithLocalDateTime() throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();

		DatedCall datedCall = new DatedCall().withChanged(LocalDateTime.now());

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		marshaller.marshal(factory.createDatedCall(datedCall), byteArrayOutputStream);

		String xml = byteArrayOutputStream.toString();
		System.out.println(xml);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		JAXBElement<DatedCall> actual = (JAXBElement<DatedCall>) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

		assertThat(actual.getValue().getChanged().getHour()).isEqualTo(datedCall.getChanged().getHour());
		assertThat(actual.getValue().getChanged()).isEqualToIgnoringNanos(datedCall.getChanged());
	}

	@Test
	public void unmarshalPublicationDeliveryAndVerifyValidBetween() throws JAXBException, FileNotFoundException {

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		@SuppressWarnings("unchecked")
		JAXBElement<PublicationDeliveryStructure> jaxbElement = (JAXBElement<PublicationDeliveryStructure>) unmarshaller
				.unmarshal(new FileInputStream(new File("src/test/resources/date_time_examples.xml")));
		PublicationDeliveryStructure actual = jaxbElement.getValue();
		CompositeFrame compositeFrame = (CompositeFrame) ((JAXBElement<? extends Common_VersionFrameStructure>) actual.dataObjects.compositeFrameOrCommonFrame
				.get(0)).getValue();
		ValidityConditions_RelStructure validityConditions = compositeFrame.getValidityConditions();
		ValidBetween validBetweenWithTimezone = (ValidBetween) validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_().get(0);
		assertThat(validBetweenWithTimezone.getFromDate()).isNotNull();
		assertThat(validBetweenWithTimezone.getToDate()).isNotNull();
		assertThat(validBetweenWithTimezone.getToDate().toString()).isEqualTo("2017-01-01T11:00");

		ValidBetween validBetweenWithoutTimezone = (ValidBetween) validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_().get(1);
		assertThat(validBetweenWithoutTimezone.getFromDate()).isNotNull();
		assertThat(validBetweenWithoutTimezone.getToDate()).isNotNull();

		assertThat(validBetweenWithoutTimezone.getToDate().toString()).isEqualTo("2017-01-01T12:00");

		Timetable_VersionFrameStructure timetableFrame = (Timetable_VersionFrameStructure) compositeFrame.getFrames().getCommonFrame().get(1).getValue();
		ServiceJourney_VersionStructure serviceJourney = (ServiceJourney_VersionStructure) timetableFrame.getVehicleJourneys()
				.getDatedServiceJourneyOrDeadRunOrServiceJourney().get(0);
		assertThat(serviceJourney.getDepartureTime()).isNotNull();
		// Specified as local time
		assertThat(serviceJourney.getDepartureTime().toString()).isEqualTo("07:55");

		LocalTime departureTimeZulu = serviceJourney.getPassingTimes().getTimetabledPassingTime().get(0).getDepartureTime();
		assertThat(departureTimeZulu).isNotNull();
		assertThat(departureTimeZulu.toString()).isEqualTo("07:55");

		LocalTime departureTimeOffset = serviceJourney.getPassingTimes().getTimetabledPassingTime().get(1).getArrivalTime();
		assertThat(departureTimeOffset).isNotNull();
		assertThat(departureTimeOffset.toString()).isEqualTo("08:40");
	}

	@Test
	public void fragmentShouldNotContainNetexNamespace() throws Exception {
		JAXBContext netexJaxBContext = JAXBContext.newInstance("net.opengis.gml._3:org.rutebanken.netex.model:uk.org.siri.siri");
		Marshaller marshaller = netexJaxBContext.createMarshaller();

		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		StringWriter stringWriter = new StringWriter();
		AvailabilityCondition availabilityCondition = new AvailabilityCondition().withFromDate(LocalDateTime.now()).withToDate(LocalDateTime.now()).withId("NSR:AvailabilityCondition:2").withVersion("v1");


		String netexNamespace="http://www.netex.org.uk/netex";

		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.setDefaultNamespace(netexNamespace);

		marshaller.marshal(new ObjectFactory().createAvailabilityCondition(availabilityCondition), xmlStreamWriter);
		String xml = stringWriter.toString();
		System.out.println(xml);
		assertFalse(xml.contains(netexNamespace));
	}

}
