<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>

</head>
<body>
	<button onclick="javascript:location.href='<%=request.getContextPath()%>/member/loginForm';">로그인</button>

	<script>
				location.href="<%=request.getContextPath()%>/member/memberList";
	</script>
</body>
</html>