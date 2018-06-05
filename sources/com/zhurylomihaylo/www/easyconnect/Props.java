package com.zhurylomihaylo.www.easyconnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

class Props {
	static Properties props;
	static private String fileName = "program.properties";
	
	static void init() {
		props = new Properties();
		File file = new File(fileName);
		if (file.exists()) {
			try(InputStream in = new FileInputStream(fileName)) {
				props.load(in);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	static Object get(Object key) {
		return  props.get(key);
	}

	static void put(Object key, Object value) {
		props.put(key, value);
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
