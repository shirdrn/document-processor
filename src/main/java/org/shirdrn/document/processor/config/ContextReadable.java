package org.shirdrn.document.processor.config;

public interface ContextReadable {

	String get(String key);	
	String get(String key, String defaultValue);
	byte getByte(String key, byte defaultValue);
	short getShort(String key, short defaultValue);
	int getInt(String key, int defaultValue);	
	long getLong(String key, long defaultValue);
	float getFloat(String key, float defaultValue);	
	double getDouble(String key, double defaultValue);	
	boolean getBoolean(String key, boolean defaultValue);
	Object getObject(String key, Object defaultValue);
	<T> T getObject(String key, Class<T> clazz);
	
}
