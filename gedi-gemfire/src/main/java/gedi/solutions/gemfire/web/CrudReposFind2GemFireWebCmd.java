package gedi.solutions.gemfire.web;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.repository.CrudRepository;

import nyla.solutions.global.exception.NoDataFoundException;
import nyla.solutions.global.exception.RequiredException;
import nyla.solutions.global.web.controller.WebCommand;

/**
 * 
 * @author Gregory Green
 *
 */
public class CrudReposFind2GemFireWebCmd implements WebCommand
{
	/**
	 * 
	 * @see nyla.solutions.global.web.controller.WebCommand#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String key = request.getParameter(keyName);
		if(key == null || key.length() == 0)
					throw new RequiredException(keyName);
		
		if(gemfireTemplate.getRegion() == null)
			throw new RequiredException("The configured GemFireTemplate does not have a region");
		
		
		Object object = crudRepository.findOne(key);
		
		if(object == null)
			throw new NoDataFoundException("value with key:"+key);
		
		gemfireTemplate.put(key, object);
		
		response.getWriter().println(responseText);
		return null;
	}// --------------------------------------------------------
	/**
	 * 
	 * @return crud repostory
	 */
	public CrudRepository<Object, Serializable> getCrudRepository()
	{
		return crudRepository;
	}// --------------------------------------------------------
	public void setCrudRepository(
			CrudRepository<Object, Serializable> crudRepository)
	{
		this.crudRepository = crudRepository;
	}// --------------------------------------------------------
	public GemfireTemplate getGemfireTemplate()
	{
		return gemfireTemplate;
	}// --------------------------------------------------------
	public void setGemfireTemplate(GemfireTemplate gemfireTemplate)
	{
		this.gemfireTemplate = gemfireTemplate;
	}
	public String getKeyName()
	{
		return keyName;
	}
	public void setKeyName(String keyName)
	{
		this.keyName = keyName;
	}

	public String getResponseText()
	{
		return responseText;
	}
	public void setResponseText(String responseText)
	{
		this.responseText = responseText;
	}


	private CrudRepository<Object, Serializable> crudRepository;
	private GemfireTemplate gemfireTemplate;
	private String keyName = "key";
	private String responseText = "OK";
}
