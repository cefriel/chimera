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
 * <p>Concrete implementation of {@link BlockWriter} for HTML output. Due to differences in font sizes the strategy of
 * printing spaces as in {@link TextBlockWriter} do not work for HTML output. </p>
 * <p>
 * This implementation uses a rather crude strategy of tracking the actual HTML printed in one line and prints the exact
 * same HTML but in an invisible. There are two different strategies for printing invisible output. The first is
 * surrounding the output with a span element whose CSS attribute visibility is set to hidden. This will hide everything
 * inside that span but will still take same exact amount of space. Unfortunately, this CSS attribute is not supported
 * in every HTML viewer including the default Java widgets so there is a even more hacky solution to surround output
 * with a font tag where the color is set to the background color. The font color of inner elements are overridden by a
 * simple string substitution operation. Note that, one drawback of this option is that selecting part of the
 * explanation might highlight the invisible parts. The solution for this is to use the
 * <code>JTextPane.setHighlighter(null)</code> call.
 * </p>
 * <p> <b>IMPORTANT:</b> This class is not intended for general HTML output and would fail miserably under certain
 * conditions. Using tables and images in the output are some examples. Unfortunately, any alternative strategy for
 * alignment (e.g. using nested HTML tables) has even more disadvantages. Some of the hackiness in this implementation
 * can be overcome by generating HTML through DOM elements.
 * </p>
 * 
 * @author Evren Sirin
 * @deprecated This class is not robust at all and should not be used for the reasons explained above.
 */
@Deprecated
public class HTMLBlockWriter extends BlockWriter {
	/**
	 * The strings
	 */
	private final StringBuilder currentLine = new StringBuilder();
	private boolean visibilityStyleSupported = true;
	private String backgroundColor = "white";
	private boolean offTheRecord = false;

	public HTMLBlockWriter(Writer out) {
		super(out, "&nbsp;");
	}

	protected void startNewLine() {
		currentLine.setLength(0);
		
		HTMLBlock block = (HTMLBlock) currentBlock();
		if (block != null) {
			currentLine.append(block.indent);
			offTheRecord = true;
			if (isVisibilityStyleSupported()) {
				super.print("<span visibility='hidden'>");
				super.print(currentLine);
				super.print("</span>");

			}
			else {
				super.print("<font color='");
				super.print(getBackgroundColor());
				super.print("'>");
				super.print(currentLine);
				super.print("</font>");
			}
			offTheRecord = false;
		}

	}

	public void println() {
		if (!offTheRecord) {
			offTheRecord = true;
			print("<br>");
			offTheRecord = false;
		}

		super.println();
	}

	@Override
	public void write(char[] buf, int off, int len) {
		super.write(buf, off, len);

		if (!offTheRecord) {
			currentLine.append(buf, off, len);
		}
	}

	@Override
	public void write(int c) {
		super.write(c);

		if (!offTheRecord) {
			currentLine.append(c);
		}
	}

	@Override
	public void write(String s, int off, int len) {
		super.write(s, off, len);

		if (!offTheRecord) {
			currentLine.append(s, off, len);
		}
	}

	public boolean isVisibilityStyleSupported() {
		return visibilityStyleSupported;
	}

	public void setVisibilityStyleSupported(boolean visibilityStyleSupported) {
		this.visibilityStyleSupported = visibilityStyleSupported;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
    protected Block createBlock(BlockSpec spec) {
	    return new HTMLBlock(spec);
    }
	
	private class HTMLBlock extends Block {
		private String indent; 
		
		public HTMLBlock(BlockSpec spec) {
	        super(spec);
        }
		
		@Override
        protected void afterBegin() {
			String line = currentLine.toString();

			if (!isVisibilityStyleSupported()) {
				line = line.replaceAll("color=", "hidecolor=");
				line = line.replaceAll("href=", "hidehref=");
			}

			indent = line;
        }	
	}
}
