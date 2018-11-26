package db_term_phase_3;

public class User {
	private String c_id;
	private String username;

	public User() {
		this.setC_id(null);
		this.setUsername(null);
	}

	public User(String c_id, String username) {
		this.setC_id(c_id);
		this.setUsername(username);
	}

	public String getC_id() {
		return c_id;
	}

	public void setC_id(String c_id) {
		this.c_id = c_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
