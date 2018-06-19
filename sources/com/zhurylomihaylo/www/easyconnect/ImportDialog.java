package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;

import javax.management.RuntimeErrorException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.h2.expression.Rownum;
import org.h2.util.StringUtils;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.awt.event.ActionEvent;

/**
 * A sample modal dialog that displays a message and waits for the user to click
 * the OK button.
 */
class ImportDialog extends JDialog {
	MainFrame owner;
	private JTextField filePath;
	private JFileChooser fileChooser;
	private JTextArea logArea;

	public ImportDialog(MainFrame owner) {
		super(owner, "Import dialog", true);
		this.owner = owner;
		buildGUI();
	}


	private void buildGUI() {
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 90, 90, 90, 90, 0 };
		gbl_panel.rowHeights = new int[] { 20, 20, 20, 20,20, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		getContentPane().add(panel);
		
				GridBagConstraints gbc_fileLabel = new GridBagConstraints();
				gbc_fileLabel.fill = GridBagConstraints.BOTH;
				gbc_fileLabel.insets = new Insets(0, 0, 5, 5);
				gbc_fileLabel.gridx = 0;
				gbc_fileLabel.gridy = 0;
				JLabel fileLabel = new JLabel("File path (*.txt)");
				panel.add(fileLabel, gbc_fileLabel);
		filePath = new JTextField();
		GridBagConstraints gbc_filePath = new GridBagConstraints();
		gbc_filePath.gridwidth = 3;
		gbc_filePath.fill = GridBagConstraints.BOTH;
		gbc_filePath.insets = new Insets(0, 0, 5, 5);
		gbc_filePath.gridx = 1;
		gbc_filePath.gridy = 0;
		panel.add(filePath, gbc_filePath);
		
				JButton btnBrowse = new JButton("Browse...");
				btnBrowse.addActionListener(browseListener());
				GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
				gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
				gbc_btnBrowse.gridx = 4;
				gbc_btnBrowse.gridy = 0;
				panel.add(btnBrowse, gbc_btnBrowse);
		
				// OK button closes the dialog
		
				JButton btnImport = new JButton("Import");
				btnImport.addActionListener(importListener());
				
						JPanel buttonsPanel = new JPanel();
						GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
						gbc_buttonsPanel.gridwidth = 6;
						gbc_buttonsPanel.insets = new Insets(0, 0, 5, 0);
						gbc_buttonsPanel.gridx = 0;
						gbc_buttonsPanel.gridy = 1;
						panel.add(buttonsPanel, gbc_buttonsPanel);
						buttonsPanel.add(btnImport);
						
						JButton btnCancel = new JButton("Cancel");
						btnCancel.addActionListener(cancelListener());
						buttonsPanel.add(btnCancel);

//		logArea = new JTextArea();
//		logArea.setEditable(false);
//		GridBagConstraints gbc_logArea = new GridBagConstraints();
//		gbc_logArea.gridwidth = 3;
//		//gbc_logArea.gridheight = GridBagConstraints.REMAINDER;
//		gbc_logArea.gridx = 0;
//		gbc_logArea.gridy = 3;
//		getContentPane().add(logArea, BorderLayout.SOUTH);
		
		pack();
	}

	private void restoreValues() {
		Object filePathStr = Props.get("filePath");
		if (filePathStr != null)
			filePath.setText((String) filePathStr);		
	} 
	
	private void storeValues() {
		Props.put("filePath", filePath.getText());
	}
	
	private ActionListener browseListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter("txt File","txt");
					fileChooser.setFileFilter(filter);
				}
				
				String filePathStr = filePath.getText();
				if (filePathStr != null && !filePathStr.equals("")) { 
					File file = new File(filePathStr);
					if (file.exists()) {
						fileChooser.setCurrentDirectory(file);
					}
				};
				int result = fileChooser.showOpenDialog(ImportDialog.this);
				if (result == JFileChooser.APPROVE_OPTION)
					filePath.setText(fileChooser.getSelectedFile().getPath());
			}
		};		
	}

	private ActionListener importListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean reject = false;
				String fileName = "";
				
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
					fileName = file.getName().replaceFirst(".txt", "").replaceFirst(".TXT", "");
				}

				if (reject)
					return;
				
				String user = "", comp = "";
				String regex = "///";
				HashSet<String> uniPairs = new HashSet<>();
				
				try(Stream<String> lines = Files.lines(Paths.get(pathString), Charset.forName("UTF-8"))){
					Iterator<String> iter = lines.iterator();
					int rowNumber = 0;
					while (iter.hasNext()) {
						rowNumber++;
						if (rowNumber < 3) continue;
						
						String line = iter.next();
						if (rowNumber % 2 == 1) {
							user = line;
							comp = "";
						} else {
							comp = line;
							uniPairs.add(user + regex + comp);
						}
						
					}
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				};

				String[] splRes;
				for (String pair: uniPairs) {
					splRes = pair.split(regex);
					if (splRes.length == 2)
						importRow(splRes[0], splRes[1], fileName);
				}
				
				owner.getDataTableModel().fireTableDataChanged();
				ImportDialog.this.setVisible(false);
			}
		};
	}

	private void importRow(String user, String comp, String fileName) {
		if (StringUtils.isNullOrEmpty(user) || StringUtils.isNullOrEmpty(comp)) return;

		if (user.equals("Администратор") || user.equals("Внешний аудитор") || user.equals("Монитор клиентам"))
			return;
		if (comp.equals("A-GANDRABUR") || comp.equals("ZHURYLO") || comp.equals("S-KURASH"))
			return;
		
		try {
			InetAddress address = InetAddress.getByName(comp);
			System.out.println("IP-address for " + comp + " is " + address);
		} catch (UnknownHostException e1) {
			System.out.println("Cannot resolve IP-address for " + comp);
			comp = comp + ".megapolis.local";
			try {
				InetAddress address = InetAddress.getByName(comp);
				System.out.println("IP-address for " + comp + " is " + address);
			} catch (UnknownHostException e) {
				System.out.println("Cannot resolve IP-address for " + comp);
				return;
			}
		}

		Connection conn = DBComm.getConnection();
		
		String cmdSel = "SELECT * FROM MainTable WHERE Person = ? AND Comp = ?";
		String cmdUpd = "INSERT INTO MainTable (Person, Comp, Orgs) VALUES (?, ?, ?)";
		
		try (PreparedStatement statSel = conn.prepareStatement(cmdSel, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
				PreparedStatement statIns = conn.prepareStatement(cmdUpd, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
			statSel.setString(1, user);
			statSel.setString(2, comp);
			ResultSet rs = statSel.executeQuery();
			if (rs.next()) {
				String orgs = rs.getString("Orgs");
				if (!orgs.contains(fileName)) {
					orgs = orgs + ", " + fileName;
					rs.updateString("Orgs", orgs);
					rs.updateRow();
				}
			} else {
				statIns.setString(1, user);
				statIns.setString(2, comp);
				statIns.setString(3, fileName);
				
				statIns.executeUpdate();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
	}
	
	private ActionListener cancelListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDialog.this.setVisible(false);
			}
		};
	}


	@Override
	public void setVisible(boolean b) {
		if (b)
			restoreValues();
		else
			storeValues();
		super.setVisible(b);
	}	
	 
}
