package demo.controller;

public class HelpAction {
	public void perform(ChatMember user, Message message) {
		Message reply = new Message();
		
		System.out.println("help action");
		
		reply.setSender("system");
		reply.setBody("<strong>HELP:</strong> Chatbotfriend is a place to specify interests and chat with the most relevant partners.<br/>" +
				" <ul><li>After registering and/or logging in, you will be assigned a partner that best matches your established interests.</li>" +
				" <li>To send a message, enter text in the box at the bottom of your screen and press 'send'.</li>" +
				" <li>When a partner sends you a message, certain words will be highlighted. Hovering over these words reveals a slider. You can specify your level of interest by sliding this left and right. Strong left denotes a strong disinterest, while strong right denotes strong interest. Moderate positions denote moderate levels of interest in the term.</li>" +
				" <li>By specifying your interest in the topics of your discussions with your partner, we will build a better understanding of your interests and be able to match you with better partners in the future.</li>" +
				" <li>You can view your preferences as a tag cloud by clicking 'my preferences' in the upper-right.</li></ul>");
		reply.setHeader("help");		
		
		// send reply to user
		user.sendMessage(reply);
	}
}
