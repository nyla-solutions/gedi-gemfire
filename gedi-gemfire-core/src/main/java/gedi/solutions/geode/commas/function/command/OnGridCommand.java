package gedi.solutions.geode.commas.function.command;


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultCollector;

import nyla.solutions.commas.Command;
import nyla.solutions.core.data.Envelope;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;



public abstract class OnGridCommand implements Command<Object,Object>
{
	/**
	 * Calls executeOnGrid((Serializable)envFuncContextArrayObj[0],(FunctionContext)envFuncContextArrayObj[1]);
	 * @param envFuncContextArrayObj
	 */
	//@Override
	public Object execute(Object envFuncContextArrayObj)
	{
		Object[] array = (Object[])envFuncContextArrayObj;
		
		return executeOnGrid((Serializable)array[0],(FunctionContext)array[1]);
	}// -----------------------------------------------
	/**
	 * Execution the function of a grid (onServer or onRegion call)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public final Object executeOnGrid(Serializable payload, FunctionContext functionContext)
    {
  		Execution execution = constructExection(payload);
		
        Envelope<Object> env = new Envelope();
        
        Map<Object,Object> header= new HashMap<Object,Object>();
        	
        //Add argument as payload
        env.setPayload(payload);
        
        //get original argument
        if(functionContext != null)
        {
            Serializable orgArg = (Serializable) functionContext.getArguments();
            if(orgArg instanceof Envelope)
            {
            	//Add all header
            	Map<Serializable,Serializable> orgHeader = ((Envelope)orgArg).getHeader();
            	if(orgHeader != null)
            	{
            		//copy original data
            		header.putAll(orgHeader);	
            	}
            }
            header.put("GediConstants.ORGINAL_FUNCTION_ID_HEADER", functionContext.getFunctionId());
            header.put("GediConstants.ORGINAL_ARGUMENT_HEADER",orgArg);
        }
        
        header.put("GediConstants.FUNCTION_NAME_HEADER", this.onGridFunctionCommandName);
        
        env.setHeader(header);
        Debugger.println(this,"header="+header);
        
        Debugger.println(this,"Executing gridFunctionName="+gridFunctionName);
        
        ResultCollector collector = execution
            .withArgs(env)
            .execute(this.gridFunctionName);
         
        
        Serializable responseResults = (Serializable)collector.getResult();
		
		if(responseResults instanceof Collection<?> )
		{
			//Return a result in collection (for a single response)
			Collection<?> collectionResults = (Collection<?>)responseResults;
			
			//if empty return null
			if(collectionResults.isEmpty())
				return null;
			else if(collectionResults.size() == 1)
				return (Serializable)collectionResults.iterator().next();
		}
		
		return responseResults;
    }// ------------------------------------------------
	/**
	 * Create the Execution object
	 * @param argument
	 * @return
	 */
	protected abstract Execution constructExection(Serializable argument);

	
	protected Serializable constructResults(Serializable responseResults)
	{
		if(responseResults instanceof Collection<?> )
		{
			//Return a result in collection (for a single response)
			Collection<?> collectionResults = (Collection<?>)responseResults;
			
			//if empty return null
			if(collectionResults.isEmpty())
				return null;
			else if(collectionResults.size() == 1)
				return (Serializable)collectionResults.iterator().next();
		}
		return null;
	}// -----------------------------------------------
	/**
	 * Return Grid Function Name
	 * @return the gridFunctionName
	 */
	public String getGridFunctionName()
	{
		return gridFunctionName;
	}// -----------------------------------------------
	/**
	 * 
	 * @return the singleton cache
	 */
	protected Cache getCache()
	{
		if(cache  == null)
			cache = CacheFactory.getAnyInstance();
		
		return cache;
			
	}// -----------------------------------------------
	/**
	 * Return on Grid Function Command Name
	 * @return the onGridFunctionCommandName
	 */
	public String getOnGridFunctionCommandName()
	{
		return onGridFunctionCommandName;
	}
	private static Cache cache = null;
	
	/**
	 * @param onGridFunctionCommandName the onGridFunctionCommandName to set
	 */
	public void setOnGridFunctionCommandName(String onGridFunctionCommandName)
	{
		this.onGridFunctionCommandName = onGridFunctionCommandName;
	}

	private String onGridFunctionCommandName = null;
	/**
	 * @param gridFunctionName the gridFunctionName to set
	 */
	public void setGridFunctionName(String gridFunctionName)
	{
		this.gridFunctionName = gridFunctionName;
	}
	private String gridFunctionName = Config.getProperty(OnGridCommand.class,"gridFunctionName","default");
}
