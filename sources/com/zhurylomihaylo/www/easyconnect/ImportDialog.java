package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.management.RuntimeErrorException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

/**
 * A sample modal dialog that displays a message and waits for the user to click
 * the OK button.
 */
class ImportDialog extends JDialog {
	MainFrame owner;
	private JTextField filePath;
	private JFileChooser fileChooser;
	private JTextArea taLogArea;
	private JCheckBox chckbxMegapolis;

	public ImportDialog(MainFrame owner) {
		super(owner, "Import dialog", true);
		this.owner = owner;
		buildGUI();
	}

	private void buildGUI() {
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 90, 90, 90, 90, 90, 0 };
		gbl_panel.rowHeights = new int[] { 20, 20, 500 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 1.0 };
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
		gbc_filePath.gridwidth = 4;
		gbc_filePath.fill = GridBagConstraints.BOTH;
		gbc_filePath.insets = new Insets(0, 0, 5, 5);
		gbc_filePath.gridx = 1;
		gbc_filePath.gridy = 0;
		panel.add(filePath, gbc_filePath);

		// OK button closes the dialog

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(importListener());

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(browseListener());
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx = 5;
		gbc_btnBrowse.gridy = 0;
		panel.add(btnBrowse, gbc_btnBrowse);

		chckbxMegapolis = new JCheckBox("Megapolis");
		GridBagConstraints gbc_chckbxMegapolis = new GridBagConstraints();
		gbc_chckbxMegapolis.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxMegapolis.gridx = 0;
		gbc_chckbxMegapolis.gridy = 1;
		panel.add(chckbxMegapolis, gbc_chckbxMegapolis);

		JPanel buttonsPanel = new JPanel();
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.gridwidth = 5;
		gbc_buttonsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_buttonsPanel.gridx = 1;
		gbc_buttonsPanel.gridy = 1;
		panel.add(buttonsPanel, gbc_buttonsPanel);
		buttonsPanel.add(btnImport);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(closeListener());
		buttonsPanel.add(btnClose);

		JPanel panelLog = new JPanel();
		GridBagConstraints gbc_panelLog = new GridBagConstraints();
		gbc_panelLog.gridwidth = 6;
		gbc_panelLog.fill = GridBagConstraints.BOTH;
		gbc_panelLog.gridx = 0;
		gbc_panelLog.gridy = 2;
		panel.add(panelLog, gbc_panelLog);
		panelLog.setLayout(new GridLayout(1, 0, 0, 0));

		taLogArea = new JTextArea();

		JScrollPane scrollPaneLog = new JScrollPane(taLogArea);
		panelLog.add(scrollPaneLog);

		pack();
	}

	private void restoreValues() {
		String filePathStr = Props.get("filePath");
		if (filePathStr != null)
			filePath.setText(filePathStr);
	}

	private void storeValues() {
		Props.set("filePath", filePath.getText());
	}

	private ActionListener browseListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter("txt File", "txt");
					fileChooser.setFileFilter(filter);
				}

				String filePathStr = filePath.getText();
				if (filePathStr != null && !filePathStr.equals("")) {
					File file = new File(filePathStr);
					if (file.exists()) {
						fileChooser.setCurrentDirectory(file);
					}
				}
				;
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
				appendLog(null);

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

				try (Stream<String> lines = Files.lines(Paths.get(pathString), Charset.forName("UTF-8"))) {
					Iterator<String> iter = lines.iterator();
					int rowNumber = 0;
					while (iter.hasNext()) {
						rowNumber++;
						if (rowNumber < 3)
							continue;

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
				}
				;

				String[] splRes;
				for (String pair : uniPairs) {
					splRes = pair.split(regex);
					if (splRes.length == 2)
						importRow(splRes[0], splRes[1], fileName);
				}

				owner.getDataTableModel().refreshData();
				owner.getDataTableModel().fireTableDataChanged();

				appendLog("Done!");
			}
		};
	}

	private void importRow(String user, String comp, String fileName) {
		if (StringUtils.isNullOrEmpty(user) || StringUtils.isNullOrEmpty(comp))
			return;

		if (user.equals("Администратор") || user.equals("Внешний аудитор")
				|| user.equals("Монитор клиентам"))
			return;
		if (comp.equals("A-GANDRABUR") || comp.equals("ZHURYLO") || comp.equals("S-KURASH")
				|| comp.equals("V-SHTEFANIUK"))
			return;

		Connection conn = DBComm.getConnection();

		String cmdSel = "SELECT * FROM MainTable WHERE Person = ? AND Comp = ?";
		String cmdUpd = "INSERT INTO MainTable (Person, Comp, IP, IP_Update_Date, IP_Check_Date, Orgs) VALUES (?, ?, ?, ?, ?, ?)";
		Date currDate = new Date(new java.util.Date().getTime());

		try (PreparedStatement statSel = conn.prepareStatement(cmdSel, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_UPDATABLE);
				PreparedStatement statIns = conn.prepareStatement(cmdUpd, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE)) {
			statSel.setString(1, user);
			statSel.setString(2, comp);
			try (ResultSet rs = statSel.executeQuery();) {
				if (rs.next()) {
					appendLog(user + " + " + comp + " = already exists!");
					String orgs = rs.getString("Orgs");
					if (!orgs.contains(fileName)) {
						orgs = orgs + ", " + fileName;
						rs.updateString("Orgs", orgs);
						rs.updateRow();
						appendLog("Organisation " + fileName + " has been added to " + user);
					}
				} else {
					Pair<String> compInfo = GeneralPurpose.getIP(comp, chckbxMegapolis.isSelected());
					String realComp = compInfo.getFirst();
					String ip = compInfo.getSecond();
					if (ip == null) {
						appendLog("Cannot resolve IP-address for " + comp);
					} else {
						appendLog("IP-address for " + realComp + " is " + ip);

						statIns.setString(1, user); // Person
						statIns.setString(2, realComp); // Comp
						statIns.setString(3, ip); // IP
						statIns.setDate(4, currDate); // IP_Update_Date
						statIns.setDate(5, currDate); // IP_Check_Date
						statIns.setString(6, fileName); // Orgs == fileName
						statIns.executeUpdate();
					}
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

	}

	private ActionListener closeListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDialog.this.setVisible(false);
			}
		};
	}

	void appendLog(String str) {
		if (str == null)
			taLogArea.setText(null);
		else
			taLogArea.append(str + "\n");
		taLogArea.update(taLogArea.getGraphics());
	}

	@Override
	public void setVisible(boolean b) {
		if (b)
			restoreValues();
		else {
			storeValues();
			taLogArea.setText(null);
		}
		super.setVisible(b);
	}

}
