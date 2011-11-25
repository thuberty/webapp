package demo.controller;

import demo.model.User;
import demo.model.UserDAO;

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
			//check if username exists
			String input = message.getBody();
			System.out.println(input);
			if (validUsername(input, user)) {
				// username exists, prompt for password
				reply.setSender("system");
				reply.setBody("Welcome " + input + "! Please enter your password.");
				reply.setHeader("login-password");

				// set username in member to remember (todo: consider best practice, username should be confirmed below with password)
				user.setUsername(input);

				// username doesn't exist, prompt for registration
			}
			else {
				// username was invalid
				reply.setSender("system");
				reply.setBody("Please enter your username.");
				reply.setHeader("login-username");
				reply.addError("invalid username");
			}			
		}
		// message is of type 'login-password', client was expected to provide password
		else if (message.getHeader().equalsIgnoreCase("login-password")) {
			// check password match
			if (user.passwordMatches(message.getBody())) {
				
				// authenticate user by placing object into session
				user.getSession().setAttribute("user", user.getUser());

				new PartnerlessAction().perform(user, message);
				return;
				
			} else {
				// password was invalid
				reply.setSender("system");
				reply.setBody("Please begin again with your username.");
				reply.setHeader("login-username");
				reply.addError("invalid password");
			}
		}

		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}

	private boolean validUsername(String body, ChatMember user) {
		// 'system' is a reserved name
		if (body.equalsIgnoreCase("system")) return false;
		if (body.length() <= 0) return false;
		return !user.isAvailable(body);

	}
}
