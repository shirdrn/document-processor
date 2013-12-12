package org.shirdrn.document.processor.config;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Configuration implements ContextReadable, ContextWriteable {

	private static final Log LOG = LogFactory.getLog(Configuration.class);
	private static final String DEFAULT_CONFIG = "config.properties";
	private String prop;
	private final Properties properties = new Properties();
	
	public Configuration(String config) {
		super();
		this.prop = config;
		load();
	}
	
	public Configuration() {
		super();
		this.prop = DEFAULT_CONFIG;
		load();
	}
	
	private void load() {
		LOG.info("Load properties file: prop=" + prop);
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(prop);
			if(in != null) {
				properties.load(in);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}
	
	@Override
	public String get(String key, String defaultValue) {
		String value = defaultValue;
		String v = properties.getProperty(key);
		if(v != null) {
			value = v;
		}
		return value;
	}
	
	@Override
	public byte getByte(String key, byte defaultValue) {
		byte value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Byte.parseByte(v);
		} catch (Exception e) { }
		return value;
	}

	@Override
	public short getShort(String key, short defaultValue) {
		short value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Short.parseShort(v);
		} catch (Exception e) { }
		return value;
	}
	
	@Override
	public int getInt(String key, int defaultValue) {
		int value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Integer.parseInt(v);
		} catch (Exception e) { }
		return value;
	}
	
	@Override
	public long getLong(String key, long defaultValue) {
		long value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Long.parseLong(v);
		} catch (Exception e) { }
		return value;
	}
	
	@Override
	public float getFloat(String key, float defaultValue) {
		float value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Float.parseFloat(v);
		} catch (Exception e) { }
		return value;
	}
	
	@Override
	public double getDouble(String key, double defaultValue) {
		double value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Double.parseDouble(v);
		} catch (Exception e) { }
		return value;
	}
	
	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Boolean.parseBoolean(v);
		} catch (Exception e) { }
		return value;
	}

	@Override
	public Object getObject(String key, Object defaultValue) {
		Object value = defaultValue;
		String v = properties.getProperty(key);
		if(v == null) {
			value = v;
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String key, Class<T> clazz) {
		return (T) properties.get(key);
	}
	

	@Override
	public void set(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public void setByte(String key, byte value) {
		properties.put(key, Byte.toString(value));		
	}

	@Override
	public void setShort(String key, short value) {
		properties.put(key, Short.toString(value));		
	}

	@Override
	public void setInt(String key, int value) {
		properties.put(key, Integer.toString(value));		
	}

	@Override
	public void setLong(String key, long value) {
		properties.put(key, Long.toString(value));		
	}

	@Override
	public void setFloat(String key, float value) {
		properties.put(key, Float.toString(value));		
	}

	@Override
	public void setDouble(String key, double value) {
		properties.put(key, Double.toString(value));		
	}

	@Override
	public void setBoolean(String key, boolean value) {
		properties.put(key, Boolean.toString(value));		
	}

	@Override
	public void setObject(String key, Object value) {
		properties.put(key, value);		
	}
	
	@Override
	public boolean equals(Object obj) {
		Configuration other = (Configuration) obj;
		return other.prop.equals(this.prop);
	}

}
