<%@page import="com.frameworkset.util.StringUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
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
						<td width="400"><pg:cell colName="appcode" /></td>

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
						<td width="400"><pg:cell colName="sessionscaninterval" /></td>

					</tr>
					<tr>


						<th width="150"><strong>失效session扫描进程开启时间:</strong></th>
						<td width="150" colspan="2"><pg:cell colName="scanStartTime"
								dateformat="yyyy-MM-dd HH:mm:ss" /></td>

					</tr>
					<tr>
						<th width="150"><strong>sessionTimeout(单位：毫秒):</strong></th>
						<td width="400"><pg:cell colName="sessionTimeout" /></td>




					</tr>

					<tr>

						<th width="150"><strong>cookiename:</strong></th>
						<td width="400"><pg:cell colName="cookiename" /></td>



					</tr>


					<tr>
						<th width="150"><strong>配置保存时间:</strong></th>
						<td width="400"><pg:cell colName="createTime"
								dateformat="yyyy-MM-dd HH:mm:ss" /></td>

					</tr>
					<tr>
						<th width="150"><strong>配置更新时间:</strong></th>
						<td width="300"><pg:cell colName="updateTime"
								dateformat="yyyy-MM-dd HH:mm:ss" /></td>

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
						<th width="150"><strong>sessionStore:</strong></th>
						<td width="400"><pg:cell colName="sessionStore" /></td>

					</tr>
					<tr>
						<th width="150"><strong>sessionListeners:</strong></th>
						<td width="400"><pg:cell colName="sessionListeners" /></td>

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

							<td><pg:cell colName="name" /></td>


							<td><pg:cell colName="cname" /></td>
							
							<td><pg:cell colName="type" /></td>
							
							<td><pg:cell colName="like" /></td>
							
							<td><pg:cell colName="enableEmptyValue" /></td>
							
							<td><pg:cell colName="useIndex" /></td>


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
							<td width="400"><pg:cell colName="rootDomain" /></td>

						</tr>
						
						<tr>
							<th width="150"><strong>shareSessionAttrs :</strong></th>
							<td width="400"><pg:cell colName="shareSessionAttrs" /></td>

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

								<td><pg:cell colName="path" /></td>
								<td><pg:cell colName="attributeNamespace" /></td>

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
