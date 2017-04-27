package gedi.solutions.geode.io;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.execute.ResultSender;

import nyla.solutions.core.exception.DataException;


/**
 * Utility class for the processing results
 * @author Gregory Green
 *
 */
public class GemFireIO
{
	private GemFireIO()
	{
		
	}// --------------------------------------------
	/**
	 * Determine if the data should be sent
	 * @param resultSender the result sender implementation
	 * @param data the data
	 * @return true if data is an exception
	 */
	public static boolean isErrorAndSendException(ResultSender<Object> resultSender, Object data)
	{
		if(data instanceof Throwable)
		{
			Throwable e = (Throwable)data;			
			
			resultSender.sendException(e);
			return true;
		}
		
		return false;
	}// --------------------------------------------
	/**
	 * Execute a function with the given execution settings
	 * @param execution the function service execution settings
	 * @param function the function to be execute
	 * @return the flatten results from one or more servers
	 * @throws Exception when remote execution errors occur
	 */
	@SuppressWarnings("unchecked")
	public  static <T> Collection<T> exeWithResults(Execution execution, Function function)
			throws Exception
	{
		ResultCollector<?, ?> resultCollector;
		try 
		{
			resultCollector = execution.execute(function);
		}
		catch (FunctionException e) 
		{
			if(e.getCause() instanceof NullPointerException)
				throw new RuntimeException("Unable to execute function:"+function.getId()+
						" assert hostnames(s) for locators and cache server can be resovled. "+
						" If you do not have access to the host file, create host.properties and add to the CLASSPATH. "+
						" Example: locahost=127.1.0.0 "+
						" also assert that all cache servers have been initialized. Check if the server's cache.xml has all required <initializer>..</initializer> configurations",
						e);
			else
				throw e;
		}
		
		Object resultsObject = resultCollector.getResult();
		
		//Return a result in collection (for a single response)
		Collection<Object> collectionResults = (Collection<Object>)resultsObject;
			
		//if empty return null
		if(collectionResults.isEmpty())
			return null;
		
		Collection<Object> list = new ArrayList<Object>(collectionResults.size());
		
		flatten(collectionResults, list);

					
		return (Collection<T>)list;		
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @param region the region
	 * @return the set of keys
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Collection<T> keySetOnServer(Region<?,?> region)
	{
		try
		{
			if(DataPolicy.EMPTY.equals(region.getAttributes().getDataPolicy()))
					return (Collection)region.keySetOnServer();
			else
				return (Collection)region.query("select * from /"+region.getName()+".keySet()");
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DataException("region:"+region,e);
		}
		
	}//--------------------------------------------------------
	/**
	 * Used to flatten results from multiple servers
	 * @param input the unflatten input
	 * @param flattenOutput the flatten results
	 * @throws Exception if an any input collection items are exceptions
	 */
	@SuppressWarnings("unchecked")
	public static <T> void flatten(Collection<Object> input,
			Collection<Object> flattenOutput)
	throws Exception
	{
		if (input == null || input.isEmpty() || flattenOutput == null)
			return;

		for (Object inputObj : input)
		{
			if(inputObj instanceof Exception )
				throw (Exception)inputObj;
			
			
			
			if(inputObj instanceof Collection)
				flatten((Collection<Object>)inputObj,flattenOutput);
			else
				flattenOutput.add(inputObj);

		}

	}// --------------------------------------------------------	
}
