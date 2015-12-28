<%@ page language="java"  pageEncoding="utf-8" session="false"%>
<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg"%>

<%--
描述：SessionList
作者：谭湘
版本：1.0
日期：2014-06-04
 --%>	

 

	   <pg:pager scope="request"  data="sessionList" desc="true" isList="false" containerid="sessionContainer">
		<pg:param name="sessionid"/>
		<pg:param name="appkey"/>
		<pg:param name="referip"/>
		<pg:param name="createtime_start"/>
		<pg:param name="createtime_end"/>
		<pg:param name="host"/>
		<pg:param name="validate"/>
		<pg:list requestKey="monitorAttributes" var="attribute">
			<pg:param name="${attribute.name}"/>
		</pg:list>
		<!-- 加入 class="tableOutline" 可控制表格宽度，滚动条展示 -->
		<div id="changeColor">
		 <table width="100%" border="0" cellpadding="0" cellspacing="0" class="stable" id="tb">
	        <pg:header>
	            <th align=center><input id="CKA" name="CKA" type="checkbox" 
								onClick="checkAll('CKA','CK')"></th>
				<th>SessionID</th>				
				<pg:list requestKey="monitorAttributes"><th><pg:cell colName="cname"/></th></pg:list>
				
	       		<th>创建时间</th>
	       		<th>最近访问时间</th>
	       		<th>有效期</th>
	       		<th>状态</th>
	       		<th>预计失效时间</th>
	       		<th>httpOnly</th>
	       		<th>secure</th>
	       		<th>创建会话url</th>
	       		<th>最近访问会话url</th>	   
	       		<th>创建会话服务器</th>
	       		<th>最近访问服务器</th>
	       		<th>客户端IP</th>	       		    		
	       		<th>操作</th>	       		
	       	</pg:header>	
			<pg:empty actual="${sessionList}" evalbody="true">
				<pg:yes>			
					<tr >
						<td colspan="100" align="center"><pg:empty actual="${message}"  evalbody="true">
							<pg:yes>
								<img src="${pageContext.request.contextPath}/include/images/no_data.jpg"/>
							</pg:yes>
							<pg:no>
								<span style=" color: red;">${message}</span>
							</pg:no>	
						</pg:empty>
						</td>
					</tr >
			    </pg:yes> 
			    <pg:no>
				      <pg:list >
				   		<tr >
					        <td class="td_center">
				                <input id="CK" type="checkbox" name="CK" <pg:equal colName="sessionid" value="${currentsessionid}">disabled</pg:equal> onClick="checkOne('CKA','CK')" value="<pg:cell colName="sessionid" />"/>
				            </td>
				            
				    		<td><pg:equal colName="sessionid" value="${currentsessionid}" evalbody="true">
					    		<pg:yes><span style=" color: blue;"><b><pg:cell colName="sessionid" /></b></span></pg:yes>
					    		<pg:no><pg:cell colName="sessionid" /></pg:no>
				    		    </pg:equal></td> 
				    		<pg:list colName="extendAttributes">
				            	<td><pg:cell colName="value" /></td>
				            </pg:list>    
				    		<td><pg:cell colName="creationTime" dateformat="yyyy-MM-dd HH:mm:ss"/></td>
				    		<td><pg:cell colName="lastAccessedTime" dateformat="yyyy-MM-dd HH:mm:ss"/></td>  
				    		<td><strong><font color="red"><pg:cell colName="maxInactiveInterval" /></font></strong></td>
				    		<td>
					       		<pg:true colName="validate" evalbody="true">
						       		<pg:yes><strong><font color="green">有效</font></strong></pg:yes>
						       		<pg:no><strong><font color="red">无效</font></strong></pg:no>
					       		</pg:true>
					       		
				       		</td>
				       		<td><pg:notnull colName="loseTime" evalbody="true">
				       		<pg:yes><strong><font color="red"><pg:cell colName="loseTime" dateformat="yyyy-MM-dd HH:mm:ss"/></font></strong></pg:yes>
				       		<pg:no><strong><font color="red"><pg:cell colName="maxInactiveInterval"  /></font></strong></pg:no>
				       		</pg:notnull ></td>
				       		<td> 
					       		<pg:true colName="httpOnly"  evalbody="true">
					       			<pg:yes><strong><font color="green">启用</font></strong></pg:yes>
					       			<pg:no><strong><font color="red">关闭</font></strong></pg:no>
					       		</pg:true>
				       		</td>
				       		<td>
					       		<pg:true colName="secure" evalbody="true">
						       		<pg:yes><strong><font color="green">启用</font></strong></pg:yes>
						       		<pg:no><strong><font color="red">关闭</font></strong></pg:no>
					       		</pg:true>
				       		</td>     
				       		
				       		<td><pg:cell colName="requesturi"/></td>
				       		<td ><pg:cell colName="lastAccessedUrl" htmlEncode="true"/></td>
				       		<td><pg:cell colName="host" /></td>
				       		<td><pg:cell colName="lastAccessedHostIP"/></td>
				       		<td><pg:cell colName="referip"/></td>
				       		
				       		
				       		
				            <td class="td_center">
				            	<pg:notequal colName="sessionid" value="${currentsessionid}"><a href="javascript:void(0)" id="viewTaskDetailInfo" onclick="delSession('<pg:cell colName="sessionid" />')">删除</a>|</pg:notequal>
				            	<a href="javascript:void(0)" id="viewTaskDetailInfo" onclick="sessionInfo('<pg:cell colName="sessionid" />')">详情</a>
				            </td>    
				        </tr>
					 </pg:list>
				</pg:no>
		   </pg:empty> 
	    </table>
	    </div>
		<div class="pages"><input type="hidden" value="<pg:querystring/>" id="querystring"/><pg:index tagnumber="5" sizescope="10,20,50,100"/></div>
	
	    </pg:pager>
    
 
