/**
 * 
 */
package common.launchsetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Config {

	public String platform;
	private final static String appenderPath = "/src/main/resources/properties/";
	private static Config config;
	private static File file;
	private static Properties prop;

	private Config(String platform) {
		this.platform = Platform.valueOf(platform).toString();
		file = new File(System.getProperty("user.dir") + appenderPath + platform + ".properties");

	}

	public Config() {
		// TODO Auto-generated constructor stub
	}

	public static Config getInstance(String platform) throws IOException {
		config = new Config(platform);
		return config;
	}

	public static Config getInstance() {
		return config;
	}

	public static String fetchConfigProperty(String property) {
		InputStream inputStream = null;
		String result = null;
		try {
			inputStream = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(inputStream);
			result = prop.getProperty(property);
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		finally {
			try {
				inputStream.close();
			} catch (IOException | NullPointerException e) {
				// e.printStackTrace();
			}
		}
		return result;

	}

	public String fetchConfig(File file, String property) {

		String result = null;
		InputStream inputStream = null;

		try {
			Properties prop = new Properties();
			// File file = new File(System.getProperty("user.dir") +
			// appenderPath + fileName + ".properties");
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
			result = prop.getProperty(property);
		} catch (NoSuchFileException nsfe) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void writePropertiesFile(String file, String key, String value) {
		FileInputStream in;
		Properties prop = new Properties();
		try {
			in = new FileInputStream(file);
			System.out.println("Found file " + file + " keys and values " + key + " " + value);
			prop.load(in);
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			// set the properties value
			prop.setProperty(key, value);
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String fetchEmailDetails(String property) {
		InputStream inputStream = null;
		String result = null;
		try {
			Properties prop = new Properties();
			File file = new File(System.getProperty("user.dir") + appenderPath + "emailDetails.properties");
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
			Date time = new Date(System.currentTimeMillis());
			result = prop.getProperty(property);
			System.out.println("Config, " + property + " = " + result + ", fetched @ " + time + " from EmailDetails.");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String fetchForReporting(String property) {
		InputStream inputStream = null;
		String result = null;
		try {
			Properties prop = new Properties();
			File file = new File(System.getProperty("user.dir") + "/maven.properties");
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
			Date time = new Date(System.currentTimeMillis());
			result = prop.getProperty(property);
			System.out.println("Config, " + property + " = " + result + ", fetched @ " + time + " from EmailDetails.");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static Map<String, String> fetchMatchingProperty(String pattern) {
		InputStream inputStream = null;
		Map<String, String> keyValueMap = new LinkedHashMap<String, String>();
		try {
			Properties properties = new Properties();
			inputStream = new FileInputStream(file);
			properties.load(inputStream);
			Enumeration<?> e = properties.propertyNames();
			while (e.hasMoreElements()) {
				String prop = (String) e.nextElement();
				if (!prop.startsWith(pattern))
					continue;
				String val = properties.getProperty(prop);
				if (val.equals("<NULL>"))
					val = "";
				if (prop.equalsIgnoreCase("cap_app")) {
					String commonPath = System.getProperty("user.dir") + "\\src\\main\\resources\\app\\";
					if (BaseTest.platform.contains("ios"))
						val = commonPath + "ios/" + properties.getProperty(prop);
					else if (BaseTest.platform.contains("android"))
						val = commonPath + "android\\" + properties.getProperty(prop);

				}
				keyValueMap.put(prop.replace(pattern + "_", ""), val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyValueMap;
	}

	public static Map<String, String> getAllKeys(String fileName) {
		Map<String, String> keyValueMap = new LinkedHashMap<>();

		Set<Object> keys;
		try {
			prop = new Properties();
			InputStream is = new FileInputStream(
					new File(System.getProperty("user.dir") + appenderPath + fileName + ".properties"));
			prop.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		keys = prop != null ? prop.keySet() : new HashSet<Object>();
		keys.forEach(key -> {
			String tempKey = (String) key;
			keyValueMap.put(tempKey, prop.getProperty(tempKey));
		});
		return keyValueMap;
	}

	public static File getFile() {
		return file;
	}

	public static void deletePropertiesFile(String fileName, String key) {
		Properties properties = new Properties();
		InputStream reader;
		try {
			reader = new FileInputStream(fileName);

			properties.load(reader);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		properties.remove(key);
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			// save properties to project root folder
			properties.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
