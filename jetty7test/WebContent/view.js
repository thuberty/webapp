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