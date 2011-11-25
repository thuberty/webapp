package demo.controller;

import demo.model.User;

public class RegisterAction  implements Action {
	public void perform(ChatMember user, Message message) {
		Message reply = new Message();

		System.out.println("register action");

		// message is of type 'register-username', the first step of the login process
		// a message of type 'login-username' was sent to the client prompting for username
		// and client is expected to return message of this type with the username.
		if (message.getHeader().equalsIgnoreCase("register-username")) {
			
			String input = message.getBody();
			System.out.println(input);

			// check if username exists
			if (availableUsername(input, user)) {
				// username doesn't exist, prompt for password
				reply.setSender("system");
				reply.setBody("Welcome " + input + "! Please enter your desired password.");
				reply.setHeader("register-password");
				
				// set username in member to remember
				user.setUsername(input);
			}
			else {
				// username was unavailable
				reply.setSender("system");
				reply.setBody("Please enter your username.");
				reply.setHeader("register-username");
				reply.addError("username already exists");
			}			
		}
		// message is of type 'register-password', client was expected to provide password
		else if (message.getHeader().equalsIgnoreCase("register-password")) {
			// todo: check password match

			if (message.getBody().length()>0){
				user.persistUser(message.getBody());
				// authenticate user by placing object into session
				user.getSession().setAttribute("user", user.getUser());

				new PartnerlessAction().perform(user, message);
				return;				
			}
			else {
				// password was empty
				reply.setSender("system");
				reply.setBody("Please enter your desired password.");
				reply.setHeader("register-password");
				reply.addError("empty password");
			}
		}

		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}

	private boolean availableUsername(String body, ChatMember user) {
		// 'system' is a reserved name
		if (body.trim().equalsIgnoreCase("system")) return false;
		if (body.trim().length() <= 0) return false;
		return user.isAvailable(body.trim());
	}
}
