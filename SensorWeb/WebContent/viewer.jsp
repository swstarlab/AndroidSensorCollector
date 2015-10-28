<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<script type="text/javascript" src="./js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="./js/jquery.csv-0.71.min.js"></script>
<script type="text/javascript" src="./js/jquery.fileDownload.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hello</title>


<script type="text/javascript">

$(function(){
	
	$("#sensorDataForm").submit(function(event){
		
		var userId = $("#sensorDataForm input[name$='userId']").val();
		var from = $("#sensorDataForm input[name$='from']").val();
		var to = $("#sensorDataForm input[name$='to']").val();
		
		from = (new Date(from)).getTime();
		to = (new Date(to)).getTime();
		
		var queryStr = "{'userId':" + userId + 
		",'from':" + from + ", 'to':" + to + "}";
		
		$("#sensorDataForm input[name$='query']").val(queryStr);
	});
	
	
	$("#locationDataForm").submit(function(event){
		
		var userId = $("#locationDataForm input[name$='userId']").val();
		var from = $("#locationDataForm input[name$='from']").val();
		var to = $("#locationDataForm input[name$='to']").val();
		
		from = (new Date(from)).getTime();
		to = (new Date(to)).getTime();
		
		var queryStr = "{'userId':" + userId + 
		",'from':" + from + ", 'to':" + to + "}";
		
		$("#locationDataForm input[name$='query']").val(queryStr);
	});
});

</script>

<style type="text/css">

#container form {
	margin: 50px 50px 50px 50px;
}

</style>

</head>
<body>

<div id="container">

	<form id="sensorDataForm" action="${pageContext.request.contextPath}/api" method="post">
		User Id:<input type="text" name="userId" value="2"><br>
		Interval: <input type="text" name="from" value="2014-10-19T22:00:00.000+0900" size="30"> ~ 
		<input type="text" name="to" value="2014-10-19T22:00:00.000+0900" size="30"> <br>
		
		<input type="hidden" name="query">
		<input type="hidden" name="service" value="GetSensorData">
		
		<input type="submit" value="Get Sensor Data" onclick="">
	</form>
	
	<form id="locationDataForm" action="${pageContext.request.contextPath}/api" method="post">
		User Id:<input type="text" name="userId" value="2"><br>
		Interval: <input type="text" name="from" value="2014-10-19T22:00:00.000+0900" size="30"> ~ 
		<input type="text" name="to" value="2014-10-19T22:00:00.000+0900" size="30"> <br>
		
		<input type="hidden" name="query">
		<input type="hidden" name="service" value="GetLocationData">
		
		<input type="submit" value="Get Location Data" onclick="">
	</form>

</div>

</body>
</html>



