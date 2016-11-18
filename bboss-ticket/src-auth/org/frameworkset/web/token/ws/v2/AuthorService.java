/**
 * 
 */
package org.frameworkset.web.token.ws.v2;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.frameworkset.web.auth.AuthenticateResponse;

/**
 * 统一认证服务
 * @author yinbp
 *
 * @Date:2016-11-15 17:18:40
 */
@WebService(name="AuthorService",targetNamespace="org.frameworkset.web.token.ws.v2.AuthorService")
public interface AuthorService {

	/**
	 * 接受加密的报文参数，账号和口令，对参数进行校验
	 * 如果校验通过，则返回用户
	 * @param authtoken
	 * @return
	 */
	public @WebResult(name = "authorResponse", partName = "partAuthorResponse") AuthenticateResponse 
				auth(@WebParam(name = "secret", partName = "partSecret") String authtoken);

}
