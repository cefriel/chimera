/*
 * Copyright (c) 2005-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.timer;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Create a table data structure that has a list of column names and list of
 * data rows. The only function of this class is to print the data in a table
 * format. Data can be given at once by the constructor or can be added
 * incrementally with the addRow function.
 * 
 * @author Evren Sirin
 * @since 2.0
 * @version 2.0
 */
public class TableData {
	Collection	data;

	List		colNames;

	boolean[]	rightAligned;

	int			colWidths[]	= null;

	String		colSep		= " | ";

	public TableData(Collection data, List colNames) {
		this.data = data;
		this.colNames = colNames;

		int cols = colNames.size();
		colWidths = new int[cols];
		rightAligned = new boolean[cols];
	}

	public TableData(List colNames) {
		this.data = new ArrayList();
		this.colNames = colNames;

		int cols = colNames.size();
		colWidths = new int[cols];
		rightAligned = new boolean[cols];
	}

	public TableData(String[] colNames) {
		this( Arrays.asList( colNames ) );
	}

	public void setAlignment(boolean[] rightAligned) {
		if( rightAligned.length != colNames.size() )
			throw new IllegalArgumentException( "Alignment has " + rightAligned.length
					+ " elements but table has " + colNames.size() + " columns" );

		this.rightAligned = rightAligned;
	}

	public void setrightAligned(int colIndex, boolean rightAligned) {
		this.rightAligned[colIndex] = rightAligned;
	}

	public void add(List row) {
		if( row.size() != colNames.size() )
			throw new IllegalArgumentException( "Row has " + row.size()
					+ " elements but table has " + colNames.size() + " columns" );

		data.add( row );
	}

	public void print(OutputStream writer) {
		print( new PrintWriter( writer ) );
	}
	
	public void print(PrintWriter out) {
		printText( out );
	}

	public void print(Writer writer) {
		printText( writer );
	}

	private void printText(Writer writer) {
		PrintWriter pw = (writer instanceof PrintWriter)
			? (PrintWriter) writer
			: new PrintWriter( writer );

		computeHeaderWidths();
		computeRowWidths();

		int lineWidth = computeLineWidth();
		int numCols = colNames.size();

		String[] row = new String[numCols];
		for( int col = 0; col < row.length; col++ ) {
			row[col] = colNames.get( col ).toString();
		}
		printRow( pw, row );

		for( int i = 0; i < lineWidth; i++ )
			pw.print( '=' );
		pw.println();

		for( Iterator i = data.iterator(); i.hasNext(); ) {
			Collection rowData = (Collection) i.next();

			Iterator j = rowData.iterator();
			for( int col = 0; j.hasNext(); col++ ) {
				Object value = j.next();
				row[col] = value == null
					? "<null>"
					: value.toString();
			}
			printRow( pw, row );
		}

		pw.flush();
	}

	private void printRow(PrintWriter pw, String[] row) {
		for( int col = 0; col < row.length; col++ ) {
			String s = row[col];
			int pad = colWidths[col];
			StringBuffer sbuff = new StringBuffer( 120 );

			if( col > 0 )
				sbuff.append( colSep );

			if( !rightAligned[col] )
				sbuff.append( s );

			for( int j = 0; j < pad - s.length(); j++ )
				sbuff.append( ' ' );

			if( rightAligned[col] )
				sbuff.append( s );

			pw.print( sbuff );
		}
		pw.println();
	}

	private int computeLineWidth() {
		int numCols = colWidths.length;
		int lineWidth = 0;
		for( int i = 0; i < numCols; i++ )
			lineWidth += colWidths[i];

		lineWidth += (numCols - 1) * colSep.length();

		return lineWidth;
	}

	private void computeHeaderWidths() {
		Iterator k = colNames.iterator();
		for( int col = 0; k.hasNext(); col++ ) {
			Object value = k.next();
			String str = (value == null)
				? "<null>"
				: value.toString();
			colWidths[col] = str.length();
		}
	}

	private void computeRowWidths() {
		for( Iterator i = data.iterator(); i.hasNext(); ) {
			Collection rowData = (Collection) i.next();

			Iterator j = rowData.iterator();
			for( int col = 0; j.hasNext(); col++ ) {
				Object value = j.next();
				String str = (value == null)
					? "<null>"
					: value.toString();

				if( colWidths[col] < str.length() )
					colWidths[col] = str.length();
			}
		}
	}

	public int getRowCount() {
		return data.size();
	}

	public int getColCount() {
		return colNames.size();
	}

	public void sort(String colName) {
		sort( colNames.indexOf( colName ) );
	}

	public void sort(final int col) {
		Object a[] = data.toArray();
		Arrays.sort( a, new Comparator() {
			public int compare(Object l1, Object l2) {
				return ((Comparable) ((List) l1).get( col )).compareTo( ((List) l2).get( col ) );
			}
		} );
		data = Arrays.asList( a );
	}

	public void sort(final int col, final Comparator c) {
		Object a[] = data.toArray();
		Arrays.sort( a, new Comparator() {
			public int compare(Object l1, Object l2) {
				return c.compare( ((List) l1).get( col ), ((List) l2).get( col ) );
			}
		} );
		data = Arrays.asList( a );
	}

	public String toString() {
		StringWriter sw = new StringWriter();
		printText( sw );

		return sw.toString();
	}
}
