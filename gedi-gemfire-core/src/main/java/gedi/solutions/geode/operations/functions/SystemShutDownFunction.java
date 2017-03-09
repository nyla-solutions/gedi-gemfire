/*
 ****************************************************************************
 *
 * Copyright (c)2014 The Vanguard Group of Investment Companies (VGI)
 * All rights reserved.
 *
 * This source code is CONFIDENTIAL and PROPRIETARY to VGI. Unauthorized
 * distribution, adaptation, or use may be subject to civil and criminal
 * penalties.
 *
 ****************************************************************************
 Module Description:

 $HeadURL: http://prdsvnrepo:8080/svn/tip/tip/projects/globalbasketconstruction/gbc-cacheserver/trunk/src/main/java/com/vanguard/tip/gbc/cacheserver/function/SystemShutDownFunction.java $
 $LastChangedRevision: 343018 $
 $Author: UHOH $
 $LastChangedDate: 2014-10-30 11:57:20 -0400 (Thu, 30 Oct 2014) $
*/
package gedi.solutions.geode.operations.functions;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.distributed.DistributedSystem;



/**
 * <p>
 * 		The function will shutdown the distribute system 
 *      thus preventing disk stores from being corrupted.
 * </p>
 * 
 * <p>
 * 			gfsh>execute function --group="gbc-data-node" --id="SystemShutdown"
 * </p>
 * 
 * <p>
 * 			Note the System.exit(0) will be executed to stop the JVM
 * </p>
 * 
 * @author Gregory Green
 *
 */
public class SystemShutDownFunction implements Function, Declarable
{	
	//@Autowired
	//private LoggingService loggingService;

	/**
	 * This method will the DistributeMember MBean and call to the shutdown method.
	 * Note that after the initiate function is executed other members may experience a 
	 * "Disconnected 
	 * 
	 */
	private static final long serialVersionUID = -4345180049555487810L;

	@Override
	public void execute(FunctionContext functionContext) 
	{
		
		String distributeMemberName = "unknown";
		LogWriter logWriter = null;
		
		try {
			
			Cache cache = CacheFactory.getAnyInstance();
			
			
			if(cache != null && !cache.isClosed())
			{
				DistributedSystem distributedSystem = cache.getDistributedSystem();
				
				//Assigned distributed member name
				distributeMemberName = distributedSystem.getDistributedMember().getName();
						
				if(distributedSystem.isConnected()  )
				{				
					MBeanServer jmx = ManagementFactory.getPlatformMBeanServer();
					
					ObjectName on = new ObjectName("GemFire:service=System,type=Distributed");
					
					
					logWriter = distributedSystem.getLogWriter();
					
					logWriter.severe("FUNCTION:SystemDownFunction invoking shutDownAllMembers on member:"+distributeMemberName);
				
					try
					{
						jmx.invoke(on, "shutDownAllMembers", null, null);
					}
					catch(Exception e)
					{
						String message = e.getMessage();
						
						if(message !=  null && message.contains("distributed system has been disconnected"))
						{
							//ignore and just exit JVM
							if(logWriter != null)
							{
								logWriter.warning("FUNCTION:SystemDownFunction shutting down disconnected member:"+distributeMemberName);
							}
							
							System.exit(0);
							
						}
						else
							throw e; //rethrow

						
					}
	
				}
			}
	
		} catch (Exception e) {
			if(logWriter != null) {
				logWriter.warning(e.toString());
			}
		}
		
		if(logWriter != null)
		{
			logWriter.warning("FUNCTION:SystemDownFunction shutting down member:"+distributeMemberName);
		}
		
		System.exit(0);

	}// --------------------------------------------

	/**
	 * @return Shutdown
	 */
	@Override
	public String getId() {
		return "SystemShutDownFunction";
	}

	@Override
	public boolean hasResult() {
		return false;
	}

	@Override
	public boolean isHA() {
		return false;
	}

	@Override
	public boolean optimizeForWrite() {
			return false;
	}

	public void init(Properties arg0)
	{
		// TODO Auto-generated method stub
		
	}
	

}
