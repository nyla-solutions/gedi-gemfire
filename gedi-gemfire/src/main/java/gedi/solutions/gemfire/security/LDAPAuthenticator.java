package gedi.solutions.gemfire.security;

import java.security.Principal;
import java.util.Properties;

import nyla.solutions.global.ds.LDAP;
import nyla.solutions.global.util.Config;
import nyla.solutions.global.util.Cryption;
import nyla.solutions.global.util.Debugger;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.security.AuthenticationFailedException;
import com.gemstone.gemfire.security.Authenticator;

/**
 * The Authenticator instance is called during the client cache connection for security. 
 * @author Gregory Green
 *
 */
public class LDAPAuthenticator implements Authenticator, SecurityConstants
{
	//@Override
	public void close()
	{

	}// ------------------------------------------------
	/**
	 * The Authenticator will construct the initial directory context. 
	 * The security credentials (username/password) will be provided to the context.
	 * @param properties the input login properties
	 * @param distributedMember the distribute member information 
	 * @return the security credentials
	 */
	//@Override
	public Principal authenticate(Properties properties, DistributedMember distributedMember)
			throws AuthenticationFailedException
	{
		if(properties == null)
			    	throw new AuthenticationFailedException("properties not provided");
		
		String userName = properties.getProperty(USERNAME_PROP);
	    if (userName == null) {
	      throw new AuthenticationFailedException(
	          LDAPAuthenticator.class.getName()+" user name property ["
	              + USERNAME_PROP + "] not provided");
	    }
	    String passwd = properties.getProperty(PASSWORD_PROP);
	    if (passwd == null || passwd.length() == 0) 
	    {
	      throw new AuthenticationFailedException(LDAPAuthenticator.class.getName()+" password property ["
	              + PASSWORD_PROP + "] not provided");
	    }
	    
	    try
	    {
		    //check if password prefixed with cryption
		    passwd = Cryption.interpret(passwd);	    	
		    Principal principal =  LDAP.authenicateUID(userName, passwd.toCharArray());
		    
		    Debugger.println(this,principal);
		    
		    return principal;
	    }
	    catch (Exception e) 
	    {
	      Debugger.printInfo(e);
	      throw new AuthenticationFailedException(
	          "LdapUserAuthenticator: Failure with provided username, password "
	              + "combination for user name: " + userName);
	    }
	    
	}// ------------------------------------------------

	//@Override
	public void init(Properties arg0, LogWriter arg1, LogWriter arg2)
			throws AuthenticationFailedException
	{
	}// ------------------------------------------------
	/**
	 * @return the mustEncryptPassword
	 */
	public boolean isMustEncryptPassword()
	{
		return mustEncryptPassword;
	}// ------------------------------------------------
	/**
	 * 
	 * @param mustEncryptPassword the mustEncryptPassword to set
	 */
	public void setMustEncryptPassword(boolean mustEncryptPassword)
	{
		this.mustEncryptPassword = mustEncryptPassword;
	}// ------------------------------------------------

	private boolean mustEncryptPassword = Config.getPropertyBoolean(LDAPAuthenticator.class,"mustEncryptPassword",false);
}
