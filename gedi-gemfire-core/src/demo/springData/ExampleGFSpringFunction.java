package solutions.gedi.demo.springData;



import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import nyla.solutions.global.security.user.data.User;
import nyla.solutions.global.security.user.data.UserProfile;

import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;

@SuppressWarnings("serial")
@Component
public class ExampleGFSpringFunction implements  Function
{
	@Resource(name="productTemplate")
	private GemfireTemplate productTemplate; 
	
	@Resource(name="Order")
	private com.gemstone.gemfire.cache.Region<String,User> userRegion;

	@Override
	public void execute(FunctionContext functionContext)
	{
		ResultSender<User> resultSender = functionContext.getResultSender();
		
		
		UserProfile user = new UserProfile();
		user.setEmail("test@gopivotal.com");
		resultSender.lastResult(user);
	}// --------------------------------------------------------

	@Override
	public String getId()
	{
		return ExampleGFSpringFunction.class.getName();
	}

	@Override
	public boolean hasResult()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHA()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean optimizeForWrite()
	{
		// TODO Auto-generated method stub
		return false;
	}
	

}
