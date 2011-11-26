package demo.controller;

import java.util.List;

import demo.model.MyDAOException;
import demo.model.Preferable;

public class CloudAction implements Action {
	public void perform(ChatMember user, Message message) {
		Message reply = null;
		
		System.out.println("cloud action");
		
		try {
			List<Preferable> preferables = ChatMember.getPreferenceDAO().getUserPreferences(user.getUser().getUid());
			
			reply = new Message();
			
			String output = "My preferences: ";
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
		
		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}
}
