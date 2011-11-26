package demo.controller;

import java.util.List;

import demo.model.MyDAOException;
import demo.model.Preferable;

public class CloudAction implements Action {
	public void perform(ChatMember user, Message message) {
		Message reply = null;
		
		System.out.println("cloud action");
		
		// this action depends on user being logged in, but is reasonable to request when not logged in.
		if (!user.isAuthenticated()) {
			reply = new Message();
			reply.setBody("Welcome! Please type one of: [register/login]");
			reply.setHeader("register-login");
			reply.setSender("system");
			reply.addError("Please login to view your preferences.");
		}
		else {
			try {
				List<Preferable> preferables = ChatMember.getPreferenceDAO().getUserPreferences(user.getUser().getUid());
				
				reply = new Message();
				
				String output = "<strong>My preferences:</strong> ";
				for (Preferable preferable : preferables) {
					output += "<span class=\"cloud-" + preferable.getPreference() + "\">" + preferable.getTerm() + "</span> ";
				}
				
				reply.setHeader("cloud");
				reply.setSender("system");
				reply.setBody(output);
			} catch (MyDAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}
}
