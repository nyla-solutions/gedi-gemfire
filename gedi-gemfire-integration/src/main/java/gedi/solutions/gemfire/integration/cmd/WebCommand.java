package gedi.solutions.gemfire.integration.cmd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* 
*<b>Command</b> is a service based HTTP interface for  executing
* Web based business operations
*/


public interface WebCommand
{
   public static final String COMMAND_PARAM_NAME = "cmd";
   
  /**
   * Abstract method for executing a application command
   * @param request the HTTP request object
   * @param request the HTTP response object
   */
  public String execute(HttpServletRequest request,HttpServletResponse response)
  throws Exception;  
}