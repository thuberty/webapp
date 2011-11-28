/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Chat Member
 */

package demo.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.http.HttpSession;

import org.mybeans.dao.DAOException;

import demo.model.Model;
import demo.model.MyDAOException;
import demo.model.Preferable;
import demo.model.PreferenceDAO;
import demo.model.User;
import demo.model.UserDAO;

/**
 * This class represents a chat member which is an instance of a user identified by
 * a unique session id. Since the same session can have multiple web sockets open,
 * a chat member can potentially have multiple open sockets (i.e. browser tabs).
 * The purpose of this object is to hold the state of such chat member and provide
 * helper methods to modify the state of the chat member.
 * 
 * When the server sends a message to a chat member, it will be routed to all sockets
 * that that member has active.
 * 
 * A chat member is authenticated when its session contains a user object which represents
 * a unique user as identified by database and user id.
 */
public class ChatMember {
	private HttpSession session;
	private User user;
	private ChatMember partner;
	private Set<ChatWebSocket> sockets;
	private static Map<String,ChatMember> members;
	private static UserDAO userDAO = null;
	private static PreferenceDAO preferenceDAO = null;
	private Set<Integer> preferables;

	public ChatMember(HttpSession session) {
		this.session = session;
		sockets = new CopyOnWriteArraySet<ChatWebSocket>();
		preferables = new CopyOnWriteArraySet<Integer>();

		// session may contain data, so load it here
		if (isAuthenticated()) {
			user = ((User)session.getAttribute("user"));
		}
		else user = new User();
	}


	public static void setMembers(Map<String,ChatMember> m) {
		members = m;
	}

	public static void setDAO(Model model) {
		userDAO = model.getUserDAO();
		preferenceDAO = model.getPreferenceDAO();
	}

	public static PreferenceDAO getPreferenceDAO() {
		return preferenceDAO;
	}

	public String processMessageTo(String data) {
		return data;
	}

	public String processMessageFrom(String data) {
		return data;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getUsername() {
		return user.getUsername();
	}

	public void setUsername(String name) {
		user.setUsername(name);
	}

	public void updateFieldsFromDB() throws DAOException {
		user = userDAO.lookup(user.getUsername());
	}

	/**
	 * Helper method for use during login to persist user
	 */
	public boolean persistUser(String password) {
		synchronized(userDAO) {
			user.setPassword(password);
			try { 
				userDAO.create(user);
				return true;
			}
			catch(DAOException e) {return false;}
		}
	}

	/**
	 * Helper method for use during login to check availability
	 */
	public boolean isAvailable(String name) {
		synchronized(userDAO) {
			try {
				return (userDAO.lookup(name) == null);
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				return false;
			}
		}
	}

	public boolean passwordMatches(String password) {
		synchronized(userDAO) {
			return userDAO.verifyPassword(user.getUsername(), password);
		}
	}
	public ChatMember getPartner() {
		return partner;
	}

	public void setPartner(ChatMember partner) {
		// save partner in session for extra persistence
		session.setAttribute("partner", partner);
		this.partner = partner;
	}

	public Set<ChatWebSocket> getSockets() {
		return sockets;
	}

	public void setSockets(Set<ChatWebSocket> sockets) {
		this.sockets = sockets;
	}

	/**
	 * Add an open socket to this member, prompting to begin authentication
	 * if necessary.
	 */
	public void addSocket(ChatWebSocket socket) {
		sockets.add(socket);
	}

	/**
	 * Removes a socket open by this chat member.
	 * 
	 * If it is the last socket belonging to this member, the member itself is
	 * removed from the global members list.
	 */
	public void removeSocket(ChatWebSocket socket) {
		sockets.remove(socket);

		// if no more sockets exist for this member, this member no longer needs to exist
		if (sockets.isEmpty()) {
			synchronized(members) {
				members.remove(session.getId());

				// inform partner of non-existence of this user
				if (partner != null) {
					partner.disconnectPartner();
					setPartner(null);
				}
			}
		}
	}

	public void disconnectPartner() {
		setPartner(null);
		Message message = new Message();
		message.setHeader("partner");
		message.setBody("Your partner disconnected. Please wait while we try to find another.");
		message.setSender("system");
		sendMessage(message);
		new PartnerlessAction().perform(this, null);
	}

	/**
	 * Returns whether this chat member is authenticated
	 */
	public boolean isAuthenticated() {
		return (session.getAttribute("user") != null);
	}
	
	/**
	 * Logs out a user
	 */
	public void logout() {
		Message message = new Message();
		message.setBody("You logged out in another window. Please refresh your browser.");
		message.setHeader("waiting");
		message.setSender("system");
		sendMessage(message);
		session.setAttribute("user", null);
		if (partner != null) {
			partner.disconnectPartner();
			setPartner(null);
		}
		user = new User();
		members.remove(session.getId());
	}

	/**
	 * Sends message to all sockets belonging to this chat member
	 */
	public void sendMessage(Message message) {		
		for (ChatWebSocket socket : sockets) {
			System.out.println(socket + " " + message);
			socket.send(message);
		}
	}

	/**
	 * Searches member set for the best match that currently also does not have a partner
	 * 
	 * Returns bonding integer or null if no partner found
	 */
	public Integer findPartner() {
		// thread safe - multiple objects grabbing same partner shouldn't be allowed
		synchronized(members) {
			int available = 0;
			ChatMember partner = null;
			int maxBonding = Integer.MIN_VALUE;
			
			for(String s : members.keySet()) {
				System.out.println(s + " " + members.get(s).getUsername());
			}
			
			for (ChatMember member : members.values()) {
				if (member.equals(this)) continue; // exclude this member
				if (member.getUser().equals(this.getUser())) continue; // exclude other instances of member
				if (member.getPartner() != null) continue; // exclude members already paired
				if (!member.isAuthenticated()) continue; // exclude non-authenticated members
				available++;
				int bonding;
				try {
					System.out.println("comparing " +user.getUsername() +" "+member.getUsername());
					bonding = comparePrefs(member);
				} catch (MyDAOException e) {
					// TODO Auto-generated catch block
					bonding = 0;
					e.printStackTrace();
				}
				if (bonding > maxBonding) {
					partner = member;
					maxBonding = bonding;
				}
			}
			// no chat members are available
			if (partner == null || available < 2) return null;

			//a partner was found
			this.setPartner(partner);
			partner.setPartner(this);
			return maxBonding;
		}
	}

	/**
	 * returns a representation of the difference between two chatmember's preferences,
	 * more positive meaning more alike, more negative meaning more unlike
	 * @param him, chatmember to compare preferences with
	 * @return sum of (difference^2) over all preferences
	 * @throws MyDAOException
	 */
	private int comparePrefs(ChatMember him) throws MyDAOException {
		List<Preferable> ourList = preferenceDAO.getUserPreferences(user.getUid());
		List<Preferable> hisList = preferenceDAO.getUserPreferences(him.getUser().getUid());
		System.out.println("oursize:" +ourList.size()+" hissize:"+hisList.size());
		ourList = intersection(ourList, hisList);
		System.out.println("overlapping preferences:"+ourList.size());
		int bond = 0;

		for (Preferable pref : ourList) {
			Preferable hisPref = hisList.get(hisList.indexOf((pref)));
			System.out.println("pref:"+pref.getPid()+": "+pref.getPreference()+"/"+hisPref.getPreference());
			bond -= Math.pow((pref.getPreference() - hisPref.getPreference()),2);
		}
		System.out.println(user.getUsername() +" / " + him.getUsername() + "=" + bond);
		return bond;
	}

	/**
	 * Intersects two Preference lists
	 * @param a
	 * @param b
	 * @return the intersection of a and b
	 */
	private static List<Preferable> intersection(List<Preferable> a, List<Preferable> b) {
		ArrayList<Preferable> result = new ArrayList<Preferable>();
		HashSet<Preferable> compare = new HashSet<Preferable>(b);

		for (Preferable p : a) {
			if (compare.contains(p)) result.add(p);
		}
		return result;

	}


	public User getUser() {
		return user;
	}

	public Set<Integer> getPreferables() {
		return preferables;
	}


	/**
	 * return the list of overlapping topics between two users
	 * @return topics that our user and our partner's user like equally
	 */
	public String getTopics() {
		List<Preferable> preferences;
		try {
			preferences = intersection(preferenceDAO.getUserPreferences(user.getUid()),
					preferenceDAO.getUserPreferences(partner.getUser().getUid()));
		} catch (MyDAOException e) {
			// TODO Auto-generated catch block
			preferences = new ArrayList<Preferable>();
		}
		String topics = "";
		int i = 0;
		for (Preferable p : preferences) {
			Preferable term = null;
			try {
				p.setUser(user);
				//our preference for this topic
				int ourpref = preferenceDAO.lookupUserPreference(p);
				p.setUser(partner.getUser());
				//our partner's preference for this topic
				int hispref = preferenceDAO.lookupUserPreference(p);
				if (ourpref == hispref && hispref > 0)
					term = preferenceDAO.lookupPreferable(p);
			} catch (MyDAOException e) {
				// TODO Auto-generated catch block
				term = null;
			}
			if (term != null && term.getTerm() != null)
				if (i == 0)
					topics = topics.concat(" " + term.getTerm());
				else
					topics = topics.concat(", or " + term.getTerm());
			i++;
		}
		return topics;
	}
}
