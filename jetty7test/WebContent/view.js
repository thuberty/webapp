// Fall 2011 - 15-437 Tyler Huberty, Jack Phelan
// view layer

function renderMessageMe(msg) {
	var output = '<div class="item incoming">';
	output += '<span class="start-quote me">&#8220;</span>' + msg.body + '<span class="end-quote me">&#8221;</span>';
	output += '<span class="from">-' + msg.sender + '</span>';
	output += '<br/></div>';
	
	var chat = $('#chat');
	chat.append(output);
	chat.children('.incoming').fadeIn();
	chat.children('.incoming').removeClass('incoming');
	chat.children('.incoming').hide();
	$("#chatarea").prop({ scrollTop: $("#chatarea").prop("scrollHeight") });
}

function renderMessagePartner(msg) {
	if (msg.sender == 'system') {
		renderMessageSystem(msg);
		return;
	}
	var output = '<div class="item incoming">';
	output += '<span class="start-quote partner">&#8220;</span>' + msg.body + '<span class="end-quote partner">&#8221;</span>';
	output += '<span class="from">-' + msg.sender + '</span>';
	output += '<br/></div>';
	
	var chat = $('#chat');
	chat.append(output);
	chat.children('.incoming').fadeIn();
	chat.children('.incoming').removeClass('incoming');
	chat.children('.incoming').hide();
	$("#chatarea").prop({ scrollTop: $("#chatarea").prop("scrollHeight") });
}

function renderMessageSystem(msg) {
	var output = '<div class="item incoming">';
	output += '<span class="start-quote system">&#8220;</span>' + msg.body + '<span class="end-quote system">&#8221;</span>';
	output += '<span class="from">-chatbotfriend</span>';
	output += '<br/></div>';
	
	var chat = $('#chat');
	chat.append(output);
	chat.children('.incoming').fadeIn();
	chat.children('.incoming').removeClass('incoming');
	chat.children('.incoming').hide();
	$("#chatarea").prop({ scrollTop: $("#chatarea").prop("scrollHeight") });
}

function renderErrorMessages(errors) {
	if (errors.length < 1) return;
	
	var errorMsg = '<span class="error">Error: ';
	var i;
	for(i=0;i<errors.length;i++) {
		if (i != 0) errorMsg += ', ';
		errorMsg += errors[i];
	}
	errorMsg += '</span>';
	
	var output = '<div class="item incoming">';
	output += '<span class="start-quote system">&#8220;</span>' + errorMsg + '<span class="end-quote system">&#8221;</span>';
	output += '<span class="from">-chatbotfriend</span>';
	output += '<br/></div>';
	
	var chat = $('#chat');
	chat.append(output);
	chat.children('.incoming').fadeIn();
	chat.children('.incoming').removeClass('incoming');
	chat.children('.incoming').hide();
	$("#chatarea").prop({ scrollTop: $("#chatarea").prop("scrollHeight") });
}

function renderMessageHelp(msg) {
	var output = '<div class="item incoming">';
	output += '<div class="help">' + msg.body + '</div>';
	output += '<br/></div>';
	
	var chat = $('#chat');
	chat.append(output);
	chat.children('.incoming').fadeIn();
	chat.children('.incoming').removeClass('incoming');
	chat.children('.incoming').hide();
	$("#chatarea").prop({ scrollTop: $("#chatarea").prop("scrollHeight") });
}