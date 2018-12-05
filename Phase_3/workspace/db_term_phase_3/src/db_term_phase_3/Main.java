package db_term_phase_3;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
				menu.enter("회원가입");
				signupScreen();
				menu.leave();
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
		// 필수 : ID, PASSWORD, ADDRESS, TEL
		// 선택 : 성별, 생년월일, 성(Lname), 이름, 직업, 타입(도/소매)
		// PASSWORD는 재확인 과정 거칠 것
		String id, password, repassword, address, tel, sex, bdate, fname, lname, job, type;
		System.out.println();
		System.out.println(menu.path());
		System.out.println(hr);
		// 필수 정보 입력
		System.out.println("사용자 계정에 필요한 필수 정보를 입력합니다. 회원가입 중단은 아이디, 비밀번호 입력 시에만 가능합니다.");
		// ID 입력
		System.out.println("아이디를 입력해 주세요. 아이디는 영문+숫자로 이루어지며 20자 이내여야 합니다.");
		System.out.println("회원가입을 중단하고 싶으면 공백을 입력해 주세요.");
		while (true) {
			System.out.print("> ");
			id = sc.nextLine().trim();
			boolean id_check = true;
			String fail_reason = "";
			String pattern = "^[a-zA-Z0-9]*$";
			// 공백 입력 시
			if(id.length() <= 0 ) {
				System.out.println("공백을 입력하였습니다. 회원가입을 중단합니다.");
				return;
			}
			// validation check: 길이(20자 이내)
			if (id.length() > 20) {
				id_check = false;
				fail_reason += "\t아이디는 20자 이내여야 합니다.\n";
			}
			// validation check: 정규식(영문+숫자)
			if (!id.matches(pattern)) { // 정규식 불일치
				id_check = false;
				fail_reason += "\t아이디는 영문+숫자로만 구성되어야 합니다.\n";
			}
			// validation check: 아이디 존재 여부
			try {
				sql = "SELECT C_id FROM CUSTOMER WHERE Username = '" + id + "'";
				ResultSet rs = stmt.executeQuery(sql);
				rs.last(); // 커서를 맨 뒤로 옮김
				if (rs.getRow() > 0) { // 이미 아이디가 존재할 경우
					id_check = false;
					fail_reason += "\t이미 존재하는 아이디입니다.\n";
				}
				rs.close();
				conn.commit();
			} catch (SQLException ex) {
				System.err.println("sql error = " + ex.getMessage());
				id_check = false;
				fail_reason += "\t아이디를 검색하는데 오류가 발생했습니다. 잘못된 아이디 형식\n";
			}

			if (id_check) { // 생성가능한 id일 경우
				System.out.println("[ " + id + " ]는 사용 가능한 아이디입니다.");
				break;
			} else { // 생성이 불가능할 경우
				System.out.println("생성할 수 없는 아이디입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: " + "\n" + fail_reason);
			}
		}
		// PASSWORD 입력
		while (true) {
			System.out.println("비밀번호를 입력해 주세요. 비밀번호는 영문+숫자+특수문자로 이루어지며 30자 이내여야 합니다.");
			System.out.println("회원가입을 중단하고 싶으면 공백을 입력해 주세요");
			System.out.print("> ");
			password = sc.nextLine().trim();
			System.out.println("비밀번호 확인을 위해 다시 입력해 주세요.");
			System.out.print("> ");
			repassword = sc.nextLine().trim();
			// 공백 입력 시
			if(password.length() <= 0 ) {
				System.out.println("공백을 입력하였습니다. 회원가입을 중단합니다.");
				return;
			}			
			if (repassword.equals(password)) { // 비밀번호가 일치하는 경우
				boolean pass_check = true;
				String fail_reason = "";
				String pattern = "^[a-zA-Z0-9\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]*$";
				// validation check: 길이(30자 이내)
				if (password.length() <= 0 || password.length() > 30) {
					pass_check = false;
					fail_reason += "\t비밀번호는 30자 이내여야 합니다.\n";
				}
				// validation check: 정규식(영문+숫자+특수문자)
				if (!password.matches(pattern)) { // 정규식 불일치
					pass_check = false;
					fail_reason += "\t비밀번호는 영문+숫자+특수문자(공백제외)로만 구성되어야 합니다.\n";
				}

				if (pass_check) { // 생성가능한 PASSWORD일 경우
					System.out.println("사용 가능한 비밀번호입니다.");
					break;
				} else { // 생성이 불가능할 경우
					System.out.println("생성할 수 없는 비밀번호입니다. 다시 입력해 주세요.");
					System.out.printf("\t사유: \n" + fail_reason);
				}
			} else { // 비밀번호가 일치하지 않는 경우
				System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해 주세요");
			}
		}
		// ADDRESS 입력
		System.out.println("주소를 입력해 주세요. ");
		while (true) {
			System.out.print("> ");
			address = sc.nextLine().trim();
			address = address.replaceAll("\\\'", "\\\\\'"); // TODO**
			if (address.length() > 200) { // 주소 길이 제한 200자를 초과하는 경우 - 200자까지만 입력
				System.out.println("주소 길이 제한을 초과하여 200자까지만 저장합니다.");
				address = String.format("%200s", address);
				System.out.println("\t" + address);
				break;
			} else if (address.length() <= 0) {	// 공백을 입력했을 경우
				System.out.println("주소 정보는 반드시 기입해야 합니다. 다시 입력해 주세요.");
			} else {	// 주소 정보가 입력됬을 경우
				break;
			}
		}		
		// TEL 입력
		System.out.println("전화번호를 입력해 주세요. (ex:010-1234-5678 | 0531234567)");
		while (true) {
			System.out.print("> ");
			tel = sc.nextLine().trim();
			boolean tel_check = true;
			String fail_reason = "";
			String pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}|\\d{2,3}\\d{3,4}\\d{4}$";
			// validation check: 정규식(전화번호)
			if (!tel.matches(pattern)) { // 정규식 불일치
				tel_check = false;
				fail_reason += "\t올바른 전화번호 형식이 아닙니다.\n";
			} else {
				tel = tel.replaceAll("-", "");
			}

			if (tel_check) { // 생성가능한 전화번호일 경우
				break;
			} else { // 생성이 불가능할 경우
				System.out.println("생성할 수 없는 전화번호 형식입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: \n" + fail_reason);
			}
		}
		// 추가 정보 입력
		// SEX 입력
		System.out.println("사용자 계정의 추가 정보를 입력합니다. 다음 항목들은 필수가 아닙니다.");
		System.out.println("입력을 원치 않을 시 공백으로 두고 개행하세요.");
		System.out.println("성별을 입력해 주세요. (m/f/M/F)");
		while (true) {
			System.out.print("> ");
			sex = sc.nextLine().trim();
			if (sex.length() <= 0) {
				sex = null;
				break;
			}
			boolean sex_check = true;
			String fail_reason = "";
			String pattern = "^[mMfF]$";
			// validation check: 정규식(전화번호)
			if (!sex.matches(pattern)) { // 정규식 불일치
				sex_check = false;
				fail_reason += "\t올바른 성별 형식이 아닙니다. (m/f/M/F)\n";
			}

			if (sex_check) { // 생성가능한 전화번호일 경우
				sex = sex.toUpperCase();
				break;
			} else { // 생성이 불가능할 경우
				System.out.println("생성할 수 없는 성별 형식입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: \n" + fail_reason);
			}
		}
		// 생년월일 입력
		System.out.println("생년월일을 입력해 주세요. (YYYY-MM-DD)");
		while (true) {
			System.out.print("> ");
			bdate = sc.nextLine().trim();
			if (bdate.length() <= 0) {
				bdate = null;
				break;
			}
			boolean bdate_check = true;
			String fail_reason = "";
			String pattern = "^(19|20|21)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$";
			// validation check: 정규식(전화번호)
			if (!bdate.matches(pattern)) { // 정규식 불일치
				bdate_check = false;
				fail_reason += "\t올바른 생년월일 형식이 아닙니다. (YYYY-MM-DD)\n";
			}

			if (bdate_check) { // 생성가능한 생년월일일 경우
				break;
			} else { // 생성이 불가능할 경우
				System.out.println("생성할 수 없는 생년월일 형식입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: \n" + fail_reason);
			}
		}
		// 이름 입력
		System.out.println("이름을 입력해 주세요.");
		System.out.print("> ");
		fname = sc.nextLine().trim();
		fname = fname.replaceAll("\\\'", "\\\\\'"); // TODO**
		if (fname.length() > 30) { // 이름 길이 제한 30자를 초과하는 경우 - 30자까지만 입력
			System.out.println("이름 길이 제한을 초과하여 30자까지만 저장합니다.");
			fname = String.format("%30s", fname);
			System.out.println("\t" + fname);
		} else if (fname.length() <= 0) {
			fname = null;
		}
		// 성 입력
		System.out.println("성을 입력해 주세요");
		System.out.print("> ");
		lname = sc.nextLine().trim();
		lname = lname.replaceAll("\\\'", "\\\\\'"); // TODO**
		if (lname.length() > 30) { // 성씨 길이 제한 30자를 초과하는 경우 - 30자까지만 입력
			System.out.println("성씨 길이 제한을 초과하여 30자까지만 저장합니다.");
			lname = String.format("%30s", lname);
			System.out.println("\t" + lname);
		} else if (lname.length() <= 0) {
			lname = null;
		}
		// 직업 입력
		System.out.println("직업을 입력해 주세요");
		System.out.print("> ");
		job = sc.nextLine().trim();
		job = job.replaceAll("\\\'", "\\\\\'"); // TODO**
		if (job.length() > 40) { // 직업 길이 제한 40자를 초과하는 경우 - 40자까지만 입력
			System.out.println("직업 길이 제한을 초과하여 40자까지만 저장합니다.");
			job = String.format("%40s", job);
			System.out.println("\t" + job);
		} else if (job.length() <= 0) {
			job = null;
		}
		// 타입 입력
		System.out.println("타입을 입력해 주세요 (소매/도매/기타)");
		while (true) {
			System.out.print("> ");
			type = sc.nextLine().trim();
			if (type.length() <= 0) {
				type = null;
				break;
			}
			boolean type_check = true;
			String fail_reason = "";
			String pattern = "^(소매)|(도매)|(기타)$";
			// validation check: 정규식(소매|도매|기타)
			if (!type.matches(pattern)) { // 정규식 불일치
				type_check = false;
				fail_reason += "\t올바른 타입 형식이 아닙니다. (소매/도매/기타)\n";
			}

			if (type_check) { // 생성가능한 타입일 경우
				break;
			} else { // 생성이 불가능할 경우
				System.out.println("생성할 수 없는 타입 형식입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: \n" + fail_reason);
			}
		}
		// 필수 : ID, PASSWORD, ADDRESS, TEL
		// 선택 : 성별, 생년월일, 성(Lname), 이름, 직업, 타입(도/소매)
		String temp_pass = "";
		for (int i = 0; i < password.length(); i++) {
			temp_pass += "*";
		}
		System.out.println("필수입력) ID: " + id + ", ADDRESS: " + address + ", TEL: " + tel);
		System.out.println("추가정보) 성별: " + sex + ", 생년월일: " + bdate + ", 성: " + lname + ", 이름: " + fname + ", 직업: " + job
				+ ", 타입: " + type);

		// INSERT CUSTOMER Query
		// Phase 03부터 새로 추가되는 고객은 C_id가 'C3'로 시작
		// SELECT 문으로 C_id가 'C3'로 시작하는 고객을 정렬하여 검색한다.
		// 검색 후 가장 마지막 숫자 다음 수를 C_id로 부여
		try {
			String c_id = "";
			// Insert 1) : SELECT C_id LIKE 'C3%'
			sql = "SELECT C_id FROM CUSTOMER WHERE C_id LIKE 'C3%' ORDER BY C_id DESC LIMIT 1";
			ResultSet rs = stmt.executeQuery(sql);
			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) { // C_id가 C3로 시작하는 고객이 존재하지 않음
				c_id = "C300000001";
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				if (rs.next()) {
					String SELECT_c_id = rs.getString(1);
					c_id = String.format("C3%08d", Integer.parseInt(SELECT_c_id.substring(2)) + 1);
				}
				rs.close();
				conn.commit();
			}			
			String insert_sql = "INSERT INTO CUSTOMER VALUES ('" + c_id + "', '" + id + "', '" + password + "', '"
					+ address + "', '" + tel + "', ";
			if (sex == null) {
				insert_sql += "NULL, ";
			} else {
				insert_sql += String.format("'%s', ", sex);
			}
			if (bdate == null) {
				insert_sql += "NULL, ";
			} else {
				// STR_TO_DATE('1972-05-10', '%Y-%m-%d')
				insert_sql += String.format("STR_TO_DATE('%s', '%%Y-%%m-%%d'), ", bdate);
			}
			if (fname == null) {
				insert_sql += "NULL, ";
			} else {
				insert_sql += String.format("'%s', ", fname);
			}
			if (lname == null) {
				insert_sql += "NULL, ";
			} else {
				insert_sql += String.format("'%s', ", lname);
			}
			if (job == null) {
				insert_sql += "NULL, ";
			} else {
				insert_sql += String.format("'%s', ", job);
			}
			if (type == null) {
				insert_sql += "NULL)";
			} else {
				insert_sql += String.format("'%s')", type);
			}
			
			int res = stmt.executeUpdate(insert_sql);
			System.out.println("계정이 생성되었습니다.");
			conn.commit();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.out.println("계정을 생성할 수 없습니다.");
		}
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
			stmt.executeBatch();
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
			stmt2.executeBatch();
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
			System.out.println(menu.path());
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
				System.out.println(menu.path());
				System.out.println(hr);
				rs = showBag();
				System.out.println();
				System.out.println("1. 장바구니에서 제거");
				System.out.println("2. 장바구니 전체 구매");
				System.out.println("0. 돌아가기");
			}

			if (rs != null)
				rs.close();

			menu.leave();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static ResultSet showOrderLog() {
		ResultSet rs = null;

		try {
			sql = "SELECT * FROM SHIPPINGORDER WHERE C_id = '" + currentUser.getC_id() + "'";

			rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("구매내역이 없습니다.");
				conn.commit();
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김

				System.out.format("%s\t%s\n", "연번", "구매시각");

				int cnt = 1;
				while (rs.next()) {
					Timestamp otime = rs.getTimestamp(5);
					System.out.format("%d\t%s\n", cnt++, otime.toString().split("\\.")[0]);
				}
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}

		return rs;
	}

	private static void showDetailOrderInfo(ResultSet rs) {
		try {
			System.out.println("[상세 정보]");
			String address = rs.getString(2);
			String name = rs.getString(3);
			String tel = rs.getString(4);
			Timestamp otime = rs.getTimestamp(5);
			System.out.println("주소: " + address);
			System.out.println("수령자 성명: " + name);
			System.out.println("수령자 연락처: " + tel);
			System.out.println("주문 시각: " + otime.toString().split("\\.")[0]);
			System.out.println();

		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	private static void showOrderList(String so_id) {
		Statement stmt2 = null;

		try {
			stmt2 = conn.createStatement();
			sql = "SELECT I.Name, I.Spec, O.Quantity FROM ITEM I, ORDER_LIST O WHERE O.So_id = '" + so_id + "' AND O.I_code = I.Code";
			ResultSet olrs = stmt2.executeQuery(sql);

			System.out.println("[구매 상품 목록]");
			System.out.format("%s\t%s\t%s\t%s\n", "연번", "품명", "규격", "주문수량");
			
			int cnt = 1;
			while (olrs.next()) {
				String name = olrs.getString(1);
				String spec = olrs.getString(2);
				int quantity = olrs.getInt(3);
				System.out.format("%d\t%s\t%s\t%s\n", cnt++, name, spec, quantity);
			}

			conn.commit();
			stmt2.close();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static void detailOrder(ResultSet rs) {
		try {
			String so_id = rs.getString(1);

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			showDetailOrderInfo(rs);
			showOrderList(so_id);
			System.out.println();
			System.out.println("0. 돌아가기");

			while (true) {
				System.out.print("> ");
				String input = sc.nextLine().trim();

				if (input.equals("0")) {
					break;
				} else {
					System.out.println("잘못된 입력입니다.");
				}

				System.out.println();
				System.out.println(menu.path());
				System.out.println(hr);
				showDetailOrderInfo(rs);
				showOrderList(so_id);
				System.out.println();
				System.out.println("0. 돌아가기");
			}

			menu.leave();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.exit(1);
		}
	}

	public static void orderLogScreen() {
		try {
			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			ResultSet rs = showOrderLog();
			System.out.println();
			System.out.println("1. 구매내역 상세 보기");
			System.out.println("0. 돌아가기");

			while (true) {
				System.out.print("> ");
				String input = sc.nextLine().trim();

				if (input.equals("0")) {
					break;
				} else {
					// 구매내역이 없는지 확인
					rs.last();
					if (rs.getRow() == 0) {
						System.out.println("구매내역이 없습니다.");
					} else if (input.equals("1")) {
						System.out.println("조회할 구매내역의 연번을 입력하세요.");
						System.out.print("> ");
						input = sc.nextLine().trim();

						if (ATOI(input) > rs.getRow() || ATOI(input) <= 0) {
							System.out.println("잘못된 입력입니다.");
						} else {
							rs.beforeFirst();
							for (int i = 0; i < ATOI(input); i++) {
								rs.next();
							}
							menu.enter("구매내역 상세 보기");
							detailOrder(rs);
						}
					} else {
						System.out.println("잘못된 입력입니다.");
					}
				}

				rs.close();
				System.out.println();
				System.out.println(menu.path());
				System.out.println(hr);
				rs = showOrderLog();
				System.out.println();
				System.out.println("1. 구매내역 상세 보기");
				System.out.println("0. 돌아가기");
			}

			if (rs != null)
				rs.close();

			menu.leave();
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
				menu.enter("매출 확인");
				searchSale();
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
	
	private static void searchSale() {		
		// 매출 확인. 총 매출을 우선 출력
		// 전체 | YYYY | YYYY-MM 중 하나를 입력 받아 결과 출력
		// 전체 : 연도 별 매출 출력
		// YYYY : 해당 연도 월별 매출 출력
		// YYYY-MM : 해당 연/월 일별 매출 출력
		String totalSale = null;
		System.out.println();
		System.out.println(menu.path());
		System.out.println(hr);
		getSale("총 매출", null);
		System.out.println("다음 중 하나를 입력할 수 있습니다 : ( 전체 | YYYY | YYYY-MM )");
		System.out.println("전체 : 연도 별 매출 출력");
		System.out.println("YYYY : 해당 연도 월별 매출 출력");
		System.out.println("YYYY-MM : 해당 연/월 일별 매출 출력");
		System.out.println("공백을 입력할 시 종료합니다.");

		while (true) {
			System.out.print("> ");
			String date = sc.nextLine().trim();
			System.out.println();
			System.out.println(date + " 에 대한 검색 결과");
			if(date.length() <= 0) {
				System.out.println("공백을 입력하였습니다. 매출 확인을 종료합니다.");
				menu.leave();
				break;
			}
			boolean date_check = true;
			String fail_reason = "";
			// validation check: 정규식(전화번호)
			if (date.matches("^(전체)$")) {	// '전체'일 경우
				getSale("전체", null);
			} else if (date.matches("^(19|20|21)\\d{2}$")) { // YYYY 일 경우
				getSale("YYYY", date);
			} else if (date.matches("^(19|20|21)\\d{2}-(0[1-9]|1[012])$")) {	// YYYY-MM 일 경우
				getSale("YYYY-MM", date);
			}
			else {
				date_check = false;
				fail_reason += "\t올바른 형식이 아닙니다. ( 전체 | YYYY | YYYY-MM )\n";
			}
			
			if (date_check) {	// 생성가능한 생년월일일 경우
				System.out.println();
				System.out.println("매출 확인");
				System.out.println(hr);
				getSale("총 매출", null);
				System.out.println("다음 중 하나를 입력할 수 있습니다 : ( 전체 | YYYY | YYYY-MM )");
				System.out.println("전체 : 연도 별 매출 출력");
				System.out.println("YYYY : 해당 연도 월별 매출 출력");
				System.out.println("YYYY-MM : 해당 연/월 일별 매출 출력");
				System.out.println("공백을 입력할 시 종료합니다.");
			} else {	// 생성이 불가능할 경우
				System.out.println("잘못된 형식입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: \n" + fail_reason);
			}
		}

	}

	private static void getSale(String input, String input_date) {
		// SELECT : 폼에 맞게 값을 가져옴
		String totalSale = null;
		if (input.equals("총 매출")) {
			sql = "SELECT SUM(i.Price*ol2.Quantity) AS TotalCost FROM ITEM i\n"
					+ "INNER JOIN (SELECT ol.So_id, ol.I_code, ol.Quantity FROM ORDER_LIST ol \n"
					+ "INNER JOIN (SELECT so.So_id FROM SHIPPINGORDER so) AS so2\n" + "ON ol.So_id=so2.So_id) AS ol2\n"
					+ "ON i.Code=ol2.I_code";
		} else if (input.equals("전체")) {
			sql = "SELECT ol2.Date, SUM(i.Price*ol2.Quantity) AS TotalCost FROM ITEM i\n"
					+ "INNER JOIN (SELECT so2.Date, ol.So_id, ol.I_code, ol.Quantity FROM ORDER_LIST ol \n"
					+ "INNER JOIN (SELECT DATE_FORMAT(so.Otime, \"%Y\") AS Date, so.So_id FROM SHIPPINGORDER so) AS so2\n"
					+ "ON ol.So_id=so2.So_id) AS ol2\n" + "ON i.Code=ol2.I_code\n" + "GROUP BY ol2.Date";
		} else if (input.equals("YYYY")) {
			sql = "SELECT ol2.Date, SUM(i.Price*ol2.Quantity) AS TotalCost FROM ITEM i\n"
					+ "INNER JOIN (SELECT so2.Date, ol.So_id, ol.I_code, ol.Quantity FROM ORDER_LIST ol \n"
					+ "INNER JOIN (SELECT DATE_FORMAT(so.Otime, \"%Y-%m\") AS Date, so.So_id FROM SHIPPINGORDER so WHERE so.Otime BETWEEN date(\""
					+ input_date + "-01-01\") and date(\"" + String.format("%d", Integer.parseInt(input_date) + 1)
					+ "-01-01\")) AS so2\n" + "ON ol.So_id=so2.So_id) AS ol2\n" + "ON i.Code=ol2.I_code\n"
					+ "GROUP BY ol2.Date;";
		} else if (input.equals("YYYY-MM")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date;
			Calendar cal;
			try {
				date = df.parse(input_date + "-01");
				// 날짜 더하기
				cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.MONTH, 1);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("변환할 수 없습니다. 알 수 없는 오류");
				totalSale = "알 수 없음";
				System.out.println("매출 : " + totalSale);
				return;
			}
			sql = "SELECT ol2.Date, SUM(i.Price*ol2.Quantity) AS TotalCost FROM ITEM i\n"
					+ "INNER JOIN (SELECT so2.Date, ol.So_id, ol.I_code, ol.Quantity FROM ORDER_LIST ol \n"
					+ "INNER JOIN (SELECT DATE_FORMAT(so.Otime, \"%Y-%m-%d\") AS Date, so.So_id FROM SHIPPINGORDER so WHERE so.Otime BETWEEN date(\""
					+ input_date + "-01\") and date(\"" + df.format(cal.getTime()) + "-01\")) AS so2\n"
					+ "ON ol.So_id=so2.So_id) AS ol2\n" + "ON i.Code=ol2.I_code\n" + "GROUP BY ol2.Date";
		}

		try {
			ResultSet rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("해당 기간에는 매출이 존재하지 않습니다.");
				totalSale = "매출 없음";
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				while (rs.next()) {
					if (input.equals("총 매출")) {
						totalSale = rs.getString(1);
						System.out.println("현재까지 총 매출 : " + String.format("%,d", Integer.parseInt(totalSale)));
					} else {
						String qDate = rs.getString(1);
						totalSale = rs.getString(2);
						System.out.printf("%s | %s\n", qDate, String.format("%,d", Integer.parseInt(totalSale)));
					}
				}
				rs.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.out.println("매출 확인을 수행하는데 문제가 발생했습니다. 나중에 다시 시도해 주세요");
			totalSale = "값을 가져올 수 없습니다.";
			System.out.println("매출 : " + totalSale);
		}
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
				accountSetting();
			} else if (input.equals("2")) {

			} else if (input.equals("3")) {
				menu.enter("장바구니");
				bagScreen();
			} else if (input.equals("4")) {
				menu.enter("구매내역 조회");
				orderLogScreen();
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
	
	private static void accountSetting() {
		// 비밀번호 확인 후 접근 허용
		// 1. 회원 정보 수정 2. 비밀번호 수정 0. 돌아가기
		System.out.println();
		System.out.println("회원 확인");
		System.out.println(hr);
		System.out.println("회원 정보 수정 전 비밀번호 확인이 필요합니다.");
		if(accountCheck()) { // 계정 확인에 성공하였을 경우
			System.out.println("회원 확인에 성공했습니다. ");
		} else {	// 계정 확인에 실패했을 경우
			System.out.println("회원 확인에 실패했습니다. 다시 시도해 주세요.");
			return;
		}
		System.out.println();
		System.out.println("계정 관리");
		System.out.println(hr);
		System.out.println("1. 회원 정보 수정");
		System.out.println("2. 비밀번호 수정");
		System.out.println("0. 돌아가기");

		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("1")) {
				accountInfoUpdate();
			} else if (input.equals("2")) {
				accountPasswordUpdate();
			} else if (input.equals("0")) {
				System.out.println("이전 메뉴로 돌아갑니다.");
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
			System.out.println("계정 관리");
			System.out.println(hr);
			System.out.println("1. 회원 정보 수정");
			System.out.println("2. 비밀번호 수정");
			System.out.println("0. 돌아가기");
		}
	}
	
	private static void accountInfoUpdate() {
		System.out.println();
		System.out.println("회원 정보 수정");
		System.out.println(hr);
		System.out.println("현재 회원 정보");
		System.out.printf("%s%s\n", "아이디　 ", ": " + currentUser.getUsername());
		System.out.printf("%s%s\n", "주소　　 ", ": " + currentUser.getAddress());
		System.out.printf("%s%s\n", "전화번호 ", ": " + currentUser.getTel());
		System.out.printf("%s%s\n", "성별　　 ", ": " + currentUser.getSex());
		System.out.printf("%s%s\n", "생년월일 ", ": " + currentUser.getBdate());
		System.out.printf("%s%s\n", "성/이름  ", ": " + currentUser.getLname() + " / " + currentUser.getFname());
		System.out.printf("%s%s\n", "직업　　 ", ": " + currentUser.getJob());
		System.out.printf("%s%s\n", "타입　　 ", ": " + currentUser.getType());
		System.out.println();
		System.out.println("변경하고자 하는 메뉴를 선택해 주세요.");
		System.out.printf("%s%s\n", "1. 주소 수정   ", "2. 전화번호 수정");
		System.out.printf("%s%s\n", "3. 성별 수정   ", "4. 생년월일 수정");
		System.out.printf("%s%s\n", "5. 성 수정　   ", "6. 이름 수정");
		System.out.printf("%s%s\n", "7. 직업 수정   ", "8. 타입 수정");
		System.out.println("0. 돌아가기");
		
		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();
			String sql = "UPDATE CUSTOMER SET ";
			if (input.equals("1")) {				// 주소 수정
				System.out.println("주소 수정을 선택하였습니다. 주소는 필수 정보입니다.");
				System.out.println("기존 주소: " + currentUser.getAddress());
				System.out.println("변경할 주소를 입력해 주세요");
				while (true) {
					System.out.print("> ");
					String address = sc.nextLine().trim();
					address = address.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리력 시 처리
					if (address.length() > 200) { // 주소 길이 제한 200자를 초과하는 경우 - 200자까지만 입력
						System.out.println("주소 길이 제한을 초과하여 200자까지만 저장합니다.");
						address = String.format("%200s", address);
						System.out.println("\t" + address);
						break;
					} else if (address.length() <= 0) {	// 공백을 입력했을 경우
						System.out.println("주소 정보는 반드시 기입해야 합니다. 다시 입력해 주세요.");
					} else {	// 주소 정보가 입력됬을 경우
						sql += "Address='" + address + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					}
				}							
			} else if (input.equals("2")) {		// 전화번호 수정
				System.out.println("전화번호 수정을 선택하였습니다. 전화번호는 필수 정보입니다.");
				System.out.println("기존 전화번호: " + currentUser.getTel());
				System.out.println("변경할 전화번호를 입력해 주세요. (ex:010-1234-5678 | 0531234567)");
				while (true) {
					System.out.print("> ");
					String tel = sc.nextLine().trim();
					boolean tel_check = true;
					String fail_reason = "";	
					String pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}|\\d{2,3}\\d{3,4}\\d{4}$";
					// validation check: 정규식(전화번호)
					if (!tel.matches(pattern)) {	// 정규식 불일치
						tel_check = false;
						fail_reason += "\t올바른 전화번호 형식이 아닙니다.\n";
					} else {
						tel = tel.replaceAll("-", "");
					}
					
					if (tel_check) {	// 생성가능한 전화번호일 경우
						sql += "Tel='" + tel + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else {	// 생성이 불가능할 경우
						System.out.println("생성할 수 없는 전화번호 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("3")) {		// 성별 수정
				System.out.println("성별 수정을 선택하였습니다.");
				System.out.println("기존 성별: " + currentUser.getSex());
				System.out.println("변경할 성별을 입력해 주세요. (m/f/M/F)");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				while (true) {
					System.out.print("> ");
					String sex = sc.nextLine().trim();
					if(sex.length() <= 0) {
						sql += "Sex=NULL WHERE Username='" + currentUser.getUsername() + "'";
						break;
					}
					boolean sex_check = true;
					String fail_reason = "";	
					String pattern = "^[mMfF남여]$";
					// validation check: 정규식(전화번호)
					if (!sex.matches(pattern)) {	// 정규식 불일치
						sex_check = false;
						fail_reason += "\t올바른 성별 형식이 아닙니다. (m/f/M/F)\n";
					}
					
					if (sex_check) {	// 생성가능한 전화번호일 경우
						sex = sex.toUpperCase();
						sql += "Sex='" + sex + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else {	// 생성이 불가능할 경우
						System.out.println("생성할 수 없는 성별 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("4")) {		// 생년월일 수정
				System.out.println("생년월일 수정을 선택하였습니다.");
				System.out.println("기존 생년월일: " + currentUser.getBdate());
				System.out.println("변경할 생년월일을 입력해 주세요. (YYYY-MM-DD)");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				while (true) {
					System.out.print("> ");
					String bdate = sc.nextLine().trim();
					if(bdate.length() <= 0) {
						sql += "Bdate=NULL WHERE Username='" + currentUser.getUsername() + "'"; break;
					}
					boolean bdate_check = true;
					String fail_reason = "";	
					String pattern = "^(19|20|21)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$";
					// validation check: 정규식(전화번호)
					if (!bdate.matches(pattern)) {	// 정규식 불일치
						bdate_check = false;
						fail_reason += "\t올바른 생년월일 형식이 아닙니다. (YYYY-MM-DD)\n";
					}
					
					if (bdate_check) {	// 생성가능한 생년월일일 경우
						sql += "Bdate=" + String.format("STR_TO_DATE('%s', '%%Y-%%m-%%d')", bdate) + " WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else {	// 생성이 불가능할 경우
						System.out.println("생성할 수 없는 생년월일 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("5")) {		// 성 수정
				System.out.println("성 수정을 선택하였습니다.");
				System.out.println("기존 성: " + currentUser.getLname());
				System.out.println("변경할 성을 입력해 주세요. ");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				System.out.print("> ");
				String lname = sc.nextLine().trim();
				lname = lname.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리
				if(lname.length() > 30) {	// 성씨 길이 제한 30자를 초과하는 경우 - 30자까지만 입력
					System.out.println("성씨 길이 제한을 초과하여 30자까지만 저장합니다.");
					lname = String.format("%30s", lname);
					System.out.println("\t" + lname);
					sql += "Lname='" + lname + "' WHERE Username='" + currentUser.getUsername() + "'";
				} else if(lname.length() <= 0) {
					sql += "Lname=NULL WHERE Username='" + currentUser.getUsername() + "'";
				} else {	//성공적으로 입력한 경우
					sql += "Lname='" + lname + "' WHERE Username='" + currentUser.getUsername() + "'";
				}
			} else if (input.equals("6")) {		// 이름 수정
				System.out.println("이름 수정을 선택하였습니다.");
				System.out.println("기존 이름: " + currentUser.getFname());
				System.out.println("변경할 이름을 입력해 주세요. ");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				System.out.print("> ");
				String fname = sc.nextLine().trim();
				fname = fname.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리
				if(fname.length() > 30) {	// 이름 길이 제한 30자를 초과하는 경우 - 30자까지만 입력
					System.out.println("이름 길이 제한을 초과하여 30자까지만 저장합니다.");
					fname = String.format("%30s", fname);
					System.out.println("\t" + fname);
					sql += "Fname='" + fname + "' WHERE Username='" + currentUser.getUsername() + "'";
				} else if(fname.length() <= 0) {
					sql += "Fname=NULL WHERE Username='" + currentUser.getUsername() + "'";
				} else {	//성공적으로 입력한 경우
					sql += "Fname='" + fname + "' WHERE Username='" + currentUser.getUsername() + "'";
				}				
			} else if (input.equals("7")) {		// 직업 수정
				System.out.println("직업 수정을 선택하였습니다.");
				System.out.println("기존 직업: " + currentUser.getJob());
				System.out.println("변경할 직업을 입력해 주세요. ");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				System.out.print("> ");
				String job = sc.nextLine().trim();
				job = job.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리
				if(job.length() > 40) {	// 직업 길이 제한 40자를 초과하는 경우 - 40자까지만 입력
					System.out.println("직업 길이 제한을 초과하여 40자까지만 저장합니다.");
					job = String.format("%40s", job);
					System.out.println("\t" + job);
					sql += "Job='" + job + "' WHERE Username='" + currentUser.getUsername() + "'";
				} else if (job.length() <= 0) {
					sql += "Job=NULL WHERE Username='" + currentUser.getUsername() + "'";
				} else {
					sql += "Job='" + job + "' WHERE Username='" + currentUser.getUsername() + "'";
				}
			} else if (input.equals("8")) {		// 타입 수정
				System.out.println("타입 수정을 선택하였습니다.");
				System.out.println("기존 타입: " + currentUser.getType());
				System.out.println("변경할 타입을 입력해 주세요. (소매/도매/기타)");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				while (true) {
					System.out.print("> ");
					String type = sc.nextLine().trim();
					if(type.length() <= 0) {
						sql += "Type=NULL WHERE Username='" + currentUser.getUsername() + "'"; break;
					}
					boolean type_check = true;
					String fail_reason = "";	
					String pattern = "^(소매)|(도매)|(기타)$";
					// validation check: 정규식(소매|도매|기타)
					if (!type.matches(pattern)) {	// 정규식 불일치
						type_check = false;
						fail_reason += "\t올바른 타입 형식이 아닙니다. (소매/도매/기타)\n";
					}
					
					if (type_check) {	// 생성가능한 타입일 경우
						sql += "Type='" + type + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else {	// 생성이 불가능할 경우
						System.out.println("생성할 수 없는 타입 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("0")) {
				System.out.println("이전 메뉴로 돌아갑니다.");
				break;
			} else {
				System.out.println("잘못된 입력입니다.");
				continue;
			}
			
			// UPDATE query 수행
			try {
				int res = stmt.executeUpdate(sql);
				if (res <= 0) System.out.println("변경 중에 문제가 생겼습니다. 다시 시도해 주세요.");
				else System.out.println("변경이 완료되었습니다.");
				conn.commit();
			} catch (SQLException ex) {
				System.err.println("sql error = " + ex.getMessage());
				System.out.println("회원 정보를 업데이트 하는데 문제가 발생했습니다. 다시 시도해주세요.");
			}
			//CurrentUser 정보 갱신
			getCurrentUserInfo();
			// currentUser == null -> 로그아웃이 강제되는 상황일 경우
			if(currentUser == null) {
				break;
			}
			
			System.out.println();
			System.out.println("회원 정보 수정");
			System.out.println(hr);
			System.out.println("현재 회원 정보");
			System.out.printf("%s%s\n", "아이디　 ", ": " + currentUser.getUsername());
			System.out.printf("%s%s\n", "주소　　 ", ": " + currentUser.getAddress());
			System.out.printf("%s%s\n", "전화번호 ", ": " + currentUser.getTel());
			System.out.printf("%s%s\n", "성별　　 ", ": " + currentUser.getSex());
			System.out.printf("%s%s\n", "생년월일 ", ": " + currentUser.getBdate());
			System.out.printf("%s%s\n", "성/이름  ", ": " + currentUser.getLname() + " / " + currentUser.getFname());
			System.out.printf("%s%s\n", "직업　　 ", ": " + currentUser.getJob());
			System.out.printf("%s%s\n", "타입　　 ", ": " + currentUser.getType());
			System.out.println();
			System.out.println("변경하고자 하는 메뉴를 선택해 주세요.");
			System.out.printf("%s%s\n", "1. 주소 수정   ", "2. 전화번호 수정");
			System.out.printf("%s%s\n", "3. 성별 수정   ", "4. 생년월일 수정");
			System.out.printf("%s%s\n", "5. 성 수정　   ", "6. 이름 수정");
			System.out.printf("%s%s\n", "7. 직업 수정   ", "8. 타입 수정");
			System.out.println("0. 돌아가기");
		}
		
	}
	
	private static void getCurrentUserInfo() {
		try {
			sql = "SELECT * FROM CUSTOMER WHERE Username = '" + currentUser.getUsername() + "'";
			ResultSet rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("존재하지 않는 회원입니다. 로그아웃 후 다시 이용해 주세요.");
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				if (rs.next()) {
					// currentUser의 계정정보를 갱신함
					String qc_id = rs.getString(1);
					String qUsername = rs.getString(2);						
					String qAddress = rs.getString(4);
					String qTel = rs.getString(5);
					String qSex = rs.getString(6);
					String qBdate = rs.getString(7);
					String qFname = rs.getString(8);
					String qLname = rs.getString(9);
					String qJob = rs.getString(10);
					String qType = rs.getString(11);
					// 추가정보 NULL일 시 "" 문자 출력
					if(qSex != null) currentUser.setSex(qSex);
					else currentUser.setSex("");
					if(qBdate != null) currentUser.setBdate(qBdate);
					else currentUser.setBdate("");
					if(qFname != null) currentUser.setFname(qFname);
					else currentUser.setFname("");
					if(qLname != null) currentUser.setLname(qLname);
					else currentUser.setLname("");
					if(qJob != null) currentUser.setJob(qJob);
					else currentUser.setJob("");
					if(qType != null) currentUser.setType(qType);
					else currentUser.setType("");
			}
				rs.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.out.println("사용자 정보를 읽는데 문제가 발생했습니다. 다시 로그인해 주세요.");
			currentUser = null;
		}
	}

	private static void accountPasswordUpdate() {
		System.out.println();
		System.out.println("비밀번호 수정");
		System.out.println(hr);
		System.out.println("비밀번호 수정을 진행합니다.");
		System.out.printf("현재 ");
		if(accountCheck()) { // 계정 확인에 성공하였을 경우 -> 변경 계속 진행
			System.out.println("회원 확인에 성공했습니다. ");
			String password, repassword;
			// PASSWORD 입력
			while (true) {
				System.out.println("변경할 비밀번호를 입력해 주세요. 비밀번호는 영문+숫자+특수문자로 이루어지며 30자 이내여야 합니다.");
				System.out.println("공백을 입력할 시 비밀번호 변경을 중단합니다.");
				System.out.print("> ");
				password = sc.nextLine().trim();
				System.out.println("비밀번호 확인을 위해 다시 입력해 주세요.");
				System.out.print("> ");
				repassword = sc.nextLine().trim();
				if(password.length() <= 0 ) {
					System.out.println("공백을 입력하였습니다. 비밀번호 변경을 중단합니다.");
					return;
				}		
				if (repassword.equals(password)) { // 비밀번호가 일치하는 경우
					boolean pass_check = true;
					String fail_reason = "";
					String pattern = "^[a-zA-Z0-9\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]*$";
					// validation check: 길이(30자 이내)
					if (password.length() <= 0 || password.length() > 30) {
						pass_check = false;
						fail_reason += "\t비밀번호는 30자 이내여야 합니다.\n";
					}
					// validation check: 정규식(영문+숫자+특수문자)
					if (!password.matches(pattern)) { // 정규식 불일치
						pass_check = false;
						fail_reason += "\t비밀번호는 영문+숫자+특수문자(공백제외)로만 구성되어야 합니다.\n";
					}

					if (pass_check) { // 생성가능한 PASSWORD일 경우
						System.out.println("사용 가능한 비밀번호입니다.");
						break;
					} else { // 생성이 불가능할 경우
						System.out.println("생성할 수 없는 비밀번호입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				} else { // 비밀번호가 일치하지 않는 경우
					System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해 주세요");
				}
			}
			
			// UPDATE Query 진행
			String sql = "UPDATE CUSTOMER SET ";
			sql += "Password='" + password + "' WHERE Username='" + currentUser.getUsername() + "'";
			try {
				int res = stmt.executeUpdate(sql);
				if (res <= 0) System.out.println("변경 중에 문제가 생겼습니다. 다시 시도해 주세요.");
				else {
					System.out.println("변경이 완료되었습니다. 다시 로그인해 주세요");
					currentUser = null;
				}
				conn.commit();
			} catch (SQLException ex) {
				System.err.println("sql error = " + ex.getMessage());
				System.out.println("비밀번호 변경에 문제가 생겼습니다. 다시 시도해 주세요");
			}
		} else {	// 계정 확인에 실패했을 경우 -> 변경 종료
			System.out.println("회원 확인에 실패했습니다. 다시 시도해 주세요.");
		}		
	}


	private static boolean accountCheck() {
		boolean result = false;
		System.out.println("비밀번호를 입력해 주세요.");
		System.out.print("> ");
		String password = sc.nextLine().trim();

		try {
			sql = "SELECT * FROM CUSTOMER WHERE Username = '" + currentUser.getUsername() + "'";
			ResultSet rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("존재하지 않는 회원입니다. 로그아웃 후 다시 이용해 주세요.");
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				if (rs.next()) {
					String qPassword = rs.getString(3);
					// 비밀번호가 일치하는 경우 -> 계정 정보 갱신 
					if (password.equals(qPassword)) {// currentUser의 계정정보를 갱신함
						String qc_id = rs.getString(1);
						String qUsername = rs.getString(2);						
						String qAddress = rs.getString(4);
						String qTel = rs.getString(5);
						String qSex = rs.getString(6);
						String qBdate = rs.getString(7);
						String qFname = rs.getString(8);
						String qLname = rs.getString(9);
						String qJob = rs.getString(10);
						String qType = rs.getString(11);
						// 추가정보 NULL일 시 "" 문자 출력
						if(qSex != null) currentUser.setSex(qSex);
						else currentUser.setSex("");
						if(qBdate != null) currentUser.setBdate(qBdate);
						else currentUser.setBdate("");
						if(qFname != null) currentUser.setFname(qFname);
						else currentUser.setFname("");
						if(qLname != null) currentUser.setLname(qLname);
						else currentUser.setLname("");
						if(qJob != null) currentUser.setJob(qJob);
						else currentUser.setJob("");
						if(qType != null) currentUser.setType(qType);
						else currentUser.setType("");
						result = true;
					} else { // 비밀번호가 틀림
						System.out.println("잘못된 비밀번호입니다.");
					}
				}
				rs.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.out.println("사용자 정보를 가져오는데 실패했습니다.");
		}
		
		return result;
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
