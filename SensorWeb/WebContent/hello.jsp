<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hello</title>

<script type="text/javascript" src="./js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">

$(function(){
	
	var date = new Date();
	
	$("#ori").val()
});

function changeTime(){
	
	var ori = $("#ori").val();
	var date = new Date(ori);
	
	$("#cha").val(date.getTime());
}

</script>

</head>
<body>

<form action="${pageContext.request.contextPath}/api" method="post">

<b>service: </b> <input type="text" name="service" value="GetSensorData"> <br><br>
<b>query: </b><br>
<textarea rows="10" cols="70" name="query">
{"userId":1, "from":1413533340570, "to":1413533341396}
</textarea> <br>
<input type="submit" value="GOOOO">

</form>



<input id="ori" type="text" value="2014-10-22T22:40:24.577+0900">
<input id="cha" type="text" value="">

<button onclick="changeTime();">Change</button>

</body>
</html>
