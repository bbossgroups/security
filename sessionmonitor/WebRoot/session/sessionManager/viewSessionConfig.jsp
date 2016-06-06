<%@ page language="java" pageEncoding="UTF-8"  session="false"%>
<%@page import="com.frameworkset.util.StringUtil" %>

<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg"%>
<br>
<pg:empty actual="${message }" evalbody="true">
	<pg:yes>
		<pg:beaninfo requestKey="sessionConfig">

			<fieldset>
				<legend>
					<strong>Session管理-基本配置</strong>
				</legend>

				<table border="0" cellpadding="0" cellspacing="0" class="table4">
					<tr>
						<th width="150"><strong>appcode:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="appcode" /></font></strong></td>

					</tr>
					<tr>
						<th width="150"><strong>sessionTimeout(session有效期，单位：毫秒):</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="sessionTimeout" /></font></strong></td>




					</tr>
					<tr>
						<th width="150"><strong>失效session销毁进程:</strong></th>
						<td width="400" colspan="2"><pg:true colName="startLifeScan"
								evalbody="true">
								<pg:yes>
									<strong><font color="green">开启</font></strong>
								</pg:yes>
								<pg:no>
									<strong><font color="red">关闭</font></strong>
								</pg:no>
							</pg:true></td>




					</tr>
					<tr>
						<th width="150"><strong>失效session扫描时间间隔(单位 毫秒):</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="sessionscaninterval" /></font></strong></td>

					</tr>
					<tr>


						<th width="150"><strong>失效session扫描进程开启时间:</strong></th>
						<td width="150" colspan="2"><strong><font color="green"><pg:cell colName="scanStartTime"
								dateformat="yyyy-MM-dd HH:mm:ss" /></font></strong></td>

					</tr>
					

					<tr>

						<th width="150"><strong>cookiename:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="cookiename" /></font></strong></td>



					</tr>


					<tr>
						<th width="150"><strong>配置保存时间:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="createTime"
								dateformat="yyyy-MM-dd HH:mm:ss" /></font></strong></td>

					</tr>
					<tr>
						<th width="150"><strong>配置更新时间:</strong></th>
						<td width="300"><strong><font color="green"><pg:cell colName="updateTime"
								dateformat="yyyy-MM-dd HH:mm:ss" /></font></strong></td>

					</tr>
					<tr>
						<th width="150"><strong>httpOnly:</strong></th>
						<td width="300"><pg:true colName="httpOnly" evalbody="true">
								<pg:yes>
									<strong><font color="green">开启</font></strong>
								</pg:yes>
								<pg:no>
									<strong><font color="red">关闭</font></strong>
								</pg:no>
							</pg:true></td>

					</tr>
					<tr>
						<th width="150"><strong>secure:</strong></th>
						<td width="400"><pg:true colName="secure" evalbody="true">
								<pg:yes>
									<strong><font color="green">开启</font></strong>
								</pg:yes>
								<pg:no>
									<strong><font color="red">关闭</font></strong>
								</pg:no>
							</pg:true></td>

					</tr>
					<tr>
						<th width="150"><strong>lazystore:</strong></th>
						<td width="400"><pg:true colName="lazystore" evalbody="true">
								<pg:yes>
									<strong><font color="green">开启</font></strong>
								</pg:yes>
								<pg:no>
									<strong><font color="red">关闭</font></strong>
								</pg:no>
							</pg:true>
							&nbsp;&nbsp;(延迟存储修改的session数据到mongodb的控制开关，<strong><font color="green">开启</font></strong>表示当请求结束前一次性将request过程中所有通过session.setAttribute(key,value)方法对session数据的修改存储到会话共享平台中，提升性能，否则每次都实时将修改存储到会话共享平台中，默认为<strong><font color="red">关闭</font></strong>)
							</td>

					</tr>
					<tr>
						<th width="150"><strong>应用监控管理session范围:</strong></th>
						<td width="800"><strong><font color="green"><pg:cell colName="monitorScope" /></font></strong> &nbsp;&nbsp;(all:代表应用可以监控所有接入统一会话管理平台应用session数据， self:代表应用只能监控管理本应用session数据)</td>




					</tr>
					<tr>
						<th width="150"><strong>sessionStore:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="sessionStore" /></font></strong></td>

					</tr>
					<tr>
						<th width="150"><strong>sessionListeners:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="sessionListeners" /></font></strong></td>

					</tr>
					
					<tr>
						<th width="150"><strong>sessionid生成器:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="sessionidGeneratorPlugin" /></font></strong></td>

					</tr>
					
					<tr>
						<th width="150"><strong>session序列化机制:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="serialType" /></font></strong></td>

					</tr>
					<tr>
						<th width="150"><strong>storeReadAttributes:</strong></th>
						<td width="400"><strong><font color="green"><pg:cell colName="storeReadAttributes" /></font></strong></td>

					</tr>

				</table>
			</fieldset>
			<pg:notempty colName="extendAttributeInfos" >
			 <br>
					<legend>
						<strong>Session管理-可查询属性:</strong>
					</legend>
					
					
					 
						<table width="100%" border="0" cellpadding="0" cellspacing="0"
						class="stable">
							<tr>

							<th>属性名称</th>


							<th>属性中文名称</th>
							
							<th>属性类型</th>
							
							<th>like查询</th>
							
							<th>启用空值查询</th>
							
							<th>启用属性索引</th>


						</tr>
						<pg:list colName="extendAttributeInfos" >
						<tr>

							<td><strong><font color="green"><pg:cell colName="name" /></font></strong></td>


							<td><strong><font color="green"><pg:cell colName="cname" /></font></strong></td>
							
							<td><strong><font color="green"><pg:cell colName="type" /></font></strong></td>
							
							<td><strong><font color="green"><pg:cell colName="like" /></font></strong></td>
							
							<td><strong><font color="green"><pg:cell colName="enableEmptyValue" /></font></strong></td>
							
							<td><strong><font color="green"><pg:cell colName="useIndex" /></font></strong></td>


						</tr>
						</pg:list>
						</table>
						
					 
					
				  <br>
				</pg:notempty>
		</pg:beaninfo>
		<pg:notempty requestKey="crossDomain">
			<pg:beaninfo requestKey="crossDomain">
				<fieldset>
					<legend>
						<strong>Session管理-跨域跨站配置:</strong>
					</legend>
					<table border="0" cellpadding="0" cellspacing="0" class="table4">
						<tr>
							<th width="150"><strong>rootDomain:</strong></th>
							<td width="400"><strong><font color="green"><pg:cell colName="rootDomain" /></font></strong></td>

						</tr>
						
						<tr>
							<th width="150"><strong>shareSessionAttrs :</strong></th>
							<td width="400"><strong><font color="green"><pg:cell colName="shareSessionAttrs" /></font></strong></td>

						</tr>


					</table>
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						class="stable"  >
						<tr>

							<th>应用上下文</th>


							<th>私有属性命名空间</th>


						</tr>

						<pg:list colName="domainApps">
							<tr>

								<td><strong><font color="green"><pg:cell colName="path" /></font></strong></td>
								<td><strong><font color="green"><pg:cell colName="attributeNamespace" /></font></strong></td>

							</tr>
						</pg:list>

					</table>
				</fieldset>

			</pg:beaninfo>
		</pg:notempty>
	</pg:yes>
	<pg:no>${message }</pg:no>
</pg:empty>
<br>
