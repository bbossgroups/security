package com.frameworkset.common.filter;

import com.frameworkset.util.SimpleStringUtil;
import com.frameworkset.util.StringUtil;
import org.frameworkset.util.AttackException;
import org.frameworkset.util.ReferHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 不带session管理功能的字符过滤器
 * @author yinbp
 *
 */
public class SimpleCharsetEncodingFilter  implements Filter{
    private static Logger logger = LoggerFactory.getLogger(SimpleCharsetEncodingFilter.class);
	private FilterConfig config = null;
    private String RequestEncoding = null;
    private String ResponseEncoding = null;
    private String mode = "0";
    private boolean checkiemodeldialog;

    private ReferHelper referHelper;
   
    private boolean ignoreParameterDecoding;

    public void init(FilterConfig arg0) throws ServletException {
    	
        this.config = arg0;
        this.RequestEncoding = config.getInitParameter("RequestEncoding");
        this.ResponseEncoding = config.getInitParameter("ResponseEncoding");
        String refererDefender_ =  config.getInitParameter("refererDefender");
        boolean refererDefender = StringUtil.getBoolean(refererDefender_, false);
        String ignoreParameterDecoding_ =  config.getInitParameter("ignoreParameterDecoding");
        ignoreParameterDecoding = StringUtil.getBoolean(ignoreParameterDecoding_, true);
        referHelper = new ReferHelper();
        referHelper.setRefererDefender(refererDefender);

        initAttackPolicy( config, referHelper);


        String refererwallwhilelist_ = config.getInitParameter("refererwallwhilelist");
        if(StringUtil.isNotEmpty(refererwallwhilelist_))
        {
        	String[] refererwallwhilelist = refererwallwhilelist_.split(",");
        	referHelper.setRefererwallwhilelist(refererwallwhilelist);
        }
        String _checkiemodeldialog = config.getInitParameter("checkiemodeldialog");
        if(_checkiemodeldialog != null && _checkiemodeldialog.equals("true"))
        	this.checkiemodeldialog = true;
        mode = config.getInitParameter("mode");
        if(mode == null)
            mode = "0";
    }


    public static void initAttackPolicy(FilterConfig config,ReferHelper referHelper){
        String encodeParameterWhileList_ = config.getInitParameter("encodeParameterWhileList");
        if(StringUtil.isNotEmpty(encodeParameterWhileList_)){
            String[] encodeParameterWhileList = encodeParameterWhileList_.split(",");
            referHelper.setEncodeParameterWhileList(encodeParameterWhileList);
        }
        BaseAttackFielterPolicy baseAttackFielterPolicy = null;
        String attackDefenderPolicy_ = config.getInitParameter("attackDefenderPolicy");
        if(attackDefenderPolicy_ == null || attackDefenderPolicy_.trim().equals("")) {
            String wallfilterrules_ = config.getInitParameter("wallfilterrules");
            String wallwhilelist_ = config.getInitParameter("wallwhilelist");

            String defaultwall = config.getInitParameter("defaultwall");

            if (StringUtil.isNotEmpty(wallwhilelist_)) {
                baseAttackFielterPolicy = new DefaultAttackFielterPolicy();
                String[] wallwhilelist = wallwhilelist_.split(",");
                baseAttackFielterPolicy.setXssWallwhilelist(wallwhilelist);
//                referHelper.setWallwhilelist(wallwhilelist);
            }
            if (StringUtil.isNotEmpty(wallfilterrules_)) {
                String[] wallfilterrules = wallfilterrules_.split(",");
//                referHelper.setWallfilterrules(wallfilterrules);
                if(baseAttackFielterPolicy == null)
                    baseAttackFielterPolicy = new DefaultAttackFielterPolicy();
                baseAttackFielterPolicy.setXssWallfilterrules(wallfilterrules);
            } else if (defaultwall != null && defaultwall.equals("true")) {
                if(baseAttackFielterPolicy == null)
                    baseAttackFielterPolicy = new DefaultAttackFielterPolicy();

                String[] wallfilterrules = ReferHelper.wallfilterrules_default;
//                referHelper.setWallfilterrules(wallfilterrules);
                baseAttackFielterPolicy.setXssWallfilterrules(wallfilterrules);
            }

            String sensitivefilterrules_ = config.getInitParameter("sensitivefilterrules");
            String sensitivewhilelist_ = config.getInitParameter("sensitivewhilelist");


            if (StringUtil.isNotEmpty(sensitivewhilelist_)) {
                baseAttackFielterPolicy = new DefaultAttackFielterPolicy();
                String[] sensitivewhilelist = sensitivewhilelist_.split(",");
                baseAttackFielterPolicy.setSensitiveWallwhilelist(sensitivewhilelist);
//                referHelper.setWallwhilelist(wallwhilelist);
            }
            if (StringUtil.isNotEmpty(sensitivefilterrules_)) {
                String[] sensitivefilterrules = sensitivefilterrules_.split(",");
//                referHelper.setWallfilterrules(wallfilterrules);
                if(baseAttackFielterPolicy == null)
                    baseAttackFielterPolicy = new DefaultAttackFielterPolicy();
                baseAttackFielterPolicy.setSensitiveWallfilterrules(sensitivefilterrules);
            }
        }
        else{
            try {
                baseAttackFielterPolicy =(BaseAttackFielterPolicy)Class.forName(attackDefenderPolicy_).newInstance();
                long attackRuleCacheRefreshInterval = -1l;
                String attackRuleCacheRefreshInterval_ = config.getInitParameter("attackRuleCacheRefreshInterval");
                if(SimpleStringUtil.isNotEmpty(attackRuleCacheRefreshInterval_)){
                    attackRuleCacheRefreshInterval = Long.parseLong(attackRuleCacheRefreshInterval_) * 1000l;
                }
                baseAttackFielterPolicy = new WrapperAttackFielterPolicy(attackRuleCacheRefreshInterval,baseAttackFielterPolicy);
            } catch (InstantiationException e) {
                logger.error("",e);
            } catch (IllegalAccessException e) {
                logger.error("",e);
            } catch (ClassNotFoundException e) {
                logger.error("",e);
            }
        }
        if(baseAttackFielterPolicy != null) {
            referHelper.setAttackFielterPolicy(baseAttackFielterPolicy);
        }
    }
   
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc)
        throws IOException, ServletException {

        if(this.config == null){
            return;
        }

        HttpServletRequest request = (HttpServletRequest)req;
        

        //是否允许过滤器，
        String filterEnabled = request.getParameter("filterEnabled");

        HttpServletResponse response = (HttpServletResponse)res;
//        response.setHeader("Cache-Control", "no-cache"); 
//        response.setHeader("Pragma", "no-cache"); 
//        response.setDateHeader("Expires", -1);  
//        response.setDateHeader("max-age", 0);
        /**
         *  向所有会话cookie 添加“HttpOnly”属性,  解决方案，过滤器中
         */
//        response.setHeader( "Set-Cookie", "name=value; HttpOnly");
        //response.setHeader( "Set-Cookie", "name=value;HttpOnly"); 
        if(referHelper.dorefer(request, response))
        {
        	return;
        }





//        response.set

        if(filterEnabled != null && !filterEnabled.trim().equalsIgnoreCase("true"))
        {
            fc.doFilter(request, response);
//        	  super.doFilter(request, response, fc);
            return;
        }
        referHelper.initAttackFielterPolicy();
        try {
//        System.out.println("old request:" + request.getClass());
            //模式0：对请求参数编码，对响应编码
            //      服务器对url不进行编码
            if (mode.equals("0")) {
                CharacterEncodingHttpServletResponseWrapper wresponsew = new
                        CharacterEncodingHttpServletResponseWrapper(response, ResponseEncoding);
                CharacterEncodingHttpServletRequestWrapper mrequestw = new
                        CharacterEncodingHttpServletRequestWrapper(fc, request, wresponsew, RequestEncoding, checkiemodeldialog, referHelper, ignoreParameterDecoding);
                mrequestw.preHandlerParameters();
                fc.doFilter(mrequestw, wresponsew);
//            super.doFilter(mrequestw, wresponsew, fc);
            }
            //模式1：对请求参数编码，对响应不编码
            //      服务器对url进行编码
            else if (mode.equals("1")) {
                CharacterEncodingHttpServletRequestWrapper mrequestw = new
                        CharacterEncodingHttpServletRequestWrapper(fc, request, response, RequestEncoding, checkiemodeldialog, referHelper, ignoreParameterDecoding);
                mrequestw.preHandlerParameters();
                fc.doFilter(request, response);
//            super.doFilter(request, response, fc);
            }
            //其他模式
            else {
                CharacterEncodingHttpServletResponseWrapper wresponsew = new
                        CharacterEncodingHttpServletResponseWrapper(response, ResponseEncoding);
                CharacterEncodingHttpServletRequestWrapper mrequestw = new
                        CharacterEncodingHttpServletRequestWrapper(fc, request, wresponsew, this.RequestEncoding, checkiemodeldialog, referHelper, ignoreParameterDecoding);
                mrequestw.preHandlerParameters();
                fc.doFilter(mrequestw, wresponsew);
//            super.doFilter(mrequestw, wresponsew, fc);
            }
        }
        catch (AttackException e){
            if(e.getAttackContext().isRedirected())
                return;
            else
                throw e;
        }
    }

    public void destroy() {
    }

}
