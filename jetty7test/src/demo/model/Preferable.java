package demo.model;

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
}
