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

package org.rutebanken.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class LocalDateTimeISO8601XmlAdapter extends XmlAdapter<String, LocalDateTime> {

	private final static DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss")
			.optionalStart().appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).optionalEnd()
			.optionalStart().appendPattern("XXXXX")
            .optionalEnd()
			
//
	.parseDefaulting(ChronoField.OFFSET_SECONDS,OffsetDateTime.now().getLong(ChronoField.OFFSET_SECONDS) ).toFormatter();

	@Override
	public LocalDateTime unmarshal(String inputDate) throws Exception {
		return LocalDateTime.parse(inputDate, formatter);

	}

	@Override
	public String marshal(LocalDateTime inputDate) throws Exception {
		if(inputDate != null) {
			return formatter.format(inputDate);
		} else {
			return null;
		}
	}

}
