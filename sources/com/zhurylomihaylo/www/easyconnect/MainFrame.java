package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

class MainFrame extends JFrame {
	//private JTextField searchF;
	private JMenuItem mntmImport;
	private JButton impButt;
	private JDialog impDialog;
	private JTable dataTable;
	private JMenuItem mntmExit;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOptions;

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
		mntmOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		menuBar.add(mntmOptions);
		mntmExit.addActionListener(exitListener());
		mntmImport.addActionListener(importListener());
		actionOnClose();
		
		dataTable = new JTable(new DataTableModel());
		getContentPane().add(new JScrollPane(dataTable), BorderLayout.CENTER);
		
		pack();
	}
	
	private ActionListener importListener() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 if (impDialog == null) // first time
					 impDialog = new ImportDialog(MainFrame.this);
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
				 if (impDialog == null) // first time
					 impDialog = new ImportDialog(MainFrame.this);
				 impDialog.setVisible(true); // pop up dialog
			}
		};
		return listener;
	}	
}
