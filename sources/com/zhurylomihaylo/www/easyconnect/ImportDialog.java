package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

/**
 * A sample modal dialog that displays a message and waits for the user to click
 * the OK button.
 */
class ImportDialog extends JDialog {
	private JTextField orgName;
	private JTextField filePath;
	private JFileChooser fileChoo;

	public ImportDialog(JFrame owner) {
		super(owner, "Import dialog", true);
		buildGUI();
		restoreValues();
	}


	private void buildGUI() {
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 90, 90, 90, 90, 0 };
		gbl_panel.rowHeights = new int[] { 20, 20, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
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

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(browseListener());
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.gridx = 4;
		gbc_btnBrowse.gridy = 1;
		panel.add(btnBrowse, gbc_btnBrowse);

		// OK button closes the dialog

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(importListener());

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(btnImport);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(closeListener());
		buttonsPanel.add(btnClose);

		pack();
	}

	private void restoreValues() {
		String orgNameStr = (String) Props.get("orgName");
		if (orgNameStr != null)
			orgName.setText(orgNameStr);
			
		String filePathStr = (String) Props.get("filePath");
		if (filePathStr != null)
			filePath.setText(filePathStr);		
	} 
	
	private void storeValues() {
		Props.put("orgName", orgName.getText());
		Props.put("filePath", filePath.getText());
	}
	
	private ActionListener browseListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileChoo == null)
					fileChoo = new JFileChooser();
				int result = fileChoo.showOpenDialog(ImportDialog.this);
				if (result == JFileChooser.APPROVE_OPTION)
					filePath.setText(fileChoo.getSelectedFile().getPath());
			}
		};		
	}

	private ActionListener importListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean reject = false;

				String orgString = orgName.getText();
				if (orgString.equals(null) || orgString.equals("")) {
					JOptionPane.showMessageDialog(ImportDialog.this, "Organisation name is empty");
					reject = true;
				}

				String pathString = filePath.getText();
				if (pathString.equals(null) || pathString.equals("")) {
					JOptionPane.showMessageDialog(ImportDialog.this, "File path is empty");
					reject = true;
				} else {
					File file = new File(pathString);
					if (!file.exists()) {
						JOptionPane.showMessageDialog(ImportDialog.this, "File " + pathString + " does not exist");
						reject = true;
					}
				}
				
				

				if (reject)
					return;

				ImportDialog.this.setVisible(false);
			}
		};
	}

	private ActionListener closeListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ImportDialog.this.setVisible(false);
			}
		};
	}


	@Override
	public void setVisible(boolean b) {
		storeValues();
		super.setVisible(b);
	}
	
	 
}
