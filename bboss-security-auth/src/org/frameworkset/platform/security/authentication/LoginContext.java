package org.frameworkset.platform.security.authentication;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

//import sun.security.util.ResourcesMgr;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.LoginModuleInfoQueue;
import org.frameworkset.platform.config.model.LoginModuleInfo;



/**
 * <p> The <code>LoginContext</code> class describes the basic methods used
 * to authenticate Subjects and provides a way to develop an
 * application independent of the underlying authentication technology.
 * A <code>Configuration</code> specifies the authentication technology, or
 * <code>LoginModule</code>, to be used with a particular application.
 * Therefore, different LoginModules can be plugged in under an application
 * without requiring any modifications to the application itself.
 *
 * <p> In addition to supporting <i>pluggable</i> authentication, this class
 * also supports the notion of <i>stacked</i> authentication.  In other words,
 * an application may be configured to use more than one
 * <code>LoginModule</code>.  For example, one could
 * configure both a Kerberos <code>LoginModule</code> and a smart card
 * <code>LoginModule</code> under an application.
 *
 * <p> A typical caller instantiates this class and passes in
 * a <i>name</i> and a <code>CallbackHandler</code>.
 * <code>LoginContext</code> uses the <i>name</i> as the index into the
 * <code>Configuration</code> to determine which LoginModules should be used,
 * and which ones must succeed in order for the overall authentication to
 * succeed.  The <code>CallbackHandler</code> is passed to the underlying
 * LoginModules so they may communicate and interact with users
 * (prompting for a username and password via a graphical user interface,
 * for example).
 *
 * <p> Once the caller has instantiated a <code>LoginContext</code>,
 * it invokes the <code>login</code> method to authenticate
 * a <code>Subject</code>.  This <code>login</code> method invokes the
 * <code>login</code> method from each of the LoginModules configured for
 * the <i>name</i> specified by the caller.  Each <code>LoginModule</code>
 * then performs its respective type of authentication (username/password,
 * smart card pin verification, etc.).  Note that the LoginModules will not
 * attempt authentication retries or introduce delays if the authentication
 * fails.  Such tasks belong to the caller.
 *
 * <p> Regardless of whether or not the overall authentication succeeded,
 * this <code>login</code> method completes a 2-phase authentication process
 * by then calling either the <code>commit</code> method or the
 * <code>abort</code> method for each of the configured LoginModules.
 * The <code>commit</code> method for each <code>LoginModule</code>
 * gets invoked if the overall authentication succeeded,
 * whereas the <code>abort</code> method for each <code>LoginModule</code>
 * gets invoked if the overall authentication failed.
 * Each successful LoginModule's <code>commit</code>
 * method associates the relevant Principals (authenticated identities)
 * and Credentials (authentication data such as cryptographic keys)
 * with the <code>Subject</code>.  Each LoginModule's <code>abort</code>
 * method cleans up or removes/destroys any previously stored authentication
 * state.
 *
 * <p> If the <code>login</code> method returns without
 * throwing an exception, then the overall authentication succeeded.
 * The caller can then retrieve
 * the newly authenticated <code>Subject</code> by invoking the
 * <code>getSubject</code> method.  Principals and Credentials associated
 * with the <code>Subject</code> may be retrieved by invoking the Subject's
 * respective <code>getPrincipals</code>, <code>getPublicCredentials</code>,
 * and <code>getPrivateCredentials</code> methods.
 *
 * <p> To logout the <code>Subject</code>, the caller simply needs to
 * invoke the <code>logout</code> method.  As with the <code>login</code>
 * method, this <code>logout</code> method invokes the <code>logout</code>
 * method for each <code>LoginModule</code> configured for this
 * <code>LoginContext</code>.  Each LoginModule's <code>logout</code>
 * method cleans up state and removes/destroys Principals and Credentials
 * from the <code>Subject</code> as appropriate.
 *
 * <p> Each of the configured LoginModules invoked by the
 * <code>LoginContext</code> is initialized with a
 * <code>Subject</code> to be authenticated, a <code>CallbackHandler</code>
 * used to communicate with users, shared <code>LoginModule</code> state,
 * and LoginModule-specific options.  If the <code>LoginContext</code>
 * was not provided a <code>Subject</code> then it instantiates one itself.
 *
 * <p> Each <code>LoginModule</code>
 * which successfully authenticates a user updates the <code>Subject</code>
 * with the relevant user information (Principals and Credentials).
 * This <code>Subject</code> can then be returned via the
 * <code>getSubject</code> method from the <code>LoginContext</code> class
 * if the overall authentication succeeds.  Note that LoginModules are always
 * invoked from within an <code>AccessController.doPrivileged</code> call.
 * Therefore, although LoginModules that perform security-sensitive tasks
 * (such as connecting to remote hosts) need to be granted the relevant
 * Permissions in the security <code>Policy</code>, the callers of the
 * LoginModules do not require those Permissions.
 *
 * <p> A <code>LoginContext</code> supports authentication retries
 * by the calling application.  For example, a LoginContext's
 * <code>login</code> method may be invoked multiple times
 * if the user incorrectly types in a password.  However, a
 * <code>LoginContext</code> should not be used to authenticate
 * more than one <code>Subject</code>.  A separate <code>LoginContext</code>
 * should be used to authenticate each different <code>Subject</code>.
 *
 * <p> Multiple calls into the same <code>LoginContext</code>
 * do not affect the <code>LoginModule</code> state, or the
 * LoginModule-specific options.
 *
 * @version 1.94, 01/23/03
 * @see javax.security.auth.Subject
 * @see javax.security.auth.callback.CallbackHandler
 * @see javax.security.auth.login.Configuration
 * @see javax.security.auth.spi.LoginModule
 * @author biaoping.yin
 */
public class LoginContext implements Serializable{
   
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
    private Map state = new HashMap();

    private LoginModuleInfoQueue moduleQueue;


    private ModuleInfo[] moduleStack;
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

        java.lang.SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AuthPermission
                                ("createLoginContext." + name));
        }

        if (name == null)
            throw new LoginException
                (ResourcesMgr.getString("Invalid null input: name"));
        if(this.appName == null && this.moduleName == null)
        {
            moduleQueue = ConfigManager.getInstance().getDefaultApplicationInfo().getLoginModuleInfos();
        }
        else
        {
            moduleQueue = ConfigManager.getInstance().getApplicationInfo(appName).getLoginModuleInfos();
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
        moduleStack = new ModuleInfo[moduleQueue.size()];
        //复制
        for (int i = 0; i < moduleQueue.size(); i++) {
            // clone returned array
//            moduleStack[i] = new ModuleInfo
//                                (new AppConfigurationEntry
//                                        (entries[i].getLoginModuleName(),
//                                        entries[i].getControlFlag(),
//                                        entries[i].getOptions()),
//                                null);
            moduleStack[i] = new ModuleInfo
                                (moduleQueue.getLoginModuleInfo(i),
                                null);

        }


        contextClassLoader = Thread.currentThread().getContextClassLoader();
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
    public LoginContext(String name, CallbackHandler callbackHandler)
    throws LoginException {
        init(name);
        if (callbackHandler == null)
            throw new LoginException(ResourcesMgr.getString
                                ("invalid null CallbackHandler provided"));
        this.callbackHandler = new SecureCallbackHandler
                                (
//                                		java.security.AccessController.getContext(),
                                callbackHandler);
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

        if (subject == null) {
            subject = new Subject();
        }

        try {
            //执行第一阶段登录
            invokeModule(LOGIN_METHOD);
            //执行第二阶段登录，提交整体登录信息
            invokeModule(COMMIT_METHOD);
            loginSucceeded = true;
        } catch (LoginException le) {

            try {
                //登录失败则退出登录
                invokeModule(ABORT_METHOD);
            } catch (LoginException le2) {
                throw le;
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
        if (subject == null) {
            throw new LoginException(ResourcesMgr.getString
                ("null subject - logout called before login"));
        }

        invokeModule(LOGOUT_METHOD);
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
     * Invokes the login, commit, and logout methods
     * from a LoginModule inside a doPrivileged block.
     */
    private void invokeModule(String methodName) throws LoginException {
//        try {
            final String finalName = methodName;
//            java.security.AccessController.doPrivileged
//                (new java.security.PrivilegedExceptionAction() {
//                public Object run() throws LoginException {
                    invoke(finalName);
//                    return null;
//                }
//            });
//        } catch (java.security.PrivilegedActionException pae) {
//            throw (LoginException)pae.getException();
//        }
    }

    /**
     * 执行具体的方法：login(),logout(),commit(),abort()
     * @param methodName String
     * @throws LoginException
     */
    private void invoke(String methodName) throws LoginException {

        LoginException firstError = null;
        LoginException firstRequiredError = null;
        boolean success = false;

        for (int i = 0; i < moduleStack.length; i++) {

            try {

                int mIndex = 0;
                Method[] methods = null;

                if (moduleStack[i].module != null) {//如果登录模块已经初始化，直接提取所有登录模块可访问方法
                    methods = moduleStack[i].module.getClass().getMethods();
                } else {//初始化登陆模块，提取所有登录模块可访问方法

                    // instantiate the LoginModule

                    Class c = Class.forName
                                (moduleStack[i].getLoginModuleInfo().getLoginModule(),
                                true,
                                contextClassLoader);

                    Constructor constructor = c.getConstructor(PARAMS);
                    Object[] args = { };

                    // allow any object to be a LoginModule
                    // as long as it conforms to the interface
                    moduleStack[i].module = constructor.newInstance(args);
                    //检测登录模块是否是ACLLoginModule类型的模块，如果是设置登录模块的名称
                    if(moduleStack[i].module instanceof ACLLoginModule)
                    {
                        ((ACLLoginModule)moduleStack[i].module)
                                .setLoginModuleName(moduleStack[i].getLoginModuleInfo().getName());
                        ((ACLLoginModule)moduleStack[i].module)
                                .setRegistTable(moduleStack[i].getLoginModuleInfo().getRegistTable());

                    }

                    methods = moduleStack[i].module.getClass().getMethods();

                    // 查找登录模块初始化方法并call the LoginModule's initialize method
                    for (mIndex = 0; mIndex < methods.length; mIndex++) {
                        if (methods[mIndex].getName().equals(INIT_METHOD))
                            break;
                    }
                    CallbackHandler handler = callbackHandler;
                    //如果登录模块指定了特定的回调函数，则使用特定的回调函数对登录模块进行初始化
                    if(moduleStack[i].getLoginModuleInfo().getCallBackHandler() != null
                       && !moduleStack[i].getLoginModuleInfo().getCallBackHandler().equals(""))
                    {
                        try
                        {
                            handler = (CallbackHandler) Class.forName(
                                    moduleStack[i].getLoginModuleInfo().
                                    getCallBackHandler()).newInstance();
                        }
                        catch(Exception e)
                        {
                            log.error("",e);
                            handler = callbackHandler;
                        }
                    }

                    Object[] initArgs = {subject,
                                        handler,
                                        state,
                                        new HashMap().put("debug",new Boolean(moduleStack[i].getLoginModuleInfo().isDebug())) };//options(java.util.Map) is null
                    // invoke the LoginModule initialize method
                    methods[mIndex].invoke(moduleStack[i].module, initArgs);
                }

                // find the requested method in the LoginModule
                for (mIndex = 0; mIndex < methods.length; mIndex++) {
                    if (methods[mIndex].getName().equals(methodName))
                        break;
                }

                // set up the arguments to be passed to the LoginModule method
                Object[] args = { };

                // invoke the LoginModule method
                boolean status = ((Boolean)methods[mIndex].invoke
                                (moduleStack[i].module, args)).booleanValue();

                //方法返回值为true,表示方法执行成功，做SUFFICIENT检查
                if (status == true) {

                    // if SUFFICIENT, return if no prior REQUIRED errors
                    if (!methodName.equals(ABORT_METHOD) &&
                        !methodName.equals(LOGOUT_METHOD) &&
                        moduleStack[i].getLoginModuleInfo().getControlFlag().equals(LoginModuleControlFlag.SUFFICIENT.controlFlag)
                     &&
                        firstRequiredError == null) {

                        //if (log != null)
                            log.debug(methodName + " SUFFICIENT success");
                        return;
                    }
                    log.debug(methodName + " success");
                    success = true;
                } else {
                   log.debug(methodName + " ignored");
                }

            } catch (NoSuchMethodException nsme) {
                //nsme.printStackTrace();
                MessageFormat form = new MessageFormat(ResourcesMgr.getString
                        ("unable to instantiate LoginModule, module, because " +
                        "it does not provide a no-argument constructor"));
                Object[] source = {moduleStack[i].entry.getLoginModule()};
                throw new LoginException(form.format(source));
            } catch (InstantiationException ie) {
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
            } catch (InvocationTargetException ite) {
                //ite.printStackTrace();

                // failure cases
                LoginException le;
//                if (ite.getTargetException() instanceof javax.security.auth.login.LoginException) {
//                    le = (LoginException)ite.getTargetException();
//                } else 
                {
                    // capture an unexpected LoginModule exception
//                    java.io.StringWriter sw = new java.io.StringWriter();
//                    ite.getCause().printStackTrace
//                                                (new java.io.PrintWriter(sw));
//                    sw.flush();
//                    le = new LoginException(sw.toString());
                	
                    le = new LoginException(ite.getTargetException().getMessage(),ite.getTargetException());
                    
                }

                //如果是REQUISITE，那么立即抛出异常
                if (moduleStack[i].entry.getControlFlag().equals(
                    LoginModuleControlFlag.REQUISITE.controlFlag)) {

                    log.debug(methodName + " REQUISITE failure");

                    // if REQUISITE, then immediately throw an exception
                    if (methodName.equals(ABORT_METHOD) ||
                        methodName.equals(LOGOUT_METHOD)) {
                        if (firstRequiredError == null)
                            firstRequiredError = le;
                    } else {
                        throwException(firstRequiredError, le);
                    }

                } else if (moduleStack[i].entry.getControlFlag().equals(LoginModuleControlFlag.REQUIRED.controlFlag)) {

                    log.debug(methodName + " REQUIRED failure");

                    // mark down that a REQUIRED module failed
                    if (firstRequiredError == null)
                        firstRequiredError = le;

                } else {

                    log.debug(methodName + " OPTIONAL failure");

                    // mark down that an OPTIONAL module failed
                    if (firstError == null)
                        firstError = le;
                }
            }
        }

        // we went thru all the LoginModules.
        if (firstRequiredError != null) {
            // a REQUIRED module failed -- return the error
            throwException(firstRequiredError, null);
        } else if (success == false && firstError != null) {
            // no module succeeded -- return the first error
            throwException(firstError, null);
        } else if (success == false) {
            // no module succeeded -- all modules were IGNORED
            throwException(new LoginException
                (ResourcesMgr.getString("Login Failure: all modules ignored")),
                null);
        } else {
            // success
            return;
        }
    }

    /**
     * Wrap the application-provided CallbackHandler in our own
     * and invoke it within a privileged block, constrained by
     * the caller's AccessControlContext.
     */
    private static class SecureCallbackHandler implements CallbackHandler,Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
//		private final java.security.AccessControlContext acc;
        private final CallbackHandler ch;

        SecureCallbackHandler(CallbackHandler ch) {
//            this.acc = acc;
            this.ch = ch;
        }
        
//        SecureCallbackHandler(java.security.AccessControlContext acc,
//                CallbackHandler ch) {
//    this.acc = acc;
//    this.ch = ch;
//}
        

        public void handle(Callback[] callbacks) throws java.io.IOException,
                                                UnsupportedCallbackException {
//            try {
                final Callback[] finalCallbacks = callbacks;
//                java.security.AccessController.doPrivileged
//                    (new java.security.PrivilegedExceptionAction() {
//                    public Object run() throws java.io.IOException,
//                                        UnsupportedCallbackException {
                        ch.handle(finalCallbacks);
//                        return null;
//                    }
//                }, acc);
//            } catch (java.security.PrivilegedActionException pae) {
//                if (pae.getException() instanceof java.io.IOException) {
//                    throw (java.io.IOException)pae.getException();
//                } else {
//                    throw (UnsupportedCallbackException)pae.getException();
//                }
//            }
        }
    }

    /**
     * LoginModule information -
     *		incapsulates Configuration info and actual module instances
     */
    private static class ModuleInfo implements Serializable{
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		LoginModuleInfo entry;
        Object module;

        ModuleInfo(LoginModuleInfo newEntry, Object newModule) {
            this.entry = newEntry;
            this.module = newModule;
        }

        public LoginModuleInfo getLoginModuleInfo() {
            return this.entry;
        }

        public Object getModule() {
            return this.module;
        }
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
