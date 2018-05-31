package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;

/**
 * A sample modal dialog that displays a message and waits for the user to click
 * the OK button.
 */
class ImportDialog extends JDialog {
	private JTextField orgName;
	private JTextField filePath;
	
	public ImportDialog(JFrame owner) {
		super(owner, "Import dialog", true);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2, 0, 0));
		
		panel.add(new JLabel("Organisation name"));
		orgName = new JTextField();
		panel.add(orgName);
		
		panel.add(new JLabel("File path (*.txt)"));
		filePath = new JTextField();
		panel.add(filePath);
		
		getContentPane().add(panel);

		// OK button closes the dialog

		JButton ok = new JButton("OK");
		ok.addActionListener(event -> setVisible(false));

		// add OK button to southern border

		JPanel panel2 = new JPanel();
		panel2.add(ok);
		getContentPane().add(panel2, BorderLayout.SOUTH);

		pack();
	}
}
