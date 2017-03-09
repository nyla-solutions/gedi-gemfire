package gedi.solutions.geode.wan.cli;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;

import javax.management.MalformedObjectNameException;

import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.management.DistributedRegionMXBean;
import com.gemstone.gemfire.management.GatewayReceiverMXBean;
import com.gemstone.gemfire.management.GatewaySenderMXBean;

import gedi.solutions.geode.client.GemFireJmxClient;
import gedi.solutions.geode.io.GemFireIO;
import gedi.solutions.geode.util.GemFireNetworking;
import gedi.solutions.geode.wan.ClusterDiffReport;
import gedi.solutions.geode.wan.GetEntriesChecksumFunction;
import gedi.solutions.geode.wan.RegionDiffDirector;
import gedi.solutions.geode.wan.RegionDiffReport;
import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.patterns.jmx.JmxSecurity;
import nyla.solutions.core.util.Debugger;
import nyla.solutions.global.json.JacksonJSON;



/**
 * <pre>
 * The CompareClusters utility will perform a comparison of a source and target cluster.
	
	[Outline of checks/requirements]
	
	1- Checks locator/remote-locators are connected
	2- Checks Gateway Senders/Receivers are running and connected
	3- Checks that all gateway enabled regions entries to equals	
	4- Checks if there are any pending entries in the gateway sender queue 
	   of the source cluster.
	5- Prints a report of the differences.
	
	Example run with detected differences: 
	
./compareClusters.py localhost 11091 localhost 21091
***************************************************
CLASSPATH 
./lib/*:./lib
***************************************************
Loading IP addresses from host.properties
Looking for host name "172.16.242.1" IP address in host.properties
Using host:172.16.242.1

Cluster-Host:localhost remoteLocators:localhost[20001] matches at least one locator:localhost[20001],localhost[20002],172.16.242.1[20002] on Cluster-Host:localhost
Cluster-Host:localhost jmxport:11092 sender:REMOTE2 with receiver:172.16.242.1:22708 queueSize:52 &gt; 0 
Cluster-Host:localhost jmxport:11092 sender:REMOTE2 with receiver:172.16.242.1:22708 queueSize:52 &gt; 0 
Cluster-Cluster-Host:localhost jmxport:11092 region:TestP has 38  keys missing on Cluster-Host:localhost jmxport:21091
	
	</pre>
 * @author Gregory Green
 *
 */
public class ClusterDiff
{
	/**
	 * ARGS_LENGTH = 5
	 */
	private static final int ARGS_LENGTH = 5;

	/**
	 * 
	 * @param args the input arguments
	 */
	public static void main(String[] args)
	{
		ClusterDiff cc = new ClusterDiff();
		
		if(args.length < ARGS_LENGTH)
		{
			cc.printUsage();
			return;
		}
		
		String host1 = args[0];
		int port1 = Integer.parseInt(args[1]);
		
		String host2 = args[2];
		int port2 = Integer.parseInt(args[3]);
		
		// jmx1, jmx2;
		ClientCache cache =  null;
		try(JMX jmx1 = JMX.connect(host1, port1);JMX jmx2 = JMX.connect(host2, port2);)
		{
			
			
			cache = GemFireJmxClient.getClientCache(jmx1);
			
			File file = new File(args[4]);

			ClusterDiffReport report = cc.compare(jmx1,jmx2);
			

			JacksonJSON.writeObjectToFile(file,report);	
			cc.print("Wrote to file:"+file);			
		}
		catch(Exception e)
		{
			cc.printError(Debugger.stackTrace(e));
		}
		finally
		{
			if(cache != null)
				cache.close();	
		}
	}// --------------------------------------------------------

	/**
	 * Compares two connect GemFire clusters
	 * @param jmx1 the first connect
	 * @param jmx2 the second connection
	 * @return the summary of differences
	 * @throws Exception when an processing error occurs
	 */
	public ClusterDiffReport compare(JMX jmx1, JMX jmx2)
	throws Exception
	{
		//1- Check the first cluster's locator remote-locators host and ports matches the second cluster.
		//Check the first cluster's locator remote-locators host and ports matches the second cluster.
		
			
			String remoteLocators1 = jmx1.getSystemProperty("gemfire.remote-locators");
			
			if(remoteLocators1 ==null || remoteLocators1.length() == 0)
			{
				printError("Cannot determine remote locators for Cluster-Host:"+jmx1.getHost()+" please start locator with -J-Dgemfire.remote-locators=...");
				return null;
			}
			
			Collection<String>locatorsCollection2 = GemFireJmxClient.listLocators(jmx2);
			
			if(locatorsCollection2 == null || locatorsCollection2.isEmpty())
			{
				printError("Cannot determine locators in Cluster-Host:"+jmx2.getHost()+" add locators gemfire.properties to all locators.");
				return null;
			}
			
			String locatorName2 = locatorsCollection2.iterator().next();

			String locators2 = GemFireJmxClient.getMemberGemFireProperty(locatorName2,"locators", jmx2);
			
			if(!GemFireNetworking.checkRemoteLocatorsAndLocatorsMatch(remoteLocators1,locators2))
			{
					printError("Cluster-Host:"+jmx1.getHost()+" remoteLocators:"+remoteLocators1
								+"  does not match any Cluster-Host:"+
								jmx2.getHost()+" locators:"+locators2);
			}
			else
			{
				print("Cluster-Host:"+jmx1.getHost()+" remoteLocators:"+remoteLocators1+" matches at least one locator:"+locators2
						+" on Cluster-Host:"+jmx2.getHost());
				
			}
			
			/*
			 * 2- Check Gateway Senders are running and connected to a receiver
				Gateway Receivers are running and connected
			 */
			this.checkConnectedReceivers(jmx1);
			this.checkConnectedReceivers(jmx2);
			
			
			/**
			 * 3- Check for any gateway enabled regions counts are equal
			 */
			Collection<DistributedRegionMXBean> regions = GemFireJmxClient.listEnabledGatewayRegionMBeans(jmx1);
			if(regions == null || regions.isEmpty())
			{
				printError("No gateway enabled regions found");
				return null;
			}
			
			
			
			
			
			HashMap<Serializable, BigInteger> map1 = null, map2 =null;
			RegionDiffDirector director;
			
			
			HashMap<String,RegionDiffReport> regionSyncReportMap = new HashMap<String,RegionDiffReport>(regions.size());
			
			String regionName = null;
			
			RegionDiffReport report = null;
			
			ClusterDiffReport clusterSyncReport = new ClusterDiffReport();
			clusterSyncReport.setSourceLocators(decorateLocator(jmx1));
			clusterSyncReport.setTargetLocators(decorateLocator(jmx2));
			
			
			/*6. Check queue size of first cluster */
			checkSenderQueueSizes(jmx1,clusterSyncReport.getSourceLocators());
			checkSenderQueueSizes(jmx2, clusterSyncReport.getTargetLocators());
			
			/*
			 * 7- Return summary of number of differences.
			 */
			
			boolean foundDifferencesInRegionData = false;
			
			for (DistributedRegionMXBean distributedRegionMXBean : regions)
			{
				regionName = distributedRegionMXBean.getName();
				map1 = getMapChecksum(jmx1,distributedRegionMXBean);
				map2 = getMapChecksum(jmx2,distributedRegionMXBean);
				
				director = new RegionDiffDirector(regionName);
				
				director.constructComparison(map1, map2);
				
				report = director.getRegionSyncReport();
				regionSyncReportMap.put(regionName, report);
				
				if(report.getKeysDifferentOnTarget() != null &&
					!report.getKeysDifferentOnTarget().isEmpty())
				{
					foundDifferencesInRegionData = true;
					printError("Cluster-"+clusterSyncReport.getSourceLocators()
							+" region:"+regionName+" has "+report.getKeysDifferentOnTarget().size()+"  keys that are different on "
							+clusterSyncReport.getTargetLocators());
				}
				
				if(report.getKeysMissingOnTarget() != null &&
						!report.getKeysMissingOnTarget().isEmpty())
				{
					foundDifferencesInRegionData = true;
					printError("Cluster-"+clusterSyncReport.getSourceLocators()
							+" region:"+regionName+" has "+report.getKeysMissingOnTarget().size()+"  keys missing on "
							+clusterSyncReport.getTargetLocators());
				}
				
				if(report.getKeysRemovedFromSource() != null &&
						!report.getKeysRemovedFromSource().isEmpty())
				{
					foundDifferencesInRegionData = true;
					printError("Cluster-"+clusterSyncReport.getTargetLocators()
							+" region:"+regionName+" has "+report.getKeysRemovedFromSource().size()+"  keys removed from Cluster-"
							+clusterSyncReport.getSourceLocators());
				}
				
			}
			
			if(!foundDifferencesInRegionData)
			{
				print("All region records are in sync");
			}
			
			clusterSyncReport.setRegionReports(regionSyncReportMap);

			return clusterSyncReport;		
	}// --------------------------------------------------------
	/**
	 * 
	 * @param jmx the JMX connection
	 * @param distributedRegionMXBean the region
	 * @return Map the results of the key and checksum big integer
	 * @throws Exception when an internal error occurs
	 */
	public HashMap<Serializable, BigInteger> getMapChecksum(JMX jmx,DistributedRegionMXBean distributedRegionMXBean)
	throws Exception
	{
		
		Collection<HashMap<Serializable, BigInteger>> results = null;
		
		
		String[] regionName = {distributedRegionMXBean.getName()};
		Execution exe = null;
		
		if(GemFireJmxClient.isReplicatedRegion(distributedRegionMXBean))
		{
			//replicate regions only need execution on one server
			exe = FunctionService.onServer(
		    		 GemFireJmxClient.getPoolForLocator(jmx));
		}
		else
		{
		   exe = FunctionService.onServers(
		    		 GemFireJmxClient.getPoolForLocator(jmx));
		}
		
		exe = exe.withArgs(regionName);
		     
		results = GemFireIO.exeWithResults(exe, new GetEntriesChecksumFunction());
		
		return flatten(results);
		
	}// --------------------------------------------------------
	/**
	 * Assert queues size for senders have no pending events
	 * @param jmx the connection
	 */
	private void checkSenderQueueSizes(JMX jmx, String cluster)
	throws Exception
	{
		
		Collection<GatewaySenderMXBean> senders = GemFireJmxClient.listGatewaySenders(jmx);
		if(senders == null || senders.isEmpty())
		{
			printError(cluster+" does have any senders");
			return;
		}
		
		int queueSize;
		boolean foundConnected = false;
		
		for (GatewaySenderMXBean gatewaySenderMXBean : senders)
		{
			if(!gatewaySenderMXBean.isRunning())
				printError(cluster+" sender:"+gatewaySenderMXBean.getSenderId()+ " connected to receiver:"
							+gatewaySenderMXBean.getGatewayReceiver()+" is not running");
			
			if(gatewaySenderMXBean.isConnected()) 
				foundConnected = true;
			
			queueSize = gatewaySenderMXBean.getEventQueueSize();
			if(queueSize > 0)
				printError(cluster+" sender:"+gatewaySenderMXBean.getSenderId()+" with receiver:"
						+gatewaySenderMXBean.getGatewayReceiver()+" queueSize:"+queueSize+" > 0 ");
		}
		
		if(!foundConnected)
			print(cluster+" did not find any connected senders ");
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @param results the collection of results
	 * @return Map of flatten results
	 */
	private HashMap<Serializable, BigInteger>  flatten(Collection<HashMap<Serializable, BigInteger>>  results)
	{
		if(results == null || results.isEmpty())
			return null;
		
		HashMap<Serializable, BigInteger> flattenMap = null;
		for (HashMap<Serializable, BigInteger> hashMap : results)
		{
			if(hashMap == null || hashMap.isEmpty())
				continue;
			
			if(flattenMap != null)
				flattenMap.putAll(hashMap);
			else
				flattenMap = hashMap;
		}
		
		return flattenMap; 
	}// --------------------------------------------------------
	/**
	 * 
	 * @param jmx the JMX connection
	 * @throws Exception when an internal error occurs
	 */
	private void checkConnectedReceivers(JMX jmx)
	throws Exception
	{
		Collection<GatewayReceiverMXBean> receivers = GemFireJmxClient.listGatewayReceivers(jmx);
		
		if(receivers != null)
		{
			boolean foundConnectedReceiver = false;
			for (GatewayReceiverMXBean gatewayReceiverMXBean : receivers)
			{
				String [] connectedSenders = gatewayReceiverMXBean.getConnectedGatewaySenders();
				
				if(connectedSenders != null && connectedSenders.length > 0 && connectedSenders[0].length() > 0)
				{
					foundConnectedReceiver = true;
				}
					
				if(!gatewayReceiverMXBean.isRunning())
				{
					printError(jmx.getHost()+" port:"+jmx.getPort()+" "+" receiver for port:"+gatewayReceiverMXBean.getPort()+"  is not running");
				}
			}
			
			if(!foundConnectedReceiver)
			{
				printError(jmx.getHost()+" port:"+jmx.getPort()+" "+" no connected receivers found");
			}
		}
		else
		{
			printError("No GatewayReceivers found in "+decoratorClusterConnection(jmx));
		}
	}// --------------------------------------------------------
	/**
	 * Prints the help usage 
	 */
	private void printUsage()
	{
		printError("Usage: gemsyncReport.py sourceJmxHost sourceJmxPort targetJmxHost targetJmxPort syncReportFile");
		
		printError("\tjmxHosts and jmxPorts must point to a GemFire JMX manager (usually the locator)");
		printError("\t\tif you are not sure of the port number try 1099");
		printError("\t You can add the jmx username/password in a file "+JmxSecurity.JMX_PROPERTY_FILE_NM+" in the classpath optional");

	}// --------------------------------------------------------
	
	private String decorateLocator(JMX jmx)
	throws MalformedObjectNameException
	{
		int locatorPort =  GemFireJmxClient.getLocatorPort(jmx);	
		
		return new StringBuilder(jmx.getHost()).append("[")
				.append(locatorPort).append("]").toString();
	}// --------------------------------------------------------
	
	
	private String decoratorClusterConnection(JMX jmx)
	{
	   return new StringBuilder("Cluster-Host:")
		.append(jmx.getHost()).append(" jmxport:").append(jmx.getPort()).toString();
	}// --------------------------------------------------------
	private void print(String message)
	{
		System.out.println(message);
	}// --------------------------------------------------------
	private void printError(String message)
	{
		System.err.println(message);
	}
}
