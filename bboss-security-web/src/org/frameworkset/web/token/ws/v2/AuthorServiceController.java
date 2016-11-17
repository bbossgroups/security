/**
 * 
 */
package org.frameworkset.web.token.ws.v2;

import javax.jws.WebService;

import org.frameworkset.util.annotations.ResponseBody;
import org.frameworkset.web.auth.AuthenticatePlugin;
import org.frameworkset.web.auth.AuthenticateResponse;
import org.frameworkset.web.token.TokenHelper;

/**
 * @author yinbp
 *
 * @Date:2016-11-16 09:45:12
 */
@WebService(name="AuthorService",targetNamespace="org.frameworkset.web.token.ws.v2.AuthorService")
public class AuthorServiceController implements AuthorService {

	/**
	 * 
	 */
	public AuthorServiceController() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.frameworkset.web.token.ws.v2.AuthorService#auth(java.lang.String)
	 */
	@Override
	public @ResponseBody(datatype="jsonp") AuthenticateResponse auth(String authtoken) {
		AuthenticatePlugin authenticatePlugin = TokenHelper.getTokenService().getAuthenticatePlugin();
		
		return authenticatePlugin.login(authtoken);
	}

}
