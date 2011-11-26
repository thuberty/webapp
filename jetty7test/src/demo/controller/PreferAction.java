package demo.controller;

import demo.model.Preferable;

public class PreferAction implements Action {
	public void perform(ChatMember user, Message message) {
		int pid, preference;
		
		System.out.println("prefer action");
		
		
			pid = Integer.parseInt(message.getSender().replaceFirst("#", ""));
			preference = Integer.parseInt(message.getBody());
			Preferable preferable = new Preferable();
			preferable.setPid(pid);
			preferable.setPreference(preference);
			preferable.setUser(user.getUser());
			System.out.println("user:"+user.getUser().getUid()+" pid:"+pid);
		try {
			ChatMember.getPreferenceDAO().setPreference(preferable);
			System.out.println("success: preference"+preferable.getPreference());
		} catch(Exception e) {
			System.out.println("prefer failed: "+e.getMessage());
			// do nothing and drop if malformed
		}
		
		// no reply for this message
	}
}
