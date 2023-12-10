package com.frameworkset.common.filter;

import bboss.org.mozilla.intl.chardet.UTF8Convertor;
import org.frameworkset.util.AttackContext;
import org.frameworkset.util.AttackException;
import org.frameworkset.util.ReferHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CharacterEncodingHttpServletRequestWrapper
    extends HttpServletRequestWrapper{
	private boolean ignoreParameterDecoding = false;
	private Map<String,String[]> parameters = null;
	private static Logger logger = LoggerFactory.getLogger(CharacterEncodingHttpServletRequestWrapper.class); 
//	private Object lock = new Object();
    private String newecoding = null;
    private String oldEncoding = null;
    private boolean isie = false;
    
    private boolean isget = false;
    private boolean isPost = false;
    private boolean postCharsetConvert=true;
    private boolean isutf8 = false;
    private boolean checkiemodeldialog;
    private static final String system_encoding = System.getProperty("sun.jnu.encoding");
    public static final String USE_MVC_DENCODE_KEY = "org.frameworkset.web.servlet.handler.HandlerUtils.USE_MVC_DENCODE_KEY";
    private ReferHelper referHelper;
	private HttpServletResponse response;
	private FilterChain chain ;
	private boolean preHandleParams;
	private boolean isWhiteUrl;
    public CharacterEncodingHttpServletRequestWrapper(FilterChain chain,HttpServletRequest request, HttpServletResponse response,
                                                      String encoding, boolean checkiemodeldialog,
                                                      ReferHelper referHelper, 
                                                      boolean ignoreParameterDecoding,boolean postCharsetConvert) {
        super(request);
        this.postCharsetConvert = postCharsetConvert;
        this.response = response;
        this.chain = chain;
//        this.wallfilterrules = wallfilterrules;
//        this.wallwhilelist = wallwhilelist;
        this.referHelper = referHelper;
        String agent = request.getHeader("User-Agent");
        if(agent != null)
        	isie = agent.contains("MSIE ");
        String method = this.getMethod();
        if(method != null) {
            if(method.equals("GET")) {
                isget = true;
            }
            else if(method.equals("POST")){
                isPost = true;
            }
        }
        
        this.newecoding = encoding != null ? encoding:system_encoding;
        isutf8 = newecoding.toLowerCase().equals("utf-8");
        this.oldEncoding = request.getCharacterEncoding();
        parameters = new HashMap<String,String[]>();
//        String _checkiemodeldialog = request.getParameter("_checkiemodeldialog");
//        if(_checkiemodeldialog != null && _checkiemodeldialog.equals("true"));
        this.checkiemodeldialog = checkiemodeldialog;
        if(ignoreParameterDecoding)
            this.ignoreParameterDecoding = ignoreParameterDecoding;
        else{
            this.ignoreParameterDecoding = referHelper.ignoreEncodeParameters(request.getRequestURI());
        }
        isWhiteUrl = referHelper.isWhiteUrl(request.getRequestURI());

    }

    public void preHandlerParameters(){
        Enumeration paramNames = super.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = super.getParameterValues(paramName);

            if(paramValues != null) {
                if(paramValues.length > 0) {
                    handlerValues(paramName, paramValues);
                }
                else{
                    this.parameters.put(paramName,paramValues);
                }
            }
        }
        preHandleParams = true;
    }

    private String[] handlerValues(String name,String[] tempArray){
            try {


                if ( tempArray == null  || tempArray.length == 0) {
                    return tempArray;
                }

                if ( !ignoreParameterDecoding && (oldEncoding == null || isIOS88591(oldEncoding)) )
                {
                    String[] clone = new String[tempArray.length];
                    String oValue = null;
                    for (int i = 0; i < tempArray.length; i++) {
                        oValue = tempArray[i];
                        if ( oValue!= null) {
                            byte[] buf = oValue.getBytes("iso-8859-1");
                            if(checkiemodeldialog && isutf8 && isie && isget )
                            {

                                String charset = UTF8Convertor.takecharset(buf) ;
                                if(charset !=null && charset.startsWith("GB"))
                                {

                                    clone[i] = new String(buf, "GBK");

                                }
//	                    	else if(charset !=null && charset.startsWith("UTF-8"))
//	                    		clone[i] = new String(oValue.getBytes("iso-8859-1"), charset);
//	                    	else if(charset !=null && charset.startsWith("UTF-"))
//	                    		clone[i] = new String(oValue.getBytes("iso-8859-1"), "UTF-16");
                                else
                                    clone[i] = new String(buf, newecoding);
                            }
                            else
                            {
                                if(!isPost) {
                                    clone[i] = new String(buf, newecoding);
                                }
                                else{
                                    if(!postCharsetConvert) {
                                        clone[i] = oValue;
                                    }
                                    else {
                                        clone[i] = new String(buf, newecoding);
                                    }
                                }
                            }
                        }
                        else
                        {
                            clone[i] = oValue;
                        }
                    }

                    if(!referHelper.isDisableAttackDefender() && !isWhiteUrl) {
                        AttackContext attackContext = new AttackContext();
                        attackContext.setRequest(this);
                        attackContext.setResponse(this.response);
                        attackContext.setChain(chain);
                        this.referHelper.wallfilter(name, clone, attackContext);
                        this.referHelper.sensitiveWallfilter(name, clone, attackContext);
                    }
                    parameters.put(name,clone);
                    return clone;
                }
                else
                {
                    if(!referHelper.isDisableAttackDefender()&& !isWhiteUrl) {
                        AttackContext attackContext = new AttackContext();
                        attackContext.setRequest(this);
                        attackContext.setResponse(this.response);
                        attackContext.setChain(chain);
                        this.referHelper.wallfilter(name, tempArray, attackContext);
                        this.referHelper.sensitiveWallfilter(name, tempArray, attackContext);
                    }
                    parameters.put(name,tempArray);
                    return tempArray;
                }

            }
            catch (AttackException e){
                throw e;
            }
            catch (Exception e) {
                if(!referHelper.isDisableAttackDefender()&& !isWhiteUrl) {
                    AttackContext attackContext = new AttackContext();
                    attackContext.setRequest(this);
                    attackContext.setResponse(this.response);
                    attackContext.setChain(chain);
                    this.referHelper.wallfilter(name, tempArray, attackContext);
                    this.referHelper.sensitiveWallfilter(name, tempArray, attackContext);
                }
                parameters.put(name, tempArray);

                return tempArray ;
            }
    }

    public String getParameter(String name) {
    	
    	
    	
//    	String values[] =getParameterValues(name);
//    	if(values != null && values.length > 0)
//    		return values[0]; 
//    	else
//    		return null;
//    	String value_ = super.getParameter(name);  
//    	if(value_ == null)
//			return null;
//        try {
//            if ( (oldEncoding == null || isIOS88591(oldEncoding)) && value_ != null) {
//                return new String(value_.getBytes("iso-8859-1"), newecoding);
//            }
//            else
//            {
//                return value_;
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return value_;
//        }
    	String[] values = getParameterValues(name);
    	if(values == null || values.length == 0)
    		return null;
    	return values[0];
    }
    

   
    public String[] getParameterValues(String name) {

        String[] tempArray = parameters.get(name);
        if(tempArray != null || this.preHandleParams)
            return tempArray;
        tempArray = super.getParameterValues(name);
        tempArray = this.handlerValues(name,tempArray);
        return tempArray;

    }
    
    
    

    private boolean isIOS88591(String endcoding) {
        endcoding = endcoding.toLowerCase();
        return endcoding.startsWith("iso") && (endcoding.indexOf("8859") != -1) && endcoding.endsWith("1");
    }
}
