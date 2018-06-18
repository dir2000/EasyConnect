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
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

class MainFrame extends JFrame {
	//private JTextField searchF;
	private JMenuItem mntmImport;
	private JButton impButt;
	private JDialog impDialog;
	private JDialog optDialog;
	private JTable dataTable;
	private JMenuItem mntmExit;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOptions;
	private TableModel tableModel;

	public MainFrame() {
		Props.init();
		DBComm.init(this);
		buildGUI();
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
		
		tableModel = new DataTableModel();
		dataTable = new JTable(tableModel);
		dataTable.addMouseListener(doubleClickListener());
		getContentPane().add(new JScrollPane(dataTable), BorderLayout.CENTER);
		
		pack();
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
				int answ = JOptionPane.showOptionDialog(MainFrame.this, "Are you sure?", "Exiting", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (answ == JOptionPane.YES_OPTION)
					System.exit(0);;
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
					//JOptionPane.showMessageDialog(MainFrame.this, table.getSelectedRow());
					// // your valueChanged overridden method
					String comp = (String) tableModel.getValueAt(row, 2);
					//JOptionPane.showMessageDialog(MainFrame.this, comp);
					Object filePathOb = Props.get("remoteProgramPath");
					if (filePathOb == null || filePathOb.equals("")) {
						JOptionPane.showMessageDialog(MainFrame.this, "Use \"Options\" menu to set remote program path!");
						return;
					}
					String command = (String) filePathOb + " " + comp;
					try {
						Runtime.getRuntime().exec(command);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					//filePath.setText((String) filePathStr);

				}
			}
		};

		return adapter;
	}
}
