<%@ page language="java" pageEncoding="utf-8"%>
<%@page import=" org.frameworkset.security.session.impl.SessionHelper"%>
<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg"%>
<%--
	描述：session管理
	作者：谭湘
	版本：1.0
	日期：2014-06-04
	 --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Session管理</title>
<script type='text/javascript' src='<%=request.getContextPath() %>/include/jquery-1.4.2.min.js' language='JavaScript'></script>
<script type='text/javascript' src='<%=request.getContextPath() %>/include/pager.js' language='JavaScript'></script>
<link href='<%=request.getContextPath() %>/include/pager.css' type='text/css' rel='stylesheet'>


<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/include/css/common.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/include/js/dialog/lhgdialog.js"></script>
<script language="javascript" type="text/javascript"
src="${pageContext.request.contextPath}/include/datepicker/My97DatePicker/WdatePicker.js?lang=zh_CN"></script>
<style type="text/css">
<!--
  #rightContentDiv{
	position:absolute;
	margin-left:220px;
	margin-right:8px;
	width: 80.5%;	   
  }
  
  #leftContentDiv{
	width:200px;
	position:absolute;
	left:0px;
	padding-left:20px;  	   
  }

-->
</style>

<script type="text/javascript">

$(document).ready(function() {
	bboss.pager.pagerevent = {   
			                            beforeload:null,   
			                          afterload:function(opt){ 
			                             getTreeDate(false,null);   
	}};   

	$("#wait").hide();
       		
	queryList(null,false,false);   
	
	$('#delBatchButton').click(function() {
		delSessions();
    });
	
	$('#delAllButton').click(function() {
		delAllSessions();
    });
    <%if(SessionHelper.isMonitorAll()){%>
    $('#delAppButton').click(function() {
		delApp();
    });
    <%}%>
	
});
       
//加载实时任务列表数据  
function queryList(appkey ,reset,loadextendattrs){
	if(reset)
		doreset();
	if(appkey != null && appkey != '')
		$("#appkey").val(appkey);
	 
	var sessionid = $("#sessionid").val();
	var createtime_start = $("#createtime_start").val();
	var createtime_end = $("#createtime_end").val();
	var referip = $("#referip").val();
	var host = $("#host").val();
	var validate = $("#validate").val();
	
	if (!$.trim(appkey)==''){
		$("#titileSpan").text("("+appkey+")Session列表");
	}
	
	var condition = $("#queryForm").serialize();
    $("#sessionContainer").load("<%=request.getContextPath()%>/session/sessionManager/querySessionData.page", 
    		condition,
    	function(){});
    
    getTreeDate(loadextendattrs,appkey);
}

function doreset(){
	$("#reset").click();
}

var treeData = null;

function getTreeDate(loadextendattrs,appKey){
	var data = {loadextendattrs:loadextendattrs};
	if(appKey != null && appKey != '')
	  $.extend(data,{ "appKey":appKey});
	$.ajax({
 	 	type: "POST",
		url : "<%=request.getContextPath()%>/session/sessionManager/getAppSessionData.page",
		data :data,
		dataType : 'json',
		async:false,
		beforeSend: function(XMLHttpRequest){
			 	XMLHttpRequest.setRequestHeader("RequestType", "ajax");
			},
		success : function(data){
			if (data) {
				treeData = data;
				
			} else {
				
			}
			initTreeModule('');
			if(loadextendattrs)
				initExtendAttributes();
		}	
	 });
	
	
}
function initExtendAttributes()
{
	var ExtendAttributesHtml = "";
	if(treeData && treeData.extendAttributes){
		var extendAttributes = treeData.extendAttributes;
		var seq = 1;
		ExtendAttributesHtml='<tr>';
		ExtendAttributesHtml +='<td class="left_box"></td><td><table width="100%" border="0" cellpadding="0" cellspacing="0" class="table2">';
		 
		for(var i=0; i<extendAttributes.length; i++){
			if( i % 3 == 0)
			{
				if(i > 0)
				{
					ExtendAttributesHtml+='</tr>';
				}
					
			 	ExtendAttributesHtml+='<tr>';
			}
			var extendAttribute = extendAttributes[i];	 
			ExtendAttributesHtml+='<th>'+extendAttribute.cname+':</th>';
			ExtendAttributesHtml+='<td><input id="'+extendAttribute.name+'" name="'+extendAttribute.name+'" type="text" class="w120"/>'; 
			if(extendAttribute.enableEmptyValue)	
				ExtendAttributesHtml+='查询'+extendAttribute.cname+'为空的记录<input id="'+extendAttribute.name+'_enableEmptyValue" name="'+extendAttribute.name+'_enableEmptyValue" type="checkbox"/>';
			ExtendAttributesHtml+='</td>';	
				
		  if(i == (extendAttributes.length - 1))
		  {
			  ExtendAttributesHtml+='</tr>';
		  }
		}
		ExtendAttributesHtml+='</table></td><td class="right_box"></td></tr>';
		$("#extendAttributes").html(ExtendAttributesHtml);
		$("#extendAttributes").show();	
	}
	else
	{
		$("#extendAttributes").html("");
		$("#extendAttributes").hide();
	}
	
	
}
 
function initTreeModule(app_query){
	var treeModuleHtml = "";
	if(treeData && treeData.apps){
		var apps = treeData.apps;
		var seq = 1;
		for(var i=0; i<apps.length; i++){
			if(app_query!=null && app_query!=""){
				if(apps[i].appkey.toLowerCase().indexOf(app_query.toLowerCase()) >= 0 ){
					treeModuleHtml += 
    					"<li id=\""+apps[i].appkey+"\"><a href=\"javascript:void(0);\" onclick=\"doClickTreeNode('"+apps[i].appkey+"',this)\" >"+apps[i].appkey+" ("+apps[i].sessions+")</a></li>";
    					seq++;	
				}
			}else{
				
				treeModuleHtml += 
					"<li id=\""+apps[i].appkey+"\"><a href=\"javascript:void(0);\" onclick=\"doClickTreeNode('"+apps[i].appkey+"',this)\" >"+apps[i].appkey+" ("+apps[i].sessions+")</a></li>";
					seq++;	
			}
		}
	}
	
	$("#app_tree_module").html(treeModuleHtml);
	if($("#appkey").val()!=""){
		if($("#"+$("#appkey").val()).length > 0){
			$("#"+$("#appkey").val()).attr("class","select_links");
		}
	}
}

function sortAppTree(){
	var app_query = $("#app_query").val();
	initTreeModule(app_query);
}

function doClickTreeNode(app_id,selectedNode){
	
	$("#app_tree_module").find("li").removeAttr("class");
	$("#"+app_id).attr("class","select_links");
	
	
   	
	$("#app_query_th").html("&nbsp;");
   	$("#wf_app_name_td").html("&nbsp;");

	queryList(app_id,true,true);
	getSessionConfig(app_id);
} 

function getSessionConfig(app_id)
{
	
	
	  $("#sessionConfig").load("<%=request.getContextPath()%>/session/sessionManager/viewSessionConfig.page?appkey="+app_id,
	    	function(){});
	    
}

function sessionInfo(sessionid){
	
	var url="<%=request.getContextPath()%>/session/sessionManager/viewSessionInfo.page?"+
			"sessionid="+sessionid+"&appkey="+$("#appkey").val();
	$.dialog({ title:'明细查看-'+$("#appkey").val(),width:1100,height:620, content:'url:'+url});
	
}

function delSession (sessionid) {
    
    $.dialog.confirm('确定要删除吗？', function(){
     	$.ajax({
	 	type: "POST",
	 	url : "<%=request.getContextPath()%>/session/sessionManager/delSessions.page",
	 	data :{"sessionids":sessionid,"appkey":$("#appkey").val()},
		dataType : 'json',
		async:false,
		beforeSend: function(XMLHttpRequest){
			 	XMLHttpRequest.setRequestHeader("RequestType", "ajax");
			},
		success : function(data){
			if (data != 'success') {
				 $.dialog.alert("删除session失败："+data);
			}else {
				queryList();
				
			}
		}	
	 });
	},function(){
	  		
	});         
    
}

function delSessions () {
	var ids="";
	
	$("#tb tr:gt(0)").each(function() {
		if ($(this).find("#CK").get(0).checked == true) {
             ids=ids+$(this).find("#CK").val()+",";
        }
    });
	
    if(ids==""){
       $.dialog.alert('请选择需要删除的session！');
       return false;
    }
    
    delSession(ids);
}
function delApp()
{
	if($("#appkey").val() == '')
	{
		  $.dialog.alert('请选择左边的应用,然后再删除应用!');
		return;
	}
	$.dialog.confirm('确定要删除'+$("#appkey").val()+'应用吗？', function(){
     	$.ajax({
	 	type: "POST",
	 	url : "<%=request.getContextPath()%>/session/sessionManager/deleteApp.page",
	 	data :{"appkey":$("#appkey").val()},
		dataType : 'json',
		async:false,
		beforeSend: function(XMLHttpRequest){
			 	XMLHttpRequest.setRequestHeader("RequestType", "ajax");
		},
		success : function(data){
			if (data != 'success') {
				 $.dialog.alert("删除"+$("#appkey").val()+"应用失败："+data);
			}else {
				
			reloadpage();
				
				
			}
		}	
	 });
	},function(){
	  		
	});   
}

function reloadpage()
{
	alert("删除"+$("#appkey").val()+"应用成功");
	window.location.reload() ; 
}
function delAllSessions () {
	if($("#appkey").val() == '')
	{
		  $.dialog.alert('请选择左边的应用,然后再清除应用会话信息!');
		return;
	}
	$.dialog.confirm('确定要清空'+$("#appkey").val()+'应用下所有的session吗？', function(){
     	$.ajax({
	 	type: "POST",
	 	url : "<%=request.getContextPath()%>/session/sessionManager/delAllSessions.page",
	 	data :{"appkey":$("#appkey").val()},
		dataType : 'json',
		async:false,
		beforeSend: function(XMLHttpRequest){
			 	XMLHttpRequest.setRequestHeader("RequestType", "ajax");
		},
		success : function(data){
			if (data != 'success') {
				 $.dialog.alert("清空"+$("#appkey").val()+"应用下所有session失败："+data);
			}else {
				queryList();
				
			}
		}	
	 });
	},function(){
	  		
	});         
}

</script>
</head>

<body>
<div class="mcontent" style="width:98%;margin:0 auto;overflow:auto;">
	
	<sany:menupath menuid="sessioncontrol" />

	<div id="leftContentDiv">
			    
		<div class="left_menu" style="width:193px;">
		    <ul>
		    	<li class="select_links">
		    		<a href="javascript:void(0)">应用查询：</a><input type="input" style="width:100px;" name="app_query" id="app_query" onKeyUp="sortAppTree()" />
		    		<ul style="display: block;" id="app_tree_module">
		    			
		    		</ul>
		    	</li>
		    </ul>
		</div>
		
	</div>
		
	<div id="rightContentDiv">
			
		<div id="searchblock">
			
			<div class="search_top">
				<div class="right_top"></div>
				<div class="left_top"></div>
			</div>
			
			<div class="search_box">
				
			
				<form id="queryForm" name="queryForm">
				<input type="hidden" id="appkey" name="appkey" value=""/>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="left_box"></td>
							<td>
								<table width="100%" border="0" cellpadding="0" cellspacing="0" class="table2">
									<tr>
										<th>SessionID：</th>
										<td><input id="sessionid" name="sessionid" type="text" class="w120"/></td>
										<th>客户端IP：</th>
										<td><input id="referip" name="referip" type="text" class="w120"/></td>
										<th>创建时间：</th>
										<td><input id="createtime_start" name="createtime_start" type="text"
											 onclick="new WdatePicker({dateFmt:'yyyy/MM/dd HH:mm:ss'})" class="w120" />
											 ~<input id="createtime_end" name="createtime_end" type="text"
											 onclick="new WdatePicker({dateFmt:'yyyy/MM/dd HH:mm:ss'})" class="w120" />
										</td>
										
									</tr>
									<tr>
										<th>状态：</th>
										<td>
											<select id="validate" name="validate" class="select1" style="width: 125px;">
												    <option value="" selected>全部</option>
													<option value="1">有效</option>
													<option value="0">无效</option>
											</select>
										</td>
										<th>创建会话服务器：</th>
										<td><input id="host" name="host" type="text" class="w120"/></td>
									</tr>
								</table>
							</td>
							<td class="right_box"></td>
						</tr>
					</table>
					
					
					<table id="extendAttributes" width="100%" border="0" cellspacing="0" cellpadding="0" style="display: none">
						
					</table>
				<input type="reset" id="reset" style="display:none"/>
				</form>
			</div>
			
				
			<div class="search_bottom">
				<div class="right_bottom"></div>
				<div class="left_bottom"></div>
			</div>
		</div>
			
		<div class="title_box">
			<div class="rightbtn">
				
											<a href="javascript:void(0)" class="bt_1" id="queryButton" onclick="queryList()"><span>查询</span></a>
											<a href="javascript:void(0)" class="bt_2" id="resetButton" onclick="doreset()"><span>重置</span></a>
											
										
				<a href="javascript:void(0)" class="bt_small" id="delAllButton"><span>清空应用下Session</span></a>
				<a href="javascript:void(0)" class="bt_small" id="delBatchButton"><span>批量删除</span></a>
				 <%if(SessionHelper.isMonitorAll()){%><a href="javascript:void(0)" class="bt_small" id="delAppButton"><span>删除应用（慎用）</span></a><%} %>
			</div>
					
			<strong><span id="titileSpan">Session列表</span></strong>
			<img id="wait" src="<%=request.getContextPath()%>/include/images/wait.gif" />				
		</div>
			
		<div id="sessionContainer" style="overflow:auto"></div>
		
		<div id="sessionConfig" style="overflow:auto"></div>
	</div>
</div>
</body>
</html>
