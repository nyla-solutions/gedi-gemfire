package gedi.solutions.gemfire.integration.cmd;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import nyla.solutions.global.patterns.servicefactory.ServiceFactory;
/*
 * <pre>
 * 	<bean id="MainExecutable" class="nyla.solutions.global.web.controller.jetty.JettyCommandExecutable">
	</bean>
 * </pre>
 */
public class JettyCommandExecutable
{
	/**
	 * 
	 * @see nyla.solutions.global.patterns.command.Command#execute(java.lang.Object)
	 */
	public static void main(String[] args)
	{	
		
		 try
			{
				 if(args.length != 2)
				 {
					 System.err.println(" Required httpPort and handleName arguments");
					 return;
				 }
				 
				

				 int httpPort = Integer.parseInt(args[0]);
				 Server server = new Server(httpPort);
				 
				 String handlerName = args[1];
				 
				 Handler handler = ServiceFactory.getInstance().create(handlerName);
				 
				 server.setHandler(handler);
				 
			     server.start();
			     server.join();
			     
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		 
	}
}
