// Fall 2011 - 15-437 Tyler Huberty, Jack Phelan
// js controller

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
		
		if (this._ws) this._ws.send($.toJSON(data));
	},
	
	sendRaw : function(data) {
		if (this._ws) this._ws.send(data);
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
			if (msg.header != 'cloud') {
				currentMessageType = msg.header;
			}
			
			renderErrorMessages(msg.errors);
			
			// dispatch to appropriate action to act on incoming message
			switch (msg.header) {
			case 'register-login':
				loggedIn = false;
				hasPartner = false;
				registerLoginAction(msg);
			    break;
			case 'register-username':
				loggedIn = false;
				hasPartner = false;
				registerUsernameAction(msg);
				break;
			case 'register-password': 
				loggedIn = false;
				hasPartner = false;
				registerPasswordAction(msg); 
				break;
			case 'login-username': 
				loggedIn = false;
				hasPartner = false;
				loginUsernameAction(msg); 
				break;
			case 'login-password': 
				loggedIn = false;
				hasPartner = false;
				loginPasswordAction(msg); 
				break;
			case 'echo':
				loggedIn = true;
				hasPartner = true;
				echoAction(msg); 
				break;
			case 'chat':
				loggedIn = true;
				hasPartner = true;
				chatAction(msg);
				break;
			case 'waiting':
				loggedIn = true;
				hasPartner = false;
				waitingAction(msg);
				break;
			case 'partner':
				loggedIn = true;
				hasPartner = true;
				partnerAction(msg);
				break;
			case 'cloud':
				cloudReceiveAction(msg);
				break;
			default: return; // drop message if unknown type
			}
			
			if (loggedIn) {
				$("#logout").show();
			}
			else {
				$("#logout").hide();
			}
			
			if (hasPartner) {
				$("#newPartner").show();
			}
			else {
				$("#newPartner").hide();
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
	$("#get-preferences").click(function(event) { cloudSendAction(); return false;});
	$("#get-help").click(function(event) { $("#help").dialog('open'); return false;});
	$("#newPartner").click(function(event) { location.reload(); return false;});
	$("#logout").click(function(event) { logoutAction(); location.reload(); return false;});
	$("#help").dialog({title:'HELP', height:400, minWidth:900, autoOpen: false, resizable:false});
});
      
