/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Preferable
 */

package demo.model;

/*
 * Bean representing a preferable - an item that a user can 'vote' on their level of interest
 */
public class Preferable implements Comparable<Preferable> {
	private String term;
	private int pid;
	private int preference;
	private User user;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getPreference() {
		return preference;
	}
	public void setPreference(int preference) {
		this.preference = preference;
	}
	@Override
	public int compareTo(Preferable o) {
		return pid-o.getPid();
	}
	
	@Override
	public boolean equals(Object p) {
		if(p instanceof Preferable) {
			return (compareTo((Preferable)p) == 0);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return pid;
	}
}
