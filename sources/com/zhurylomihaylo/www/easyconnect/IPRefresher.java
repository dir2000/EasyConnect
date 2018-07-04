package com.zhurylomihaylo.www.easyconnect;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class IPRefresher implements Runnable {
	@Override
	public void run() {
		String cmdSel = "SELECT * FROM MainTable WHERE IP_Check_Date IS NULL OR IP_Check_Date < ?";
		Date currDate = new Date(new java.util.Date().getTime());

		try (Connection conn = DBComm.createConnection();
				PreparedStatement statSel = conn.prepareStatement(cmdSel, ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);) {

			statSel.setDate(1, currDate);

			try (ResultSet rs = statSel.executeQuery();) {
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
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
