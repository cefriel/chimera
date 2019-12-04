/*
 * Copyright (c) 2005-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.common.io.block;

import com.complexible.common.base.Copyable;
import com.google.common.base.Preconditions;

/**
 * Formatting specification of a block defining the markers, indentation level, and newlines.
 * 
 * @author Evren Sirin
 */
public class BlockSpec implements Copyable<BlockSpec> {
	public static final BlockSpec INDENTED = new BlockSpec().immutable();

	private boolean mutable = true;
	
	private String title = null;
	private String beginMarker = null;
	private String endMarker = null;
	private int indentSize = -1;
	private boolean newLineBeforeBegin = false;
	private boolean newLineAfterBegin = false;
	private boolean newLineBeforeEnd = false;
	private boolean newLineAfterEnd = false;
	
	private void assertMutable() {
		Preconditions.checkState(mutable, "This object is immutable");
	}
	
	public BlockSpec indentSize(int indentSize) {
		assertMutable();
		this.indentSize = indentSize;
		return this;
	}
	
	public BlockSpec marker(BlockMarker marker) {
		assertMutable();
		beginMarker = marker == null ? null : marker.getBegin();
		endMarker = marker == null ? null : marker.getEnd();
		return this;
	}
	
	public BlockSpec marker(String beginMarker, String endMarker) {
		assertMutable();
		this.beginMarker = beginMarker;
		this.endMarker = endMarker;
		return this;
	}
	
	public BlockSpec newLineAfterBegin(boolean enabled) {
		assertMutable();
		newLineAfterBegin = enabled;
		return this;
	}
	
	public BlockSpec newLineAfterEnd(boolean enabled) {
		assertMutable();
		newLineAfterEnd = enabled;
		return this;
	}
	
	public BlockSpec newLineBeforeBegin(boolean enabled) {
		assertMutable();
		newLineBeforeBegin = enabled;
		return this;
	}
	
	public BlockSpec newLineBeforeEnd(boolean enabled) {
		assertMutable();
		newLineBeforeEnd = enabled;
		return this;
	}		
	
	public BlockSpec title(String title) {
		assertMutable();
		this.title = title;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getBeginMarker() {
		return beginMarker;
	}

	public String getEndMarker() {
		return endMarker;
	}

	public int getIndentSize() {
		return indentSize;
	}

	public boolean isNewLineBeforeBegin() {
		return newLineBeforeBegin;
	}


	public boolean isNewLineAfterBegin() {
		return newLineAfterBegin;
	}

	public boolean isNewLineBeforeEnd() {
		return newLineBeforeEnd;
	}

	public boolean isNewLineAfterEnd() {
		return newLineAfterEnd;
	}

	@Override
    public BlockSpec copy() {
		return new BlockSpec().title(title).marker(beginMarker, endMarker).indentSize(indentSize)
						.newLineBeforeBegin(newLineBeforeBegin).newLineAfterBegin(newLineAfterBegin)
						.newLineBeforeEnd(newLineBeforeEnd).newLineAfterEnd(newLineAfterEnd);
    }
	
    public BlockSpec immutable() {
		BlockSpec spec = copy();
		spec.mutable = false;
		return spec;
    }
}
