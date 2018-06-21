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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.management.RuntimeErrorException;
import javax.swing.JOptionPane;

class DBComm {
	private static Properties props;
	private static Connection conn;

	static void init(MainFrame frame) {
		createConnection();
		checkSchema();
	}

	static private void createConnection() {
		readDatabaseProperties();
		
		String url = props.getProperty("jdbc.url");
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			for (Throwable t : ex)
				//JOptionPane.showMessageDialog(null, t.getStackTrace(), "Connection error", JOptionPane.ERROR_MESSAGE);
			//System.exit(1);
				throw new RuntimeException(ex);
		}

	}

	static private void readDatabaseProperties() {
		props = new Properties();

		Path path = Paths.get("database.properties");
		if (Files.exists(path) && Files.isReadable(path)) {
			try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
				props.load(in);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, ex.getStackTrace(), "File read error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			props.setProperty("jdbc.drivers", "org.h2.Driver");
			props.setProperty("jdbc.url", "jdbc:h2:./database");
		}
		String drivers = props.getProperty("jdbc.drivers");
		if (drivers != null)
			System.setProperty("jdbc.drivers", drivers);
	}

	static Connection getConnection() {
		return conn;
	}

	static void checkSchema() {
		String command = "CREATE TABLE IF NOT EXISTS MainTable"
				+ " (Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,"
				+ " Person VARCHAR_IGNORECASE(128) NOT NULL,"
				+ " Comp VARCHAR(128),"
				+ " IP VARCHAR(15),"
				+ " IP_Check_Date Date,"
				+ " Orgs VARCHAR(256));";
		try {
			Statement stat = conn.createStatement();
			stat.executeUpdate(command);
		} catch (SQLException ex) {
			for (Throwable t : ex)
				JOptionPane.showMessageDialog(null, t.getStackTrace(), "SQL exception", JOptionPane.ERROR_MESSAGE);
			System.out.println(command);
			System.exit(1);
		}
	}
	
	static ResultSet getDataRowSet() {
		Statement statSel;
		try {
			statSel = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = statSel.executeQuery("SELECT * FROM MainTable ORDER BY Person");
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
