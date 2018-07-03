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
import java.sql.SQLException;

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
	private JDialog editRecordDialog;
	private JTable dataTable;
	private JMenuItem mntmExit;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOptions;
	private DataTableModel dataTableModel;
	private JPanel buttonsPanel;
	private JButton btnInsert;
	private JButton btnEdit;
	private JButton btnDelete;
	private JButton btnRefresh;
	private JPanel controlsPanel;
	private JTextField tfFilter;
	private JLabel lblFilter;
	private JButton btnClearFilter;
	private TableRowSorter<TableModel> rowSorter;

	MainFrame() {
		Props.init();
		DBComm.init(this);
		buildGUI();
		refreshIPs();
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
						JOptionPane.showMessageDialog(MainFrame.this, t.getStackTrace(), "Connection closing error",
								JOptionPane.ERROR_MESSAGE);
				}
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

	private ActionListener exitListener() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answ = JOptionPane.showOptionDialog(MainFrame.this, "Are you sure?", "Exiting",
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
				int row = table.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
					// // your valueChanged overridden method
					String comp = (String) dataTableModel.getValueAt(row, 2);
					// JOptionPane.showMessageDialog(MainFrame.this, comp);
					Object filePathOb = Props.get("remoteProgramPath");
					if (filePathOb == null || filePathOb.equals("")) {
						JOptionPane.showMessageDialog(MainFrame.this,
								"Use \"Options\" menu to set remote program path!");
						return;
					}
					File file = new File((String) filePathOb);
					if (!file.exists()) {
						JOptionPane.showMessageDialog(MainFrame.this,
								"Remote access program \"" + filePathOb + "\" does not exist.");
						return;
					}
					String command = (String) filePathOb + " " + comp;
					try {
						Runtime.getRuntime().exec(command);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
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

	DataTableModel getDataTableModel() {
		return dataTableModel;
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
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}
	
			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = tfFilter.getText();
	
				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}
	
			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Not supported yet.");
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

	private void refreshIPs() {
		IPRefresher ipr = new IPRefresher();
		Thread thr = new Thread(ipr);
		thr.setDaemon(true);
		thr.start();
	}

	JTable getDataTable() {
		return dataTable;
	}

	private void buildGUI() {
		menuBar = new JMenuBar();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		setJMenuBar(menuBar);
	
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
	
		mntmImport = new JMenuItem("Import...");
		mnFile.add(mntmImport);
	
		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
	
		mntmOptions = new JMenuItem("Options");
		menuBar.add(mntmOptions);
	
		mntmExit.addActionListener(exitListener());
		mntmImport.addActionListener(importListener());
		mntmOptions.addActionListener(optionsListener());
	
		actionOnClose();
	
		dataTableModel = new DataTableModel();
		dataTable = new JTable(dataTableModel);
		dataTable.addMouseListener(doubleClickListener());
	
		rowSorter = new TableRowSorter<>(dataTableModel);
		dataTable.setRowSorter(rowSorter);
	
		controlsPanel = new JPanel();
		getContentPane().add(controlsPanel, BorderLayout.NORTH);
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
	
		buttonsPanel = new JPanel();
		controlsPanel.add(buttonsPanel);
		FlowLayout fl_buttonsPanel = (FlowLayout) buttonsPanel.getLayout();
		fl_buttonsPanel.setAlignment(FlowLayout.LEFT);
	
		btnInsert = new JButton("Insert");
		btnInsert.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Insert list item.png")));
		btnInsert.addActionListener(insertListener());
		// btnInsert.addActionListener((e) -> JOptionPane.showMessageDialog(this,
		// "Insert!"));
		buttonsPanel.add(btnInsert);
	
		btnEdit = new JButton("Edit");
		btnEdit.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Change list item.png")));
		buttonsPanel.add(btnEdit);
	
		btnDelete = new JButton("Delete");
		btnDelete.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Delete list item.png")));
		buttonsPanel.add(btnDelete);
	
		btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Refresh.png")));
		buttonsPanel.add(btnRefresh);

		lblFilter = new JLabel("Filter");
		buttonsPanel.add(lblFilter);

		tfFilter = new JTextField();
		buttonsPanel.add(tfFilter);
		tfFilter.setColumns(20);
		tfFilter.getDocument().addDocumentListener(filterListener());
		
		btnClearFilter = new JButton("Clear");
		buttonsPanel.add(btnClearFilter);
		btnClearFilter.addActionListener(clearFilterListener());
		JScrollPane scrollPane = new JScrollPane(dataTable);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	
		pack();
	}
}
