/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Prefer action
 */

package demo.controller;

import demo.model.Preferable;

/**
 * Handles preference requests - updates to a user's preferences
 * invoked when a user 'votes' on an interest.
 */
public class PreferAction implements Action {
	public void perform(ChatMember user, Message message) {
		int pid, preference;
		
		System.out.println("prefer action");		
		
			pid = Integer.parseInt(message.getSender().replaceFirst("#", ""));
			
			if (!user.getPreferables().contains(pid)) {
				System.out.println("forged pid");
				// forged pid, ignore request
				return;
			}
			
			preference = Integer.parseInt(message.getBody());
			
			if (preference < -2 || preference > 2) {
				// forged preference, ignore request
				return;
			}
			
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
