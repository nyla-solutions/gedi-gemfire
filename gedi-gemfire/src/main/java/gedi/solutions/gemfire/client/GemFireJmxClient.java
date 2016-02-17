package gedi.solutions.gemfire.client;

import gedi.solutions.gemfire.io.GemFireIO;
import gedi.solutions.gemfire.operations.functions.ClearRegionFunction;


import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.openmbean.CompositeData;

import nyla.solutions.global.patterns.jmx.JMX;

import com.gemstone.gemfire.cache.CacheClosedException;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.management.DistributedRegionMXBean;
import com.gemstone.gemfire.management.DistributedSystemMXBean;
import com.gemstone.gemfire.management.GatewayReceiverMXBean;
import com.gemstone.gemfire.management.GatewaySenderMXBean;
import com.gemstone.gemfire.management.MemberMXBean;

/**
 * Wrapper to establish GemFire client cache based on JMX information.
 * 
 * @author Gregory Green
 *
 */
public class GemFireJmxClient
{
	private static ClientCache clientCache = null;
	private static ResourceBundle _bundle = null;
	private static final String hostPropFileName = "host.properties";

	
	public synchronized static void clearRegion(String regionName, JMX jmx)
			throws Exception
			{
				GemFireIO.exeWithResults(FunctionService.onRegion( GemFireJmxClient.getRegion(regionName,jmx)), 
						new ClearRegionFunction());
			}


	/**
	 * 
	 * @param jmx the JMX managers
	 */
	public static void startGatewaySenders(JMX jmx)
	{
		Collection<GatewaySenderMXBean> senders = listGatewaySenders(jmx);
		
		if(senders == null || senders.isEmpty())
			return;
		
		for (GatewaySenderMXBean gatewaySenderMXBean : senders)
		{			
			gatewaySenderMXBean.start();
		}
	}// --------------------------------------------------------
	/**
	 * 
	 * @param jmx the JMX managers
	 */
	public static void stopGatewaySenders(JMX jmx)
	throws Exception
	{
		Collection<GatewaySenderMXBean> senders = listGatewaySenders(jmx);
		
		if(senders == null || senders.isEmpty())
			return;
		
		
		for (GatewaySenderMXBean gatewaySenderMXBean : senders)
		{			
			gatewaySenderMXBean.stop();
		}
	}// --------------------------------------------------------
	/***
	 * 
	 * @param distributedRegionMXBean
	 * @return return if region.getRegionType contains the term  REPLICATE
	 */
	public static boolean isReplicatedRegion(DistributedRegionMXBean distributedRegionMXBean)
	{
		if(distributedRegionMXBean == null)
			return true;
		
		String type = distributedRegionMXBean.getRegionType();
		
		return type != null && type.toUpperCase().contains("REPLICATE");
	}// --------------------------------------------------------
	/**
	 * 
	 * @param jmx the JMX connection
	 * @return the collection of distributed region names
	 */
	public static Collection<DistributedRegionMXBean> listEnabledGatewayRegionMBeans(JMX jmx)
	{	
		QueryExp queryExp = Query.eq(Query.attr("GatewayEnabled"), Query.value(true)); 
		Collection<ObjectName> ons = jmx.searchObjectNames("GemFire:service=Region,name=*,type=Distributed", queryExp);
	
		if(ons == null || ons.isEmpty())
			return null;
		
		ArrayList<DistributedRegionMXBean> regions = new ArrayList<DistributedRegionMXBean>(ons.size());
		
		for (ObjectName objectName : ons)
		{
			DistributedRegionMXBean region = jmx.newBean(DistributedRegionMXBean.class, objectName);
			regions.add(region);
		}
		
		return regions;
	}// --------------------------------------------------------
	public static DistributedRegionMXBean getRegionMBean(String regionName, JMX jmx)
	{	
		ObjectName on = getRegionObjectName(regionName, jmx);
		
		if(on ==null)
			return null;
		
		DistributedRegionMXBean region = jmx.newBean(DistributedRegionMXBean.class, on);
		return region;
	}// --------------------------------------------------------
	/**
	 * 
	 * @param memberName the member name
	 * @param propertyName the property name
	 * @param jmx the JMX connection
	 * @return the GemFire property on the member
	 * @throws MalformedObjectNameException
	 */
	public static String getMemberGemFireProperty(String memberName, String propertyName, JMX jmx)
	throws MalformedObjectNameException
	{
		
		ObjectName objectName = new ObjectName(new StringBuilder("GemFire:type=Member,member=").append(memberName).toString());
		
		CompositeData cd = (CompositeData)jmx.invoke(objectName, "listGemFireProperties", null,null);
	
		return String.valueOf(cd.get(propertyName));
	}// --------------------------------------------------------
	/**
	 * Dynamically create a GemFire pool with just the server
	 * @param serverName the server name to create a pool for
	 * @param jmx the JMX connection
	 * @return the pool with the server name and its host/port configured
	 * @throws InstanceNotFoundException when the server name does not exist
	 */
	public static synchronized Pool getPoolForServer(String serverName, JMX jmx)
	throws InstanceNotFoundException
	{
		Pool pool = PoolManager.find(serverName);
		
		if(pool != null)
			return pool;
		
		PoolFactory poolFactory = PoolManager.createFactory();
		
		//LogWriter logWriter = getClientCache(jmx).getLogger();
		
		try
		{
			//get host name
			//ex: object GemFire:type=Member,member=server_1
			ObjectName objectName = new ObjectName(new StringBuilder("GemFire:type=Member,member=").append(serverName).toString());
			
			String host =  jmx.getAttribute(objectName, "Host");
			
			if(host == null || host.length() == 0)
				throw new IllegalArgumentException("host not found for serverName:"+serverName+" not found");
			
		    host = lookupNetworkHost(host);
		    
		    
				String findJmxPort = new StringBuilder("GemFire:service=CacheServer,port=*,type=Member,member=")
			.append(serverName).toString();
			
			//search ObjectNames
			Set<ObjectName> objectNames = jmx.searchObjectNames(findJmxPort);
			
			if(objectNames == null || objectNames.isEmpty())
				throw new IllegalArgumentException("Unable to to find port with server name:"+serverName);
			
			ObjectName portObjectName  = objectNames.iterator().next();
			Integer port = jmx.getAttribute(portObjectName, "Port");
			
			
			if(port == null)
				throw new IllegalArgumentException("Unable to obtain port for objectName:"+portObjectName+" for server:"+serverName);
			
			System.out.println("Found cache server host"+host+" port:"+port);
			
			poolFactory= poolFactory.addServer(host, port.intValue());
			
			return poolFactory.create(serverName);
		}
		catch(InstanceNotFoundException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to create pool for servername:"+serverName+" error:"+e.getMessage(),e);
		}
		
	}// --------------------------------------------------------
	/**
	 * This methods create a pool for connecting to a locator
	 * @param locatorName the locator name
	 * @param jmx the JMX connection
	 * @return the pool instance
	 * @throws InstanceNotFoundException
	 */
	public static synchronized Pool getPoolForLocator(JMX jmx)
	throws InstanceNotFoundException
	{
		String locatorsPoolName = jmx.getHost()+"["+jmx.getPort()+"]";
		
		Pool pool = PoolManager.find(locatorsPoolName);
				
		if(pool != null)
					return pool;
				
				
				PoolFactory poolFactory = PoolManager.createFactory();
				
				try
				{
					int port = getLocatorPort(jmx);
					
					poolFactory= poolFactory.addLocator(jmx.getHost(), port);
					
					return poolFactory.create(locatorsPoolName);
				}
				catch (Exception e)
				{
					throw new RuntimeException("Unable to create pool for locator:"+jmx.getHost()+" error:"+e.getMessage(),e);
				}
				
			}// --------------------------------------------------------
	/**
	 * Get region based on a given name (create the region if it exists on the server, but not on the client).
	 * @param regionName the region name the obtains
	 * @param jmx the GemFire JMX connection
	 * @return the created region
	 */
	public static Region<Object, Object> getRegion(String regionName, JMX jmx)
	{
		Region<Object, Object>	region = getClientCache(jmx).getRegion(regionName);


		if (region == null)
		{
			//check if region exist on server
			
			if(isExistingRegionOnServer(regionName,jmx))
			{		// create it locally

				region = clientCache.createClientRegionFactory(
					ClientRegionShortcut.PROXY).create(regionName);
			}
		}

		return region;

	}// --------------------------------------------------------

	/**
	 * <pre>
	 * List all regions that match a wildcard expression (ex: R*).
	 * Note that special internal regions that begin with the name __ will be skipped.
	 * </pre>
	 * @param regionPattern the region pattern
	 * @param jmx the JMX manager connection
	 * @return the collection of region 
	 */
	public static Collection<Region<?,?>> listRootRegions(String regionPattern, JMX jmx)
	{
		//Use JMX to query for distributed regions
		//Ex: name GemFire:service=Region,name=/exampleRegion,type=Distributed
	
		
		String regionJmxPattern = String.format("GemFire:service=Region,name=/%s,type=Distributed",regionPattern);
		//this.getLogger().info("Searching for JMX region patterns: "+regionJmxPattern);
		
		Set<ObjectName> regionObjNameSet = jmx.searchObjectNames(regionJmxPattern);
		
		if(regionObjNameSet == null || regionObjNameSet.isEmpty())
		{
			//search with quotes
			regionJmxPattern = String.format("GemFire:service=Region,name=\"/%s\",type=Distributed",regionPattern);
			regionObjNameSet = jmx.searchObjectNames(regionJmxPattern);
		}
		
	
		if(regionObjNameSet == null || regionObjNameSet.isEmpty())
		{
			//this.getLogger().warn("No regions found");
			return null;
		}
		
		
		//sort the list
		regionObjNameSet = new TreeSet<ObjectName>(regionObjNameSet);
		
		ArrayList<Region<?,?>> regionSet = new ArrayList<Region<?,?>>(regionObjNameSet.size());

		String regionName = null;
		try
		{
			for (ObjectName regionObjectName : regionObjNameSet)
			{
				regionName = jmx.getAttribute(regionObjectName, "Name");
				
				
				if(regionName.startsWith("__")) {
					continue; //skip special regions
				}
				
				regionSet.add(getRegion(regionName,jmx));
				
			}
			
			
			
			return regionSet;
		}
		catch (InstanceNotFoundException e)
		{
			throw new RuntimeException("Cannot list regions:"+regionPattern+" ERROR:"+e.getMessage(),e);
		}
	}// --------------------------------------------------------
	
	/**
	 * 
	 * @param jmx the JMX connection
	 * @return the locator port obtained from JMX
	 * @throws MalformedObjectNameException
	 */
	public static int getLocatorPort(JMX jmx)
			throws MalformedObjectNameException
	{
		// Get Locator port
		// locator bean GemFire:service=Locator,type=Member,member=locator
		String locatorNamePattern = "GemFire:type=Member,member=*";
		QueryExp queryExp = Query.eq(Query.attr("Manager"), Query.value(true));
		Set<ObjectName> objectNames = jmx.searchObjectNames(locatorNamePattern,
				queryExp);

		if (objectNames == null || objectNames.isEmpty())
		{
			throw new RuntimeException(
					"Data export error: no manager locators found through JMX connection");
		}

		ObjectName locatorJmxMgrObjName = objectNames.iterator().next();

		try
		{
			String locatorMemberName = jmx.getAttribute(locatorJmxMgrObjName,
					"Member");

			ObjectName locatorServiceObjName = new ObjectName(String.format(
					"GemFire:service=Locator,type=Member,member=%s",
					locatorMemberName));

			// get port
			return jmx.getAttribute(locatorServiceObjName, "Port");
		}
		catch (InstanceNotFoundException e)
		{
			throw new RuntimeException(e.getMessage(),e);
		}

	}// --------------------------------------------------------
	/**
	 * Return the client cache based on the JMX connection (if no cache instance)
	 * @param jmx the gemfire JMX manager connection
	 * @return the Client cache instance
	 */
	public static synchronized ClientCache getClientCache(JMX jmx)
	{
		try
		{
			if (clientCache == null || clientCache.isClosed())
			{

				try
				{
					clientCache = ClientCacheFactory.getAnyInstance();
				}
				catch(CacheClosedException e)
				{
					clientCache = null;
				}
				
				if (clientCache != null)
					return clientCache;

				
				// Get Locator port
				// locator bean GemFire:service=Locator,type=Member,member=locator
				String locatorNamePattern = "GemFire:type=Member,member=*";
				QueryExp queryExp = Query.eq(Query.attr("Manager"), Query.value(true));
				Set<ObjectName> objectNames = jmx.searchObjectNames(locatorNamePattern,
						queryExp);

				if (objectNames == null || objectNames.isEmpty())
				{
					throw new RuntimeException(
							"Data export error: no manager locators found through JMX connection");
				}

				ObjectName locatorJmxMgrObjName = objectNames.iterator().next();

				String locatorMemberName = jmx.getAttribute(locatorJmxMgrObjName,
						"Member");

				ObjectName locatorServiceObjName = new ObjectName(String.format(
						"GemFire:service=Locator,type=Member,member=%s",
						locatorMemberName));

				// get port
				
				int port = jmx.getAttribute(locatorServiceObjName, "Port");
				String host = jmx.getAttribute(locatorJmxMgrObjName, "Host");
				
				
				host = lookupNetworkHost(host);
				
				clientCache = new ClientCacheFactory().addPoolLocator(
						host, port)
						.setPoolSubscriptionEnabled(false)
						.setPdxReadSerialized(
								Boolean.valueOf(
										System.getProperty("PdxReadSerialized","false"))
										.booleanValue()
								)
						.create();
				
			}
			
			return clientCache;
		}
		catch (Exception e)
		{
			throw new RuntimeException("JMX connection error ",e);
		}
	}// --------------------------------------------------------
	/**
	 * Determine if a given region exists
	 * @param regionName the region to create
	 * @param jmx the GemFire JMX manager connection
	 * @return true if the region exists, else false
	 */
	private static boolean isExistingRegionOnServer(String regionName,JMX jmx)
	{
		String regionJmxPattern = String.format("GemFire:service=Region,name=/%s,type=Distributed",regionName);

		//System.out.println("searching for:"+regionJmxPattern);
		
		Set<ObjectName> regionObjNameSet = jmx.searchObjectNames(regionJmxPattern);
		
		
		if(regionObjNameSet == null || regionObjNameSet.isEmpty())
		{
			//search with quotes
			regionJmxPattern = String.format("GemFire:service=Region,name=\"/%s\",type=Distributed",regionName);
			
			//System.out.println("searching for:"+regionJmxPattern);
			
			regionObjNameSet = jmx.searchObjectNames(regionJmxPattern);
			
		}
		return regionObjNameSet != null && !regionObjNameSet.isEmpty();
	}// --------------------------------------------------------
	private static ObjectName getRegionObjectName(String regionName,JMX jmx)
	{
		String regionJmxPattern = String.format("GemFire:service=Region,name=/%s,type=Distributed",regionName);

		//System.out.println("searching for:"+regionJmxPattern);
		
		Set<ObjectName> regionObjNameSet = jmx.searchObjectNames(regionJmxPattern);
		
		
		if(regionObjNameSet == null || regionObjNameSet.isEmpty())
		{
			//search with quotes
			//GemFire:service=Region,name="/ui-test-region",type=Distributed
			regionJmxPattern = String.format("GemFire:service=Region,name=\"/%s\",type=Distributed",regionName);
			
			//System.out.println("searching for:"+regionJmxPattern);
			
			regionObjNameSet = jmx.searchObjectNames(regionJmxPattern);
			
		}
		if(regionObjNameSet == null)
			return null;
		
		return regionObjNameSet.iterator().next();
	}// --------------------------------------------------------
	/**
	 * 
	 * @return the string containing the 
	 */
	public static String getPrimaryGatewaySenderMember(JMX jmx)
	{
		String objectNamePattern = "GemFire:service=GatewaySender,gatewaySender=REMOTE,type=Member,member=*";

		Collection<ObjectName> objectNames =  jmx.searchObjectNames(objectNamePattern);
		if(objectNames == null)
			return null;
		
		GatewaySenderMXBean bean = null;
		GatewaySenderMXBean primarySender = null;
		ObjectName primaryObjectName = null;
		for (ObjectName objectName : objectNames)
		{
			
			bean = jmx.newBean(GatewaySenderMXBean.class, objectName);
			
			if(bean.isPrimary())
			{
				primarySender = bean;
				primaryObjectName = objectName;
				break;	
			}
		}
		
		if(primarySender == null)
			return null;
		
		return primaryObjectName.getKeyProperty("member");
	}// --------------------------------------------------------
	
	/**
	 * 
	 * @return list of gateway senders
	 * @throws Exception
	 */
	public static Collection<GatewaySenderMXBean> listGatewaySenders(JMX jmx)
	{
		try
		{
			DistributedSystemMXBean system = jmx.newBean(DistributedSystemMXBean.class, 
					new ObjectName("GemFire:service=System,type=Distributed"));
			
			ObjectName[] objectNames =  system.listGatewaySenderObjectNames();
			
			if(objectNames == null)
				return null;
			
			GatewaySenderMXBean gatewaySender = null;
			
			ArrayList<GatewaySenderMXBean> list = new ArrayList<GatewaySenderMXBean>(objectNames.length);
			
			for (ObjectName objectName : objectNames)
			{
				gatewaySender = jmx.newBean(GatewaySenderMXBean.class, objectName);
				list.add(gatewaySender);
			}
			
			return list;
		}
		catch (MalformedObjectNameException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}// --------------------------------------------------------
	/**
	 * Obtain a GemFire JMX client
	 * @param name the JMX client name
	 * @param jmx the JMX connection
	 * @return
	 */
	public static MemberMXBean getMember(String name,JMX jmx)
	{
		try
		{

			String pattern = "GemFire:type=Member,member="+name;
			
			Set<ObjectName> objectNames = jmx.searchObjectNames(pattern);
			
			if(objectNames == null || objectNames.isEmpty())
				return null;
			
			ObjectName serverName = new ObjectName(pattern);
			
			return jmx.newBean(com.gemstone.gemfire.management.MemberMXBean.class,serverName);
			
			
		}
		catch (MalformedObjectNameException e)
		{
			throw new RuntimeException("Unable to get member "+name
					+" ERROR:"+e.getMessage(),e);
		}
		
	}// --------------------------------------------------------
    static DistributedSystemMXBean getDistributedSystemMXBean(JMX jmx)
    throws Exception
    {

		DistributedSystemMXBean system = jmx.newBean(DistributedSystemMXBean.class, 
				new ObjectName("GemFire:service=System,type=Distributed"));
		
		return system;	
    }// --------------------------------------------------------
	public static Collection<GatewayReceiverMXBean> listGatewayReceivers(JMX jmx)
			throws Exception
			{

				DistributedSystemMXBean system = jmx.newBean(DistributedSystemMXBean.class, 
						new ObjectName("GemFire:service=System,type=Distributed"));
				
				ObjectName[] objectNames =  system.listGatewayReceiverObjectNames();
				
				if(objectNames == null)
					return null;
				
				GatewayReceiverMXBean gatewayReceiver = null;
				
				ArrayList<GatewayReceiverMXBean> list = new ArrayList<GatewayReceiverMXBean>(objectNames.length);
				
				for (ObjectName objectName : objectNames)
				{
					gatewayReceiver = jmx.newBean(GatewayReceiverMXBean.class, objectName);
					list.add(gatewayReceiver);
				}
				
				return list;
			}// --------------------------------------------------------
	/**
	 * 
	 * @return member names
	 */
	public static Collection<String> listMembers(JMX jmx)
	{

		Set<ObjectName> memberObjects = jmx.searchObjectNames("GemFire:type=Member,member=*");
		
		if(memberObjects == null || memberObjects.isEmpty())
		{
			return null;
		}
		
		ArrayList<String> memberList = new ArrayList<String>(memberObjects.size());
		
		MemberMXBean  bean = null;
		for (ObjectName objectName : memberObjects)
		{
			bean = jmx.newBean(MemberMXBean.class, objectName);
			try
			{
				memberList.add(bean.getName());
			}
			catch(UndeclaredThrowableException e)
			{
				//will not be added
			}
		}
		
		return memberList;
	}// --------------------------------------------------------
	/**
	 * 
	 * @param jmx the JMX connection
	 * @return
	 */
	public static Collection<String> listLocators(JMX jmx)
	{
		Set<ObjectName> locatorObjects = jmx.searchObjectNames("GemFire:service=Locator,type=Member,member=*");
		
		if(locatorObjects == null || locatorObjects.isEmpty())
		{
			return null;
		}
		
		ArrayList<String> locatorList = new ArrayList<String>(locatorObjects.size());
		
		for (ObjectName objectName : locatorObjects)
		{
			locatorList.add(objectName.getKeyProperty("member"));
		}
		
		return locatorList;
	}// --------------------------------------------------------
	/**
	 * Determines the unique set of the host names for the distributed system
	 * @param jmx the JMX connection
	 * @return string of server host names
	 */
	public static Collection<String> listHosts(JMX jmx)
	{
		Set<ObjectName> objectNames = jmx.searchObjectNames("GemFire:type=Member,member=*");
		
		if(objectNames == null || objectNames.isEmpty())
		{
			return null;
		}
		
		HashSet<String>  hostLists = new HashSet<String>(objectNames.size());
		
		MemberMXBean memberMXBean = null;
		for (ObjectName objectName : objectNames)
		{
			memberMXBean = jmx.newBean(MemberMXBean.class, objectName);
	
			hostLists.add(memberMXBean.getHost());
		}
		
		if(hostLists == null || hostLists.isEmpty())
			return null;
		
		return hostLists;
	}// --------------------------------------------------------
	/**
	 * List the unique locator 
	 * @param jmx the JMX
	 * @return the unique list of host names
	 */
	public static Set<String> listLocatorHosts(JMX jmx)
	{
		Set<ObjectName> objectNames = jmx.searchObjectNames("GemFire:type=Member,member=*");
		
		if(objectNames == null || objectNames.isEmpty())
		{
			return null;
		}
		
		HashSet<String>  hostLists = new HashSet<String>(objectNames.size());
		
		MemberMXBean memberMXBean = null;
		for (ObjectName objectName : objectNames)
		{
			memberMXBean = jmx.newBean(MemberMXBean.class, objectName);
	
			if(memberMXBean.isLocator())
				hostLists.add(memberMXBean.getHost());
		}
		
		if(hostLists == null || hostLists.isEmpty())
			return null;
		
		return hostLists;
	}// --------------------------------------------------------
	/**
	 * List the unique cache server hosts 
	 * @param jmx the JMX
	 * @return the unique list of host names
	 */
	public static Set<String> listCacheServerHosts(JMX jmx)
	{
		Set<ObjectName> objectNames = jmx.searchObjectNames("GemFire:type=Member,member=*");
		
		if(objectNames == null || objectNames.isEmpty())
		{
			return null;
		}
		
		HashSet<String>  hostLists = new HashSet<String>(objectNames.size());
		
		MemberMXBean memberMXBean = null;
		for (ObjectName objectName : objectNames)
		{
			memberMXBean = jmx.newBean(MemberMXBean.class, objectName);
	
			if(memberMXBean.isCacheServer())
				hostLists.add(memberMXBean.getHost());
		}
		
		if(hostLists == null || hostLists.isEmpty())
			return null;
		
		return hostLists;
	}// --------------------------------------------------------
	/**
	 * 
	 * @param serverName the member name
	 * @param jmx the JMX connection
	 * @return true if the member is running and connected
	 */
	public static boolean checkMemberStatus(String serverName,JMX jmx)
	{
		
		try
		{
			ObjectName objectName = new ObjectName("GemFire:type=Member,member="+serverName);
			
			String status = (String)jmx.invoke(objectName, "status", null, null);
			
			return status != null && status.contains("online");
		}
		catch (Exception e)
		{
			return false;
		}
	}// --------------------------------------------------------
	/**
	 * 
	 * @return ObjectName(GemFire:service=System,type=Distributed) TotalRegionEntryCount attribute
	 */
	public static long getTotalRegionEntryCount(JMX jmx)
	{
		try
		{
			
			ObjectName on = new ObjectName("GemFire:service=System,type=Distributed");
			
			return jmx.getAttribute(on, "TotalRegionEntryCount");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to obtain TotalRegionEntryCount ERROR:"+e.getMessage(),e);
		}
	}// --------------------------------------------------------
	/**
	 * 
	 * @return the region name that do not have redundancy
	 * @throws Exception
	 */
	public static Collection<String> listRegionsWithNumBucketsWithoutRedundancy(JMX jmx)
	throws Exception
	{
		//Get object GemFire:service=System,type=Distributed
		ObjectName objectName  = new ObjectName("GemFire:service=System,type=Distributed");
		
		//DistributedSystemMXBean distributedSystemMXBean = 
		DistributedSystemMXBean bean = jmx.newBean(DistributedSystemMXBean.class, objectName);
		
		//listDistributedRegionObjectNames
		ObjectName [] regionObjectNames = bean.listDistributedRegionObjectNames();
		
		if(regionObjectNames == null)
			return null;
		
		//com.gemstone.gemfire.management.DistributedRegionMXBean
		DistributedRegionMXBean region = null;
		
		ArrayList<String> regionNamesWithoutRedundancy = new ArrayList<String>();
		for (ObjectName regionObjectName : regionObjectNames)
		{
			region = jmx.newBean(DistributedRegionMXBean.class, regionObjectName);
			if(region.getNumBucketsWithoutRedundancy() > 0)
				regionNamesWithoutRedundancy.add(region.getName());
		}
		
		if(regionNamesWithoutRedundancy.isEmpty())
			return null;
		
		return regionNamesWithoutRedundancy;
		
	}// --------------------------------------------------------

	/**
	 * Supports resolving host network lookup issues
	 * @param host the host to resolve the IP
	 * @return the resolved host (or original if mapping does not exists)
	 */
	static synchronized String lookupNetworkHost(String host)
	{
		try
		{
			if(_bundle == null)
			{
				URL url = GemFireJmxClient.class.getResource(hostPropFileName);
				
				String filePath = null;
				if(url == null)
					filePath = hostPropFileName;
				else
					filePath = url.toString();
				
				
				System.out.println(new StringBuilder("Loading IP addresses from ")
						.append(filePath).toString());
				
				_bundle = ResourceBundle.getBundle("host");
			}
		
			System.out.println(new StringBuilder("Looking for host name \"").append(host).append("\" IP address in ")
					.append(hostPropFileName).toString());
			
			String newHost = _bundle.getString(host);
			System.out.println(new StringBuilder(host).append("=").append(newHost).toString());
			
			return newHost;
		}
		catch(Exception e)
		{
			System.out.println("Using host:"+host);
			return host;
		}
	}// --------------------------------------------------------
	/**
	 * Closes the client  cache
	 */
	public static void closeClientCache()
	{
		if(clientCache != null)
		{
			clientCache.close();
			
		}
		
	}
}
