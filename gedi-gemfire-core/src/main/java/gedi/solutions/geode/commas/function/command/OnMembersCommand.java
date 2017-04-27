package gedi.solutions.geode.commas.function.command;

import java.io.Serializable;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.distributed.DistributedSystem;

/**
 *  This command calls another bridge based onMembers function.
 * @author Gregory Green
 *
 */
public class OnMembersCommand extends OnGridCommand
{  
	/**
	 * Create the Execution object
	 * @param argument
	 * @return FunctionService.onServer(PoolManager.find(poolName));
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Execution constructExection(Serializable argument)
	{		
		DistributedSystem ds = this.getCache().getDistributedSystem();
		
		return FunctionService.onMembers(ds);

	}// -----------------------------------------------
	
	
}
