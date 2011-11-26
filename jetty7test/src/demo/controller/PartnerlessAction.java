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
		
		// password correct, find partner
		boolean partnerFound = user.findPartner();

		// partner not found yet, inform user
		if (!partnerFound) {
			reply.setSender("system");
			reply.setBody("Unfortunately, there are no partners available to chat with you. Please wait to be matched.");
			reply.setHeader("waiting");
		}
		else {
			// inform user of matched partner
			reply.setSender("system");
			reply.setBody(user.getPartner().getUsername() + " is your new chat partner!");
			reply.setHeader("partner");

			// inform partner of match
			Message partnerMessage = new Message();
			partnerMessage.setSender("system");
			partnerMessage.setBody(user.getUsername() + " is your new chat partner!");
			partnerMessage.setHeader("partner");
			user.getPartner().sendMessage(partnerMessage);
		}
		
		System.out.println("partnerless action");
		
		// send reply to user
		if (reply != null) {
			user.sendMessage(reply);
		}
	}
}
