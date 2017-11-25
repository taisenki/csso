<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>csso单点登录系统集成示例</title>
</head>
<body>
<p>这是csso集成单点登录系统的示例一个用，演示了如何集成单点登录系统csso.</p>
<p style="color: red;">
	单点登录成功，当前登录的用户是：${user.userId}.登录的应用ID是：${user.appId}
</p>

<p>
<a href="${csso_server_logout_url}?service=http://${ip}:${port}/csso-app/index.jsp">统一登出</a>
</p>
</body>
</html>