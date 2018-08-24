<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${ctp}css/content.css">
<link rel="stylesheet" type="text/css" href="${ctp}css/input.css">
<link rel="stylesheet" type="text/css" href="${ctp}css/weebox.css">

<link rel="stylesheet" type="text/css" href="${ctp}script/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${ctp}script/themes/icon.css">
<link rel="stylesheet" type="text/css" href="${ctp}css/default.css">

<script type="text/javascript" src="${ctp}script/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="${ctp}script/jquery.validate.js"></script>

<script type="text/javascript" src="${ctp}script/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${ctp}script/locale/easyui-lang-zh_CN.js"></script>

<script type="text/javascript" src="${ctp}script/messages_cn.js"></script>
<script type="text/javascript" src="${ctp}script/bgiframe.js"></script>
<script type="text/javascript" src="${ctp}script/weebox.js"></script>
<script type="text/javascript">
	function myformatter(date) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		return y + '-' + (m < 10 ? ('0' + m) : m) + '-'
				+ (d < 10 ? ('0' + d) : d);
	}
	function myparser(s) {
		if (!s)
			return new Date();
		var ss = (s.split('-'));
		var y = parseInt(ss[0], 10);
		var m = parseInt(ss[1], 10);
		var d = parseInt(ss[2], 10);
		if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
			return new Date(y, m - 1, d);
		} else {
			return new Date();
		}
	}
	function myformatter(date) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		return y + '-' + (m < 10 ? ('0' + m) : m) + '-'
				+ (d < 10 ? ('0' + d) : d);
	}
	function myparser(s) {
		if (!s)
			return new Date();
		var ss = (s.split('-'));
		var y = parseInt(ss[0], 10);
		var m = parseInt(ss[1], 10);
		var d = parseInt(ss[2], 10);
		if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
			return new Date(y, m - 1, d);
		} else {
			return new Date();
		}
	}
	$(function() {
		$("#loginName").change(function() {
			var val = this.value;
			val = $.trim(val);
			this.value = val;

			var reg = /^[a-zA-Z]\w+\w$/g;
			if (val == "" || val.length < 6 || !reg.test(val)) {
				alert("loginName不合法！");
				$(this).focus();
				return;
			}
			
			var oldLoginName = $("#oldLoginName").val();
			if(oldLoginName == val){
				alert("loginName可用！");
				return;
			}

			var url = "emp-validateLoginName";
			var args = {
				"time" : new Date() + "",
				"loginName" : val
			}
			$.post(url, args, function(data) {
				if (data == "1") {
					alert("loginName可用！");
				} else if (data == "2") {
					alert("loginName不可用！");
				} else {
					alert("请重试！");
				}
			});

		});

		//$("#employeeForm").validate();

		$("#role_a_id")
				.click(
						function() {
							$.weeboxs
									.open(
											'#roles',
											{
												title : '角色分配',
												onopen : function() {
													$(
															":checkbox[name='roles2']")
															.each(
																	function(
																			index) {
																		var checked = $(
																				this)
																				.attr(
																						"checked");
																		$(
																				$(":checkbox[name!='roles2']")[index])
																				.attr(
																						"checked",
																						checked);
																	});
												},
												onok : function(box) {
													$(
															":checkbox[name!='roles2']")
															.each(
																	function(
																			index) {
																		var checked = $(
																				this)
																				.attr(
																						"checked");
																		$(
																				$(":checkbox[name='roles2']")[index])
																				.attr(
																						"checked",
																						checked);
																	});
													box.close();
												},
											});
							return false;
						});
	})
</script>
</head>
<body>
<%-- 	<s:debug></s:debug> --%>
	<br>

	<s:form id="employeeForm" cssClass="employeeForm" method="POST" action="/emp-save">
	<s:if test="employeeId != null">
		<input type="hidden" name="id" value="${employeeId}"/>
		<s:if test="#parameters.oldLoginName != null">
			<input type="hidden" value="${param.oldLoginName}" name="oldLoginName" id="oldLoginName"/>
		</s:if>
		<s:else>
			<input type="hidden" value="${loginName}" name="oldLoginName" id="oldLoginName"/>
		</s:else>
	</s:if>
		<fieldset>
			<p>
				<label for="message">
					<font color="red">添加 \ 修改员工信息</font>
				</label>
			</p>

			<p>
				<label for="loginName">登录名(必填)</label>
				<s:textfield name="loginName" id="loginName" cssClass="required" minLength="6"></s:textfield>
				<label id="loginlabel" class="error" for="loginname" generated="true">
					<!-- 显示服务器端简单验证的信息 -->
					<s:fielderror fieldName="loginName"></s:fielderror>
				</label>
			</p>

			<p>
				<label for="employeeName">姓名 (必填)</label>
				<s:textfield name="employeeName"></s:textfield>
			</p>

			<p>
				<label for="logingallow">登录许可 (必填)</label>
				<s:radio list="#{'1':'允许','0':'禁止' }" name="enabled" cssClass="border:none"></s:radio>
			</p>

			<p>
				<label for="gender">性别 (必填)</label>
				<s:radio list="#{'1':'男','0':'女' }" name="gender" cssClass="border:none"></s:radio>
			</p>

			<p>
				<label for="dept">部门 (必填)</label>
				<s:select list="#request.departments" listKey="departmentId"
					listValue="departmentName" name="department.departmentId"></s:select>

				<label class="error" for="dept" generated="true">
					<font color="red">
						<!-- 显示服务器端简单验证的信息 -->
					</font>
				</label>
			</p>

			<p>
				<label for="birth">生日 (必填)</label>
				<s:textfield name="birth" cssClass="easyui-datebox"
					data-options="formatter:myformatter,parser:myparser"></s:textfield>
			</p>

			<p>
				<label for="email">Email (必填)</label>
				<s:textfield name="email"></s:textfield>
				<label class="error" for="email" generated="true">
					<!-- 显示服务器端简单验证的信息 -->
				</label>
			</p>

			<p>
				<label for="mobilePhone">电话 (必填)</label>
				<s:textfield name="mobilePhone"></s:textfield>
			</p>

			<p>
				<label for="role">
					<a id="role_a_id" href="">分配角色(必选)</a>
				</label>
			</p>

			<div style="display: none" id="roles">
				<s:iterator value="#request.roles">
					<input type="checkbox" value="${roleId }" style="border: none" />${roleName}<br>
				</s:iterator>
			</div>

			<div style="display: none">
				<s:checkboxlist list="#request.roles" name="roles2" listKey="roleId"
					listValue="roleName" cssStyle="border:none"></s:checkboxlist>
			</div>

			<p>
				<label for="mobilePhone">创建人</label>
				<s:if test="employeeId == null">
					${sessionScope.employee.loginName }
					<input type="hidden" value="${sessionScope.employee.employeeId}"
						name="editor.employeeId" />
				</s:if>
				<s:else>
					${editor.loginName }
				</s:else>
			</p>

			<p>
				<input class="submit" type="submit" value="提交" />
			</p>

		</fieldset>

	</s:form>

</body>
</html>