package com.zhurylomihaylo.www.easyconnect;

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
import javax.swing.JTextField;

class MainFrame extends JFrame {
	JTextField searchF;
	JButton impButt;
	JDialog impDialog;

	public MainFrame() {
		Props.init();
		DBComm.init(this);
		actionOnClose();
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
		impButt = new JButton("Import");
		impButt.addActionListener(importListener());
		add(impButt);
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
}
