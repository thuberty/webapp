// actions

function loginUsernameAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', '');
	renderMessageSystem(msg);
}

function registerUsernameAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', '');
	renderMessageSystem(msg);
}

function loginPasswordAction(msg) {
	$('#phrase').prop('type', 'password');
	$('#phrase').prop('disabled', '');
	renderMessageSystem(msg);
}

function registerPasswordAction(msg) {
	$('#phrase').prop('type', 'password');
	$('#phrase').prop('disabled', '');
	renderMessageSystem(msg);
}

function echoAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', '');
	renderMessageMe(msg);
}

function chatAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', '');
	renderMessagePartner(msg);
}

function waitingAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', 'disabled');
	renderMessageSystem(msg);
}

function registerLoginAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', '');
	renderMessageSystem(msg);
}

function partnerAction(msg) {
	$('#phrase').prop('type', 'text');
	$('#phrase').prop('disabled', '');
	renderMessageSystem(msg);
}

function preferenceAction(pid, preference) {
	var data = {sender:pid, body:preference, header:'preference'};
	room.sendRaw($.toJSON(data));
}