package gedi.solutions.geode.bridge.function.command;

import java.io.Serializable;

import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;

/**
 *  This command calls another bridge based on servers function.
 * @author Gregory Green
 *
 */
public class OnServersCommand extends OnGridCommand
{  
	/**
	 * Create execution object
	 * @return FunctionService.onServer(PoolManager.find(poolName));
	 */
	@Override
	protected Execution constructExection(Serializable argument)
	{
    	Pool pool = PoolManager.find(poolName);
        
        return FunctionService.onServers(pool);
	}// -----------------------------------------------
	/**
	 * @return the poolName
	 */
	public String getPoolName()
	{
		return poolName;
	}

	/**
	 * @param poolName the poolName to set
	 */
	public void setPoolName(String poolName)
	{
		this.poolName = poolName;
	}

	private String poolName = null;
}
