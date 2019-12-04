/*
 * Copyright (c) 2005-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.io.Writer;

/**
 * <p>
 * Concrete implementation of {@link BlockWriter} for purely textual output like console output. It can
 * probably be used for any kind of output where monospaced font is used.
 * </p>
 * <p>
 * This implementation simply counts the number of characters printed on one line and pads the next line with the same
 * number of spaces.
 * </p>
 * 
 * @author Evren Sirin
 */
public class TextBlockWriter extends BlockWriter {
	/**
	 * The current column (number of the characters printed) for the current line
	 */
	private int column = 0;

	/**
	 * @param out
	 */
	public TextBlockWriter(Writer out) {
		super(out, " ");
	}

	/**
	 * {@inheritDoc}
	 */
	protected void startNewLine() {
		TextBlock currentBlock = (TextBlock) currentBlock();
		if (currentBlock != null) {
			printSpace(currentBlock.indent);
		}
	}

	public void println() {
		super.println();

		column = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(char[] buf, int off, int len) {
		super.write(buf, off, len);

		column += len;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(int c) {
		super.write(c);

		column += 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(String s, int off, int len) {
		super.write(s, off, len);

		column += len;
	}

	@Override
    protected Block createBlock(BlockSpec spec) {
	    return new TextBlock(spec);
    }
	
	private class TextBlock extends Block {
		private int indent; 
		
		public TextBlock(BlockSpec spec) {
	        super(spec);
        }
		
		@Override
        protected void afterBegin() {
	        indent = column;
        }	
	}
}
