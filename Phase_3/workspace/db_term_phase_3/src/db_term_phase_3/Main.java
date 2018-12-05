package db_term_phase_3;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
	public static final String URL = "jdbc:mysql://localhost:3306/shopx?serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=utf8";
	public static final String USER_ID = "admin";
	public static final String USER_PASSWD = "admin";

	private static Connection conn = null; // Connection object
	private static Statement stmt = null; // Statement object
	private static String sql = ""; // an SQL statement

	private static User currentUser = null;

	private static Menu menu = null;

	private static Logger LOG;
	private static Scanner sc;
	private static String hr = "----------------------------------------------------------------------";

	public static int ATOI(String sTmp) {
		String tTmp = "0", cTmp = "";

		sTmp = sTmp.trim();
		for (int i = 0; i < sTmp.length(); i++) {
			cTmp = sTmp.substring(i, i + 1);
			if (cTmp.equals("0") || cTmp.equals("1") || cTmp.equals("2") || cTmp.equals("3") || cTmp.equals("4")
					|| cTmp.equals("5") || cTmp.equals("6") || cTmp.equals("7") || cTmp.equals("8") || cTmp.equals("9"))
				tTmp += cTmp;
			else if (cTmp.equals("-") && i == 0)
				tTmp = "-";
			else
				break;
		}

		return (Integer.parseInt(tTmp));
	}

	public static void rootScreen() {
		menu = new Menu();
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
		// TODO 비밀번호 입력 암호화.
		/*
		 * Console cons = System.console(); String password = new
		 * String(cons.readPassword());
		 */

		try {
			sql = "SELECT C_id, Password FROM CUSTOMER WHERE Username = '" + username + "'";
			ResultSet rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("존재하지 않는 회원입니다.");
				conn.commit();
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				if (rs.next()) {
					String c_id = rs.getString(1);
					String qPassword = rs.getString(2);
					if (password.equals(qPassword)) {
						currentUser = new User(c_id, username);

						if (c_id.charAt(0) == 'A') { // 관리자 계정
							menu.enter("관리자 메뉴");
							adminMainScreen();
						} else { // 일반 계정
							menu.enter("쇼핑몰 X");
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

	public static ResultSet showBag() {
		ResultSet rs = null;

		try {
			sql = "SELECT I.Name, I.Spec, I.Price, S.Quantity, S.C_id, I.Code " + "FROM ITEM I, SHOPPINGBAG S "
					+ "WHERE S.I_code = I.Code " + "AND S.C_id = '" + currentUser.getC_id() + "'";

			rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("장바구니가 비었습니다.");
				conn.commit();
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김

				System.out.format("%s\t%s\t%s\t%s\t%s\n", "연번", "품명", "규격", "단가", "수량");

				int cnt = 1;
				while (rs.next()) {
					String name = rs.getString(1);
					String spec = rs.getString(2);
					String price = rs.getString(3);
					int quantity = rs.getInt(4);
					System.out.format("%d\t%s\t%s\t%s\t%s\n", cnt++, name, spec, price, quantity);
				}
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}

		return rs;
	}

	public static void removeAllFromShoppingBag() {
		try {
			sql = "DELETE FROM SHOPPINGBAG WHERE C_id = '" + currentUser.getC_id() + "'";

			stmt.addBatch(sql);
			int[] count = stmt.executeBatch();
			System.out.println("장바구니를 비웠습니다.");
			conn.commit();
			stmt.clearBatch();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static void removeFromShoppingBag(ResultSet rs) {
		try {
			System.out.println("제거하고 싶은 상품의 연번 또는 전체삭제를 원할 경우 all을 입력해 주세요.");
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("all")) {
				removeAllFromShoppingBag();
			} else {
				rs.last();
				int inputNum = ATOI(input);

				if (inputNum <= 0 || rs.getRow() < inputNum) {
					System.out.println("잘못된 입력입니다.");
				} else {
					rs.beforeFirst();
					for (int i = 0; i < inputNum; i++) {
						rs.next();
					}

					String c_id = rs.getString(5);
					String i_code = rs.getString(6);

					sql = "DELETE FROM SHOPPINGBAG WHERE C_id = '" + c_id + "' AND I_code = '" + i_code + "'";

					stmt.addBatch(sql);
					int[] count = stmt.executeBatch();

					System.out.println(count.length + "개의 상품이 장바구니에서 삭제되었습니다.");

					conn.commit();
					stmt.clearBatch();
				}
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static void purchase(ResultSet rs) {
		Statement stmt2 = null;

		try {
			stmt2 = conn.createStatement();

			// ITEM 재고 확인
			// 만약 목장갑의 Stock이 3000이고 Quantity가 10이라면, 10개짜리 목장갑 묶음이 300개가 있는 것.
			sql = "SELECT I.Code, I.Stock, I.Min_quantity, S.Quantity FROM ITEM I, SHOPPINGBAG S WHERE S.I_code = I.Code AND S.C_id = '"
					+ currentUser.getC_id() + "'";
			ResultSet itemrs = stmt2.executeQuery(sql);
			boolean orderValid = true;

			while (itemrs.next()) {
				int stock = itemrs.getInt(2);
				int min_quantity = itemrs.getInt(3);
				int o_quantity = itemrs.getInt(4);

				if (stock < o_quantity) {
					System.out.println("재고 수량이 부족한 제품이 장바구니에 있습니다.");
					orderValid = false;
					break;
				}
				if (min_quantity > o_quantity) {
					System.out.println("주문 수량이 최소 주문 수량에 미달하는 제품이 장바구니에 있습니다.");
					orderValid = false;
					break;
				}
			}

			if (!orderValid) {
				itemrs.close();
				conn.commit();
				return;
			}

			// ITEM 재고 수정
			itemrs.beforeFirst();
			while (itemrs.next()) {
				String i_code = itemrs.getString(1);
				int stock = itemrs.getInt(2);
				int o_quantity = itemrs.getInt(4);

				sql = "UPDATE ITEM SET Stock = " + Integer.toString(stock - o_quantity) + " WHERE Code = '" + i_code
						+ "'";
				stmt2.addBatch(sql);
			}
			int[] count = stmt2.executeBatch();
			stmt2.clearBatch();
			itemrs.close();

			// SHIPPINGORDER 추가
			sql = "SELECT MAX(So_id) FROM SHIPPINGORDER";
			ResultSet sors = stmt2.executeQuery(sql);
			sors.next();
			String so_id = sors.getString(1);
			String new_soid = "SO" + String.format("%08d", (ATOI(so_id.substring(2)) + 1));
			sors.close();

			System.out.println("배송에 필요한 정보를 입력해 주세요. 미입력시 회원 정보의 값으로 대체됩니다.");
			System.out.println("배송받으실 주소를 입력해 주세요.");
			System.out.print("> ");
			String address = sc.nextLine().trim();
			if (address.length() > 200) { // 주소 길이 제한 200자를 초과하는 경우 - 200자까지만 입력
				System.out.println("주소 길이 제한을 초과하여 200자까지만 저장합니다.");
				address = String.format("%200s", address);
			} else if (address.length() == 0) { // 공백을 입력했을 경우
				sql = "SELECT address FROM CUSTOMER WHERE C_id = '" + currentUser.getC_id() + "'";
				ResultSet addrs = stmt2.executeQuery(sql);
				addrs.next();
				address = addrs.getString(1);
				addrs.close();
			}

			System.out.println("배송받으실 분의 이름을 입력해 주세요.");
			System.out.print("> ");
			String name = sc.nextLine().trim();
			if (name.length() > 60) { // 이름 길이 제한 60자를 초과하는 경우 - 60자까지만 입력
				System.out.println("이름 길이 제한을 초과하여 60자까지만 저장합니다.");
				address = String.format("%60s", address);
			} else if (address.length() == 0) { // 공백을 입력했을 경우
				sql = "SELECT name FROM CUSTOMER WHERE C_id = '" + currentUser.getC_id() + "'";
				ResultSet namers = stmt2.executeQuery(sql);
				namers.next();
				name = namers.getString(1);
				namers.close();
			}

			System.out.println("전화번호를 입력해 주세요. (ex:010-1234-5678 | 0531234567)");
			String tel = "";
			while (true) {
				System.out.print("> ");
				tel = sc.nextLine().trim();

				if (tel.length() == 0) {
					sql = "SELECT tel FROM CUSTOMER WHERE C_id = '" + currentUser.getC_id() + "'";
					ResultSet telrs = stmt2.executeQuery(sql);
					telrs.next();
					tel = telrs.getString(1);
					telrs.close();
					break;
				}
				boolean tel_check = true;
				String pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}|\\d{2,3}\\d{3,4}\\d{4}$";
				// validation check: 정규식(전화번호)
				if (!tel.matches(pattern)) { // 정규식 불일치
					tel_check = false;
				} else {
					tel = tel.replaceAll("-", "");
				}

				if (tel_check) { // 가능한 전화번호일 경우
					break;
				} else { // 불가능할 경우
					System.out.println("잘못된 전화번호 형식입니다. 다시 입력해 주세요.");
				}
			}

			// 입력한 정보 보여주기
			System.out.println("[배송 정보]");
			System.out.println("주소: " + address);
			System.out.println("이름: " + name);
			System.out.println("전화번호: " + tel);
			System.out.println();

			// 구매 여부 최종결정
			System.out.println("입력된 정보로 장바구니의 상품을 전체 구매하시겠습니까? (y/n)");
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("y") || input.equals("Y")) {
				Date date = new Date(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String otime = sdf.format(date);

				sql = "SELECT Sh_id, Region FROM SHIPPINGCOMPANY";
				ResultSet shrs = stmt2.executeQuery(sql);
				String sh_id = "SH00000001";
				while (shrs.next()) {
					String cur_shid = shrs.getString(1);
					String region = shrs.getString(2);

					if (region.matches(address.substring(0, 1))) {
						sh_id = cur_shid;
						break;
					}
				}
				shrs.close();

				sql = "INSERT INTO SHIPPINGORDER VALUES ('" + new_soid + "', '" + address + "', '" + name + "', '" + tel
						+ "', '" + otime + "', '" + currentUser.getC_id() + "', '" + sh_id + "')";
				stmt2.executeUpdate(sql);

				// ORDER_LIST에 추가
				rs.beforeFirst();
				while (rs.next()) {
					sql = "INSERT INTO ORDER_LIST VALUES ('" + new_soid + "', '" + rs.getString(6) + "', "
							+ rs.getInt(4) + ")";
					stmt.addBatch(sql);
				}
				int[] ol_count = stmt.executeBatch();
				stmt.clearBatch();

				// 메세지 출력
				System.out.println(ol_count.length + "개의 상품이 주문되었습니다.");

				// 장바구니 비우기
				removeAllFromShoppingBag();

				conn.commit();
			}
			stmt2.close();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static void bagScreen() {
		try {
			System.out.println();
			System.out.println("장바구니");
			System.out.println(hr);
			ResultSet rs = showBag();
			System.out.println();
			System.out.println("1. 장바구니에서 제거");
			System.out.println("2. 장바구니 전체 구매");
			System.out.println("0. 돌아가기");

			while (true) {
				System.out.print("> ");
				String input = sc.nextLine().trim();

				if (input.equals("0")) {
					break;
				} else {
					// 장바구니 비어있을 시 삭제, 주문 못하게
					rs.last();
					if (rs.getRow() == 0) {
						System.out.println("장바구니에 상품이 없습니다.");
					} else {
						if (input.equals("1")) {
							removeFromShoppingBag(rs);
						} else if (input.equals("2")) {
							purchase(rs);
						} else {
							System.out.println("잘못된 입력입니다.");
							continue;
						}
					}
				}

				rs.close();
				System.out.println();
				System.out.println("장바구니");
				System.out.println(hr);
				rs = showBag();
				System.out.println();
				System.out.println("1. 장바구니에서 제거");
				System.out.println("2. 장바구니 전체 구매");
				System.out.println("0. 돌아가기");
			}

			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static void adminMainScreen() {
		System.out.println();
		System.out.println(menu.path());
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
				currentUser = null; // 로그아웃 시
				System.out.println("성공적으로 로그아웃하였습니다.");
				break;
			} else {
				System.out.println("잘못된 입력입니다.");
				continue;
			}
			// currentUser == null -> 로그아웃이 강제되는 상황일 경우
			if (currentUser == null) {
				break;
			}

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.println("1. 물품 주문");
			System.out.println("2. 재고 부족 상품 조회");
			System.out.println("3. 매출 확인");
			System.out.println("4. 배송 업체 별 배송 횟수 조회");
			System.out.println("0. 로그아웃");
		}

		menu.leave();
	}

	private static void customerMainScreen() {
		System.out.println();
		System.out.println(menu.path());
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
				// accountSetting();
			} else if (input.equals("2")) {

			} else if (input.equals("3")) {
				bagScreen();
			} else if (input.equals("4")) {

			} else if (input.equals("0")) {
				currentUser = null;
				System.out.println("성공적으로 로그아웃하였습니다.");
				break;
			} else {
				System.out.println("잘못된 입력입니다.");
				continue;
			}

			// currentUser == null -> 로그아웃이 강제되는 상황일 경우
			if (currentUser == null) {
				break;
			}

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.println("1. 계정 관리");
			System.out.println("2. 상품 조회");
			System.out.println("3. 장바구니");
			System.out.println("4. 구매내역 조회");
			System.out.println("0. 로그아웃");
		}

		menu.leave();
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
			System.err.println("Cannot close: " + ex.getMessage());
			System.exit(1);
		}
	}
}
