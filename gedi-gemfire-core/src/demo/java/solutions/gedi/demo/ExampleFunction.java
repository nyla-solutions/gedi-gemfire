package solutions.gedi.demo;

import javax.annotation.Resource;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.stereotype.Component;
import nyla.solutions.global.security.user.data.User;
import nyla.solutions.global.util.Debugger;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.SelectResults;


@Component
public class ExampleFunction
{

	@Resource(name="userTemplate")
	private GemfireTemplate userTemplate; 
	@Resource(name="Users")
	private Region<String,User> userRegion;
 

	@GemfireFunction
	public User findUserByEmail(String email) 
	{
		
		Debugger.println("searching for user name '" + email + "'");
		
		SelectResults<User> results = userTemplate.query("name = '" + email + "'");

		if (results.isEmpty()) {
			Debugger.printWarn("cannot find  '" + email + "'");
			return null;
		}
		
		User user = results.asList().get(0);
		
		return user;
	}// --------------------------------------------------------

}
