package com.zhurylomihaylo.www.easyconnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

class Props {
	static private String fileName;	
	static Properties props;
	
	static {
		fileName = "program.properties";
		
		props = new Properties();
		//default values
		props.setProperty("jdbc.drivers", "org.h2.Driver");
		props.setProperty("jdbc.url", "jdbc:h2:./database;IGNORECASE=TRUE");
	}
	
	static void init() {
		File file = new File(fileName);
		if (file.exists()) {
			Properties fileProps = new Properties();
			try(InputStream in = new FileInputStream(fileName)) {
				fileProps.load(in);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			for (Map.Entry<Object, Object> e : props.entrySet()) {
				  String key = (String) e.getKey();
				  if (fileProps.getProperty(key) == null)
					  fileProps.setProperty(key, (String) e.getValue());
				}
		    
			props = fileProps;
		}
		
	}
	
	static String get(String key) {
		return  props.getProperty(key);
	}

	static void set(String key, String value) {
		props.setProperty(key, value);
	}
	
	static void store() {
		try(OutputStream out = new FileOutputStream(fileName)) {
			props.store(out, fileName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
