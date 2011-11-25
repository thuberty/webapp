package demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a message java bean that represents a message to or from a client socket.
 * 
 * Since message objects themselves cannot be transmitted over the socket channel,
 * before sending, this object is serialized into JSON.
 * 
 * Upon receiving of JSON from a client across the socket channel, the JSON should
 * be parsed and this bean built for processing on the server side.
 * 
 * A message is made up of a sender, body, header, and list of errors:
 * -The sender identifies who is sending the message and when it is a message from the
 * server and not another client, it should be "system".
 * -The body contains the bulk of the information.
 * -The header provides the recipient with the type of message that this is (i.e. login).
 * -The errors list contains errors in the previous request that the end point should consider. 
 */
public class Message {
	private String sender;
	private String body;
	private String header;
	private ArrayList<String> errors = new ArrayList<String>();
	static Message defaultMessage = null;
	
	
	static Message getDefaultMessage() {
		if (defaultMessage == null) {
			defaultMessage = new Message();
			defaultMessage.setBody("");
			defaultMessage.setHeader("register-login");
		}
		return defaultMessage;
	}
	public String[] getErrors() {
		String[] b = {};
		return errors.toArray(b);
	}

	public void setErrors(ArrayList<String> errors) {
		this.errors = errors;
	}
	
	public void addError(String error) {
		errors.add(error);
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
}