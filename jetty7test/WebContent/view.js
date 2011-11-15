// view layer

function renderMessage(msg) {
	var chat = $('#chat');
	var spanFrom = document.createElement('span');
	spanFrom.className = 'from';
	spanFrom.innerHTML = msg.sender + ':&nbsp;';
	var spanText = document.createElement('span');
	spanText.className = 'text';
	spanText.innerHTML = msg.body;
	var lineBreak = document.createElement('br');
	chat.append(spanFrom);
	chat.append(spanText);
	chat.append(lineBreak);
	chat.scrollTop = chat.scrollHeight - chat.clientHeight;
}