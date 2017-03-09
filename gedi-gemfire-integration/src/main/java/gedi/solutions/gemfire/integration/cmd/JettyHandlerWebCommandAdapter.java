package gedi.solutions.gemfire.integration.cmd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nyla.solutions.global.exception.RequiredException;
import nyla.solutions.global.patterns.servicefactory.ServiceFactory;
import nyla.solutions.global.util.Debugger;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class JettyHandlerWebCommandAdapter extends AbstractHandler
{

	/**
	 * 
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request,
	 *  javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void handle(String target, Request request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException, ServletException
			{
		
		try
		{
			//get command name
			String cmd = httpRequest.getParameter(this.cmdParameter);
			
			if(cmd == null || cmd.length() == 0)
						throw new RequiredException("cmd");
			
			WebCommand webCommand = ServiceFactory.getInstance().create(cmd);
			
			webCommand.execute(httpRequest, httpResponse);
		}
		catch (Exception e)
		{
			httpResponse.setContentType("text/html; charset=utf-8");
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			httpResponse.getWriter().println(Debugger.stackTrace(e));
		}
		
		request.setHandled(true);
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public String getCmdParameter()
	{
		return cmdParameter;
	}

	public void setCmdParameter(String cmdParameter)
	{
		this.cmdParameter = cmdParameter;
	}

	private String cmdParameter = "cmd";
	

}
