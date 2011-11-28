/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Register prompt action
 */

package demo.controller;

/*
 * Initiates the user registration flow
 */
public class RegisterPromptAction implements Action{

	@Override
	public void perform(ChatMember user, Message msg) {
		//prompting the user to select a name, to be followed by registeraction
		Message m = new Message();
		m.setBody("What username do you want?");
		m.setHeader("register-username");
		m.setSender("system");
		user.sendMessage(m);
		
	}

}
