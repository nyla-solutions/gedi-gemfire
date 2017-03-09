package gedi.solutions.geode.operations.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultSender;


public class PrintExampleJsonFunction implements Function
{
	/**
	 * 
	 * @see com.gemstone.gemfire.cache.execute.Function#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	public void execute(FunctionContext fc)
	{
		ResultSender<String> rs = fc.getResultSender();
		
		String[] args = (String[]) fc.getArguments();
		try
		{
			
			if(args == null || args.length == 0)
				throw new FunctionException("args");
			
			String className = args[0];
			
			if(className == null || className.length() == 0)
			{
				rs.lastResult("{}");
				return;
			}
		
		
	
			ObjectMapper objectMapper = new ObjectMapper();
			
			Object obj = forClassName(className).newInstance();
			
			rs.lastResult(objectMapper.writeValueAsString(obj));
		}
	
		catch (Exception e)
		{
			CacheFactory.getAnyInstance().getLogger().error(e.toString());
			rs.sendException(e);
		}
	}// --------------------------------------------------------
	private Class<?> forClassName(String className) 
	throws ClassNotFoundException
	{
		if(className == null || className.length() == 0)
			throw new FunctionException("class name string is empty: "); 
		
		
		return Class.forName(className);
	}// --------------------------------------------------------

	public String getId()
	{
		return "PrintExampleJsonFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return false;
	}

	public boolean optimizeForWrite()
	{
		return true;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8579019703991148580L;

}
