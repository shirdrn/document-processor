package org.shirdrn.document.processor.utils;


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
}
