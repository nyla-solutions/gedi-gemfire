package solutions.gedi.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import solutions.gedi.gemfire.demo.functions.client.ClientConfig;
import nyla.solutions.global.security.user.data.User;


public class ExampleClient
{
	
	public ExampleClient()
	{
		appContext = new AnnotationConfigApplicationContext(ClientConfig.class);
	}// --------------------------------------------------------
	
	public User getUSer(String email)
	{
		try
		{
			ExampleFunctionService userService = appContext.getBean(ExampleFunctionService.class);
			return userService.findUserByEmail(email);
		}
		finally
		{
			appContext.close();
		}
	}// --------------------------------------------------------
	private AnnotationConfigApplicationContext appContext = null;
}
