package com.zhurylomihaylo.www.easyconnect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

class MainFrame extends JFrame {
	JTextField searchF;
	JButton impButt;
	JDialog impDialog;
	
	public MainFrame() {
		DBComm.init(this);
		buildGUI();
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
