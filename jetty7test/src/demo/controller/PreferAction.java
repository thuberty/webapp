package demo.controller;

import demo.model.Preferable;

public class PreferAction implements Action {
	public void perform(ChatMember user, Message message) {
		int pid, preference;
		
		try {
			pid = Integer.parseInt(message.getBody());
			preference = Integer.parseInt(message.getBody());
			
			Preferable preferable = new Preferable();
			preferable.setPid(pid);
			preferable.setPreference(preference);
			preferable.setUser(user.getUser());
			
			ChatMember.getPreferenceDAO().setPreference(preferable);
		} catch(Exception e) {
			// do nothing and drop if malformed
		}
		
		// no reply for this message
	}
}
