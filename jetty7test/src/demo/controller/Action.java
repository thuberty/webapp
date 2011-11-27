/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Action interface
 */

package demo.controller;

public interface Action {
	public void perform(ChatMember user, Message msg);
}
