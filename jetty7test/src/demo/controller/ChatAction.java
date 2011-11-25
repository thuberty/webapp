package demo.controller;


/**
 * Main chat action when a member is chatting with a partner.
 * 
 * Observes the chat and identifies strength of related interests between
 * the partners, while forwarding the chat messages back and forth between
 * chat members.
 */
public class ChatAction implements Action {
	public void perform(ChatMember user, Message message) {
		Message echo = new Message();
		Message partnerMessage = new Message();
		
		System.out.println("chat action");
		
		// todo: preprocess messages
		
		// echo message back to user
		echo.setBody(message.getBody());
		echo.setSender(user.getUsername());
		echo.setHeader("echo");
		user.sendMessage(echo);
		
		// forward message to partner
		partnerMessage.setBody(message.getBody());
		partnerMessage.setSender(user.getUsername());
		partnerMessage.setHeader("chat");
		user.getPartner().sendMessage(partnerMessage);
	}
}
