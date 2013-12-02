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
	
	public static void checkFile(File file, boolean throwExceptionWhenExistence) {
		if(throwExceptionWhenExistence) {
			if(file.exists()) {
				throw new RuntimeException("File has been existed: " + file);
			}
		} else {
			if(!file.exists()) {
				throw new RuntimeException("File does not exist: " + file);
			}
		}
	}
	
	public static void checkFile(String file, boolean throwExceptionWhenExistence) {
		File f = new File(file);
		checkFile(f, throwExceptionWhenExistence);
	}
}
