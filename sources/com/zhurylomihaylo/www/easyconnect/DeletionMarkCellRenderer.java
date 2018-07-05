package com.zhurylomihaylo.www.easyconnect;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class DeletionMarkCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4,
			int arg5) {
		// TODO Auto-generated method stub
		JLabel cell = (JLabel) super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
		
		boolean value = (boolean) arg1;
		if (value) {
			cell.setIcon(new ImageIcon(MainFrame.class.getResource("/images/Deletion mark.png")));
			cell.setHorizontalAlignment(SwingConstants.CENTER);
		}
		else
			cell.setIcon(null);
		cell.setText(null);
		return cell;
	}

}
