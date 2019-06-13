<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${event}</title>
</head>
<body>
    <center>
        <h2>日志事件：${event}</h2>
    </center>
<%
	List logs = (List)session.getAttribute("logs");
	if (logs != null){
		for (int i = 0; i < logs.size(); i++){
			if (i > 100){
				break;
			}
			%> <p> <%= logs.get(i) %></p> <%
		}
	}
%>
</body>
</html>