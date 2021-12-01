package com.frameworkset.common.filter;

import com.frameworkset.util.StringUtil;
import org.frameworkset.security.session.impl.SessionFilter;
import org.frameworkset.util.ReferHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.frameworkset.common.filter.SimpleCharsetEncodingFilter.initAttackPolicy;

public abstract class BaseCharsetEncodingFilter extends SessionFilter{

	private FilterConfig config = null;
    private String RequestEncoding = null;
    private String ResponseEncoding = null;
    private String mode = "0";
    private boolean checkiemodeldialog;
    private boolean ignoreParameterDecoding;
//    private static String[] wallfilterrules;
//    private String[] wallwhilelist;
    private ReferHelper referHelper;
//    private static String[] wallfilterrules_default = new String[]{"<script","%3Cscript","script","<img","%3Cimg","alert(","alert%28","eval(","eval%28","style=","style%3D",
//    	"javascript","update ","drop ","delete ","insert ","create ","select ","truncate "};
    
    
    public void init(FilterConfig arg0) throws ServletException {
    	super.init(arg0);
        this.config = arg0;
        this.RequestEncoding = config.getInitParameter("RequestEncoding");
        this.ResponseEncoding = config.getInitParameter("ResponseEncoding");
        String refererDefender_ =  config.getInitParameter("refererDefender");
        boolean refererDefender = StringUtil.getBoolean(refererDefender_, false);
        String ignoreParameterDecoding_ =  config.getInitParameter("ignoreParameterDecoding");
        ignoreParameterDecoding = StringUtil.getBoolean(ignoreParameterDecoding_, false);
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
//            fc.doFilter(request, response);
        	  super.doFilter(request, response, fc);
            return;
        }
        referHelper.initAttackFielterPolicy();
//        System.out.println("old request:" + request.getClass());
        //模式0：对请求参数编码，对响应编码
        //      服务器对url不进行编码
        if(mode.equals("0"))
        {
            CharacterEncodingHttpServletResponseWrapper wresponsew = new
                    CharacterEncodingHttpServletResponseWrapper(response, ResponseEncoding);
            CharacterEncodingHttpServletRequestWrapper mrequestw = new
                CharacterEncodingHttpServletRequestWrapper(fc,request, wresponsew,RequestEncoding,checkiemodeldialog,referHelper,ignoreParameterDecoding);
            mrequestw.preHandlerParameters();
//            fc.doFilter(mrequestw, wresponsew);
            super.doFilter(mrequestw, wresponsew, fc);
        }
        //模式1：对请求参数编码，对响应不编码
        //      服务器对url进行编码
        else if(mode.equals("1"))
        {
        	 CharacterEncodingHttpServletRequestWrapper mrequestw = new
                     CharacterEncodingHttpServletRequestWrapper(fc,request,response ,RequestEncoding,checkiemodeldialog,referHelper,ignoreParameterDecoding);
            request.setCharacterEncoding(RequestEncoding);
            mrequestw.preHandlerParameters();
//            fc.doFilter(request,response);
            super.doFilter(request, response, fc);
        }
        //其他模式
        else
        {
            CharacterEncodingHttpServletResponseWrapper wresponsew = new
                    CharacterEncodingHttpServletResponseWrapper(response, ResponseEncoding);
            CharacterEncodingHttpServletRequestWrapper mrequestw = new
                CharacterEncodingHttpServletRequestWrapper(fc,request,wresponsew, this.RequestEncoding,checkiemodeldialog,referHelper,ignoreParameterDecoding);
            mrequestw.preHandlerParameters();
//            fc.doFilter(mrequestw, wresponsew);
            super.doFilter(mrequestw, wresponsew, fc);
        }
    }

    public void destroy() {
    	super.destroy();
    }
    
  
    

}
