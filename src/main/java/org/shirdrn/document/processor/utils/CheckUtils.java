package org.shirdrn.document.processor.utils;

import java.io.File;


public class CheckUtils {

	public static <T> void checkNotNull(T object) {
		if(object == null) {
			throw new RuntimeException("Should NOT NULL!");
		}
	}
	
	public static <T> void checkNotNull(T object, String description) {
		if(object == null) {
			throw new RuntimeException(
					"Should NOT NULL: " + description);
		}
	}
	
	public static void checkFileExists(File file) {
		if(!file.exists()) {
			throw new RuntimeException("File does not exist: " + file);
		}
	}
	
	public static void checkFileExists(String file) {
		File f = new File(file);
		if(!f.exists()) {
			throw new RuntimeException("File does not exist: file=" + file);
		}
	}
}
