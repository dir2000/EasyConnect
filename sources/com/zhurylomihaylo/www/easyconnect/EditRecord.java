package com.zhurylomihaylo.www.easyconnect;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.text.ParseException;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.MaskFormatter;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import java.awt.Dimension;

class EditRecord extends JDialog {
	MainFrame owner;	
	private JTextField tfPerson;
	private JTextField tfComputer;
	private JFormattedTextField ftfIPAddress;
	
	public EditRecord(MainFrame owner) {
		super(owner, "Edit record", true);
		this.owner = owner;		
		buildGUI();
	}
	
	private void buildGUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblPerson = new JLabel("Person");
		lblPerson.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblComputer = new JLabel("Computer");
		lblComputer.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblIpaddress = new JLabel("IP-address");
		lblIpaddress.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		tfPerson = new JTextField();
		tfPerson.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tfPerson.setColumns(10);
		
		tfComputer = new JTextField();
		tfComputer.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tfComputer.setColumns(10);
		
		JButton btnDefineComp = new JButton("Define computer name");
		btnDefineComp.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("###.###.###.###");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ftfIPAddress = new JFormattedTextField(mf);
		ftfIPAddress.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JPanel buttonPanel = new JPanel();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblIpaddress, Alignment.TRAILING)
						.addComponent(lblComputer, Alignment.TRAILING))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnDefineComp)
						.addComponent(tfPerson)
						.addComponent(ftfIPAddress)
						.addComponent(tfComputer))
					.addGap(10))
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addComponent(lblPerson)
					.addGap(436))
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(buttonPanel)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPerson)
						.addComponent(tfPerson))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(12)
							.addComponent(lblIpaddress))
						.addGroup(gl_panel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(ftfIPAddress)))
					.addGap(11)
					.addComponent(btnDefineComp)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComputer)
						.addComponent(tfComputer))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(buttonPanel)
					.addContainerGap(72, Short.MAX_VALUE))
		);
		
		JButton btnOk = new JButton("OK");
		btnOk.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonPanel.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonPanel.add(btnCancel);
		panel.setLayout(gl_panel);
		
		pack();
	}
}
