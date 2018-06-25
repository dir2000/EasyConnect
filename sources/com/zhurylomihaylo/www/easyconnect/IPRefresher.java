package com.zhurylomihaylo.www.easyconnect;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


class IPRefresher implements Runnable {
	Connection conn;
	
	@Override
	protected void finalize() throws Throwable {
		if (!conn.isClosed())
			conn.close();
	}

	@Override
	public void run() {
		try {
			conn = DBComm.createConnection();
			String cmdSel = "SELECT * FROM MainTable WHERE IP_Check_Date IS NULL OR IP_Check_Date < ?";			
			PreparedStatement statSel = conn.prepareStatement(cmdSel, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Date currDate = new Date(new java.util.Date().getTime());
			statSel.setDate(1, currDate);
			ResultSet rs = statSel.executeQuery();
			while (rs.next()) {
				String comp = rs.getString("Comp");
				Pair<String> compInfo = GeneralPurpose.getIP(comp, false);				
				String ip = compInfo.getSecond();
				if (ip != null) {
					rs.updateString("IP", ip);
					rs.updateDate("IP_UPDATE_DATE", currDate);
				}
				rs.updateDate("IP_CHECK_DATE", currDate);					
				rs.updateRow();				
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
