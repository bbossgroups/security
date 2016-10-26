package org.frameworkset.platform.security.util;

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import org.frameworkset.platform.security.authentication.CheckCallBack;
import org.frameworkset.platform.security.authentication.Credential;
import org.frameworkset.platform.security.authorization.AuthPrincipal;
import com.frameworkset.util.StringUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class CookieUtil implements Serializable{
    private static Logger log = Logger.getLogger(CookieUtil.class);

    /**
     * 定义principal分隔符正则表达式
     * loginMudleName#$#userName^_^loginMudleName1#$#userName1
     */
    private static final String principalRegexExpress = "\\^_\\^";

    private static final String headRegexExpress = "\\^@\\^";
    /**
     * 定义identity分隔符正则表达式
     * loginMudleName#$#userName
     */

    private static final String identityRegexExpress = "\\#\\$\\#";

    private static final String principal_credentialRegexExpress = "\\^\\|\\^";

    public static final String credential_splitRegexExpress = "@\\|@";

    public static final String CREDENTIAL_ATTRIBUTE_SPLIT = "@\\^@";
    public static final String ATTRIBUTE_SPLIT = "@~@";

    public static void main(String[] args) {
        CookieUtil cookieutil = new CookieUtil();
        Object[] t = cookieutil.refactorPricipal("encrypt=false^@^loginMudleName#$#userName^_^loginMudleName1#$#userName1^|^loginMudleName1@^@name=value@~@name1=value1@|@loginMudleName1@^@name=value@~@name1=value1");
//        System.out.println(t.get("loginMudleName"));
//        System.out.println(t.get("loginMudleName1"));
//        System.out.println( t.isEmpty());
//        Iterator it = t.keySet().iterator();
//        while (it.hasNext())
//        {
//            String key = (String)it.next();
//            System.out.println(t.get(key));
//        }
    }

    /**
     * 从cookie中恢复所有的principal信息,如果是加密的cookie，则需要解密
     * @param cookie String 格式为：encrypt#$#false^_^loginMudleName#$#userName^_^loginMudleName1#$#userName1
     * 说明：
     *      encrypt#$#false，标识cookie是否加密，encrypt#$#false标识没有加密，encrypt#$#true表示加密了
     *      loginMudleName#$#userName，标识用户Principal,loginMudleName标识用户隶属登录模块，userName标识用户名称
     * @return Map
     */
    public Object[] refactorPricipal(String cookie)
    {
        log.debug("Refactor Pricipals from Cookie:" + cookie);
        //"encrypt=false^@^loginMudleName#$#userName^_^loginMudleName1#$#userName1^|^loginMudleName1@^@name=value@~@name1=value1@|@loginMudleName1@^@name=value@~@name1=value1"
        String cookieMessages[] = StringUtil.split(cookie,headRegexExpress);
        //cookie头部信息
        String head[] = StringUtil.split(cookieMessages[0],"=");
        //cookie信息体
        String body = cookieMessages[1];
        //判断头部信息是否加密,如果加密则解密密文
        if(head[1].equals("true"))
            body = decrypt(body);

        String principals_credentials[] = StringUtil.split(body,principal_credentialRegexExpress);
        String t_principals = principals_credentials[0];
        String t_credentials = principals_credentials[1];

       
        Principal principalIdxs = null;
        Credential credentialIdxs = null;
        String principals[] = StringUtil.split(t_principals,principalRegexExpress);


        for(int i = 0; principals != null && i < principals.length; i ++)
        {
            String identitys[] = StringUtil.split(principals[i],identityRegexExpress);
            AuthPrincipal authPrincipal = new AuthPrincipal(identitys[1],null,
                     identitys[0]);
            log.debug(authPrincipal);
            principalIdxs = authPrincipal;
            
        }

        String credentials[] = StringUtil.split(t_credentials,credential_splitRegexExpress);
        for(int i = 0; credentials != null && i < credentials.length; i ++)
        {
            String c_messages[] = StringUtil.split(credentials[i],CREDENTIAL_ATTRIBUTE_SPLIT);
            CheckCallBack checkCallBack = new CheckCallBack();
            String attributes[] = StringUtil.split(c_messages[1],ATTRIBUTE_SPLIT);
            for(int j = 0; j < attributes.length; j ++)
            {
                log.debug(attributes[j]);
                String attribute[] = StringUtil.split(attributes[j],"=");
                checkCallBack.setUserAttribute(attribute[0],attribute[1]);
            }

            Credential credential = new Credential(checkCallBack,c_messages[0]);
          
            credentialIdxs = credential;
        }
        return new Object[] {principalIdxs,credentialIdxs};
    }

    /**
     * 解密加密串，根据加密算法对加密串进行解密
     * @param encryptStr String
     * @return String
     */

    public String decrypt(String encryptStr)
    {
        return encryptStr;
    }

    public String encrypt(String decrypt)
    {
        return decrypt;
    }

    public Subject createSubject()
    {
        return new Subject();
    }
}
