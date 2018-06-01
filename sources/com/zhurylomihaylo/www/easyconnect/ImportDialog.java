package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {90, 90, 90, 90, 0};
		gbl_panel.rowHeights = new int[] {20, 20, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		GridBagConstraints gbc_orgLabel = new GridBagConstraints();
		gbc_orgLabel.fill = GridBagConstraints.BOTH;
		gbc_orgLabel.insets = new Insets(0, 0, 5, 5);
		gbc_orgLabel.gridx = 0;
		gbc_orgLabel.gridy = 0;
		JLabel orgLabel = new JLabel("Organisation name");
		panel.add(orgLabel, gbc_orgLabel);
		orgName = new JTextField();
		GridBagConstraints gbc_orgName = new GridBagConstraints();
		gbc_orgName.gridwidth = 3;
		gbc_orgName.fill = GridBagConstraints.BOTH;
		gbc_orgName.insets = new Insets(0, 0, 5, 5);
		gbc_orgName.gridx = 1;
		gbc_orgName.gridy = 0;
		panel.add(orgName, gbc_orgName);
		
		getContentPane().add(panel);
		
		GridBagConstraints gbc_fileLabel = new GridBagConstraints();
		gbc_fileLabel.fill = GridBagConstraints.BOTH;
		gbc_fileLabel.insets = new Insets(0, 0, 0, 5);
		gbc_fileLabel.gridx = 0;
		gbc_fileLabel.gridy = 1;
		JLabel fileLabel = new JLabel("File path (*.txt)");
		panel.add(fileLabel, gbc_fileLabel);
		filePath = new JTextField();
		GridBagConstraints gbc_filePath = new GridBagConstraints();
		gbc_filePath.gridwidth = 3;
		gbc_filePath.fill = GridBagConstraints.BOTH;
		gbc_filePath.insets = new Insets(0, 0, 0, 5);
		gbc_filePath.gridx = 1;
		gbc_filePath.gridy = 1;
		panel.add(filePath, gbc_filePath);

		// OK button closes the dialog

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDialog.this.setVisible(false);
			}
		});

		JPanel panel2 = new JPanel();
		panel2.add(ok);
		getContentPane().add(panel2, BorderLayout.SOUTH);

		pack();
	}
}
