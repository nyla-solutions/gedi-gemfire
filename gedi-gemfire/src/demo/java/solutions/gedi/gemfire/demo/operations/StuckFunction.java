package solutions.gedi.gemfire.demo.operations;

import org.springframework.stereotype.Component;

import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;

/**
 * @author uhoh
 *
 */
@Component
public class StuckFunction implements Function
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -447987547385138276L;

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext arg0)
	{
		try
		{
			while(true)
			{
				//Thread.sleep(Integer.MAX_VALUE);
				//do nothing;
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#getId()
	 */
	@Override
	public String getId()
	{
		return "StuckFunction";
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#hasResult()
	 */
	@Override
	public boolean hasResult()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#isHA()
	 */
	@Override
	public boolean isHA()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#optimizeForWrite()
	 */
	@Override
	public boolean optimizeForWrite()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
