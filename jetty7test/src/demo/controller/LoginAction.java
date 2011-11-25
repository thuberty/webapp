package demo.controller;

import demo.model.User;

/**
 * Login action class - replies to user messages when user is not authenticated
 */
public class LoginAction implements Action {
	public void perform(ChatMember user, Message message) {
		Message reply = new Message();
		
		System.out.println("login action");
		
		// message is of type 'login-username', the first step of the login process
		// a message of type 'login-username' was sent to the client prompting for username
		// and client is expected to return message of this type with the username.
		if (message.getHeader().equalsIgnoreCase("login-username")) {
			if (validUsername(message.getBody())) {
				// todo: check if username exists
				
				// username exists, prompt for password
				reply.setSender("system");
				reply.setBody("Welcome " + message.getBody() + "! Please enter your password.");
				reply.setHeader("login-password");
				
				// set username in member to remember (todo: consider best practice, username should be confirmed below with password)
				user.setName(message.getBody());
				
				// username doesn't exist, prompt for registration
			}
			else {
				// username was invalid
				reply.setSender("system");
				message.setBody("Please enter your username.");
				reply.setHeader("login-username");
				reply.addError("invalid username");
			}			
		}
		// message is of type 'login-password', client was expected to provide password
		else if (message.getHeader().equalsIgnoreCase("login-password")) {
			// todo: check password match
			
			User newUser = new User();
			newUser.setPassword(message.getBody());
			newUser.setUsername(user.getName());
			// todo: properly look up user in db
			
			// authenticate user by placing object into session
			user.getSession().setAttribute("user", newUser);
			
			// password correct, find partner
			boolean partnerFound = user.findPartner();
			
			// partner not found yet, inform user
			if (!partnerFound) {
				reply.setSender("system");
				reply.setBody("awaiting partner");
				reply.setHeader("waiting");
			}
			else {
				// inform user of matched partner
				reply.setSender("system");
				reply.setBody(user.getPartner().getName() + " is your new chat partner!");
				reply.setHeader("partner");
				
				// inform partner of match
				Message partnerMessage = new Message();
				partnerMessage.setSender("system");
				partnerMessage.setBody(user.getName() + " is your new chat partner!");
				partnerMessage.setHeader("partner");
				user.getPartner().sendMessage(partnerMessage);
			}
		}
		
		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}

	private boolean validUsername(String body) {
		// 'system' is a reserved name
		if (body.trim().equalsIgnoreCase("system")) return false;
		
		// username can't be empty
		if (body.trim().length() > 0) return true;
		return false;
	}
}
