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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Deque;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;

/**
 * <p>
 * Provides support for generating nicely aligned output. This class is geared towards textual outputs and is used for 
 * aligning the outputs in consecutive lines.
 * </p>
 * <p>
 * The idea is to create virtual blocks of output in a line (i.e. invisible tabbing points) and when a new line is
 * printed tab the output with spaces (or some similar invisible character) to the last tabbing point. Alignment blocks
 * are created in a LIFO fashion that is and you can only align w.r.t. latest block.
 * </p>
 * <p>
 * This class is an abstract class to provide some general functionality that can be reused in implementations that
 * supports purely textual output or HTML output.
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class BlockWriter extends PrintWriter {
	private static final BlockSpec INVISIBLE = BlockSpec.INDENTED.copy().indentSize(0).immutable();
	
	private final Deque<Block> blocks = Queues.newArrayDeque();
	private final String space;
	private int tabSize = 3;
	private boolean newLine = true;

	/**
	 * @param out
	 */
	public BlockWriter(Writer out, String space) {
		super(out);

		Preconditions.checkNotNull(space);

		this.space = space;
	}

	/**
	 * Do the preprocessing step required at the beginning of each line for alignment;
	 */
	protected abstract void startNewLine();

	/**
	 * Clear all the blocks previously defined.
	 */
	public void endBlocks() {
		while (!blocks.isEmpty()) {
			endBlock();
		}
	}
	
	protected abstract Block createBlock(BlockSpec spec);
	
	protected Block currentBlock() {
		return blocks.peek();
	}
	
	public void beginBlock(BlockSpec spec) {
		Block block = createBlock(spec);
		block.beforeBegin();
		blocks.push(block);
		block.afterBegin();
	}

	/**
	 * Begins a new block for alignment. This will mark the current location in the current line as the point to be used
	 * for alignment next time a new line is printed. Previously defined alignment blocks will be inaccessible until
	 * this block is closed with a {@link #endBlock()} call.
	 */
	public BlockWriter beginBlock() {
		beginBlock(INVISIBLE);
		return this;
	}

	/**
	 * Ends the current alignment block. All subsequent lines will be aligned w.r.t. the previous block or not at all if
	 * there was no previous block.
	 * 
	 * @throws IllegalStateException
	 *             if there is no block previously created with a {@link #beginBlock()} call.
	 */
	public BlockWriter endBlock() throws IllegalStateException {
		Preconditions.checkState(!blocks.isEmpty(), "No block to end!");
		Block block = blocks.peek();
		block.beforeEnd();
		blocks.pop();
		block.afterEnd();
		return this;
	}	

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void println() {
		// print the new line
		super.println();

		// set the flag to indicate we will need the alignment preprocessing
		// next time something is printed
		newLine = true;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void write(int c) {
		// do the preprocessing
		checkNewLine();

		// do the write
		super.write(c);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void write(char[] buf, int off, int len) {
		// do the preprocessing
		checkNewLine();

		// do the write
		super.write(buf, off, len);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void write(String s, int off, int len) {
		// do the preprocessing
		checkNewLine();

		// do the write
		super.write(s, off, len);
	}
	
	private void checkNewLine() {
		if (newLine) {
			newLine = false;
			
			startNewLine();
		}
	}

	/**
	 * Prints one space.
	 */
	public BlockWriter printSpace() {
		printSpace(1);
		return this;
	}

	/**
	 * Prints <code>n</code> spaces.
	 */
	public BlockWriter printSpace(int n) {
		for (int i = 0; i < n; i++) {
	        super.print(space);
        }
		return this;
	}
	
	/**
	 * Prints {@link #printSpace() space} as many times as specified by the {@link #getTabSize() tab size}.
	 */
	public BlockWriter printTab() {
		printSpace(tabSize);
		return this;
	}

	/**
	 * Returns the tab size.
	 */
	public int getTabSize() {
    	return tabSize;
    }

	/**
	 * Sets the tab size.
	 */
	public void setTabSize(int tabSize) {
    	this.tabSize = tabSize;
    }	
	
	protected abstract class Block {	
		private final BlockSpec spec;
		
        protected Block(BlockSpec spec) {
	        this.spec = spec;
        }

        protected void beforeBegin() {
	        if (spec.getTitle() != null) {
	        	print(spec.getTitle());
	        	printSpace();
	        }
	        
	        if (spec.isNewLineBeforeBegin()) {
	        	println();
	        }
	        
	        if (spec.getBeginMarker() != null) {
	        	print(spec.getBeginMarker());
	        }	        
	        
	        if (spec.isNewLineAfterBegin()) {
	        	println();
	        }
	        
	        if (spec.getIndentSize() != 0) {
	        	printSpace(spec.getIndentSize() < 0 ? tabSize : spec.getIndentSize());
	        }

		}
		
		protected void afterBegin() {    
        }
	        
		protected void beforeEnd() {
	        if (spec.isNewLineBeforeEnd()) {
	        	println();
	        }
		}

		protected void afterEnd() {	        
	        if (spec.getEndMarker() != null) {
	        	print(spec.getEndMarker());
	        }
	        
	        if (spec.isNewLineAfterEnd()) {
	        	println();
	        }	
		}
	}
}
