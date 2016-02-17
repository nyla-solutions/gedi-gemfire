package solutions.gedi.geode.operations;

import static org.junit.Assert.*;
import gedi.solutions.gemfire.operations.ObjectSizing;

import org.junit.Before;
import org.junit.Test;

import nyla.solutions.global.security.user.data.UserProfile;
import nyla.solutions.global.util.Debugger;

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
