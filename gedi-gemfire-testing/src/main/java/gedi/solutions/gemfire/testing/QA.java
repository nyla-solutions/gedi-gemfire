package gedi.solutions.gemfire.testing;


import gedi.solutions.gemfire.client.GemFireJmxClient;
import gedi.solutions.gemfire.client.SingletonGemFireJmx;
import gedi.solutions.gemfire.util.GemFireMgmt;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.ValueExp;

import nyla.solutions.global.operations.Shell;
import nyla.solutions.global.operations.Shell.ProcessInfo;
import nyla.solutions.global.patterns.jmx.JMX;
import nyla.solutions.global.patterns.jmx.JMXSecurityException;
import nyla.solutions.global.util.Config;
import nyla.solutions.global.util.Debugger;

import org.junit.After;
import org.junit.Assert;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.management.GatewaySenderMXBean;
import com.gemstone.gemfire.management.MemberMXBean;

/**
 * Utility class for integration testing
 * @author Gregory Green
 *
 */
public final class QA
{
	private static final String PREFIX = "ASSERT ";
	
	private static int testCounter = 1;
	
	/**
	 * 
	 * @param clz
	 * @param methodName
	 */
	public synchronized static void logStartTest(Class<?> clz, String methodName)
	{
		/*##### Test 1: Planed Locator down - Start ####
	##### Test 1: Planned Locator down - Successful ####*/

		log("");
		log("");
		log("#TEST "+testCounter+" "+clz.getSimpleName()+"."+methodName+" - START ");
		log("");
			
		//increment test counter
		testCounter++;
		
	}// --------------------------------------------------------
	public static void logSuccessTest(Class<?> clz, String methodName)
	{
		log("#TEST "+clz.getSimpleName()+"."+methodName+" - SUCCESS ");
		
	}// --------------------------------------------------------
	public static void logFailedTest(Class<?> clz, String methodName,Exception e)
	{
		
		log("FAILURE STACKTRACE"+Debugger.stackTrace(e));
		log("");
		log("#TEST "+clz.getSimpleName()+"."+methodName+" - FAILED EXCEPTION:"+e.toString());
		
	}// --------------------------------------------------------
	/**
	 * Updates data in a given test region
	 * @return
	 */
	public static Object[] updateTestRegionEntry()
	{
		JMX jmx = SingletonGemFireJmx.getJmx();
		String regionName = QA.getTestRegion();
		
		Region<Object,Object> region = GemFireJmxClient.getRegion(regionName, jmx);
		
		String value = "ID:"+UUID.randomUUID().toString()+" DATE:"+Calendar.getInstance().getTime().toString();
		String key =  "DELETE";
		
		region.put(key,value);
		
		Object compareValue = region.get(key);
		QA.assertEquals("region:"+regionName+" key:"+key+" "+value+"="+compareValue, value,compareValue );
		
		Object [] objects = {key, compareValue};
		
				
		return objects;
	}// --------------------------------------------------------
	@After
	public static void removeTestRegionEntry()
	{
		JMX jmx = SingletonGemFireJmx.getJmx();
		String regionName = QA.getTestRegion();
		
		Region<Object,Object> region = GemFireJmxClient.getRegion(regionName, jmx);
		
		String key =  "DELETE";
		
		region.remove(key);
		
	}// --------------------------------------------------------
	/**
	 * Check that all members are running
	 */
	public static void assertMemberStatuses()
	{
		Collection<String> listCollection = GemFireJmxClient.listMembers(SingletonGemFireJmx.getJmx());
		
		assertTrue("Member list:"+listCollection+" not empty",listCollection != null && !listCollection.isEmpty());
	
		
		boolean ok = false;
		for (String member : listCollection)
		{
			ok = GemFireJmxClient.checkMemberStatus(member,SingletonGemFireJmx.getJmx());
			Assert.assertTrue("Check member:"+member+" is running true="+ok,ok);
		}
		
	}// --------------------------------------------------------
	
	public static void assertHasConnectGatewaySendersWithPrimary()
	throws Exception
	{
		Collection<GatewaySenderMXBean> senders = GemFireJmxClient.listGatewaySenders(SingletonGemFireJmx.getJmx());
		QA.assertNotNull("Has senders",senders) ;
		
		GatewaySenderMXBean primary = null;
		
		
		for (int i = 0; i < getRetryCount(); i++)
		{
			
			for (GatewaySenderMXBean sender : senders)
			{
				
				if(sender.isPrimary())
					primary = sender;
			}
			
			if(primary != null)
				break;
			
			delay();
		}
		
		assertNotNull("Has new primary gateway sender",primary);
		
		QA.assertTrue("Primary Sender ID:"+primary.getSenderId()+" receiver:"+primary.getGatewayReceiver()+" is connected",primary.isConnected());
		
	}// --------------------------------------------------------
	
	/**
	 * Wait for a given member to startup
	 * @param member the member to wait for
	 */
	public static void waitForMemberStart(String member)
	{
		boolean isRunning =  false;
		boolean printedStartMember  = false;
		int count = 0;
		while(!isRunning)
		{
			isRunning = GemFireJmxClient.checkMemberStatus(member,SingletonGemFireJmx.getJmx());
			
			if(!printedStartMember )
			{
					log("Waiting for member:"+member+".  Starting member to continue. "+
			            " You can perform a gfsh status command to confirm whether the member is running");
					printedStartMember = true;
			}
			
			
			try{ QA.delay();}catch(Exception e){}
			
			 if(count > QA.retryCount)
			 {
				 throw new RuntimeException("member:"+member+" did not start after "
			 +QA.retryCount+" checks with a delay of "+QA.sleepDelay+" milliseconds");
			 }
			 
			count++;
		}
	}// --------------------------------------------------------
	/**
	 * Wait for a given member to startup
	 * @param member the member to wait for
	 * @throws InterruptedException 
	 */
	public static void waitForMemberStop(String member) throws InterruptedException
	{
		boolean isRunning =  true;
		boolean printedStartMember  = false;
		
		int count  = 0;
		while(isRunning && count < retryCount)
		{
			isRunning = GemFireJmxClient.checkMemberStatus(member,SingletonGemFireJmx.getJmx());
			
			if(!printedStartMember )
			{
					log("Waiting for member:"+member+" to stop.");
					printedStartMember = true;
			}
			
			QA.delay();
			
			count++;
		}
		
		if(isRunning)
		{
			throw new RuntimeException("member:"+member+" failed to stop after "+QA.retryCount+
					" checks with a delay of "+sleepDelay+" milliseconds");
		}
	}// --------------------------------------------------------
	
	public static void assertRegionsWithoutRedundancyNotNull(
			) throws Exception, InterruptedException
	{
		Collection<String> regions = null;
		
		for(int i=0;i< getRetryCount();i++)
		{
			regions =  GemFireJmxClient.listRegionsWithNumBucketsWithoutRedundancy(
					SingletonGemFireJmx.getJmx());
				
			if(regions != null)
				break;
			else
				delay(); //delay and retry
		}
		
		assertNotNull("Has regions without redundancy ",regions);
	}// --------------------------------------------------------
	/**
	 * This check if the actual count equals a configured expected count.
	 * 
	 * @throws Exception
	 */
	public static void checkClusterUp()
	throws Exception
	{
	//Check server one and running
		
		int actualCount = 0;
		boolean printed = false;
		
		int attempts = 0;
		
		while (actualCount != expectedMemberCount)
		{
			if(attempts > retryCount)
			{
				throw new RuntimeException("actualCount:"+actualCount+ "  !=  expectedMemberCount:"+ expectedMemberCount+" after "+retryCount+"  attempts with a delay of "+QA.sleepDelay+" millisecond per attempt");
			}
				try
				{
					Collection<String> members = GemFireJmxClient.listMembers(SingletonGemFireJmx.getJmx());
					
					if(members != null && !members.isEmpty() )
					{
						actualCount = members.size();
						
						if(expectedMemberCount ==  actualCount)
							break;	
					}
				}
				catch(JMXSecurityException e)
				{
					QA.log(Debugger.stackTrace(e));
					throw e;
				}
				catch (Exception e)
				{
					//if(!printed && e.getMessage().contains("connection"))
					//{
						//Needed to bypass find bugs 
					//}
				}
				
				

				
				if(!printed)
				{
					QA.log("Waiting because expected member count:"+expectedMemberCount+" is not equal to actual count:"+actualCount);
					
					startCluster();
					
					printed = true;
				}
				System.out.println("Sleeping for "+sleepDelay+ " milliseconds");
				Thread.sleep(sleepDelay);
		
				
				attempts++;
		}
		
		
		Collection<String> regions = GemFireJmxClient.listRegionsWithNumBucketsWithoutRedundancy(
				SingletonGemFireJmx.getJmx());
		
		while(regions != null)
		{
			log("Waiting for Redundancy for regions:"+regions);
			delay();
			regions = GemFireJmxClient.listRegionsWithNumBucketsWithoutRedundancy(
					SingletonGemFireJmx.getJmx());
		}
		
		//Check physical server name
		Collection<String> hosts = GemFireJmxClient.listHosts(SingletonGemFireJmx.getJmx());
		
		
		QA.assertTrue("physicalServerHostName:"+hosts+"  is not empty", hosts != null && !hosts.isEmpty());
		QA.assertTrue( "physicalServerHostName:"+QA.physicalServerHostName+" in list of valid hosts:"+hosts,
				hosts.contains(QA.physicalServerHostName));
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @param cmds
	 * @return
	 */
	private static String decorateCompleteScript(String[] cmds)
	{
		if(cmds == null)
			return null;
		
		StringBuilder completeCommandScript = new StringBuilder();
		for (String cmd : cmds)
		{
			if(completeCommandScript.length() != 0)
				completeCommandScript.append(" ");
			
			completeCommandScript.append(cmd);
		}
		return completeCommandScript.toString();
	}// --------------------------------------------------------
	/**
	 * Start the cluster
	 * @throws IOException
	 */
	public static void startCluster() throws IOException
	{
		int attemptCount= 0;
		
		Shell shell = new Shell();
		
		

		QA.log("Running: "+decorateCompleteScript(QA.getStartClusterCommand()));
		ProcessInfo pi = shell.execute(QA.getStartClusterCommand());
		QA.log(QA.getStartClusterCommand()+" exitValue:"+pi.exitValue);
		
		
		if(pi.exitValue != 0)
		{
			QA.log("WARNING: Unable to automate start of cluster exitValue="+pi.exitValue+". Please start all members to continue test.");
		}
		
		//reconnect
		while(true)
		{
			System.out.println("Trying to connect");
			
			try {
				SingletonGemFireJmx.reconnect();
				
				QA.log("Cluster has STARTED");
				break;
			}
			catch(Exception e)
			{
				QA.log("Waiting for cluster to start");
			}
			
			if(attemptCount > retryCount)
			{
				throw new RuntimeException("Cannot connect or start with jmx host:"+primaryLocatorJmxHost+" and jmxPort:"+primaryLocatorJmxPort
						+" after "+retryCount+" attempts with a delay of "+sleepDelay);
				
			}

			attemptCount++;
			
			try{ QA.delay(); } catch(Exception e){}
		}
	}// --------------------------------------------------------
	/**
	 * This method asserts that a given count equals the TotalRegionEntryCount.
	 * It will try to overcome potential instabilities in the count due to members
	 * starts/stops. It gets the count several times with a delay before aborting and failing
	 * the assertion.
	 * 
	 * @param count
	 * @throws Exception
	 */
	public static void assertTotalRegionEntryCountEqualsGreaterThan(long count)
	throws Exception
	{
		long actualCnt = 0;
		
		for (int i = 0; i < getRetryCount(); i++)
		{
			actualCnt = GemFireJmxClient.getTotalRegionEntryCount(SingletonGemFireJmx.getJmx());
			if(actualCnt >= count)
			{
				break;
			}
			delay();
		}
		
		assertTrue(" Total Region Entry Count "+actualCnt+">="+count, actualCnt >= count);
	}// --------------------------------------------------------
	/**
	 * 
	 * @param members the member names to be killed
	 * @throws InterruptedException 
	 */
	public static void killMembers(Collection<String> members) throws InterruptedException
	
	{
		//loop thru and kill with no wait
		for (String name : members)
		{
			killMember(name);
		}
		
		//loop again and wait for each to be killed
		for (String name : members)
		{
			waitForMemberStop(name);
		}
	}// --------------------------------------------------------
	/**
	 * Prompts user to manual kill a given member.
	 * The user will be provided with the host name and process id
	 * @param name the member name
	 */
	public static void killMember(String name)
	{
		
		try
		{
			MemberMXBean member = GemFireJmxClient.getMember(name, SingletonGemFireJmx.getJmx());

			if(member == null)
				return;
			
			
			String host = member.getHost();
			int processId = member.getProcessId();
			
			killMemberProcessOnHost(name,processId,host);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
	}// --------------------------------------------------------
	public static void startMemberOnHost(String name, String host)
	{
		
		QA.log("Executing starting member:"+name+" on host "+host);
		
		
		Shell shell = new Shell();
		ProcessInfo pi = null;
		
		String[] originalArgs = QA.getStartMemberCommand();
		
		String [] cmdArgs = new String[originalArgs.length+2];
		System.arraycopy(originalArgs, 0, cmdArgs, 0, originalArgs.length);
		
		cmdArgs[originalArgs.length] = name;
		cmdArgs[originalArgs.length+1] = host;
		
		QA.log("Executing "+QA.decorateCompleteScript(cmdArgs));
		
		pi = shell.execute(cmdArgs);
		
		QA.log(QA.getStartMemberCommand()+"  results:"+pi);
		
		if(pi.exitValue != 0 )
		{
			
			QA.log("WARNING: UNABLE to start process manually start:"+pi);

			QA.log("Manually start member:"+name+"  on host:"+host);

		}
	}// --------------------------------------------------------
	public static void killMemberProcessOnHost(String name,int processId, String host)
	{
		try
		{
			//try to execute shell
			Shell shell = new Shell();
			ProcessInfo pi = null;
			
			
			pi = shell.execute(QA.getKillProcessOnHostCommand(),host,String.valueOf(processId));
			
			QA.log(QA.getKillProcessOnHostCommand()+" "+host+" "+processId+" \n exitValues:"+pi.exitValue);
			
			if(pi.exitValue != 0 )
			{
				
				QA.log("WARNING: UNABLE to kill processId:"+processId+" on host:"+host+" results:"+pi);

				QA.log("Manually kill member:"+name+"  on host:"+host+" with processId:"+processId);
				
				QA.log("Example:");
				QA.log("kill -9 "+processId);
				

			}
			
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	
	}// --------------------------------------------------------
	 /**
	    * Shut down each member in a given RedundancyZone
	    * @param redundancyZone the Redundancy Zone to shutdown
	 * @throws InterruptedException 
	    */
	   public static void killRedundancyZone(String redundancyZone) 
	   throws InterruptedException
	   {
		   if (redundancyZone == null || redundancyZone.length() == 0)
			   throw new IllegalArgumentException("redundancyZone required");
		   
		   String objectNamePattern = "GemFire:type=Member,member=*";
		   QueryExp exp = Query.eq(Query.attr("RedundancyZone"),Query.value(redundancyZone));
		   
		   Collection<ObjectName> memberObjectNames = SingletonGemFireJmx.getJmx().searchObjectNames(objectNamePattern, exp);
		   
		   Collection<String> members = new ArrayList<String>(memberObjectNames.size());
		   for (ObjectName objectName : memberObjectNames)
		   {
			   members.add(objectName.getKeyProperty("member"));
			   
		   }
		   
		   killMembers(members);
	   }// --------------------------------------------------------
	   
	   /**
		 * 
		 * @param hostName the host name and or IP address
		 * 
		 * @return the number of members
	 * @throws InterruptedException 
		 */
		public static Collection<String> killMembersOnHost(String hostName) 
		throws InterruptedException
		{
			
			JMX jmx = SingletonGemFireJmx.getJmx();
			
			
			String objectNamePattern = "GemFire:type=Member,member=*";
			QueryExp queryExp = null;
			ValueExp[] values = null;
			
			
			//Also get the IP
			try
			{
				InetAddress[] addresses = InetAddress.getAllByName(hostName); 
				InetAddress address = null;
				if(addresses != null)
				{
					values = new ValueExp[addresses.length];
					
					for (int i=0; i <addresses.length;i++)
					{
						address = addresses[i];
						values[i] = Query.value(address.getHostAddress());
						
					}
				}
			}
			catch (UnknownHostException e)
			{
				QA.log(e.getMessage());
			}
			
			if(values != null)
			{
				queryExp = Query.or(Query.eq(Query.attr("Host"), Query.value(hostName)),
									Query.in(Query.attr("Host"), values));
			}
			else
			{
				queryExp = Query.eq(Query.attr("Host"), Query.value(hostName));
			}
			
			/*
			 * QueryExp query = Query.and(Query.eq(Query.attr("Enabled"), Query.value(true)),
	               Query.eq(Query.attr("Owner"), Query.value("Duke")));
			 */
			Set<ObjectName> memberObjectNames =  jmx.searchObjectNames(objectNamePattern, queryExp);
			
			if(memberObjectNames == null || memberObjectNames.isEmpty())
				return null;
			
			Collection<Object[]> cacheServers = new ArrayList<Object[]>();
			Collection<Object[]> locators = new ArrayList<Object[]>();
			MemberMXBean member = null;
		
			ArrayList<String> memberNames = new ArrayList<String>(memberObjectNames.size()); 
			
			//determine host/port details
			for (ObjectName objectName : memberObjectNames)
			{
				Object[] processIdandHostName  = new Object[3];
				
				member = GemFireJmxClient.getMember(objectName.getKeyProperty("member"),SingletonGemFireJmx.getJmx());
				
				memberNames.add(member.getName());
				
				processIdandHostName[0] = member.getName();
				processIdandHostName[1] = member.getProcessId();
				processIdandHostName[2] = member.getHost();
				if(member.isCacheServer())
				{
					cacheServers.add(processIdandHostName);	
				}
				else if(member.isLocator())
				{
					locators.add(processIdandHostName);
				}
				
			}
			
			for (Object[] processIdandHostName : cacheServers)
			{
				killMemberProcessOnHost((String)processIdandHostName[0],
						(int)processIdandHostName[1],
						(String)processIdandHostName[2]);
			}		
			
			//kill locator and do not wait
			for (Object[] processIdandHostName : locators)
			{
				killMemberProcessOnHost((String)processIdandHostName[0],
						(int)processIdandHostName[1],
						(String)processIdandHostName[2]);
			}		
			
			//wait for member to stop
			String memberName = null;
			for (ObjectName objectName : memberObjectNames)
			{
				memberName = objectName.getKeyProperty("member");
				QA.waitForMemberStop(memberName);
			}
	

			return memberNames;
		}// --------------------------------------------------------
	
	/**
	 * @return the expectedMemberCount
	 */
	public static int getExpectedMemberCount()
	{
		return expectedMemberCount;
	}// --------------------------------------------------------
	

	/**
	 * 
	 * @return the physicalServerHostName
	 */
	public static final String getPhysicalServerHostName()
	{
		return physicalServerHostName;
	}
	
	/**
	 * @return the targetCacheServerName
	 */
	public static String getTargetCacheServerName()
	{
		return targetCacheServerName;
	}// --------------------------------------------------------

	
	/**
	 * @return the targetCacheServerHost
	 */
	public static String getTargetCacheServerHost()
	{
		return targetCacheServerHost;
	}
	/**
	 * @return the primaryLocatorServerName
	 */
	public static String getPrimaryLocatorServerName()
	{
		return primaryLocatorServerName;
	}

	/**
	 * @return the secondaryLocatorHost
	 */
	public static String getSecondaryLocatorHost()
	{
		return secondaryLocatorHost;
	}

	/**
	 * @return the secondaryLocatorJmxPort
	 */
	public static int getSecondaryLocatorJmxPort()
	{
		return secondaryLocatorJmxPort;
	}

	/**
	 * @return the primaryLocatorJmxHost
	 */
	public static String getPrimaryLocatorJmxHost()
	{
		return primaryLocatorJmxHost;
	}


	/**
	 * @return the primaryLocatorJmxPort
	 */
	public static int getPrimaryLocatorJmxPort()
	{
		return primaryLocatorJmxPort;
	}

	/**
	 * @return the targetRedundancyZone
	 */
	public static String getTargetRedundancyZone()
	{
		return targetRedundancyZone;
	}

	public static void delay() 
			throws InterruptedException
	{
		System.out.println("Sleeping for "+sleepDelay+" milliseconds");
		Thread.sleep(sleepDelay);
	}// --------------------------------------------------------
	
	
	/**
	 * @return the retryCount
	 */
	public static int getRetryCount()
	{
		return retryCount;
	}
	public static void log(String message)
	{
		Debugger.println(new StringBuilder(message).append("   "));
	}// --------------------------------------------------------
	public static void assertNotNull(String message,Object object)
	{
		log(PREFIX+message);
		
		Assert.assertNotNull(message, object);
	}// --------------------------------------------------------
	public static void assertNull(String message,Object object)
	{
		log(PREFIX+message);
		
		Assert.assertNull(message, object);
		
		
	}// --------------------------------------------------------
	public static void assertConnectionWithPrimaryLocator()
	{
		QA.assertNotNull("Connection with primary locator="+QA.getPrimaryLocatorJmxHost()+"["+QA.getPrimaryLocatorJmxPort()+"]",
				GemFireMgmt.reconnectJMX(QA.getPrimaryLocatorJmxHost(),QA.getPrimaryLocatorJmxPort()));
	}
	public static void assertConnectionWithSecondaryLocator()
	{
		QA.assertNotNull(
				"Checking connection with secondary locator="+QA.getSecondaryLocatorHost()+"["+QA.getSecondaryLocatorJmxPort()+"]",
				GemFireMgmt.reconnectJMX(
						QA.getSecondaryLocatorHost(),QA.getSecondaryLocatorJmxPort()));
	}// --------------------------------------------------------
	public static void assertTrue(String message,boolean condition)
	{
		log(PREFIX+message);
		
		Assert.assertTrue(message, condition);
		
	}// --------------------------------------------------------
	public static void assertEquals(String message,Object ob1,Object ob2)
	{
		log(PREFIX+message);
		
		Assert.assertEquals(message, ob1,ob2);
		
	}// --------------------------------------------------------
	/**
	 * @return the testregion
	 */
	public static String getTestRegion()
	{
		return testRegion;
	}
	
	

	/**
	 * @return the remotelocatorhost
	 */
	public static String getRemoteLocatorHost()
	{
		return remoteLocatorHost;
	}

	/**
	 * @return the remoteLocatorJmxPort
	 */
	public static int getRemoteLocatorJmxPort()
	{
		return remoteLocatorJmxPort;
	}



	/**
	 * @return the secondaryLocatorServerName
	 */
	public static String getSecondaryLocatorServerName()
	{
		return secondaryLocatorServerName;
	}



	/**
	 * @return the exportlocationpath
	 */
	public static String getExportDirectoryPath()
	{
		return exportDirectoryPath;
	}



	/**
	 * @return the testRegionWithSpecialChars
	 */
	public static String getTestRegionWithNoneStandardChars()
	{
		return testRegionWithNoneStandardChars;
	}



	/**
	 * @return the loadDataCount
	 */
	public static int getLoadDataCount()
	{
		return loadDataCount;
	}
	

	/**
	 * @return the killProcessOnHostCommand
	 */
	public static String getKillProcessOnHostCommand()
	{
		return killProcessOnHostCommand;
	}

	

	/**
	 * @return the startClusterCommand
	 */
	private static String[] getStartClusterCommand()
	{
		return startClusterCommand;
	}



	/**
	 * @return the startmembercommand
	 */
	private static String[] getStartMemberCommand()
	{
		return startMemberCommand;
	}



	/**
	 * @return the test2region
	 */
	public static String getTest2Region()
	{
		return test2Region;
	}



	private final static String remoteLocatorHost = Config.getProperty(QA.class,"remoteLocatorHost");
	private final static int remoteLocatorJmxPort = Config.getPropertyInteger(QA.class,"remoteLocatorJmxPort");
	
	private final static int    expectedMemberCount = Config.getPropertyInteger(QA.class,"expectedMemberCount");
	private final static String targetCacheServerName = Config.getProperty(QA.class, "targetCacheServerName");
	private final static String targetCacheServerHost = Config.getProperty(QA.class, "targetCacheServerHost");
	private final static String primaryLocatorServerName =  Config.getProperty(QA.class,"primaryLocatorServerName");
	private final static String secondaryLocatorHost = Config.getProperty(QA.class,"secondaryLocatorHost");
	private final static int    secondaryLocatorJmxPort = Config.getPropertyInteger(QA.class,"secondaryLocatorJmxPort");
	private final static String secondaryLocatorServerName = Config.getProperty(QA.class,"secondaryLocatorServerName");
	private final static String primaryLocatorJmxHost =Config.getProperty(QA.class,"primaryLocatorJmxHost");
	private final static int    primaryLocatorJmxPort = Config.getPropertyInteger(QA.class,"primaryLocatorJmxPort");
	private final static String targetRedundancyZone =  Config.getProperty(QA.class,"targetRedundancyZone");
	private final static String physicalServerHostName =  Config.getProperty(QA.class,"physicalServerHostName");
	private final static String testRegion = Config.getProperty(QA.class,"testRegion");
	private final static String test2Region = Config.getProperty(QA.class,"test2Region","testRegionR");
	private final static long   sleepDelay = Config.getPropertyLong(QA.class.getName()+".sleepDelay",1000*1); //seconds
	private final static int    retryCount = Config.getPropertyInteger(QA.class,"retryCount",45);
	private final static String exportDirectoryPath = Config.getProperty(QA.class,"exportDirectoryPath","../target/data");
	private final static String testRegionWithNoneStandardChars = Config.getProperty(QA.class,"testRegionWithNoneStandardChars","test_specialchars-_region");
	private final static int    loadDataCount  = Config.getPropertyInteger(QA.class,"loadDataCount",100);
	private final static String killProcessOnHostCommand = Config.getProperty(QA.class,"killProcessOnHostCommand");
	private final static String[] startClusterCommand = Config.getPropertyStrings(QA.class,"startClusterCommand");
	private final static String[] startMemberCommand = Config.getPropertyStrings(QA.class,"startMemberCommand");
}
