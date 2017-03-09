package gedi.solutions.gemfire.integration;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nyla.solutions.global.patterns.servicefactory.ServiceFactory;
import nyla.solutions.global.util.Debugger;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.data.repository.CrudRepository;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;

/**
 * Example URL
 * 
 * http://localhost:9090/?key=2d554dbe-a55b-48a8-8994-f464b936c274&crudRepository=employeeRepository&regionName=employees
 * 
 * @author Gregory Green
 *
 */
public class GemIntegrationApp extends AbstractHandler
{
	public GemIntegrationApp()
	{
	}

	/**
	 * 
	 * @param args arguments must cannot the port number
	 */
	public static void main(String[] args)
	{
		 try
		{
			 if(args.length != 1)
			 {
				 System.err.println("Usage gemIntegration.py httpPort");
				 return;
			 }
			 
			 new ClientCacheFactory().create();
			 
			 ServiceFactory.getInstance();
			 

			 int httpPort = Integer.parseInt(args[0]);
			 Server server = new Server(httpPort);
			 server.setHandler(GemIntegrationApp.getInstance());
		      server.start();
		      server.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}// --------------------------------------------------------

	/**
	 * 
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void handle(String target, Request request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException, ServletException
			{
		
		try
		{
			String key = request.getParameter("key");
			
			Enumeration<String> names = httpRequest.getParameterNames();
			StringBuilder params = new StringBuilder();
			while(names.hasMoreElements())
			{
				params.append(names.nextElement()).append(",");
			}
			
			
			if( key == null || key.length() == 0)
				throw new IllegalArgumentException("key parameter required in params:"+params+" queryString:"+request.getQueryString()+" path:"+request.getContextPath());
			
			String crudRepository = request.getParameter("crudRepository");
			
			if( crudRepository == null || crudRepository.length() == 0)
				throw new IllegalArgumentException("crudRepository parameter required");
			
			
			
			String regionName = request.getParameter("regionName");
			
			if( regionName == null || regionName.length() == 0)
				throw new IllegalArgumentException("regionName parameter required");
			
			httpResponse.setContentType("text/html; charset=utf-8");
			httpResponse.setStatus(HttpServletResponse.SC_OK);
			httpResponse.getWriter().println("OK");
			
			service.execute(new SyncGemFireRunner(key,crudRepository,regionName));
		}
		catch (Exception e)
		{
			httpResponse.setContentType("text/html; charset=utf-8");
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			httpResponse.getWriter().println(Debugger.stackTrace(e));
		}
		
		request.setHandled(true);
		
	}// --------------------------------------------------------
	class SyncGemFireRunner implements Runnable
	{
		SyncGemFireRunner(Serializable key,
		 String crudRepositoryName,
		String regionName)
		{
			this.key = key;
			this.crudRepositoryName = crudRepositoryName;
			this.regionName = regionName;
		}
		
		/**
		 * 
		 * @param key
		 * @param crudRepositoryName
		 * @param regionName
		 */
		public void run()
		{
			try
			{
				Thread.sleep(delay);
				
				if(GemIntegrationApp.getInstance().contains(key))
					return;
				
				CrudRepository<Object, Serializable> crudRepository = ServiceFactory.getInstance().create(crudRepositoryName);
				
				Region<Object,Object> region = CacheFactory.getAnyInstance().getRegion(regionName);
				
				
				GemIntegrationApp.getInstance().getInflight().put(key, "");
				Object newValue = crudRepository.findOne(key);
				
				
				Object oldValue = region.get(key);
				
				if(oldValue == null || !oldValue.equals(newValue))
				{
					region.put(key, newValue);
				}
				
				GemIntegrationApp.getInstance().getInflight().remove(key);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}// --------------------------------------------------------
		
		final Serializable key;
		final String crudRepositoryName;
		final String regionName;
	}
	
	
	
	synchronized Map<Serializable, Object> getInflight()
	{
		return inflight;
	}// --------------------------------------------------------



	public static synchronized GemIntegrationApp getInstance()
	{
		if(instance == null)
			instance= new GemIntegrationApp();
		
		return instance;
	}
	private static GemIntegrationApp instance =null;
	private final Map<Serializable, Object> inflight = new HashMap<Serializable, Object>();
	private int delay = 100;
	private ExecutorService service = Executors.newCachedThreadPool();
}
