package demo.controller;


/**
 * Action taken when a member has no chat partner.
 * 
 * Tries to find a chat partner, preferring those that are a good match for the member.
 * If a partner is not available, informs member of such.
 */
public class PartnerlessAction implements Action {
	public void perform(ChatMember user, Message message) {
		Message reply = new Message();
		
		reply.setSender("bob");
		reply.setBody("hello");
		reply.setHeader("waiting");
		
		System.out.println("partnerless action");
		
		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}
}
