package demo.controller;

public class HelpAction {
	public void perform(ChatMember user, Message message) {
		Message reply = new Message();
		
		System.out.println("help action");
		
		reply.setSender("system");
		reply.setBody("Chatbotfriend is a place to specify interests and chat with the most relevant partners." +
				" After registering and/or logging in, you will be assigned a partner that best matches your established interests." +
				" To send a message, enter text in the box at the bottom of your screen and press 'send'." +
				" When a partner sends you a message, certain words will be highlighted. Hovering over these words reveals a slider. You can specify your level of interest by sliding this left and right. Strong left denotes a strong disinterest, while strong right denotes strong interest. Moderate positions denote moderate levels of interest in the term." +
				" By specifying your interest in the topics of your discussions with your partner, we will build a better understanding of your interests and be able to match you with better partners in the future.");
		reply.setHeader("help");		
		
		// send reply to user
		user.sendMessage(reply);
	}
}
