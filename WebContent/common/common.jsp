<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- <s:url value="/" var="ctp"></s:url --%>
<% 
	String ctp = request.getContextPath()+"/";
	request.setAttribute("ctp", ctp);
%>
