package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

class MainFrame extends JFrame {
	// private JTextField searchF;
	private JMenuItem mntmImport;
	private JButton impButt;
	private JDialog impDialog;
	private JDialog optDialog;
	private EditRecord editRecordDialog;
	private JTable dataTable;
	private JMenuItem mntmExit;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOptions;
	private DataTableModel dataTableModel;
	private JPanel buttonsPanel;
	private JButton btnInsert;
	private JButton btnEdit;
	private JButton btnDeletionMark;
	private JButton btnRefresh;
	private JPanel controlsPanel;
	private JTextField tfFilter;
	private JLabel lblFilter;
	private JButton btnClearFilter;
	private TableRowSorter<TableModel> rowSorter;
	private Thread thrIPRefresher;
	private JButton btnConnect;
	private JMenuItem mntmDeleteMarkedRecords;
	private JPanel connectPanel;

	MainFrame() {
//		Locale [] al = DateFormat.getAvailableLocales();
//		String [] alString = new String[al.length];
//		for (int i = 0; i < al.length; i++) 
//			alString[i] = al[i].toString();
//		Arrays.sort(alString);
//		System.out.println(Arrays.toString(alString));
		//System.out.println(Locale.getDefault());
		
		Props.init();
		DBComm.init(this);
		buildGUI();
		refreshIPs();
	}

	private void refreshIPs() {
		IPRefresher ipr = new IPRefresher();
		thrIPRefresher = new Thread(ipr);
		thrIPRefresher.start();
	}

	DataTableModel getDataTableModel() {
		return dataTableModel;
	}

	JTable getDataTable() {
		return dataTable;
	}

	private void actionOnClose() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				Props.store();

				try {
					Connection conn = DBComm.getConnection();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					for (Throwable t : ex)
						JOptionPane.showMessageDialog(MainFrame.this, t.getStackTrace(), Messages.getString("MainFrame.ConnectionClosingError"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
				}
				
				if (thrIPRefresher.isAlive())
					thrIPRefresher.interrupt();
			}
		});
	}

	private ActionListener importListener() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (impDialog == null) {
					impDialog = new ImportDialog(MainFrame.this);
					impDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				}
				impDialog.setVisible(true); // pop up dialog
			}
		};
		return listener;
	}

	private ActionListener deleteMarkedRecordsListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "DELETE FROM MainTable WHERE DELETIONMARK = TRUE"; //$NON-NLS-1$
				Connection conn = DBComm.getConnection();
				try (Statement stat = conn.createStatement();){
					int rowCount = stat.executeUpdate(command);
					if (rowCount != 0) {
						dataTableModel.refreshData();
						dataTableModel.fireTableDataChanged();
					}
					JOptionPane.showMessageDialog(MainFrame.this, rowCount + Messages.getString("MainFrame.RrecordsWereDeleted.")); //$NON-NLS-1$
				} catch (SQLException e1) {
					throw new RuntimeException(e1);
				} 
			}
		};
	}

	private ActionListener exitListener() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answ = JOptionPane.showOptionDialog(MainFrame.this, Messages.getString("MainFrame.AreYouSure"), Messages.getString("MainFrame.Exiting"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (answ == JOptionPane.YES_OPTION)
					System.exit(0);
				;
			}
		};
		return listener;
	}

	private ActionListener optionsListener() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (optDialog == null) // first time
					optDialog = new OptionsDialog(MainFrame.this);
				optDialog.setVisible(true); // pop up dialog
			}
		};
		return listener;
	}

	private MouseAdapter doubleClickListener() {
		MouseAdapter adapter = new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
					// // your valueChanged overridden method
					int row = table.convertRowIndexToModel(table.rowAtPoint(point));					
					connectToRemote(row);
				}
			}

			
		};

		return adapter;
	}

	private ActionListener insertListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (editRecordDialog == null) {
					editRecordDialog = new EditRecord(MainFrame.this);
					editRecordDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				}
				editRecordDialog.setVisible(true); // pop up dialog
			}
		};
	}

	private ActionListener editListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = dataTable.getSelectedRow();
				if (row == -1)
					return;
				row = dataTable.convertRowIndexToModel(row);
				
				if (editRecordDialog == null) {
					editRecordDialog = new EditRecord(MainFrame.this);
					editRecordDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				}
				
				int order;
				order = DBComm.getFieldDescription("ID").getOrder(); //$NON-NLS-1$
				int id = (int) dataTableModel.getValueAt(row, order - 1);
				order = DBComm.getFieldDescription("PERSON").getOrder(); //$NON-NLS-1$
				String person = (String) dataTableModel.getValueAt(row, order - 1);
				order = DBComm.getFieldDescription("COMP").getOrder(); //$NON-NLS-1$
				String comp = (String) dataTableModel.getValueAt(row, order - 1);
				order = DBComm.getFieldDescription("IP").getOrder(); //$NON-NLS-1$
				String ip = (String) dataTableModel.getValueAt(row, order - 1);
				order = DBComm.getFieldDescription("IP_CHECK_DATE").getOrder(); //$NON-NLS-1$
				Date ip_Check_Date = (Date) dataTableModel.getValueAt(row, order - 1);
				order = DBComm.getFieldDescription("IP_UPDATE_DATE").getOrder(); //$NON-NLS-1$
				Date ip_Update_Date = (Date) dataTableModel.getValueAt(row, order - 1);
				order = DBComm.getFieldDescription("ORGS").getOrder(); //$NON-NLS-1$
				String orgs = (String) dataTableModel.getValueAt(row, order - 1);
				
				editRecordDialog.populate(id, person, comp, ip, ip_Check_Date, ip_Update_Date, orgs);
				
				editRecordDialog.setVisible(true); // pop up dialog				
			}
		};
	}

	private ActionListener deletionMarkListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = dataTable.getSelectedRow();
				if (row == -1)
					return;
				row = dataTable.convertRowIndexToModel(row);
				
				String query = "UPDATE MainTable SET DeletionMark = ? WHERE Id = ?"; //$NON-NLS-1$

				int dmOrder = DBComm.getFieldDescription("DELETIONMARK").getOrder(); //$NON-NLS-1$
				boolean dmark = (boolean) dataTableModel.getValueAt(row, dmOrder - 1);
				dmark = !dmark;
				
				int idOrder = DBComm.getFieldDescription("ID").getOrder(); //$NON-NLS-1$
				int id = (int) dataTableModel.getValueAt(row, idOrder - 1);
				
				Connection conn = DBComm.getConnection();
				try (PreparedStatement st = conn.prepareStatement(query);) {
					st.setBoolean(1, dmark);
					st.setInt(2, id);
					
					st.executeUpdate();
				} catch (SQLException ex) {
					throw new RuntimeException(ex);
				}
				
				dataTableModel.refreshData();
				dataTableModel.fireTableDataChanged();
				
				int convertedRow;
				for (int i = 0; i < dataTableModel.getRowCount(); i++) {// For each row
					if (dataTableModel.getValueAt(i, idOrder - 1).equals(id)) {
						convertedRow = dataTable.convertRowIndexToView(i);
						dataTable.setRowSelectionInterval(convertedRow, convertedRow);
					}
				}				
			}
		};
	}

	private ActionListener refreshListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dataTableModel.refreshData();
				dataTableModel.fireTableDataChanged();

			}
		};
	}

	private DocumentListener filterListener() {
		return new DocumentListener() {
	
			//https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = tfFilter.getText();
	
				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)(?u)" + text)); //$NON-NLS-1$
				}
			}
	
			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = tfFilter.getText();
	
				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)(?u)" + text)); //$NON-NLS-1$
				}
			}
	
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException(Messages.getString("MainFrame.NotSupportedYet")); //$NON-NLS-1$
			}
	
		};
	}

	private ActionListener clearFilterListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tfFilter.setText(null);
				rowSorter.setRowFilter(null);
			}
		};
	}

	private ActionListener connectListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = dataTable.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(MainFrame.this, Messages.getString("MainFrame.NoRowIsSelected")); //$NON-NLS-1$
					return;
				}
				row = dataTable.convertRowIndexToModel(row);
				connectToRemote(row);
			}
		};
	}

	private void connectToRemote(int row) {
		int order = DBComm.getFieldDescription("COMP").getOrder(); //$NON-NLS-1$
		String comp = (String) dataTableModel.getValueAt(row, order - 1);
		// JOptionPane.showMessageDialog(MainFrame.this, comp);
		String filePathOb = Props.get("remoteProgramPath"); //$NON-NLS-1$
		if (filePathOb == null || filePathOb.equals(Messages.getString("MainFrame.EmptyString"))) { //$NON-NLS-1$
			JOptionPane.showMessageDialog(MainFrame.this,
					Messages.getString("MainFrame.UseOptionsMenuToSetRemoteProgramPath")); //$NON-NLS-1$
			return;
		}
		File file = new File(filePathOb);
		if (!file.exists()) {
			JOptionPane.showMessageDialog(MainFrame.this,
					Messages.getString("MainFrame.RemoteAccessProfram") + filePathOb + Messages.getString("MainFrame.DoesNotExist")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		String command = filePathOb + Messages.getString("MainFrame.OneSpace") + comp; //$NON-NLS-1$
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void buildGUI() {
		menuBar = new JMenuBar();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		setJMenuBar(menuBar);
	
		mnFile = new JMenu(Messages.getString("MainFrame.File")); //$NON-NLS-1$
		menuBar.add(mnFile);
	
		mntmImport = new JMenuItem(Messages.getString("MainFrame.Import")); //$NON-NLS-1$
		mnFile.add(mntmImport);
		
		mntmDeleteMarkedRecords = new JMenuItem(Messages.getString("MainFrame.DeleteMarkedRecords")); //$NON-NLS-1$
		mntmDeleteMarkedRecords.addActionListener(deleteMarkedRecordsListener());
		mnFile.add(mntmDeleteMarkedRecords);
	
		mntmExit = new JMenuItem(Messages.getString("MainFrame.Exit")); //$NON-NLS-1$
		mnFile.add(mntmExit);
	
		mntmOptions = new JMenuItem(Messages.getString("MainFrame.Options")); //$NON-NLS-1$
		menuBar.add(mntmOptions);
	
		mntmExit.addActionListener(exitListener());
		mntmImport.addActionListener(importListener());
		mntmOptions.addActionListener(optionsListener());
	
		actionOnClose();
	
		dataTableModel = new DataTableModel();
		
		//
		
		dataTable = new JTable(dataTableModel);
		dataTable.addMouseListener(doubleClickListener());
		
		DeletionMarkCellRenderer renderer = new DeletionMarkCellRenderer();
		String header = DBComm.getFieldDescription("DELETIONMARK").getHeader(); //$NON-NLS-1$
		TableColumn column = dataTable.getColumn(header); 
		column.setCellRenderer(renderer);
		column.setMaxWidth(25);

		//hide id column
		header = DBComm.getFieldDescription("ID").getHeader(); //$NON-NLS-1$
		column = dataTable.getColumn(header);
		dataTable.getColumnModel().removeColumn(column);
		
		rowSorter = new TableRowSorter<>(dataTableModel);
		dataTable.setRowSorter(rowSorter);
		
		//
		
		controlsPanel = new JPanel();
		getContentPane().add(controlsPanel, BorderLayout.NORTH);
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
	
		buttonsPanel = new JPanel();
		controlsPanel.add(buttonsPanel);
		FlowLayout fl_buttonsPanel = (FlowLayout) buttonsPanel.getLayout();
		fl_buttonsPanel.setAlignment(FlowLayout.LEFT);
	
		btnInsert = new JButton(Messages.getString("MainFrame.Insert")); //$NON-NLS-1$
		btnInsert.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Insert list item.png"))); //$NON-NLS-1$
		btnInsert.addActionListener(insertListener());
		// btnInsert.addActionListener((e) -> JOptionPane.showMessageDialog(this,
		// "Insert!"));
		buttonsPanel.add(btnInsert);
	
		btnEdit = new JButton(Messages.getString("MainFrame.Edit")); //$NON-NLS-1$
		btnEdit.addActionListener(editListener());
		btnEdit.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Change list item.png"))); //$NON-NLS-1$
		buttonsPanel.add(btnEdit);
	
		btnDeletionMark = new JButton(Messages.getString("MainFrame.SetUnsetDeletionMark")); //$NON-NLS-1$
		btnDeletionMark.addActionListener(deletionMarkListener());
		btnDeletionMark.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Deletion mark.png"))); //$NON-NLS-1$
		buttonsPanel.add(btnDeletionMark);
	
		btnRefresh = new JButton(Messages.getString("MainFrame.Refresh")); //$NON-NLS-1$
		btnRefresh.addActionListener(refreshListener());
		btnRefresh.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Refresh.png"))); //$NON-NLS-1$
		buttonsPanel.add(btnRefresh);

		lblFilter = new JLabel(Messages.getString("MainFrame.Filter")); //$NON-NLS-1$
		buttonsPanel.add(lblFilter);

		tfFilter = new JTextField();
		buttonsPanel.add(tfFilter);
		tfFilter.setColumns(12);
		tfFilter.getDocument().addDocumentListener(filterListener());
		
		btnClearFilter = new JButton(Messages.getString("MainFrame.Clear")); //$NON-NLS-1$
		buttonsPanel.add(btnClearFilter);
		
		connectPanel = new JPanel();
		controlsPanel.add(connectPanel);
		connectPanel.setLayout(new BorderLayout(0, 0));
		
		btnConnect = new JButton(Messages.getString("MainFrame.Connest")); //$NON-NLS-1$
		connectPanel.add(btnConnect, BorderLayout.NORTH);
		btnConnect.addActionListener(connectListener());
		btnConnect.setIcon(new ImageIcon(MainFrame.class.getResource("/images/UVNC.png"))); //$NON-NLS-1$
		btnClearFilter.addActionListener(clearFilterListener());
		JScrollPane scrollPane = new JScrollPane(dataTable);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	
		setSize(700, 1000);
	}
}
