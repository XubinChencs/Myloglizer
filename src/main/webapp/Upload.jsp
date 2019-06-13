<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>日志处理结果展示</title>
</head>
<body>
<h1>日志处理结果展示</h1>

<script language="JavaScript">

function checkForm(){
	var lDiv = document.getElementById("loading");
	if(lDiv.style.display=='none'){
	    lDiv.style.display='block';
	}
	return true;
}

function unfold(i){
	var temp = document.createElement("form");
	temp.action = "/LogParser/EventServlet";
	temp.method = "post";
	temp.style.display  = "none";
	var opt = document.createElement("textarea");
	opt.name = id;
	opt.value = i;
	temp.appendChild(opt);
	document.body.appendChild(temp);
	temp.submit();
}
</script>

<form method="post" action="/LogParser/UploadServlet" enctype="multipart/form-data" onsubmit = "return checkForm()">
    请选择一个文件:
    <input type="file" name="uploadFile"/>
    <br/><br/>
    请指定日志分隔符(默认为任何空白符):
    <input type="checkbox" name="blank" /> 空格(" ")
	<input type="checkbox" name="equal" /> 等号("=")
	<input type="checkbox" name="colon" /> 冒号(":")
	<input type="checkbox" name="enter" /> 回车("\n")
	<br/><br/>
    <input type="submit" value="process" />
</form>
 <center>
        <div id="loading" style="display:none">
    		<div class="loading-indicator">正在执行中，请稍候……</div>
		</div> 
 </center>
<% 
	List<String> events = (List<String>)session.getAttribute("events");
	if (events != null){
		%> <br/><br/> 一共整理出<%=events.size()%>个日志格式。 <% 
		for (int i = 0; i < events.size(); i++){
			String event = (String)events.get(i);
			%> </br> <p> <%=String.valueOf(i+1) %>: <%= event %></p>
				<form method="post" action="/LogParser/EventServlet">
					<input type ="hidden" name = "id" value = <%=String.valueOf(i+1) %> />
    				<input type="submit"  value="详细信息"/>
				</form>
			<%
	}
}%>

</body>
</html>