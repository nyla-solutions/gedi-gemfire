package gedi.solutions.geode.client;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ClientSetting
{
	/**
	 * SYS_PROPERTY="gedi.properties"
	 */
	public static final String SYS_PROPERTY = "config.properties";

	/**
	 * RESOURCE_BUNDLE_NAME = "config"
	 */
	public static final String RESOURCE_BUNDLE_NAME = "config";

	private ClientSetting()
	{
		loadProperties();
	}// ------------------------------------------------

	public static ClientSetting getInstance()
	{
		if (instance == null)
		{
			instance = new ClientSetting();
		}

		return instance;
	}// ------------------------------------------------

	/**
	 * 
	 * @param property
	 * @return results
	 */
	public final String getProperty(String property)
	{
		// get from system
		String results = System.getProperty(property);

		if (results == null)
		{
			try
			{
				// check resource
				results = this.properties.getProperty(property);
			}
			catch (MissingResourceException e)
			{
			}
		}

		if (results == null || results.length() == 0)
			throw new IllegalStateException("Missing property \"" + property
					+ "\" in config.properties");

		return results;
	}// ------------------------------------------------

	public final String getProperty(String property, String defaultValue)
	{
		if (property == null)
			return defaultValue;

		// get from system
		String results = System.getProperty(property);

		if (results == null || results.length() == 0)
		{
			try
			{
				// check resource
				results = this.properties.getProperty(property);
			}
			catch (MissingResourceException e)
			{
			}
		}

		if (results == null || results.length() == 0)
			return defaultValue;

		return results;
	}// ------------------------------------------------

	/**
	 * Get an boolean property from config.properties resource bundle
	 * 
	 * @param property the property name
	 * @param defaultValue the default value if the property does not exist
	 * @return the resource or default value
	 */
	public final boolean getPropertyBoolean(String property,
			boolean defaultValue)
	{
		// get from system
		String results = System.getProperty(property);

		if (results == null || results.length() == 0)
		{
			try
			{
				// check resource
				results = this.properties.getProperty(property);
			}
			catch (MissingResourceException e)
			{
			}
		}

		if (results == null || results.length() == 0)
			return Boolean.valueOf(defaultValue).booleanValue();

		return Boolean.valueOf(results).booleanValue();
	}// ------------------------------------------------

	/**
	 * Get an integer property from config.properties resource bundle
	 * 
	 * @param property the property name
	 * @param defaultValue the default value if the property does not exist
	 * @return the resource or default value
	 */
	public final int getPropertyInteger(String property, int defaultValue)
	{
		// get from system
		String results = System.getProperty(property);

		try
		{
			if (results == null || results.length() == 0)
			{
				try
				{
					// check resource
					results = this.properties.getProperty(property);

				}
				catch (MissingResourceException e)
				{
				}
			}

		}
		catch (MissingResourceException e)
		{
		}

		if (results == null || results.length() == 0)
			return Integer.valueOf(defaultValue).intValue();

		return Integer.valueOf(results).intValue();
	}// ------------------------------------------------

	/**
	 * Load the configuration properties from the properties file.
	 * <p/>
	 * <p/>
	 * <p/>
	 * Caller must test to ensure that properties is Non-null.
	 * 
	 * @throws IllegalArgumentException Translates an IOException from reading
	 *             <p/>
	 *             the properties file into a run time exception.
	 */

	private synchronized void loadProperties()
	{
		// If multiple threads are waiting to invoke this method only allow
		// the first one to do so. The rest should just return since the first
		// thread through took care of loading the properties.
		try
		{
			String file = getSystemPropertyFile();
			if (file != null && file.length() > 0)
			{
				System.out.println("CONFIG: LOADING CONFIG properties  from "
						+ file);
				FileInputStream fis = new FileInputStream(file);

				try
				{
					properties = new Properties();
					// Load the properties object from the properties file
					properties.load(fis);
					System.out
							.println("CONFIG: FINISHED LOADING CONFIG properties  from "
									+ file);

				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e.toString());
				}
				finally
				{
					if (fis != null)
						fis.close(); // Always close the file, even on exception

				}

			}
			else
			{

				// try to get properties from resource bundle

				ResourceBundle rb = ResourceBundle
						.getBundle(RESOURCE_BUNDLE_NAME);

				Enumeration<String> keys = rb.getKeys();

				String k = null;

				// String v = null;

				properties = new Properties();

				while (keys.hasMoreElements())
				{

					k = keys.nextElement();

					properties.put(k, rb.getString(k + ""));

				}
			}// end els load from resource bundle

		}
		catch (Exception e)
		{

			throw new RuntimeException(e.toString());

		}
	}// ------------------------------------------------------------

	/**
	 * @return the system property file
	 */

	private static String getSystemPropertyFile()
	{
		String file = System.getProperty(SYS_PROPERTY);
		return file;

	}// -----------------------------------------------------------

	/**
	 * @return the properties
	 */
	public Properties getProperties()
	{
		return properties;
	}

	// private ResourceBundle brigeResourceBundle =
	// ResourceBundle.getBundle("config");
	private Properties properties = null; // configuration properties
	private static ClientSetting instance = null;
}
