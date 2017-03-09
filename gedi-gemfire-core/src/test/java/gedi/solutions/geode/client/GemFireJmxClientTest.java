package gedi.solutions.geode.client;


import org.junit.Assert;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;

import nyla.solutions.core.patterns.jmx.JMX;

public class GemFireJmxClientTest
{

	@Test
	public void testGetRegion()
	{
		//52.54.153.210 11099
		//JMX jmx = JMX.connect("localhost", 1099);
		JMX jmx = JMX.connect("34.207.209.21", 11099);
		
		//Address, UserProfile
		Region<String,String> region = GemFireJmxClient.getRegion("Address", jmx);
		
		
		region.put("greg", "hello");
		
		Assert.assertEquals("hello",region.get("greg"));
	}

}
