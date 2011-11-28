/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Logout Action
 */

package demo.controller;

/**
 * Responds to a user's request to be logged out
 */
public class LogoutAction implements Action {
	public void perform(ChatMember user, Message message) {
		
		System.out.println("logout");
		
		user.logout();			
		
		// no reply
	}
}
