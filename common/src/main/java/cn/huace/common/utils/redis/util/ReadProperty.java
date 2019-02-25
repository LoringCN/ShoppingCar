package cn.huace.common.utils.redis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperty {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReadProperty.class);

	private static ReadProperty dbconfig = new ReadProperty();

	private Properties props = new Properties();

	public static ReadProperty getInstance() {
		return dbconfig;
	}

	/**
	 * 默认的构造方法
	 */
	public ReadProperty() {

	}

	private void loadProperties() {
		InputStream in = null;
		try {
			in = ReadProperty.class
					.getResourceAsStream("/application.properties");
			props.load(in);
		} catch (Exception e) {
			LOGGER.error("load application  configuration exception", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("IO exception", e);
				}
			}
		}
	}

	private void loadProperties(String path) {
		InputStream in = null;
		try {
			in = ReadProperty.class.getResourceAsStream(path);
			props.load(in);
		} catch (Exception e) {
			LOGGER.error("load application  configuration exception", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("IO exception", e);
				}
			}
		}
	}

	public String getProperty(String propName) {
		loadProperties();
		return props.getProperty(propName);
	}

	public String getProperty(String path, String propName) {
		loadProperties(path);
		return props.getProperty(propName);
	}
}