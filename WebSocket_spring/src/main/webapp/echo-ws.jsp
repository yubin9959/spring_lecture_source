<%@ page contentType="text/html; charset=utf-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>ìì½</title>
<script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#sendBtn').click(function() {
			sendMessage();
		});
	});

	var wsocket;
	function sendMessage() {
		wsocket = new WebSocket(
				"ws:/localhost:8181${pageContext.request.contextPath}/echo-ws");
		wsocket.onmessage = onMessage;
		wsocket.onclose = onClose;
		wsocket.onopen = function() {
			wsocket.send($("#message").val());
		};
	}
	function onMessage(evt) {
		var data = evt.data;
		alert("서버에서 데이터 받음: " + data);
		/* wsocket.close(); */
	}
	function onClose(evt) {
		alert("연결 끊김");
	}

	window.onbeforeunload = function() {

		wsocket.close();

	}
</script>
</head>
<body>
	<input type="text" id="message">
	<input type="button" id="sendBtn" value="전송">
</body>
</html>