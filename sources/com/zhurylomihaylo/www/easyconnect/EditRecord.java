package com.zhurylomihaylo.www.easyconnect;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

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
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class EditRecord extends JDialog {
	MainFrame owner;
	private int id;
	private JTextField tfPerson;
	private JTextField tfComp;
	Date ip_Update_Date;	
	Date ip_Check_Date;
	private JTextField tfOrgs;
	private JFormattedTextField tfIP;

	EditRecord(MainFrame owner) {
		super(owner, Messages.getString("EditRecord.InsertNewRecord"), true); //$NON-NLS-1$
		this.owner = owner;
		buildGUI();
	}

	private ActionListener okListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String errText = Messages.getString("EditRecord.EmptyString"); //$NON-NLS-1$
				String person = tfPerson.getText();
				if (person ==  null || person.equals(Messages.getString("EditRecord.EmptyString"))) { //$NON-NLS-1$
					errText = errText + Messages.getString("EditRecord.ThePersonFieldIsEmpty"); //$NON-NLS-1$
				} else {
					person = person.trim();
				}
				String comp = tfComp.getText();
				if (comp ==  null || comp.equals(Messages.getString("EditRecord.EmptyString"))) { //$NON-NLS-1$
					if (!errText.equals(Messages.getString("EditRecord.EmptyString"))) //$NON-NLS-1$
						errText = errText + "\n"; //$NON-NLS-1$
					errText = errText + Messages.getString("EditRecord.TheComputerFieldIsEmpty."); //$NON-NLS-1$
				} else {
					comp = comp.trim();
				}				
				if (!errText.equals(Messages.getString("EditRecord.EmptyString"))) { //$NON-NLS-1$
					JOptionPane.showMessageDialog(EditRecord.this, errText, Messages.getString("EditRecord.Check"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					return;
				}
				
				String ip = tfIP.getText().trim();
				String orgs = tfOrgs.getText().trim();
				
				Connection conn = DBComm.getConnection();	
				String query;
				if (id == 0) {
					int exId = getAnExistingId(person, comp, conn);
					if (exId != -1) {
						JOptionPane.showMessageDialog(
								EditRecord.this, Messages.getString("EditRecord.TheDatabaseRecordWithThisPerson") + person //$NON-NLS-1$
										+ Messages.getString("EditRecord.AndComputerName") + comp + Messages.getString("EditRecord.AlreadyExists"), //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("EditRecord.Check"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						return;
					}
					
					query = "INSERT INTO MainTable VALUES(NULL,false,?,?,?,?,?,?)"; //$NON-NLS-1$
				}
				else
					query = "UPDATE MainTable SET Person = ?, Comp = ?, IP = ?, IP_Update_Date = ?, IP_Check_Date = ?, Orgs = ? WHERE Id = ?"; //$NON-NLS-1$
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
					
					id = getAnExistingId(person, comp, conn);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				
				DataTableModel model = owner.getDataTableModel();
				model.refreshData();
				model.fireTableDataChanged();

				// https://stackoverflow.com/questions/22066387/how-to-search-an-element-in-a-jtable-java
				int idOrder = DBComm.getFieldDescription("ID").getOrder() - 1; //$NON-NLS-1$
				int convertedRow;
				for (int i = 0; i < model.getRowCount(); i++) {// For each row
					if (model.getValueAt(i, idOrder).equals(id)) {
						convertedRow = owner.getDataTable().convertRowIndexToView(i);
						
						JTable dataTable = owner.getDataTable();
						dataTable.setRowSelectionInterval(convertedRow, convertedRow);
						
						Rectangle cellRect = dataTable.getCellRect(convertedRow, 0, true);
						dataTable.scrollRectToVisible(cellRect);
						
						break;
					}
				}

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

	void populate(int id, String person, String comp, String ip, Date ip_Check_Date, Date ip_Update_Date, String orgs) {
		this.id = id;
		tfPerson.setText(person);
		tfIP.setText(ip);
		tfComp.setText(comp);
		this.ip_Check_Date = ip_Check_Date;
		this.ip_Update_Date = ip_Update_Date;
		tfOrgs.setText(orgs);
		setTitle(Messages.getString("EditRecord.UpdateRecord")); //$NON-NLS-1$
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

	private ActionListener defineComputerNameListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = tfIP.getText();
				if (ip ==  null || ip.equals(Messages.getString("EditRecord.EmptyString")) || ip.equals("   .   .   .   ")) { //$NON-NLS-1$ //$NON-NLS-2$
					JOptionPane.showMessageDialog(EditRecord.this, Messages.getString("EditRecord.TheIPAddressFieldIsEmpty")); //$NON-NLS-1$
					return;
				}
				String comp = GeneralPurpose.getComp(ip);
				if (comp == null)
					JOptionPane.showMessageDialog(EditRecord.this, Messages.getString("EditRecord.CannotResolveComputer") + ip); //$NON-NLS-1$
				else
					tfComp.setText(comp);
			}
		};
	}
	
	private int getAnExistingId(String person, String comp, Connection conn) {
		String query;
		query = "SELECT * FROM MainTable WHERE Person = ? AND Comp = ?"; //$NON-NLS-1$
		try (PreparedStatement statSel = conn.prepareStatement(query);){
			statSel.setString(1, person);
			statSel.setString(2, comp);
			try (ResultSet rs = statSel.executeQuery();) {
				if (rs.next()) {
					return rs.getInt("Id"); //$NON-NLS-1$
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return -1;
	}

	private void buildGUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
	
		JLabel lblPerson = new JLabel(Messages.getString("EditRecord.Person")); //$NON-NLS-1$
		lblPerson.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
	
		JLabel lblIpaddress = new JLabel(Messages.getString("EditRecord.IPAddress")); //$NON-NLS-1$
		lblIpaddress.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
	
		tfPerson = new JTextField();
		tfPerson.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		tfPerson.setColumns(10);
	
		JButton btnDefineComputerName = new JButton(Messages.getString("EditRecord.DefineComputerName")); //$NON-NLS-1$
		btnDefineComputerName.addActionListener(defineComputerNameListener());
	
		JLabel lblComputerName = new JLabel(Messages.getString("EditRecord.ComputerName")); //$NON-NLS-1$
		lblComputerName.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
	
		tfComp = new JTextField();
		tfComp.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		tfComp.setColumns(10);
	
		JPanel pnlButtons = new JPanel();
		
		JLabel lblOrgs = new JLabel(Messages.getString("EditRecord.Organisation")); //$NON-NLS-1$
		lblOrgs.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		
		tfOrgs = new JTextField();
		tfOrgs.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		tfOrgs.setColumns(10);
		
//		MaskFormatter mf = null;
//		try {
//			mf = new MaskFormatter("###.###.###.###");
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
		tfIP = new JFormattedTextField(new IPAddressFormatter());
		tfIP.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
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
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
										.addComponent(lblComputerName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblPerson, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblIpaddress, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
									.addGap(18))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblOrgs, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
									.addGap(26)))
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(tfOrgs, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
								.addComponent(tfComp, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
								.addComponent(btnDefineComputerName)
								.addComponent(tfPerson, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
								.addComponent(tfIP, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))))
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
	
		JButton btnOK = new JButton("OK"); //$NON-NLS-1$
		btnOK.addActionListener(okListener());
		btnOK.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		pnlButtons.add(btnOK);
	
		JButton btnCancel = new JButton(Messages.getString("EditRecord.Cancel")); //$NON-NLS-1$
		btnCancel.addActionListener(cancelListener());
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		pnlButtons.add(btnCancel);
		panel.setLayout(gl_panel);
	
		pack();
	}
	
}
