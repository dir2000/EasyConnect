package com.zhurylomihaylo.www.easyconnect;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import javax.management.RuntimeErrorException;
import javax.swing.JOptionPane;

class DBComm {
	private static Properties props;
	private static Connection conn;
	private static HashMap<String, FieldDescription> fieldsDescriptions;

	static void init(MainFrame frame) {
		initDriver();		
		conn = createConnection();
		checkSchema();
	}

	static Connection createConnection() {
		
		String url = Props.get("jdbc.url");
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void initDriver() {
		String drivers = Props.get("jdbc.drivers");
		System.setProperty("jdbc.drivers", drivers);
	}

	static Connection getConnection() {
		return conn;
	}

	private static void checkSchema() {
		String command = "CREATE TABLE IF NOT EXISTS MainTable"
				+ " (Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,"
				+ " Person VARCHAR_IGNORECASE(128) NOT NULL,"
				+ " Comp VARCHAR(128),"
				+ " IP VARCHAR(15),"
				+ " IP_Update_Date Date,"
				+ " IP_Check_Date Date,"
				+ " Orgs VARCHAR(256));";
		//String command2 = "ALTER TABLE MainTable DROP COLUMN IF EXISTS IP_Act_Date";
		String command3 = "ALTER TABLE MainTable ADD COLUMN IF NOT EXISTS IP_Update_Date Date AFTER IP";
		try (Statement stat = conn.createStatement();){
			stat.executeUpdate(command);
			//stat.executeUpdate(command2);
			stat.executeUpdate(command3);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		
		fieldsDescriptions = new HashMap<>();
		fieldsDescriptions.put("ID", new FieldDescription("ID", "ID", int.class, 1, false));
		fieldsDescriptions.put("PERSON", new FieldDescription("PERSON", "Person", String.class, 2, true));
		fieldsDescriptions.put("COMP", new FieldDescription("COMP", "Computer", String.class, 3, true));
		fieldsDescriptions.put("IP", new FieldDescription("IP", "IP", String.class, 4, true));		
		fieldsDescriptions.put("IP_CHECK_DATE", new FieldDescription("IP_CHECK_DATE", "IP check date", java.sql.Date.class, 5, true));
		fieldsDescriptions.put("IP_UPDATE_DATE", new FieldDescription("IP_UPDATE_DATE", "IP update date", java.sql.Date.class, 6, true));
		fieldsDescriptions.put("ORGS", new FieldDescription("ORGS", "Organisations", String.class, 7, true));
	}
	
	static FieldDescription getFieldDescription(String name) {
		return fieldsDescriptions.get(name);
	}
}
