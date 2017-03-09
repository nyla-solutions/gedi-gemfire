package gedi.solutions.geode.bridge.function.command;

import java.io.Serializable;
import java.util.Set;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;

import gedi.solutions.geode.bridge.RegionKeyInterpreter;

/**
 * This command calls another bridge based on region function.
 * 
 * @author Gregory Green
 *
 */
public class OnRegionCommand extends OnGridCommand
{  
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Execution constructExection(Serializable argument)
	{
        Region region = this.getCache().getRegion(regionName);
        
        Execution regionExecution = FunctionService.onRegion(region);
		
        
         Set filterKeys = regionKeyInterpreter.toFilter(argument);
        
        if(filterKeys != null && !filterKeys.isEmpty())
        {
        	regionExecution = regionExecution.withFilter(filterKeys);
        }
        
        return regionExecution;
	}// -----------------------------------------------

    /**
	 * @return the regionName
	 */
	public String getRegionName()
	{
		return regionName;
	}

	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}

	/**
	 * @return the regionKeyInterpreter
	 */
	public RegionKeyInterpreter getRegionKeyInterpreter()
	{
		return regionKeyInterpreter;
	}


	/**
	 * @param regionKeyInterpreter the regionKeyInterpreter to set
	 */
	public void setRegionKeyInterpreter(RegionKeyInterpreter regionKeyInterpreter)
	{
		this.regionKeyInterpreter = regionKeyInterpreter;
	}	
	
	private String regionName = null;
	private RegionKeyInterpreter regionKeyInterpreter = null;
	
}
