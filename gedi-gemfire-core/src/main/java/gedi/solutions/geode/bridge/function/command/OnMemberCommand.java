package gedi.solutions.geode.bridge.function.command;
import java.io.Serializable;

import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.distributed.DistributedSystem;

/**
 *  This command calls another bridge based onMember function.
 * @author Gregory Green
 *
 */
public class OnMemberCommand extends OnGridCommand
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
		
    	return FunctionService.onMember(ds, ds.getDistributedMember());
	}// -----------------------------------------------
	
}
