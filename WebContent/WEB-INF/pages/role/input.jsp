<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${ctp }css/content.css">
<link rel="stylesheet" type="text/css" href="${ctp }css/input.css">

<script type="text/javascript" src="${ctp }script/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="${ctp }script/jquery.validate.js"></script>

<script type="text/javascript" src="${ctp }script/messages_cn.js"></script>
<script type="text/javascript" src="${ctp }script/jquery.blockUI.js"></script>
<script type="text/javascript">
	$(function() {
		$("select").change(function() {
			$("p[class^='authority-']").hide();
			var val = $(this).val();
			$("p[class='authority-" + val + "']").show();

		});
		$(":checkbox").click(
				function() {
					var checked = $(this).is(":checked");

					if (checked) {
						var relatedAuthorites = $(this).attr("class");
						var ids = relatedAuthorites.split(",");
						for (var i = 0; i < ids.length; i++) {
							var id = $.trim(ids[i]);
							if (id != "") {
								$(":checkbox[value='" + id + "']").attr(
										"checked", true);
							}
						}
					} else {
						var val = $(this).val();
						$(":checkbox[class*='," + val + ",']").attr("checked",
								false);
					}
				});
		$("#employeeForm").validate();
	})
</script>
<title>Insert title here</title>
</head>
<body>
	<br>
	<s:form id="employeeForm" method="post" cssClass="employeeForm" action="/role-save">

		<fieldset>
			<p>
				<label for="name">角色名(必填*)</label>
				<s:textfield name="roleName" cssClass="required"></s:textfield>
			</p>

			<p>
				<label for="authority">授予权限(必选)</label>
				<input type="checkbox" name="authorities2" style="display: none" class="required" />
			</p>

			<p>
				<label>权限名称(必填)</label>
				<!-- 父权限 -->
				<s:select list="#request.parentAuthority" listKey="id" listValue="displayName"
					headerKey="" headerValue="请选择..." name="abcd"></s:select>
			</p>

			<s:checkboxlist list="#request.subAuthorities" listKey="id" listValue="displayName"
				name="authorities2" templateDir="/ems/template" listCssClass="relatedAuthorites"></s:checkboxlist>

			<p>
				<input class="submit" type="submit" value="Submit" />
			</p>
		</fieldset>
	</s:form>
</body>
</html>