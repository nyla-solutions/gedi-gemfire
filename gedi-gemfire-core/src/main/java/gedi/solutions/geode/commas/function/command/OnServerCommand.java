package gedi.solutions.geode.commas.function.command;
import java.io.Serializable;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;


/**
 *  This command calls another bridge based on server function.
 * @author Gregory Green
 *
 */
public class OnServerCommand extends OnGridCommand
{  
	/**
	 * Create execution object
	 * @return FunctionService.onServer(PoolManager.find(poolName));
	 */
	@Override
	protected Execution constructExection(Serializable argument)
	{
    	Pool pool = PoolManager.find(poolName);
        
        return FunctionService.onServer(pool);
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
