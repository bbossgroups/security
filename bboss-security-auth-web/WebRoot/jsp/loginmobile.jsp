<%@ page session="false" language="java"
	contentType="text/html; charset=utf-8"%>

<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg" %>

 
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8">
    <title>${defaultmodulename }</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets_mobile/css/jquery.mobile-1.4.5.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets_mobile/css/customer.css"/>
    <script src="${pageContext.request.contextPath}/assets_mobile/js/jquery-2.1.4.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets_mobile/js/jquery.mobile-1.4.5.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets_mobile/js/jquery.mobile.simpledialog2.min.js"></script>
    <script type="application/javascript">
        var submitForm=function(){
                $.ajax({
                    url: "test/seeHello",
                    dataType: "json",
                    crossDomain: true,
                    type: "POST",
                    data: {
                        name:$("#name").val(),
                    }
                }).then( function ( response ) {
                    $('<div>').simpledialog2({
                        mode: 'blank',
                        headerText: 'Hello World',
                        headerClose: true,
                        dialogAllow: true,
                        dialogForce: true,
                        blankContent :
                        "<br><h1 style='font-size: 18px;'><strong style='font-size: 18px;'>"+response.msg+"</strong></h1><br><br>"+
                        "<a data-rel='back' class='ui-btn-active' data-role='button' href='javascript:void(0);'>关闭</a>"
                    })
                });
            }
        

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
    			
    			$('<div>').simpledialog2({
                    mode: 'blank',
                    headerText: '<pg:message code="sany.pdp.common.alert"/>',
                    headerClose: true,
                    dialogAllow: true,
                    dialogForce: true,
                    blankContent :
                    "<br><h1 style='font-size: 18px;'><strong style='font-size: 18px;'><pg:message code='sany.pdp.input.login.name'/></strong></h1><br><br>"+
                    "<a data-rel='back' class='ui-btn-active' data-role='button' href='javascript:void(0);'>关闭</a>"
                })
    			 $("#userName").focus();
    	    	event.returnValue=false;
    		    }else if(p==""&&s!=""){
    		    	$('<div>').simpledialog2({
                        mode: 'blank',
                        headerText: '<pg:message code="sany.pdp.common.alert"/>',
                        headerClose: true,
                        dialogAllow: true,
                        dialogForce: true,
                        blankContent :
                        "<br><h1 style='font-size: 18px;'><strong style='font-size: 18px;'><pg:message code='sany.pdp.input.password'/></strong></h1><br><br>"+
                        "<a data-rel='back' class='ui-btn-active' data-role='button' href='javascript:void(0);'>关闭</a>"
                    })
    		    	$("#password").focus();
    	    		event.returnValue=false;
    		    }
    		    
    	    if(s!=""&&p!=""){
     			//document.getElementById('password').value = strEnc(p,s, "", "");
    	    	$("#IntegralFormPanel").submit();
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
    		$("#IntegralFormPanel").reset();
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
    				//IntegralFormPanel.subsystem_id.focus();
    				
    				document.getElementById('password').value = strEnc(password,userName, "", "");
    				$("#IntegralFormPanel").submit();
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
    				//IntegralFormPanel.subsystem_id.focus();
    				
    				document.getElementById('password').value = strEnc(password,userName, "", "");
    				$("#IntegralFormPanel").submit();
    				event.returnValue=false;
    			}
    		}
    	}
    	
    	
    	function changeLan(){
    		window.location.href="${pageContext.request.contextPath}/security/cookieLocale.page?language="+$('#language').val() ;
    	}
    </script>
</head>

<body>
        <div id="indexPage" data-role="page">
            <div data-role="header"><h1>${defaultmodulename }</h1></div>
            <div role="main" class="ui-content">
            <!--<section class="pub_box_tr zsfg">-->
                <!--<a class="pub_img_tl" href="#">-->
                    <!--<img src="images/map/logo.jpg">-->
                    <!--<div class="edt_koa">-->
                        <!--<div class="cin_cia"><h3 class="new_tit">Hello World</h3></div>-->
                    <!--</div>-->
                <!--</a>-->
            <!--</section>-->
            	 
                <form id="IntegralFormPanel" name="IntegralFormPanel" action="login.page" method="post" target="_self">
					<pg:dtoken/>
					<input type="hidden" name="flag" value="yes" />
					<input name="macaddr_" type="hidden" />
					<input name="machineName_" type="hidden" />
					<input name="machineIp_" type="hidden" />
					<pg:notempty actual="${successRedirect  }">
						<input name="<%=org.frameworkset.web.interceptor.AuthenticateFilter.referpath_parametername %>" type="hidden" value="${successRedirect  }"/>
						
						
					</pg:notempty>
                    <li class="ui-field-contain">
                        <label for="name">请输入您的名字:</label>
                        <input name="userName" id="userName" type="text" placeholder="请输入您的名字" value="">
                    </li>
                     <li class="ui-field-contain">
                        <label for="password">请输入您的口令:</label>
                        <input name="password" id="password" type="password" placeholder="请输入您的口令" value="">
                    </li>
                    <pg:notempty actual="${language }" evalbody="true">
					<pg:yes>
						<li class="ui-field-contain">
							<label for="language"><pg:message code="sany.pdp.language"/>：</label>
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
                    <a type="input" onclick="login(event);" href="javascript:void(0);" class="ui-btn ui-corner-all ui-btn-active" data-theme="c">提交</a>
                </form>
            </div>
            <div class="nav-glyphish-example" data-role="footer" data-position="fixed" data-theme="a">
                <div class="nav-glyphish-example" id="menuListNavbar" data-role="navbar" data-grid="b">
                    <ul id="menuListPanel">

                        <li><a id="setup" data-ajax="false" href="guideMain.html" data-transition="flip"  data-icon="clock">网点预约</a></li>
                        <li><a id="control" data-ajax="false" href="Integral.html" data-transition="flip" data-icon="star">积分查询</a></li>
                        <li><a id="update" data-ajax="false" href="contract.html" data-transition="flip" data-icon="action">业务签约</a></li>
                    </ul>
                </div>
            </div>
        </div>
</body>
</html>