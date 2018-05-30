package com.zhurylomihaylo.www.easyconnect;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JFrame;

class MainFrame extends JFrame {
	private Properties props;
	private Connection conn;	

	public MainFrame() {
         readDatabaseProperties();
         initConnection();
	}

	private void initConnection() {
	     try
	      {
	         conn = getConnection();
	      }
	      catch (SQLException ex)
	      {
	         for (Throwable t : ex)
	            t.printStackTrace();
	      }

	      addWindowListener(new WindowAdapter()
	         {
	            public void windowClosing(WindowEvent event)
	            {
	               try
	               {
	                  if (conn != null) conn.close();
	               }
	               catch (SQLException ex)
	               {
	                  for (Throwable t : ex)
	                     t.printStackTrace();
	               }               
	            }
	         });
		
	}
	
	private void readDatabaseProperties()  {
		props = new Properties();

		Path path = Paths.get("database.properties");
		if (Files.exists(path) && Files.isRegularFile(path)) {
			try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
				props.load(in);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		} else {
			props.setProperty("jdbc.drivers", "org.h2.Driver");
			props.setProperty("jdbc.url", "jdbc:h2:./database");
		}
		String drivers = props.getProperty("jdbc.drivers");
		if (drivers != null)
			System.setProperty("jdbc.drivers", drivers);
	}

	private Connection getConnection() throws SQLException {
		String url = props.getProperty("jdbc.url");
		return DriverManager.getConnection(url);
	}

}
