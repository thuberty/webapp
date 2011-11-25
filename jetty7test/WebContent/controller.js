
if (!window.WebSocket) {
	window.WebSocket=window.MozWebSocket;
	if (!window.WebSocket)
		alert("WebSocket not supported by this browser");
}

function $F() {
	return $(arguments[0]).val();
}

function getKeyCode(ev) {
	if (window.event)
		return window.event.keyCode;
	return ev.keyCode;
}

var currentMessageType;

// event-driven controller
var room = {
	join : function(name) {
		this._username = name;
		var location = document.location.toString()
		        .replace('http://', 'ws://')
				.replace('https://', 'wss://')
				+ "chat";
		this._ws = new WebSocket(location, "chat");
		this._ws.onopen = this.onopen;
		this._ws.onmessage = this.onmessage;
		this._ws.onclose = this.onclose;
	},

	onopen : function() {
		$('#join').addClass('hidden');
		$('#joined').removeClass('hidden');
		$('#phrase').focus();
	},

	send : function(user, message) {
		user = user.replace(':', '_');
		
		var data = {sender:user, body:message, header:currentMessageType};
		
		if (this._ws)
			this._ws.send($.toJSON(data));
	},

	chat : function(text) {
		if (text != null && text.length > 0)
			room.send(room._username, text);
	},

	onmessage : function(m) {
		if (m.data) {
			// get msg object from json data
			var msg = jQuery.parseJSON(m.data);
			
			// track current transaction type
			currentMessageType = msg.header;
			
			renderErrorMessages(msg.errors);
			
			// dispatch to appropriate action to act on incoming message
			switch (msg.header) {
			case 'register-login':
				registerLoginAction(msg);
			    break;
			case 'register-username':
				registerUsernameAction(msg);
				break;
			case 'register-password': 
				registerPasswordAction(msg); 
				break;
			case 'login-username': 
				loginUsernameAction(msg); 
				break;
			case 'login-password': 
				loginPasswordAction(msg); 
				break;
			case 'echo': 
				echoAction(msg); 
				break;
			case 'chat': 
				chatAction(msg);
				break;
			case 'waiting': 
				waitingAction(msg);
				break;
			case 'partner':
				partnerAction(msg);
				break;
			default: return; // drop message if unknown type
			}
		}
	},

	onclose : function(m) {
		this._ws = null;
		$('#join').className = '';
		$('#joined').className = 'hidden';
		$('#username').focus();
		$('#chat').innerHTML = '';
	}
};

// init chat infrastructure
$(document).ready(function() {
	$('#phrase').attr('autocomplete','OFF');
	$('#phrase').bind('keyup', function(ev) {   var keyc=getKeyCode(ev); if (keyc==13 || keyc==10) { room.chat($F('#phrase')); $('#phrase').val(""); return false; } return true; });
	$('#sendB').click(function(event) { room.chat($F('#phrase')); $('#phrase').val(""); return false; });
	room.join('name');
	$('#chatarea').height($(window).height() - 240);
	$(window).resize(function() {
		  $('#chatarea').height($(window).height() - 240);
	});
});
      
