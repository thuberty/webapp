package demo.model;

/**
 * User data bean
 * 
 * Represents an instance of an authenticated user (where a user is a registered one from the db)
 */
public class User implements Comparable<User>{
	private String username;
	private String password;
	private int uid;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	@Override
	public int compareTo(User arg0) {
		return this.getUid() - arg0.getUid();
	}
}
