package org.shirdrn.document.processor.config;

public interface ContextWriteable extends ContextReadable {

	void set(String key, String value);	
	void setByte(String key, byte value);
	void setShort(String key, short value);
	void setInt(String key, int value);	
	void setLong(String key, long value);	
	void setFloat(String key, float value);
	void setDouble(String key, double value);	
	void setBoolean(String key, boolean value);
	void setObject(String key, Object value);
	
}
