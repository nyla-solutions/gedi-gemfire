package solutions.gedi.demo;

import org.springframework.data.gemfire.function.annotation.OnServer;

import nyla.solutions.global.security.user.data.User;

@OnServer
public interface ExampleFunctionService
{
	
	public User findUserByEmail(String email);
}
