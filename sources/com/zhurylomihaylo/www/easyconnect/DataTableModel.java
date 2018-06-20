
package com.zhurylomihaylo.www.easyconnect;

//import com.sun.rowset.CachedRowSetImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class DataTableModel extends AbstractTableModel {

	ResultSet dataRowSet; // The ResultSet to interpret
	ResultSetMetaData metadata; // Additional information about the results
	int numcols, numrows; // How many rows and columns in the table


	DataTableModel() {
		refreshData();
	}
	
	void refreshData() {
		if (dataRowSet != null)
			try {
				dataRowSet.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		dataRowSet = DBComm.getDataRowSet();
		updateNums();		
	}
	
	void updateNums() {
		try {
			this.metadata = dataRowSet.getMetaData();
			numcols = metadata.getColumnCount();
			this.dataRowSet.beforeFirst();
			this.numrows = 0;
			while (this.dataRowSet.next()) {
				this.numrows++;
			}
			this.dataRowSet.beforeFirst();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

//	public void addEventHandlersToRowSet(RowSetListener listener) {
//		this.dataRowSet.addRowSetListener(listener);
//	}

	void insertRow(String coffeeName, int supplierID, float price, int sales, int total) throws SQLException {

		try {
			this.dataRowSet.moveToInsertRow();
			this.dataRowSet.updateString("COF_NAME", coffeeName);
			this.dataRowSet.updateInt("SUP_ID", supplierID);
			this.dataRowSet.updateFloat("PRICE", price);
			this.dataRowSet.updateInt("SALES", sales);
			this.dataRowSet.updateInt("TOTAL", total);
			this.dataRowSet.insertRow();
			this.dataRowSet.moveToCurrentRow();
		} catch (SQLException e) {
			// JDBCTutorialUtilities.printSQLException(e);
			throw new RuntimeException(e);
		}
	}

	void close() {
		try {
			dataRowSet.getStatement().close();
		} catch (SQLException e) {
			// JDBCTutorialUtilities.printSQLException(e);
			throw new RuntimeException(e);
		}
	}

	/** Automatically close when we're garbage collected */
	protected void finalize() {
		close();
	}

	/** Method from interface TableModel; returns the number of columns */

	public int getColumnCount() {
		return numcols;
	}

	/** Method from interface TableModel; returns the number of rows */

	public int getRowCount() {
		return numrows;
	}

	/**
	 * Method from interface TableModel; returns the column name at columnIndex
	 * based on information from ResultSetMetaData
	 */

	public String getColumnName(int column) {
		try {
			return this.metadata.getColumnLabel(column + 1);
		} catch (SQLException e) {
			return e.toString();
		}
	}

	/**
	 * Method from interface TableModel; returns the most specific superclass for
	 * all cell values in the specified column. To keep things simple, all data in
	 * the table are converted to String objects; hence, this method returns the
	 * String class.
	 */

	public Class getColumnClass(int column) {
		return String.class;
	}

	/**
	 * Method from interface TableModel; returns the value for the cell specified by
	 * columnIndex and rowIndex. TableModel uses this method to populate itself with
	 * data from the row set. SQL starts numbering its rows and columns at 1, but
	 * TableModel starts at 0.
	 */

	public Object getValueAt(int rowIndex, int columnIndex) {

		try {
			this.dataRowSet.absolute(rowIndex + 1);
			Object o = this.dataRowSet.getObject(columnIndex + 1);
			if (o == null)
				return null;
			else
				return o.toString();
		} catch (SQLException e) {
			return e.toString();
		}
	}


	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	// Because the sample does not allow users to edit any cells from the
	// TableModel, the following methods, setValueAt, addTableModelListener,
	// and removeTableModelListener, do not need to be implemented.

	public void setValueAt(Object value, int row, int column) {
		System.out.println("Calling setValueAt row " + row + ", column " + column);
	}

}
