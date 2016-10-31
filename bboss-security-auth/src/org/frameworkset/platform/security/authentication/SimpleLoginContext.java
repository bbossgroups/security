/*
 *  Copyright 2008 bbossgroups
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.frameworkset.platform.security.authentication;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.LoginModuleInfoQueue;
import org.frameworkset.platform.config.model.LoginModuleInfo;

/**
 * <p>Title: SimpleLoginContext.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年5月8日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class SimpleLoginContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LoginContext.class);
    private static final String INIT_METHOD		= "initialize";
    private static final String LOGIN_METHOD		= "login";
    private static final String COMMIT_METHOD		= "commit";
    private static final String ABORT_METHOD		= "abort";
    private static final String LOGOUT_METHOD		= "logout";
    private static final String OTHER			= "other";
    private static final String DEFAULT_HANDLER		=
                                "auth.login.defaultCallbackHandler";
    /**应用名称*/
    private String appName;
    /**模块名称*/
    private String moduleName;
    private Subject subject = null;
    private boolean subjectProvided = false;
    private boolean loginSucceeded = false;
    private transient CallbackHandler callbackHandler;
    private ACLLoginModule module = null;
    private Map state = new HashMap();

    private LoginModuleInfoQueue moduleQueue;


    private transient ClassLoader contextClassLoader = null;
    private static final Class[] PARAMS = { };

//    private static final sun.security.util.Debug debug =
//        sun.security.util.Debug.getInstance("logincontext", "\t[LoginContext]");

    /**
     * 初始化系统环境上下文
     * @param appName String
     * @param moduleName String
     */
    public void initContext(String appName,String moduleName)
    {
        this.appName = appName;
        this.moduleName = moduleName;
    }
    /**
     * 初始化登录模块
     * @param name String
     * @throws LoginException
     */
    private void init(String name) throws LoginException {

       

       
        
        moduleQueue = ConfigManager.getInstance().getDefaultApplicationInfo().getLoginModuleInfos();
        

        LoginModuleInfo moduleInfo = this.moduleQueue.getLoginModuleInfo(0);
		
        

        if (subject == null) {
            subject = new Subject();
        }
        
   
        		
        

            try {


                // instantiate the LoginModule


               
              

            // instantiate the LoginModule

            Class c = Class.forName
                        (moduleInfo.getLoginModule());

            Constructor constructor = c.getConstructor(PARAMS);
            Object[] args = { };

            // allow any object to be a LoginModule
            // as long as it conforms to the interface
            module = (ACLLoginModule)constructor.newInstance(args);
            
            //检测登录模块是否是ACLLoginModule类型的模块，如果是设置登录模块的名称
                         
        	module.setLoginModuleName(moduleInfo.getName());
        	module.setRegistTable(moduleInfo.getRegistTable());
              module.initialize(subject, callbackHandler);

        }
            catch (InstantiationException ie) {
                //ie.printStackTrace();
                throw new LoginException(ResourcesMgr.getString
                        ("unable to instantiate LoginModule: ") +
                        ie.getMessage());
            } catch (ClassNotFoundException cnfe) {
                //cnfe.printStackTrace();
                throw new LoginException(ResourcesMgr.getString
                        ("unable to find LoginModule class: ") +
                        cnfe.getMessage());
            } catch (IllegalAccessException iae) {
                //iae.printStackTrace();
                throw new LoginException(ResourcesMgr.getString
                        ("unable to access LoginModule: ") +
                        iae.getMessage());
            }  
        // get the Configuration
//        if (config == null) {
//            config = (Configuration)java.security.AccessController.doPrivileged
//                (new java.security.PrivilegedAction() {
//                public Object run() {
//                    return Configuration.getConfiguration();
//                }
//            });
//        }
            catch (IllegalArgumentException e) {
            	  //iae.printStackTrace();
                throw new LoginException(ResourcesMgr.getString
                        ("IllegalArgumentException: ") +
                        e);
			} catch (InvocationTargetException e) {
				  //iae.printStackTrace();
                throw new LoginException(ResourcesMgr.getString
                        ("InvocationTargetException: ") +
                        e.getTargetException());
			} catch (NoSuchMethodException e) {
				 throw new LoginException(ResourcesMgr.getString
	                        ("NoSuchMethodException: ") +
	                        e);
			} catch (SecurityException e) {
				throw new LoginException(ResourcesMgr.getString
                        ("SecurityException: ") +
                        e);
			}

        // get the LoginModules configured for this application
//        AppConfigurationEntry[] entries = config.getAppConfigurationEntry(name);
//        if (entries == null) {
//
//            if (sm != null) {
//                sm.checkPermission(new AuthPermission
//                                ("createLoginContext." + OTHER));
//            }
//
//            entries = config.getAppConfigurationEntry(OTHER);
//            if (entries == null) {
//                MessageFormat form = new MessageFormat(ResourcesMgr.getString
//                        ("No LoginModules configured for name"));
//                Object[] source = {name};
//                throw new LoginException(form.format(source));
//            }
//        }
       


//        contextClassLoader = Thread.currentThread().getContextClassLoader();
//                (ClassLoader)java.security.AccessController.doPrivileged
//                (new java.security.PrivilegedAction() {
//                public Object run() {
//                    return Thread.currentThread().getContextClassLoader();
//                }
//        });
    }

    

   
   


    /**
     * Constructor for the <code>LoginContext</code> class.
     *
     * <p> Initialize the new <code>LoginContext</code> object with a name
     * and a <code>CallbackHandler</code> object.
     *
     * <p> <code>LoginContext</code> uses the name as the index
     * into the <code>Configuration</code> to determine which LoginModules
     * should be used.  If the provided name does not match any in the
     * <code>Configuration</code>, then the <code>LoginContext</code>
     * uses the default <code>Configuration</code> entry, "<i>other</i>".
     * If there is no <code>Configuration</code> entry for "<i>other</i>",
     * then a <code>LoginException</code> is thrown.
     *
     * <p> <code>LoginContext</code> passes the <code>CallbackHandler</code>
     * object to configured LoginModules so they may communicate with the user.
     * The <code>CallbackHandler</code> object therefore allows LoginModules to
     * remain independent of the different ways applications interact with
     * users.  This <code>LoginContext</code> must wrap the
     * application-provided <code>CallbackHandler</code> in a new
     * <code>CallbackHandler</code> implementation, whose <code>handle</code>
     * method implementation invokes the application-provided
     * CallbackHandler's <code>handle</code> method in a
     * <code>java.security.AccessController.doPrivileged</code> call
     * constrained by the caller's current <code>AccessControlContext</code>.
     *
     * <p> Since no <code>Subject</code> can be specified to this constructor,
     * it instantiates a <code>Subject</code> itself.
     *
     * <p>
     *
     * @param name the name used as the index into the
     *		<code>Configuration</code>. <p>
     *
     * @param callbackHandler the <code>CallbackHandler</code> object used by
     *		LoginModules to communicate with the user.
     *
     * @exception LoginException if the specified <code>name</code>
     *          does not appear in the <code>Configuration</code>
     *          and there is no <code>Configuration</code> entry
     *          for "<i>other</i>", or if the specified
     *		<code>callbackHandler</code> is <code>null</code>.
     */
    public SimpleLoginContext(String name, CallbackHandler callbackHandler)
    throws LoginException {
    	 if (callbackHandler == null)
             throw new LoginException(ResourcesMgr.getString
                                 ("invalid null CallbackHandler provided"));
         this.callbackHandler = callbackHandler;
        init(name);
       
    }
    public SimpleLoginContext(String name, CallbackHandler callbackHandler,Subject subject)
    	    throws LoginException {
    	    	 if (callbackHandler == null)
    	             throw new LoginException(ResourcesMgr.getString
    	                                 ("invalid null CallbackHandler provided"));
    	         this.callbackHandler = callbackHandler;
    	         this.subject = subject;
    	        init(name);
    	       
    	    }
    

    /**
     * Perform the authentication and, if successful,
     * associate Principals and Credentials with the authenticated
     * <code>Subject</code>.
     *
     * <p> This method invokes the <code>login</code> method for each
     * LoginModule configured for the <i>name</i> provided to the
     * <code>LoginContext</code> constructor, as determined by the login
     * <code>Configuration</code>.  Each <code>LoginModule</code>
     * then performs its respective type of authentication
     * (username/password, smart card pin verification, etc.).
     *
     * <p> This method completes a 2-phase authentication process by
     * calling each configured LoginModule's <code>commit</code> method
     * if the overall authentication succeeded (the relevant REQUIRED,
     * REQUISITE, SUFFICIENT, and OPTIONAL LoginModules succeeded),
     * or by calling each configured LoginModule's <code>abort</code> method
     * if the overall authentication failed.  If authentication succeeded,
     * each successful LoginModule's <code>commit</code> method associates
     * the relevant Principals and Credentials with the <code>Subject</code>.
     * If authentication failed, each LoginModule's <code>abort</code> method
     * removes/destroys any previously stored state.
     *
     * <p> If the <code>commit</code> phase of the authentication process
     * fails, then the overall authentication fails and this method
     * invokes the <code>abort</code> method for each configured
     * <code>LoginModule</code>.
     *
     * <p> If the <code>abort</code> phase
     * fails for any reason, then this method propagates the
     * original exception thrown either during the <code>login</code> phase
     * or the <code>commit</code> phase.  In either case, the overall
     * authentication fails.
     *
     * <p> In the case where multiple LoginModules fail,
     * this method propagates the exception raised by the first
     * <code>LoginModule</code> which failed.
     *
     * <p> Note that if this method enters the <code>abort</code> phase
     * (either the <code>login</code> or <code>commit</code> phase failed),
     * this method invokes all LoginModules configured for the specified
     * application regardless of their respective <code>Configuration</code>
     * flag parameters.  Essentially this means that <code>Requisite</code>
     * and <code>Sufficient</code> semantics are ignored during the
     * <code>abort</code> phase.  This guarantees that proper cleanup
     * and state restoration can take place.
     *
     * <p>
     *
     * @exception LoginException if the authentication fails.
     */
    public void login() throws LoginException {

        loginSucceeded = false;

        

        try {
            //执行第一阶段登录
            this._invoke(LOGIN_METHOD);
            //执行第二阶段登录，提交整体登录信息
            this._invoke(COMMIT_METHOD);
            loginSucceeded = true;
        } catch (LoginException le) {

            try {
                //登录失败则退出登录
            	this._invoke(ABORT_METHOD);
            } catch (LoginException le2) {
                throw le;
            }
            throw le;
        }
    }
    
    
    private void _invoke(String action) throws LoginException
    {
    	

       
        
   
        		
        

            try {


                // instantiate the LoginModule


                   

             

                // set up the arguments to be passed to the LoginModule method
                boolean status = false;
                // invoke the LoginModule method
                if(action.equals(LOGIN_METHOD))
                {
                	status =  module.login();
                }
                else if(action.equals(COMMIT_METHOD))
                {
                	status =  module.commit();
                }
                else if(action.equals(ABORT_METHOD))
                {
                	status =  module.abort();
                }
                else if(action.equals(LOGOUT_METHOD))
                {
                	status =  module.logout();
                }

                //方法返回值为true,表示方法执行成功，做SUFFICIENT检查
                if (status == true) {

                   
                                        return;
                } else {
                   log.debug(action+" failed.");
                }

            }catch (LoginException e) {
            	throw e;
				
			}
        	catch (Exception ite) {
                //ite.printStackTrace();

                // failure cases
                LoginException le;

               
 
                	if(ite instanceof LoginException)
                	{
                		le = (LoginException)ite ;
                	}
                	else
                	{
                		le = new LoginException(ite .getMessage(),ite );
                	}
                    
                	throw le;

               
            }
           
            
        
    }

    /**
     * Logout the <code>Subject</code>.
     *
     * <p> This method invokes the <code>logout</code> method for each
     * <code>LoginModule</code> configured for this <code>LoginContext</code>.
     * Each <code>LoginModule</code> performs its respective logout procedure
     * which may include removing/destroying
     * <code>Principal</code> and <code>Credential</code> information
     * from the <code>Subject</code> and state cleanup.
     *
     * <p> Note that this method invokes all LoginModules configured for the
     * specified application regardless of their respective
     * <code>Configuration</code> flag parameters.  Essentially this means
     * that <code>Requisite</code> and <code>Sufficient</code> semantics are
     * ignored for this method.  This guarantees that proper cleanup
     * and state restoration can take place.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     */
    public void logout() throws LoginException {
        

        this._invoke(LOGOUT_METHOD);
    }

    /**
     * Return the authenticated Subject.
     *
     * <p>
     *
     * @return the authenticated Subject.  If authentication fails
     *		and a Subject was not provided to this LoginContext's
     *		constructor, this method returns <code>null</code>.
     *		Otherwise, this method returns the provided Subject.
     */
    public Subject getSubject() {
        if (!loginSucceeded && !subjectProvided)
            return null;
        return subject;
    }

    private void throwException(LoginException originalError, LoginException le)
    throws LoginException {
        LoginException error = (originalError != null) ? originalError : le;
        throw error;
    }

   

   

   

    /**
     * This class represents whether or not a <code>LoginModule</code>
     * is REQUIRED, REQUISITE, SUFFICIENT or OPTIONAL.
     */
    public static class LoginModuleControlFlag implements Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = 6514118449819585015L;

		private String controlFlag;

        /**
         * Required <code>LoginModule</code>.
         */
        public static final LoginModuleControlFlag REQUIRED =
                                new LoginModuleControlFlag("required");

        /**
         * Requisite <code>LoginModule</code>.
         */
        public static final LoginModuleControlFlag REQUISITE =
                                new LoginModuleControlFlag("requisite");

        /**
         * Sufficient <code>LoginModule</code>.
         */
        public static final LoginModuleControlFlag SUFFICIENT =
                                new LoginModuleControlFlag("sufficient");

        /**
         * Optional <code>LoginModule</code>.
         */
        public static final LoginModuleControlFlag OPTIONAL =
                                new LoginModuleControlFlag("optional");

        private LoginModuleControlFlag(String controlFlag) {
            this.controlFlag = controlFlag;
        }

        /**
         * Return a String representation of this controlFlag.
         *
         * @return a String representation of this controlFlag.
         */
        public String toString() {
            return "LoginModuleControlFlag: " + controlFlag;
        }
    }
    
	public static void resetUserAttribute(HttpServletRequest request,
			CheckCallBack checkCallBack, String userAttribute) {
		LoginModuleInfoQueue moduleQueue = ConfigManager.getInstance().getDefaultApplicationInfo().getLoginModuleInfos();
		int size = moduleQueue.getACLLoginModuleSize();
		for(int i = 0; i < size;i ++)
		{
			ACLLoginModule aclLoginModule = moduleQueue.getACLLoginModule(i);
			aclLoginModule.resetUserAttribute(request, checkCallBack, userAttribute);
		}
		
	}
	
	public static void resetUserAttribute(HttpServletRequest request,
			CheckCallBack checkCallBack) {
		LoginModuleInfoQueue moduleQueue = ConfigManager.getInstance().getDefaultApplicationInfo().getLoginModuleInfos();
		int size = moduleQueue.getACLLoginModuleSize();
		for(int i = 0; i < size;i ++)
		{
			ACLLoginModule aclLoginModule = moduleQueue.getACLLoginModule(i);
			aclLoginModule.resetUserAttributes(request, checkCallBack);
		}
		
	}

}
