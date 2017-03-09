package solutions.gedi.gemfire.demo.functions.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;

import nyla.solutions.global.security.user.data.User;
import nyla.solutions.global.security.user.data.UserProfile;
 
@ImportResource("client-cache-config.xml")
@EnableGemfireFunctionExecutions(basePackages="solutions.gedi.demo")
@Configuration
public class ClientConfig {
	
	@Bean
	public User getUSer()
	{
		return new UserProfile();
	}

}
