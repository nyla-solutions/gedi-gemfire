package solutions.gedi.geode.operations.functions;

import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;

import solutions.gedi.demo.ComplexObject;
import com.gemstone.gemfire.cache.Region;

public class ImportJsonFunctionTest
{

	public ImportJsonFunctionTest()
	{
	}
	
	@Resource
	Region<String,ComplexObject> deals;

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testExecute()
	{
//		Assert.assertNotNull(deals);
//			
//		int count = 20;
//		
//		for (int i = 0; i < count; i++)
//		{
//			
//			SimpleObject simpleObject = new SimpleObject();
//			simpleObject.setFieldInt(i);
//			
//			simpleObject.setFieldCalendar(Calendar.getInstance());
//			
//			simpleObject.setFieldDate(simpleObject.getFieldCalendar().getTime());
//			simpleObject.setFieldDoubleObject(new Double(i));
//			simpleObject.setFieldDouble(simpleObject.getFieldDoubleObject().doubleValue());
//			simpleObject.setSetWithNoGet(true);
//			
//			
//			ComplexObject complexObject = new ComplexObject();
//			complexObject.setSimpleObject(simpleObject);
//			
//			ComplexObject [] complexArray = {CopyHelper.copy(complexObject),CopyHelper.copy(complexObject)};
//			//test recursion
//			complexObject.setComplexArray(complexArray);
//			complexObject.setComplexColleciton(Arrays.asList(complexArray));
//			
//			deals.put("obj"+i, complexObject);
//				
//		}
//		
//		int regionCount = deals.keySetOnServer().size();
//		
//		Assert.assertTrue(regionCount > 0);
//		
//		
//		//export records
//		Execution exe = FunctionService.onRegion(deals);
//		
//		exe.execute("ExportJsonFunction");
//		
//		
//		//delete all region records
//		
//		deals.clear();
//		
//		Assert.assertTrue(deals.isEmpty() && deals.keySetOnServer().size() == 0);
//		
//		//import 
//		exe.execute("ImportJsonFunction");
//		
//		Assert.assertEquals(regionCount,deals.keySetOnServer().size());
		
		
	}

}
