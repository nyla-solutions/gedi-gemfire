package gedi.solutions.geode.operations.functions;

import java.util.Properties;
import java.util.Set;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;


/**
 * Deletes all entries in a given region
 * @author Gregory Green
 *
 */
public class ClearRegionFunction implements Function, Declarable
{


	/**
	 * @see com.gemstone.gemfire.cache.Declarable#init(java.util.Properties)
	 */
	@Override
	public void init(Properties arg0)
	{
	}

	/**
	 * Return the number of records deleted
	 * @see com.gemstone.gemfire.cache.execute.Function#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext functionContext)
	{
		LogWriter logWriter = CacheFactory.getAnyInstance().getLogger();
		
		
		if(!(functionContext instanceof RegionFunctionContext))
		{
			throw new IllegalArgumentException("onRegion execution required");
		}
		
		ResultSender<Integer> sender = functionContext.getResultSender();
		
		RegionFunctionContext rfc = (RegionFunctionContext)functionContext;
		
		Region<Object,Object> region = rfc.getDataSet();
		
		logWriter.warning("Executing "+this.getClass().getName()+"  for region:"+region.getFullPath());
		
		Set<Object> keySet = region.keySet();
		
		
		int removeCount = 0;
		
		if(keySet == null)
		{
			sender.lastResult(removeCount);
			return;
		}
		

		for (Object key : keySet)
		{
			region.remove(key);
			removeCount++;
		}
		
		sender.lastResult(removeCount);
		
	}// --------------------------------------------------------

	/**
	 * @see com.gemstone.gemfire.cache.execute.Function#getId()
	 */
	@Override
	public String getId()
	{
		return "ClearRegionFunction";
	}

	/**
	 * @see com.gemstone.gemfire.cache.execute.Function#hasResult()
	 */
	@Override
	public boolean hasResult()
	{
		return true;
	}

	/**
	 * @see com.gemstone.gemfire.cache.execute.Function#isHA()
	 */
	@Override
	public boolean isHA()
	{
		return false;
	}

	/**
	 * @see com.gemstone.gemfire.cache.execute.Function#optimizeForWrite()
	 */
	@Override
	public boolean optimizeForWrite()
	{
		return false;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 2608607529152147249L;
}
