package gedi.solutions.geode.client;

import org.apache.geode.cache.Region;
import org.junit.Ignore;
import org.junit.Test;

import nyla.solutions.core.patterns.jmx.JMX;

@Ignore
public class OOMTest
{

	@Test
	public void testGetRegion()
	{
		//52.54.153.210 11099
		//JMX jmx = JMX.connect("localhost", 1099);
		JMX jmx = JMX.connect("localhost", 10099);
		
		Region<Long,String> region = GemFireJmxClient.getRegion("BillingProfile", jmx);
		
		String fixed = "0123456789";
		
		while(true)
		{
			for (long i = 0; i < Long.MAX_VALUE; i++)
			{
				String value = region.get(i);
				if(value == null)
					region.put(i, fixed);
				else
					region.put(i, value+value);
			}
		}
		
	}

}
