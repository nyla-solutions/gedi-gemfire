package gedi.solutions.gemfire.integration.cmd;

import java.io.Serializable;
import nyla.solutions.global.util.Debugger;

/**
 * 
 * @author Gregory Green
 *
 */
public class SaveFromRdms2GemCmd extends AbstractGemIntCmd
{
	/**
	 * 
	 * @see nyla.solutions.global.web.controller.WebCommand#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void process(Serializable key) 
	{		
		Object object =  this.getCrudRepository().findOne(key);
				
		if(object != null)
		{
			Debugger.println(this,"Saving key:"+key+" ");
			this.getGemfireTemplate().put(key, object);
		}
		else
		{
			Debugger.println(this,"Removing key:"+key+" ");
			this.getGemfireTemplate().remove(key);
		}			
	}// --------------------------------------------------------
}
