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

package org.rutebanken.netex;

import org.apache.commons.lang3.builder.ToStringStyle;

public class OmitNullsToStringStyle extends org.apache.commons.lang3.builder.ToStringStyle {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7043613680114711384L;
	public static ToStringStyle INSTANCE = new OmitNullsToStringStyle();

    public OmitNullsToStringStyle() {
        this.setUseClassName(false);
        this.setUseIdentityHashCode(false);
        this.setUseFieldNames(true);
        this.setContentStart("[");
        this.setContentEnd("]");
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        if (value != null) {
            super.append(buffer, fieldName, value, fullDetail);
        }
    }

}
