package db_term_phase_3;

import java.sql.*;

public class Main {
	public static final String URL = "jdbc:mysql://localhost:3306/shopx?serverTimezone=Asia/Seoul";
	public static final String USER_ID = "admin";
	public static final String USER_PASSWD = "admin";

	public static void main(String[] args) {
		Connection conn = null; // Connection object
		Statement stmt = null; // Statement object
		String sql = ""; // an SQL statement
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Success!");
		} catch (ClassNotFoundException e) {
			System.err.println("error = " + e.getMessage());
			System.exit(1);
		}

		try {
			conn = DriverManager.getConnection(URL, USER_ID, USER_PASSWD);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
		} catch (SQLException ex) {
			System.err.println("Cannot get a connection: " + ex.getMessage());
			System.exit(1);
		}

		try {
			sql = "SELECT * from S_CATEGORY";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String sc_id = rs.getString(1);
				String name = rs.getString(2);
				String mc_id = rs.getString(3);
				System.out.println("SC_ID = " + sc_id + ", Name = " + name + ", MC_ID = " + mc_id);
			}
			rs.close();
			conn.commit();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}

		try {
			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			System.err.println("Cannot get a connection: " + ex.getMessage());
			System.exit(1);
		}
	}
}
