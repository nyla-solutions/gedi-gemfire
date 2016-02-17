package gedi.solutions.gemfire.testing.wan;

import gedi.solutions.gemfire.client.GemFireJmxClient;
import gedi.solutions.gemfire.client.SingletonGemFireJmx;
import gedi.solutions.gemfire.testing.QA;
import gedi.solutions.gemfire.wan.ClusterDiffReport;
import gedi.solutions.gemfire.wan.cli.ClusterDiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import nyla.solutions.global.io.IO;
import nyla.solutions.global.json.JacksonJSON;
import nyla.solutions.global.patterns.jmx.JMX;
import nyla.solutions.global.util.Debugger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;


/**
 * Compare Clusters test
 * @author Gregory Green
 *
 */
public class ClusterDiffTest
{

	public ClusterDiffTest()
	{
	}

	@Before
	public void setUp() throws Exception
	{
		QA.checkClusterUp();
	}

	@Test
	public void testEmpty()
	{
		String[] empty = {};
		
		ClusterDiff.main(empty);
	
	}// --------------------------------------------------------

	@Test
	public void testMain()
	{	
		
		//Put date in 
		
		
		String[] args = {QA.getPrimaryLocatorJmxHost(),
				String.valueOf(QA.getPrimaryLocatorJmxPort()),
				 QA.getRemoteLocatorHost(),String.valueOf(QA.getRemoteLocatorJmxPort())};
		
		ClusterDiff.main(args);
	}// --------------------------------------------------------

	@Test
	public void testCompare()
	throws Exception
	{	
		//String [] touchArgs = { QA.getPrimaryLocatorJmxHost(),String.valueOf(QA.getPrimaryLocatorJmxPort())};
		
		//GemTouch.processTouch(touchArgs);
		
		JMX jmx1 = JMX.connect(QA.getPrimaryLocatorJmxHost(),QA.getPrimaryLocatorJmxPort());
		JMX jmx2 = JMX.connect(QA.getRemoteLocatorHost(),QA.getRemoteLocatorJmxPort());

		GemFireJmxClient.getClientCache(jmx1);
		
		GemFireJmxClient.stopGatewaySenders(jmx1);
		GemFireJmxClient.stopGatewaySenders(jmx2);

		    
		//QAUtil.loadTestData(QA.getTestRegion(), 100);
		
		Region<Object,Object> r = GemFireJmxClient.getRegion(QA.getTestRegion(),jmx1);
		Assert.assertNotNull(r);
		
		//String id =  UUID.randomUUID().toString();
		PdxInstance pdxJson = JSONFormatter.fromJSON("{id:\"ABDCD\"}");
		
		Object obj = pdxJson.getObject();
		Assert.assertNotNull(obj);
		//r.put(id, pdxJson);
		
			ClusterDiff cc = new ClusterDiff();
			

			ClusterDiffReport report = cc.compare(jmx1, jmx2);
			
			QA.assertTrue("Report has regions", report.getRegionReports() != null&&  !report.getRegionReports().isEmpty());
			
			
			QA.assertNotNull("Report not null report"+report,report);

			
           File file = new File("target/report.json");
           
           //Gson gson = new Gson();
           //Util.writeFile(file,gson.toJson(report), false);
		   JacksonJSON.writeObjectToFile(file, report);
		   
		   
		   String json = IO.readFile(file.getAbsolutePath());
		   
		   Debugger.println("report json:\n"+json);
		   
		   try(Reader  reader = new InputStreamReader(new FileInputStream(file), IO.CHARSET))
		   {
			   //ClusterSyncReport results = gson.fromJson(reader, ClusterSyncReport.class);
			   ClusterDiffReport results = JacksonJSON.fromJson(reader, ClusterDiffReport.class);
			   
			   Assert.assertTrue(report.equals(results));
		   }
		   
		   //Test updating the target
		   
			GemFireJmxClient.startGatewaySenders(jmx1);
			GemFireJmxClient.startGatewaySenders(jmx2);
		 			
	}// --------------------------------------------------------
	
	@Test
	public void testDetectedTargetMissingRecords()
	throws Exception
	{	
		JMX jmx1 = JMX.connect(QA.getPrimaryLocatorJmxHost(),QA.getPrimaryLocatorJmxPort());
		JMX jmx2 = JMX.connect(QA.getRemoteLocatorHost(),QA.getRemoteLocatorJmxPort());
	
		syncAll(jmx1, jmx2);
		
		
		//GemFireJmxClient.getClientCache(jmx1);
		GemFireJmxClient.clearRegion(QA.getTestRegion(),jmx1);
		GemFireJmxClient.clearRegion(QA.getTest2Region(),jmx2);		
		
		
		
		//Assert not differences
		
		ClusterDiff cc = new ClusterDiff();
			
		Region<Object,Object> testRegion = GemFireJmxClient.getRegion(QA.getTestRegion(),jmx1);
		
		String id = UUID.randomUUID().toString();
		Date date = Calendar.getInstance().getTime();
		
		testRegion.put(id,date);
		
		ClusterDiffReport report = cc.compare(jmx1, jmx2);
		
		QA.assertTrue("Is differences", report.isDifferent());
		
		QA.assertTrue("Report has regions", report.getRegionReports() != null&&  !report.getRegionReports().isEmpty());
		
		syncAll(jmx1, jmx2);
		
		//Go other way

		SingletonGemFireJmx.reconnectJMX(jmx2.getHost(), jmx2.getPort());
		Region<Object,Object> test2Region = GemFireJmxClient.getRegion(QA.getTest2Region(),jmx1);
		
		 id = UUID.randomUUID().toString();
	     date = Calendar.getInstance().getTime();
		
	     test2Region.put(id,date);
	     
	     SingletonGemFireJmx.reconnectJMX(jmx1.getHost(), jmx1.getPort());
	     GemFireJmxClient.getClientCache(jmx1);
	     
	     //
		 report = cc.compare(jmx1, jmx2);
			
		 QA.assertTrue("Is differences", report.isDifferent());
		 QA.assertTrue("Report has regions", report.getRegionReports() != null&&  !report.getRegionReports().isEmpty());
		 
		 QA.assertTrue("Report has region:"+test2Region.getName()+" in "+report.getRegionReports().keySet(), 
				 report.getRegionReports() != null&&  report.getRegionReports().keySet().contains(test2Region.getName()));
		 
		
		 syncAll(jmx1, jmx2);
		 
		GemFireJmxClient.startGatewaySenders(jmx1);
		GemFireJmxClient.startGatewaySenders(jmx2);
		 			
	}// --------------------------------------------------------

	private void syncAll(JMX jmx1, JMX jmx2)
	throws Exception
	{
		GemFireJmxClient.startGatewaySenders(jmx1);
		GemFireJmxClient.startGatewaySenders(jmx2);

		GemFireJmxClient.clearRegion(QA.getTestRegion(),SingletonGemFireJmx.getJmx());
		GemFireJmxClient.clearRegion(QA.getTest2Region(),SingletonGemFireJmx.getJmx());		
		
		
		Thread.sleep(1000*5);
		
		GemFireJmxClient.stopGatewaySenders(jmx1);
		GemFireJmxClient.stopGatewaySenders(jmx2);

	}
}
