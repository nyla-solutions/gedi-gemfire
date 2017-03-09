package gedi.solutions.gemfire.integration.cmd;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.repository.CrudRepository;

import nyla.solutions.global.exception.RequiredException;

/**
 * 
 * @author Gregory Green
 *
 */
public abstract class AbstractGemIntCmd implements WebCommand
{
	/**
	 * 
	 * @see nyla.solutions.global.web.controller.WebCommand#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public final String execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		final  String key = request.getParameter(keyName);
		if(key == null || key.length() == 0)
					throw new RequiredException(keyName);
		
		
		if(gemfireTemplate.getRegion() == null)
			throw new RequiredException("The configured GemFireTemplate does not have a region");
		
		//Select JAP record and save to Gemfire
		this.executorService.execute(new Runnable()
		{
			public void run()
			{
				//Used to prevents double updates
				if(inFlightMap != null)
				{
							//check if inflight
							inFlightMap.put(key,Boolean.TRUE);
				}
				
				process(key);
				
				if(inFlightMap != null)
				{
					//check if inflight
					inFlightMap.remove(key);
				}
			}
		});		
				
		response.getWriter().println(responseText);
		return null;
	}// --------------------------------------------------------
	protected abstract void process(Serializable key);
	
	/**
	 * 
	 * @return
	 */
	public final CrudRepository<Object, Serializable> getCrudRepository()
	{
		return crudRepository;
	}// --------------------------------------------------------
	/**
	 * 
	 * @param crudRepository
	 */
	public final void setCrudRepository(
			CrudRepository<Object, Serializable> crudRepository)
	{
		this.crudRepository = crudRepository;
	}// --------------------------------------------------------
	public final GemfireTemplate getGemfireTemplate()
	{
		return gemfireTemplate;
	}// --------------------------------------------------------
	public final void setGemfireTemplate(GemfireTemplate gemfireTemplate)
	{
		this.gemfireTemplate = gemfireTemplate;
	}
	public final String getKeyName()
	{
		return keyName;
	}
	public final void setKeyName(String keyName)
	{
		this.keyName = keyName;
	}

	public String getResponseText()
	{
		return responseText;
	}
	public final void setResponseText(String responseText)
	{
		this.responseText = responseText;
	}
	public final Map<Object, Object> getInFlightMap()
	{
		return inFlightMap;
	}
	public final void setInFlightMap(Map<Object, Object> inFlightMap)
	{
		this.inFlightMap = inFlightMap;
	}
	
	public final ExecutorService getExecutorService()
	{
		return executorService;
	}
	public final void setExecutorService(ExecutorService executorService)
	{
		this.executorService = executorService;
	}
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private CrudRepository<Object, Serializable> crudRepository;
	private GemfireTemplate gemfireTemplate;
	private Map<Object,Object> inFlightMap;
	private String keyName = "key";
	private String responseText = "OK";
}
