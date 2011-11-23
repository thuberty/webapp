// actions

function loginUsernameAction(msg) {
	$('#phrase').prop('type', 'text');
	renderMessageSystem(msg);
}

function loginPasswordAction(msg) {
	$('#phrase').prop('type', 'password');
	renderMessageSystem(msg);
}

function echoAction(msg) {
	$('#phrase').prop('type', 'text');
	renderMessageMe(msg);
}

function chatAction(msg) {
	$('#phrase').prop('type', 'text');
	renderMessagePartner(msg);
}

function waitingAction(msg) {
	$('#phrase').prop('type', 'text');
	renderMessageSystem(msg);
}