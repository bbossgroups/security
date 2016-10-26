<%@ page session="false" language="java"
	contentType="text/html; charset=utf-8"%>

<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg" %>

 

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<script type="text/javascript" src="${pageContext.request.contextPath}/include/jquery-1.4.2.min.js"></script>


<script language="JavaScript">

	
	function reloadhref()
	{
		location.href = "login.page";
	}
	
	function changcode()
	{
		
		$("#img1").attr("src","Kaptcha.jpg?"+Math.random());
		
	}
	
	 
	
	function login(event){
	
		var s = $("#userName").val();
		var p = $("#password").val();
		var y = $("#rand").val();
		if(y==""){
			alert("验证码不能为空");
			return;
		}
		if((s==""&&p!="")||(s==""&&p=="")){
			$.dialog.alert("<pg:message code='sany.pdp.input.login.name'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
	    	$("#userName").focus();
	    	event.returnValue=false;
		    }else if(p==""&&s!=""){
		    $.dialog.alert("<pg:message code='sany.pdp.input.password'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
	    	$("#password").focus();
	    	event.returnValue=false;
		    }
		    
	    if(s!=""&&p!=""){
 			document.getElementById('password').value = strEnc(p,s, "", "");
	    	$("#loginForm").submit();
		 }
		   
		/*var ischecked = document.getElementById("remeberpassword").checked;	
		if(ischecked){
			if (s!="" && p!=""){
				s="USERNAME="+s;
				p="PASSWORD="+p;
				document.cookie=s+"#"+p;  // 将保存到客户机中
				//alert(s);
			}
		}*/
		
	}
	
	function reset(){
		$("#loginForm").reset();
	}
	
	function enterKeydowngoU(event){
		var userName = $("#userName").val();
		var password = $("#password").val();
		if(event.keyCode == 13){
		var y = $("#rand").val();
		if(y==""){
			$.dialog.alert("验证码不能为空");
			return;
		}
			if(userName == ""){
				$.dialog.alert("<pg:message code='sany.pdp.input.login.name'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
				$("#userName").focus();
				event.returnValue=false;
			}else{
				$("#password").focus();
				event.returnValue=false;
			}
		}
	}
	
	function enterKeydowngoP(event){
		var userName = $("#userName").val();
		var password = $("#password").val();
		if(event.keyCode == 13){
		var y = $("#rand").val();
		if(y==""){
			$.dialog.alert("验证码不能为空");
			return;
		}
			if(userName == "" ){
				$.dialog.alert("<pg:message code='sany.pdp.input.login.name'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
				$("#userName").focus();
				event.returnValue=false;
			}else if(userName != "" && password == ""){
				$.dialog.alert("<pg:message code='sany.pdp.input.password'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
				$("#password").focus();
				event.returnValue=false;
			}else if(userName != "" && password != ""){
				//loginForm.subsystem_id.focus();
				
				document.getElementById('password').value = strEnc(password,userName, "", "");
				$("#loginForm").submit();
				event.returnValue=false;
			}
		}
	}
	
	
	function enterKeydowngoV(event){
		var userName = $("#userName").val();
		var password = $("#password").val();
		if(event.keyCode == 13){
		var y = $("#rand").val();
		if(y==""){
			$.dialog.alert("验证码不能为空");
			$("#rand").focus();
			return;
		}
			if(userName == "" ){
				$.dialog.alert("<pg:message code='sany.pdp.input.login.name'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
				$("#userName").focus();
				event.returnValue=false;
			}else if(userName != "" && password == ""){
				$.dialog.alert("<pg:message code='sany.pdp.input.password'/>",function(){},null,"<pg:message code='sany.pdp.common.alert'/>");
				$("#password").focus();
				event.returnValue=false;
			}else if(userName != "" && password != ""){
				//loginForm.subsystem_id.focus();
				
				document.getElementById('password').value = strEnc(password,userName, "", "");
				$("#loginForm").submit();
				event.returnValue=false;
			}
		}
	}
	
	
	function changeLan(){
		window.location.href="${pageContext.request.contextPath}/security/cookieLocale.page?language="+$('#language').val() ;
	}
</script>
</HEAD>
<body>
<body id="logging_bg">
  <div class="c_log">
	<div class="c_logWrapper">
		<div class="c_logContent">
            
			<form id="loginForm" name="loginForm" action="login.page" method="post">
			<pg:dtoken/>
			<input type="hidden" name="flag" value="yes" />
			<input name="macaddr_" type="hidden" />
			<input name="machineName_" type="hidden" />
			<input name="machineIp_" type="hidden" />
			<pg:notempty actual="${successRedirect  }">
				<input name="<%=org.frameworkset.web.interceptor.AuthenticateFilter.referpath_parametername %>" type="hidden" value="${successRedirect  }"/>
				
				
			</pg:notempty>
			<ul class="c_log_right">
				<pg:null actual="${ errorMessage}" evalbody="true">
					<pg:yes></pg:yes>
					<pg:no>
						<pg:equal actual="${ errorMessage}" value="PasswordExpired" evalbody="true">
							<pg:yes><li><label></label><font color='red'>密码已经失效（有效期为${ expiredays}天），过期时间为${expriedtime_}!<a href='#' onclick='javascript:modifyExpiredPassword()'>点击修改</a></font></li></pg:yes>
							<pg:no><li><label></label><font color='red'>${ errorMessage}</font></li></pg:no>
						</pg:equal>
					</pg:no>
				</pg:null>
				 
				<li><label><pg:message code="sany.pdp.user.login.name"/>：</label><input id="userName" name="userName" type="text"   onkeydown="enterKeydowngoU(event)"  /></li>
				<li><label><pg:message code="sany.pdp.login.password"/>：</label><input id="password" name="password" type="password" type="text"	onkeydown="enterKeydowngoP(event)" autocomplete = "off"/></li>
				<pg:true actual="${enable_login_validatecode }">
					<li><label>验证码：</label>
					<input id="rand" name="rand" type="text" style="width:120px" onkeydown="enterKeydowngoV(event)"/><a onclick="changcode()">看不清,换一张</a>
					
					</li>
					<li >
					<img src="Kaptcha.jpg" height="25" width="200" id="img1">
					</li>
				</pg:true>
				<pg:notempty actual="${systemList }" evalbody="true">
					<pg:yes>
					<li><label><pg:message code="sany.pdp.system"/>：</label>
					
					<select name="subsystem_id" style="width:160px;margin-left:-110px;">
						<pg:list actual="${systemList }">
							<option value="<pg:cell colName="sysid"/>"
								<pg:true colName="selected">
								selected </pg:true>
								>
								<pg:cell colName="name"/>
							</option>
						</pg:list>
						
						
						
					</select>
					
					</li>
					</pg:yes>
					<pg:no>
						<input type="hidden" name="subsystem_id" value="module"/>
					</pg:no>
				</pg:notempty>
				
				<pg:notempty actual="${loginStyle }" evalbody="true">
					<pg:yes>
						<li><label><pg:message code="sany.pdp.style"/>：</label>               
						<select name="loginPath"  style="width:160px;margin-left:-110px;">
							<pg:case actual="${loginStyle }">
								<option value="5"
									<pg:empty>selected</pg:empty>
									<pg:equal value="5">selected</pg:equal>>
									common
								</option>
								<option value="6"
									<pg:equal value="6">selected</pg:equal>>
									common-fixwidth
								</option>
								<option value="3"
									<pg:equal value="3">selected</pg:equal>>
									ISany
								</option>
								<option value="1"
									<pg:equal value="1">selected</pg:equal>>
									<pg:message code="sany.pdp.tradition"/>
								</option>
								<option value="2"
									<pg:equal value="2">selected</pg:equal>>
									Desktop
								</option>
								<option value="4"
									<pg:equal value="4">selected</pg:equal>>
									WEBIsany
								</option>
							</pg:case>
						</select>
						</li>
					</pg:yes>
					<pg:no>
						<input type="hidden" name="loginPath" value="5"/>
					</pg:no>
				</pg:notempty>
                
                
                <pg:notempty actual="${language }" evalbody="true">
					<pg:yes>
						<li>
							<label><pg:message code="sany.pdp.language"/>：</label>
							<select name="language" id="language" style="width:160px;margin-left:-110px;" onchange="changeLan()">
							<pg:case actual="${language }">
								<option value="zh_CN" <pg:equal value="zh_CN">selected</pg:equal>>
									<pg:message code="sany.pdp.language.chinese"/>
								</option>
								<option value="en_US" <pg:equal value="en_US">selected</pg:equal>>
									<pg:message code="sany.pdp.language.english"/>
								</option>
								
							</pg:case>	
							</select>
						</li>
					</pg:yes>
					<pg:no>
						<input type="hidden" name="language" value="zh_CN"/>
					</pg:no>
				</pg:notempty>
                
				
				<li class="log_bts" ><a href="javascript:void(0)" class="log_bt c_20"  onclick="login(event)"><span><pg:message code="sany.pdp.login"/></span></a><a href="javascript:void(0)" class="log_bt log_cancel" onclick="reset()" ><span><pg:message code="sany.pdp.common.operation.reset"/></span></a></li>
			</ul>
			</form>			
			 			
		  </div>
		   <div class="c_log_bot"></div>
		   <p class="c_edition"><a href="http://www.bbossgroups.com" target="_blank" ><font color="white">BBoss V1.0</font> </a> <a href="http://www.miitbeian.gov.cn" target="_blank" ><font color="white">湘ICP备16000994号</font></a> 				 
				<script type="text/javascript">
					var cnzz_protocol = (("https:" == document.location.protocol) ? " https://"
							: " http://");
					document
							.write(unescape("%3Cspan id='cnzz_stat_icon_1254131450'%3E%3C/span%3E%3Cscript src='"
									+ cnzz_protocol
									+ "s11.cnzz.com/z_stat.php%3Fid%3D1254131450%26show%3Dpic2' type='text/javascript'%3E%3C/script%3E"));
				</script>
				 
			 
		   </p>
      </div>
        <div class="c_logHeight"></div>
	</div>
</div>		

</body>
</html>

