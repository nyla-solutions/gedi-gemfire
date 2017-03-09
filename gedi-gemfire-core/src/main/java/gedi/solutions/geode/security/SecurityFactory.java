package gedi.solutions.geode.security;

import com.gemstone.gemfire.security.AuthInitialize;
import com.gemstone.gemfire.security.Authenticator;

/**
 * SecurityFactory is representable for the create authorization/authentication objects
 * @author Gregory Green
 *
 */
public class SecurityFactory
{
	private SecurityFactory()
	{
	}
	/**
	 * 
	 * @return new CryptionPropertyAuthInitialize()
	 */
	public static AuthInitialize createAuthInitialize()
	{
		//DO NOT USE spring
		return new CryptionPropertyAuthInitialize();
	}// ------------------------------------------------

	/**
	 * 
	 * @return new LDAPAuthenticator()
	 */
	public static Authenticator createAuthenticator()
	{
		return new LDAPAuthenticator();
	}// ------------------------------------------------
}
