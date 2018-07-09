package com.zhurylomihaylo.www.easyconnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

class Props {
	static private Path filePath;
	static Properties props;

	static {
		Path dirPath = Paths.get(System.getProperty("user.home"), ".EasyConnect");
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectory(dirPath);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		filePath =  dirPath.resolve("program.properties");

		props = new Properties();
		// default values
		props.setProperty("jdbc.drivers", "org.h2.Driver");
		props.setProperty("jdbc.url", "jdbc:h2:./database;IGNORECASE=TRUE;AUTO_SERVER=TRUE");
	}

	static void init() {
		if (Files.exists(filePath)) {
			Properties fileProps = new Properties();
			try (InputStream in = new FileInputStream(filePath.toFile())) {
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
		return props.getProperty(key);
	}

	static void set(String key, String value) {
		props.setProperty(key, value);
	}

	static void store() {
		try (OutputStream out = new FileOutputStream(filePath.toFile())) {
			props.store(out, filePath.toString());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
