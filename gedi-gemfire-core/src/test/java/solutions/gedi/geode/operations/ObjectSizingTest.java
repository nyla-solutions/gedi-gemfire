package solutions.gedi.geode.operations;

import org.junit.Before;
import org.junit.Test;

import gedi.solutions.geode.operations.ObjectSizing;
import nyla.solutions.core.security.user.data.UserProfile;
import nyla.solutions.core.util.Debugger;

public class ObjectSizingTest
{

	public ObjectSizingTest()
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testSizeObject()
	{
		UserProfile userProfile= new UserProfile();
		
		long size = ObjectSizing.sizeObjectBytes(userProfile);
		
		Debugger.println("size:"+size);
	}


}
