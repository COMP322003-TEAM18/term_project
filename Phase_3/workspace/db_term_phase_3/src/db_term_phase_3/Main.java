package db_term_phase_3;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
	public static final String URL = "jdbc:mysql://localhost:3306/shopx?serverTimezone=Asia/Seoul";
	public static final String USER_ID = "admin";
	public static final String USER_PASSWD = "admin";

	private static Connection conn = null; // Connection object
	private static Statement stmt = null; // Statement object
	private static String sql = ""; // an SQL statement

	private static User currentUser = null;

	private static Logger LOG;
	private static Scanner sc;
	private static String hr = "----------------------------------------------------------------------";

	public static void rootScreen() {
		sc = new Scanner(System.in);

		System.out.println();
		System.out.println("쇼핑몰 X 어플리케이션");
		System.out.println(hr);
		System.out.println("1. 로그인");
		System.out.println("2. 회원가입");
		System.out.println("0. 종료");

		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("1")) {
				loginScreen();
			} else if (input.equals("2")) {
				signupScreen();
			} else if (input.equals("0")) {
				System.out.println("어플리케이션을 종료합니다.");
				sc.close();
				System.exit(0);
			} else {
				System.out.println("잘못된 입력입니다.");
				continue;
			}

			System.out.println();
			System.out.println("쇼핑몰 X 어플리케이션");
			System.out.println(hr);
			System.out.println("1. 로그인");
			System.out.println("2. 회원가입");
			System.out.println("0. 종료");
		}
	}

	public static void loginScreen() {
		System.out.println();
		System.out.println("로그인");
		System.out.println(hr);
		System.out.println("아이디를 입력해 주세요.");
		System.out.print("> ");
		String username = sc.nextLine().trim();
		System.out.println("비밀번호를 입력해 주세요.");
		System.out.print("> ");
		String password = sc.nextLine().trim();

		try {
			sql = "SELECT C_id, Password FROM CUSTOMER WHERE Username = '" + username + "'";
			ResultSet rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("존재하지 않는 회원입니다.");
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				if (rs.next()) {
					String c_id = rs.getString(1);
					String qPassword = rs.getString(2);
					if (password.equals(qPassword)) {
						currentUser = new User(c_id, username);

						if (c_id.charAt(0) == 'A') { // 관리자 계정
							adminMainScreen();
						} else { // 일반 계정
							customerMainScreen();
						}
					} else { // 비밀번호가 틀림
						System.out.println("잘못된 비밀번호입니다.");
					}
				}
				rs.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}

	}

	public static void signupScreen() {

	}

	public static void adminMainScreen() {
		System.out.println();
		System.out.println("관리자 메뉴");
		System.out.println(hr);
		System.out.println("1. 물품 주문");
		System.out.println("2. 재고 부족 상품 조회");
		System.out.println("3. 매출 확인");
		System.out.println("4. 배송 업체 별 배송 횟수 조회");
		System.out.println("0. 로그아웃");

		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("1")) {

			} else if (input.equals("2")) {

			} else if (input.equals("3")) {

			} else if (input.equals("4")) {

			} else if (input.equals("0")) {
				System.out.println("성공적으로 로그아웃하였습니다.");
				break;
			} else {
				System.out.println("잘못된 입력입니다.");
				continue;
			}

			System.out.println();
			System.out.println("관리자 메뉴");
			System.out.println(hr);
			System.out.println("1. 물품 주문");
			System.out.println("2. 재고 부족 상품 조회");
			System.out.println("3. 매출 확인");
			System.out.println("4. 배송 업체 별 배송 횟수 조회");
			System.out.println("0. 로그아웃");
		}
	}

	private static void customerMainScreen() {
		System.out.println();
		System.out.println("메인 메뉴");
		System.out.println(hr);
		System.out.println("1. 계정 관리");
		System.out.println("2. 상품 조회");
		System.out.println("3. 장바구니");
		System.out.println("4. 구매내역 조회");
		System.out.println("0. 로그아웃");

		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("1")) {

			} else if (input.equals("2")) {

			} else if (input.equals("3")) {

			} else if (input.equals("4")) {

			} else if (input.equals("0")) {
				System.out.println("성공적으로 로그아웃하였습니다.");
				break;
			} else {
				System.out.println("잘못된 입력입니다.");
				continue;
			}
			
			System.out.println();
			System.out.println("메인 메뉴");
			System.out.println(hr);
			System.out.println("1. 계정 관리");
			System.out.println("2. 상품 조회");
			System.out.println("3. 장바구니");
			System.out.println("4. 구매내역 조회");
			System.out.println("0. 로그아웃");
		}
	}

	public static void main(String[] args) {
		LOG = Logger.getGlobal();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			LOG.info("JDBC Driver founded.");
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

		rootScreen();
		
		try {
			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			System.err.println("Cannot get a connection: " + ex.getMessage());
			System.exit(1);
		}
	}
}
