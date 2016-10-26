package org.frameworkset.platform.security;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.security.authorization.AccessException;

/**
 * 应用自定义的权限扩展
 * @author biaoping.yin
 *
 */
public interface PermissionModule {
	public final String USER_NAME_KEY = "USER_NAME";
	public final String USER_PASSWORD_KEY = "USER_PASSWORD";
	/**
	 * 优先执行，如果返回true，说明用户可以直接访问对应资源，不需要进行后续的权限检查
	 * @param userAccount
	 * @param resourceID
	 * @param action
	 * @param resourceType
	 * @return
	 */
	public boolean checkPermission(String userAccount,String resourceID, String action,
			String resourceType) ;
	/**
	 * 优先执行，如果返回true，说明用户可以直接访问对应资源，不需要进行后续的权限检查
	 * @param principal
	 * @param resourceID
	 * @param action
	 * @param resourceType
	 * @return
	 */
	public boolean checkPermission(Principal principal,String resourceID, String action,
			String resourceType) ;
	/**
	 * 判断用户是否是部门管理员
	 * @param userAccount
	 * @return
	 */
	public boolean isOrgManager(String userAccount) ;
	
	/**
	 * 通过账号获取用户id
	 * @param name
	 * @return
	 */
	public  String getUserIDByUserAccount(String name) ;
	public boolean isOrganizationManager(String userAccount,String orgId) ;
	public boolean isSubOrgManager(String userAccount,String orgId);
	/**
	 * 获取用户所属机构id
	 * @return
	 */
	public String getChargeOrgId(String userAccount);
	/**
	 * 获取用户主管部门长
	 * @param org
	 * @return
	 */
	public String getOrgLeader(String org);
	/**
	 * 获取用户口令有效期
	 * @param userAccount
	 * @return
	 */
	public int getUserPasswordDualTimeByUserAccount(String userAccount);
	/**
	 * 获取用户口令过期时间
	 * @param userAccount
	 * @return
	 */
	public Date getPasswordExpiredTimeByUserAccount(String userAccount);
	/**
	 * 特殊用户特殊对待，返回ip对应得用户，则用户可以直接登录系统
	 * @param ip
	 * @return
	 */
	public String isSpesialUser(String ip);
	/**
	 * 返回用户的口令明文
	 * @param userName
	 * @return
	 */
	public String getUserPassword(String userName);
	/**
	 * 图形校验码校验
	 * HttpServletRequest request
	 * String rand = request.getParameter("rand");
	 *  String session_rand = (String) session
                                    .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
                            session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
                            if (session_rand == null || (!session_rand.equalsIgnoreCase(rand))) {
                                throw new AccessException("验证码错误!");
                            }
	 * @param code
	 * @param session
	 * @return
	 */
	public boolean validatecode(HttpServletRequest request) throws AccessException;
	/**
	 * 明文口令
	 * @param worknumber
	 * @return
	 */
	public Map getUserNameAndPasswordByWorknumber(String worknumber);
}
