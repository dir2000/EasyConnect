package com.zhurylomihaylo.www.easyconnect;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.text.ParseException;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.MaskFormatter;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class EditRecord extends JDialog {
	MainFrame owner;
	private int id;
	private JTextField tfPerson;
	private JTextField tfIP;
	private JTextField tfComp;
	Date ip_Update_Date;	
	Date ip_Check_Date;
	private JTextField tfOrgs;

	EditRecord(MainFrame owner) {
		super(owner, "Edit record", true);
		this.owner = owner;
		buildGUI();
	}

	private ActionListener okListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String errText = "";
				String person = tfPerson.getText();
				if (person ==  null || person.equals("")) {
					errText = errText + "The person field is empty.";
				}
				String comp = tfComp.getText();
				if (comp ==  null || comp.equals("")) {
					if (!errText.equals(""))
						errText = errText + "\n";
					errText = errText + "The computer field is empty.";
				}				
				if (!errText.equals("")) {
					JOptionPane.showMessageDialog(EditRecord.this, errText, "Check", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String ip = tfIP.getText();
				String orgs = tfOrgs.getText();
				
				Connection conn = DBComm.getConnection();	
				String query;
				if (id == 0)
					query = "INSERT INTO MainTable VALUES(NULL,?,?,?,?,?,?)";
				else
					query = "UPDATE MainTable SET Person = ?, Comp = ?, IP = ?, IP_Update_Date = ?, IP_Check_Date = ?, Orgs = ? WHERE Id = ?";
				try (PreparedStatement st = conn.prepareStatement(query);) {
					st.setString(1, person);
					st.setString(2, comp);
					st.setString(3, ip);
					st.setDate(4, ip_Update_Date);
					st.setDate(5, ip_Check_Date);
					st.setString(6, orgs);
					if (id != 0)
						st.setInt(7, id);
					
					st.executeUpdate();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				
				owner.getDataTableModel().refreshData();
				owner.getDataTableModel().fireTableDataChanged();
				
				EditRecord.this.setVisible(false);
			}
		};
	}

	private ActionListener cancelListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditRecord.this.setVisible(false);
			}
		};
	}

	void populate(int id, String person, String ip, String comp, Date ip_Check_Date, Date ip_Update_Date, String orgs) {
		this.id = id;
		tfPerson.setText(person);
		tfIP.setText(ip);
		tfComp.setText(comp);
		this.ip_Check_Date = ip_Check_Date;
		this.ip_Update_Date = ip_Update_Date;
		tfOrgs.setText(orgs);
	}

	@Override
	public void setVisible(boolean b) {
		if (b == false) {
			id = 0;
			tfPerson.setText(null);
			tfIP.setText(null);
			tfComp.setText(null);
			ip_Check_Date = new Date(0);
			ip_Update_Date = new Date(0);
			tfOrgs.setText(null);
		}
		super.setVisible(b);
	}

	private void buildGUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
	
		JLabel lblPerson = new JLabel("Person");
		lblPerson.setFont(new Font("Tahoma", Font.PLAIN, 16));
	
		JLabel lblIpaddress = new JLabel("IP-Address");
		lblIpaddress.setFont(new Font("Tahoma", Font.PLAIN, 16));
	
		tfPerson = new JTextField();
		tfPerson.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tfPerson.setColumns(10);
	
		tfIP = new JTextField();
		tfIP.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tfIP.setColumns(10);
	
		JButton btnDefineComputerName = new JButton("Define computer name");
	
		JLabel lblComputerName = new JLabel("Computer name");
		lblComputerName.setFont(new Font("Tahoma", Font.PLAIN, 16));
	
		tfComp = new JTextField();
		tfComp.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tfComp.setColumns(10);
	
		JPanel pnlButtons = new JPanel();
		
		JLabel lblOrgs = new JLabel("Organisations");
		lblOrgs.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		tfOrgs = new JTextField();
		tfOrgs.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tfOrgs.setColumns(10);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(pnlButtons, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(lblComputerName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(Alignment.LEADING, gl_panel.createParallelGroup(Alignment.TRAILING, false)
											.addComponent(lblPerson, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(lblIpaddress, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)))
									.addGap(18))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblOrgs, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGap(26)))
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(tfOrgs, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
								.addComponent(tfComp, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
								.addComponent(btnDefineComputerName)
								.addComponent(tfIP, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
								.addComponent(tfPerson, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPerson)
						.addComponent(tfPerson, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIpaddress)
						.addComponent(tfIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnDefineComputerName)
					.addGap(11)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComputerName)
						.addComponent(tfComp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOrgs)
						.addComponent(tfOrgs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
					.addComponent(pnlButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
	
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(okListener());
		btnOK.setFont(new Font("Tahoma", Font.PLAIN, 16));
		pnlButtons.add(btnOK);
	
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(cancelListener());
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		pnlButtons.add(btnCancel);
		panel.setLayout(gl_panel);
	
		pack();
	}
}
