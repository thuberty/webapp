package demo.controller;

import java.io.IOException;
import net.sf.json.JSONObject;
import org.eclipse.jetty.websocket.WebSocket;


/**
 * This class represents an open web socket. Each instance of this class belongs
 * to a chat member. This class contains the methods invoked by the server for
 * changes in state to the web socket. This class takes first action on these 
 * changes by dispatching to controller actions and altering the state of the
 * appropriate chat member. This class is perhaps the highest level controller
 * since most communication is done from the client to the server via a web socket.
 * 
 * Objects of this class hold the state of the socket, such as the connection and
 * the chat member it belongs to.
 */
public class ChatWebSocket implements WebSocket.OnTextMessage
{
	volatile private Connection _connection;

	private ChatMember member;

	public ChatMember getMember() {
		return member;
	}

	public void setMember(ChatMember member) {
		this.member = member;
	}

	/** 
	 * Callback for when a WebSocket connection is opened.
	 * Stores the connection for future use and adds a socket to the chat member.
	 */
	public void onOpen(Connection connection)
	{
		_connection=connection;
		member.addSocket(this);

		// check if user authenticated
		onMessage(null);
	}


	/** 
	 * Callback for when a WebSocket connection is closed.
	 * 
	 * Inform chat member that the socket should be removed.
	 * If this is the only open socket, that chat member will subsequently destroy itself.
	 */
	public void onClose(int closeCode, String message)
	{
		member.removeSocket(this);
	}

	/** 
	 * Callback for when a WebSocket message is received.
	 * 
	 * Dispatch handling of the requests to the appropriate action.
	 * This is a central controller hub.
	 */
	public void onMessage(String data)
	{

		Message message;
		if (data != null) message = (Message) JSONObject.toBean(new JSONObject().fromObject(data), Message.class);
		else message = Message.getDefaultMessage();     	
		//----------------------------------------------
		// Member not authenticated
		//----------------------------------------------
		if (!member.isAuthenticated()) {
			//user is responding to register or login prompt
			if(message.getHeader().equals("register-login")){
				if (message.getBody().trim().equalsIgnoreCase("login"))
					new LoginPromptAction().perform(member, message);
				else if (message.getBody().trim().equalsIgnoreCase("register"))
					new RegisterPromptAction().perform(member, message);
				else {
					//send them the register or login prompt
					Message m = new Message();
					m.setBody("Welcome! Please type one of: [register/login]");
					m.setHeader("register-login");
					m.setSender("system");
					send(m);
				}
			} else if (message.getHeader().startsWith("login")) {
				new LoginAction().perform(member, message);
			} else if (message.getHeader().startsWith("register")) {			
				new RegisterAction().perform(member, message);
			}
		}
		else {
			//----------------------------------------------
			// Member is waiting for a partner
			//----------------------------------------------
			if (member.getPartner() == null) {
				new PartnerlessAction().perform(member, message);
			}
			//----------------------------------------------
			// Member has a partner
			//----------------------------------------------
			else {
				new ChatAction().perform(member, message);
			}
		}


		/*
		 * 	    	// send reply to all sockets

	        for (String sessionId : user.members.keySet()) {
	        	for (ChatWebSocket member : user.members.get(sessionId).getSockets()) {
		            member.send(reply);
	        	}
	        }
		 */

		/*     	
    	// process member's message before sending
    	data = user.processMessageFrom(data);
    	// check if partner still exists
    	if (_members.contains(partner)) {
    		// process message to partner before sending
    		data = partner.getUser().processMessageTo(data);
    		try {
				partner._connection.sendMessage(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		// send error to member
    	}
		 */
	}

	/**
	 * To send a message to client via this socket, this method should be used.
	 * 
	 * Converts message to JSON before sending.
	 */
	public void send(Message message) {
		JSONObject json = JSONObject.fromObject(message);

		try {
			_connection.sendMessage(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
