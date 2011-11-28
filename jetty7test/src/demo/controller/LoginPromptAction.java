/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Login Prompt Action
 */

package demo.controller;

public class LoginPromptAction implements Action{

	@Override
	public void perform(ChatMember user, Message msg) {
		//prompting the user to give his name, to be followed by loginAction
		Message m = new Message();
		m.setBody("What's your username?");
		m.setHeader("login-username");
		m.setSender("system");
		user.sendMessage(m);
		
	}

}
