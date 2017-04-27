package gedi.solutions.geode.commas.function.command;

import nyla.solutions.commas.Command;
import nyla.solutions.core.util.Debugger;

/**
 * Returns a fixes results from for the
 * @author Gregory Green
 *
 */
public class FixedResponseCommand implements Command<Object,Object>
{
	/**
	 * 
	 */
	//@Override
	public Object execute(Object argument)
	{
		Debugger.dump(argument);
		
		Debugger.println("returning results="+results);
		return results;
	}// ------------------------------------------------
	
	
	/**
	 * @return the results
	 */
	public Object getResults()
	{
		return results;
	}


	/**
	 * @param results the results to set
	 */
	public void setResults(Object results)
	{
		this.results = results;
	}


	private Object results = null;
	

}
