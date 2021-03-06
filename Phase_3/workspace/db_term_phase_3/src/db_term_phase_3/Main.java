package db_term_phase_3;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	public static int ATOI(String str) {
		if (str == null || str.length() < 1)
			return 0;

		// trim white spaces
		str = str.trim();

		char flag = '+';

		// check negative or positive
		int i = 0;
		if (str.charAt(0) == '-') {
			flag = '-';
			i++;
		} else if (str.charAt(0) == '+') {
			i++;
		}
		// use double to store result
		double result = 0;

		// calculate value
		while (str.length() > i && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
			result = result * 10 + (str.charAt(i) - '0');
			i++;
		}

		if (flag == '-')
			result = -result;

		// handle max and min
		if (result > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;

		if (result < Integer.MIN_VALUE)
			return Integer.MIN_VALUE;

		return (int) result;
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
			if (id.length() <= 0) {
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
			if (password.length() <= 0) {
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
			} else if (address.length() <= 0) { // 공백을 입력했을 경우
				System.out.println("주소 정보는 반드시 기입해야 합니다. 다시 입력해 주세요.");
			} else { // 주소 정보가 입력됬을 경우
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
					c_id = String.format("C3%08d", ATOI(SELECT_c_id.substring(2)) + 1);
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
			System.err.println("장바구니를 불러올 수 없습니다.");
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
			System.err.println("장바구니를 비울 수 없습니다.");
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
			System.err.println("장바구니에서 상품을 지울 수 없습니다.");
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
			System.err.println("장바구니에서 상품을 구매할 수 없습니다.");
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
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("장바구니를 불러올 수 없습니다.");
		}

		menu.leave();
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
			System.err.println("구매내역을 불러올 수 없습니다.");
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
			sql = "SELECT I.Name, I.Spec, O.Quantity FROM ITEM I, ORDER_LIST O WHERE O.So_id = '" + so_id
					+ "' AND O.I_code = I.Code";
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
			System.err.println("구매내역을 불러올 수 없습니다.");
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
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("구매 상세내역을 불러올 수 없습니다.");
		}

		menu.leave();
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
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("구매내역을 불러올 수 없습니다.");
		}

		menu.leave();
	}

	private static void searchOutOfSold() {
		// 재고 부족 상품 조회
		//
		System.out.println();
		System.out.println(menu.path());
		System.out.println(hr);

		// SELECT 쿼리 실행
		try {
			sql = "SELECT i.Name, i.Spec, i.Stock, i.Min_quantity" + " FROM ITEM i WHERE i.Stock < i.Min_quantity";
			ResultSet rs = stmt.executeQuery(sql);

			rs.last(); // 커서를 맨 뒤로 옮김
			if (rs.getRow() == 0) {
				System.out.println("재고가 부족한 상품이 없습니다.");
				System.out.println("작업을 종료합니다.");
			} else {
				rs.beforeFirst(); // 커서를 맨 앞으로 옮김
				System.out.println(String.format("[ %s   | %s   | %s   | %s   ]", "제품명", "규격", "재고", "최소수량"));
				while (rs.next()) {
					String qName = rs.getString(1);
					String qSpec = rs.getString(2);
					String qStock = rs.getString(3);
					String qMin_quantity = rs.getString(4);
					System.out.println(
							String.format("[ %s   | %s   | %s   | %s   ]", qName, qSpec, qStock, qMin_quantity));
				}
				rs.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.out.println("조회 중에 문제가 발생했습니다. 다시 시도해 주세요");
		}

		menu.leave();
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
				menu.enter("물품 주문");
				searchItem();
			} else if (input.equals("2")) {
				menu.enter("재고 부족 상품 조회");
				searchOutOfSold();
			} else if (input.equals("3")) {
				menu.enter("매출 확인");
				searchSale();
			} else if (input.equals("4")) {
				menu.enter("배송 업체 별 배송 횟수 조회");
				orderCount();
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

	private static void orderCount() {
		try {
			sql = "SELECT SH.Sh_id, SH.Name, COUNT(*) FROM SHIPPINGORDER SO, SHIPPINGCOMPANY SH WHERE SO.Sh_id = SH.Sh_id GROUP BY SO.Sh_id";
			ResultSet rs = stmt.executeQuery(sql);

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.format("%s\t%s\t%s\n", "연번", "배송업체명", "총 배송 횟수");

			int cnt = 1;
			while (rs.next()) {
				String name = rs.getString(2);
				int orderCnt = rs.getInt(3);

				System.out.format("%d\t%s\t%d\n", cnt++, name, orderCnt);
			}

			rs.close();
			conn.commit();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("배송 횟수를 조회할 수 없습니다.");
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
			if (date.length() <= 0) {
				System.out.println("공백을 입력하였습니다. 매출 확인을 종료합니다.");
				menu.leave();
				break;
			}
			boolean date_check = true;
			String fail_reason = "";
			// validation check: 정규식(전화번호)
			if (date.matches("^(전체)$")) { // '전체'일 경우
				getSale("전체", null);
			} else if (date.matches("^(19|20|21)\\d{2}$")) { // YYYY 일 경우
				getSale("YYYY", date);
			} else if (date.matches("^(19|20|21)\\d{2}-(0[1-9]|1[012])$")) { // YYYY-MM 일 경우
				getSale("YYYY-MM", date);
			} else {
				date_check = false;
				fail_reason += "\t올바른 형식이 아닙니다. ( 전체 | YYYY | YYYY-MM )\n";
			}

			if (date_check) { // 생성가능한 생년월일일 경우
				System.out.println();
				System.out.println("매출 확인");
				System.out.println(hr);
				getSale("총 매출", null);
				System.out.println("다음 중 하나를 입력할 수 있습니다 : ( 전체 | YYYY | YYYY-MM )");
				System.out.println("전체 : 연도 별 매출 출력");
				System.out.println("YYYY : 해당 연도 월별 매출 출력");
				System.out.println("YYYY-MM : 해당 연/월 일별 매출 출력");
				System.out.println("공백을 입력할 시 종료합니다.");
			} else { // 생성이 불가능할 경우
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
					+ input_date + "-01-01\") and date(\"" + String.format("%d", ATOI(input_date) + 1)
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
						System.out.println("현재까지 총 매출 : " + String.format("%,d", ATOI(totalSale)));
					} else {
						String qDate = rs.getString(1);
						totalSale = rs.getString(2);
						System.out.printf("%s | %s\n", qDate, String.format("%,d", ATOI(totalSale)));
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
				menu.enter("상품 조회");
				lookItem();
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
		if (accountCheck()) { // 계정 확인에 성공하였을 경우
			System.out.println("회원 확인에 성공했습니다. ");
		} else { // 계정 확인에 실패했을 경우
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
			if (input.equals("1")) { // 주소 수정
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
					} else if (address.length() <= 0) { // 공백을 입력했을 경우
						System.out.println("주소 정보는 반드시 기입해야 합니다. 다시 입력해 주세요.");
					} else { // 주소 정보가 입력됬을 경우
						sql += "Address='" + address + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					}
				}
			} else if (input.equals("2")) { // 전화번호 수정
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
					if (!tel.matches(pattern)) { // 정규식 불일치
						tel_check = false;
						fail_reason += "\t올바른 전화번호 형식이 아닙니다.\n";
					} else {
						tel = tel.replaceAll("-", "");
					}

					if (tel_check) { // 생성가능한 전화번호일 경우
						sql += "Tel='" + tel + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else { // 생성이 불가능할 경우
						System.out.println("생성할 수 없는 전화번호 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("3")) { // 성별 수정
				System.out.println("성별 수정을 선택하였습니다.");
				System.out.println("기존 성별: " + currentUser.getSex());
				System.out.println("변경할 성별을 입력해 주세요. (m/f/M/F)");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				while (true) {
					System.out.print("> ");
					String sex = sc.nextLine().trim();
					if (sex.length() <= 0) {
						sql += "Sex=NULL WHERE Username='" + currentUser.getUsername() + "'";
						break;
					}
					boolean sex_check = true;
					String fail_reason = "";
					String pattern = "^[mMfF남여]$";
					// validation check: 정규식(전화번호)
					if (!sex.matches(pattern)) { // 정규식 불일치
						sex_check = false;
						fail_reason += "\t올바른 성별 형식이 아닙니다. (m/f/M/F)\n";
					}

					if (sex_check) { // 생성가능한 전화번호일 경우
						sex = sex.toUpperCase();
						sql += "Sex='" + sex + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else { // 생성이 불가능할 경우
						System.out.println("생성할 수 없는 성별 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("4")) { // 생년월일 수정
				System.out.println("생년월일 수정을 선택하였습니다.");
				System.out.println("기존 생년월일: " + currentUser.getBdate());
				System.out.println("변경할 생년월일을 입력해 주세요. (YYYY-MM-DD)");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				while (true) {
					System.out.print("> ");
					String bdate = sc.nextLine().trim();
					if (bdate.length() <= 0) {
						sql += "Bdate=NULL WHERE Username='" + currentUser.getUsername() + "'";
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
						sql += "Bdate=" + String.format("STR_TO_DATE('%s', '%%Y-%%m-%%d')", bdate) + " WHERE Username='"
								+ currentUser.getUsername() + "'";
						break;
					} else { // 생성이 불가능할 경우
						System.out.println("생성할 수 없는 생년월일 형식입니다. 다시 입력해 주세요.");
						System.out.printf("\t사유: \n" + fail_reason);
					}
				}
			} else if (input.equals("5")) { // 성 수정
				System.out.println("성 수정을 선택하였습니다.");
				System.out.println("기존 성: " + currentUser.getLname());
				System.out.println("변경할 성을 입력해 주세요. ");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				System.out.print("> ");
				String lname = sc.nextLine().trim();
				lname = lname.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리
				if (lname.length() > 30) { // 성씨 길이 제한 30자를 초과하는 경우 - 30자까지만 입력
					System.out.println("성씨 길이 제한을 초과하여 30자까지만 저장합니다.");
					lname = String.format("%30s", lname);
					System.out.println("\t" + lname);
					sql += "Lname='" + lname + "' WHERE Username='" + currentUser.getUsername() + "'";
				} else if (lname.length() <= 0) {
					sql += "Lname=NULL WHERE Username='" + currentUser.getUsername() + "'";
				} else { // 성공적으로 입력한 경우
					sql += "Lname='" + lname + "' WHERE Username='" + currentUser.getUsername() + "'";
				}
			} else if (input.equals("6")) { // 이름 수정
				System.out.println("이름 수정을 선택하였습니다.");
				System.out.println("기존 이름: " + currentUser.getFname());
				System.out.println("변경할 이름을 입력해 주세요. ");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				System.out.print("> ");
				String fname = sc.nextLine().trim();
				fname = fname.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리
				if (fname.length() > 30) { // 이름 길이 제한 30자를 초과하는 경우 - 30자까지만 입력
					System.out.println("이름 길이 제한을 초과하여 30자까지만 저장합니다.");
					fname = String.format("%30s", fname);
					System.out.println("\t" + fname);
					sql += "Fname='" + fname + "' WHERE Username='" + currentUser.getUsername() + "'";
				} else if (fname.length() <= 0) {
					sql += "Fname=NULL WHERE Username='" + currentUser.getUsername() + "'";
				} else { // 성공적으로 입력한 경우
					sql += "Fname='" + fname + "' WHERE Username='" + currentUser.getUsername() + "'";
				}
			} else if (input.equals("7")) { // 직업 수정
				System.out.println("직업 수정을 선택하였습니다.");
				System.out.println("기존 직업: " + currentUser.getJob());
				System.out.println("변경할 직업을 입력해 주세요. ");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				System.out.print("> ");
				String job = sc.nextLine().trim();
				job = job.replaceAll("\\\'", "\\\\\'"); // ' 문자 입력 시 처리
				if (job.length() > 40) { // 직업 길이 제한 40자를 초과하는 경우 - 40자까지만 입력
					System.out.println("직업 길이 제한을 초과하여 40자까지만 저장합니다.");
					job = String.format("%40s", job);
					System.out.println("\t" + job);
					sql += "Job='" + job + "' WHERE Username='" + currentUser.getUsername() + "'";
				} else if (job.length() <= 0) {
					sql += "Job=NULL WHERE Username='" + currentUser.getUsername() + "'";
				} else {
					sql += "Job='" + job + "' WHERE Username='" + currentUser.getUsername() + "'";
				}
			} else if (input.equals("8")) { // 타입 수정
				System.out.println("타입 수정을 선택하였습니다.");
				System.out.println("기존 타입: " + currentUser.getType());
				System.out.println("변경할 타입을 입력해 주세요. (소매/도매/기타)");
				System.out.println("값을 지우려면 공백을 입력하세요.");
				while (true) {
					System.out.print("> ");
					String type = sc.nextLine().trim();
					if (type.length() <= 0) {
						sql += "Type=NULL WHERE Username='" + currentUser.getUsername() + "'";
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
						sql += "Type='" + type + "' WHERE Username='" + currentUser.getUsername() + "'";
						break;
					} else { // 생성이 불가능할 경우
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
				if (res <= 0)
					System.out.println("변경 중에 문제가 생겼습니다. 다시 시도해 주세요.");
				else
					System.out.println("변경이 완료되었습니다.");
				conn.commit();
			} catch (SQLException ex) {
				System.err.println("sql error = " + ex.getMessage());
				System.out.println("회원 정보를 업데이트 하는데 문제가 발생했습니다. 다시 시도해주세요.");
			}
			// CurrentUser 정보 갱신
			getCurrentUserInfo();
			// currentUser == null -> 로그아웃이 강제되는 상황일 경우
			if (currentUser == null) {
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
					currentUser.setC_id(qc_id);
					currentUser.setAddress(qAddress);
					currentUser.setTel(qTel);
					// 추가정보 NULL일 시 "" 문자 출력
					if (qSex != null)
						currentUser.setSex(qSex);
					else
						currentUser.setSex("");
					if (qBdate != null)
						currentUser.setBdate(qBdate);
					else
						currentUser.setBdate("");
					if (qFname != null)
						currentUser.setFname(qFname);
					else
						currentUser.setFname("");
					if (qLname != null)
						currentUser.setLname(qLname);
					else
						currentUser.setLname("");
					if (qJob != null)
						currentUser.setJob(qJob);
					else
						currentUser.setJob("");
					if (qType != null)
						currentUser.setType(qType);
					else
						currentUser.setType("");
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
		if (accountCheck()) { // 계정 확인에 성공하였을 경우 -> 변경 계속 진행
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
				if (password.length() <= 0) {
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
				if (res <= 0)
					System.out.println("변경 중에 문제가 생겼습니다. 다시 시도해 주세요.");
				else {
					System.out.println("변경이 완료되었습니다. 다시 로그인해 주세요");
					currentUser = null;
				}
				conn.commit();
			} catch (SQLException ex) {
				System.err.println("sql error = " + ex.getMessage());
				System.out.println("비밀번호 변경에 문제가 생겼습니다. 다시 시도해 주세요");
			}
		} else { // 계정 확인에 실패했을 경우 -> 변경 종료
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
						currentUser.setC_id(qc_id);
						currentUser.setAddress(qAddress);
						currentUser.setTel(qTel);
						// 추가정보 NULL일 시 "" 문자 출력
						if (qSex != null)
							currentUser.setSex(qSex);
						else
							currentUser.setSex("");
						if (qBdate != null)
							currentUser.setBdate(qBdate);
						else
							currentUser.setBdate("");
						if (qFname != null)
							currentUser.setFname(qFname);
						else
							currentUser.setFname("");
						if (qLname != null)
							currentUser.setLname(qLname);
						else
							currentUser.setLname("");
						if (qJob != null)
							currentUser.setJob(qJob);
						else
							currentUser.setJob("");
						if (qType != null)
							currentUser.setType(qType);
						else
							currentUser.setType("");
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

	private static void lookItem() {
		System.out.println();
		System.out.println(menu.path());
		System.out.println(hr);
		System.out.println("1. 카테고리 별 조회");
		System.out.println("2. 검색");
		System.out.println("0. 돌아가기");

		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.equals("0")) {
				break;
			} else if (input.equals("1")) {
				menu.enter("카테고리 별 조회");
				showItemListByCategory();
			} else if (input.equals("2")) {
				menu.enter("검색");
				searchItem();
			} else {
				System.out.println("잘못된 입력입니다.");
			}

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.println("1. 카테고리 별 조회");
			System.out.println("2. 검색");
			System.out.println("0. 돌아가기");
		}

		menu.leave();
	}

	private static void showItemListByCategory() {
		// 카테고리 별 상품 조회
		// 대분류 -> 중분류 -> 소분류 순서대로 제시 후 선택
		// 소분류 선택 시 showItemList() 메소드 호출
		// 상품 선택 시 showItemInfo() 메소드 호출
		System.out.println();
		System.out.println(menu.path());
		System.out.println(hr);
		System.out.println("카테고리 별 상품 조회를 진행합니다. 항목의 번호를 입력해주세요");
		System.out.println("0번 입력 시 이전 항목으로 돌아갑니다.");
		ArrayList<String> selected_category_id = new ArrayList<String>();
		ArrayList<String> selected_category_name = new ArrayList<String>();
		ArrayList<String> current_id_list = new ArrayList<String>();
		ArrayList<String> current_name_list = new ArrayList<String>();

		// 소분류까지 모두 선택 시 ShowItemList() 호출
		while (selected_category_id.size() < 3) {
			int cnt = 0;
			System.out.println();
			System.out.print("현재 선택된 카테고리: ");
			for (int i = 0; i < selected_category_id.size(); i++) {
				System.out.print(selected_category_name.get(i) + " >> ");
			}
			System.out.println();
			current_id_list.clear();
			current_name_list.clear();
			sql = "SELECT * FROM ";
			// 대/중/소분류 선택에 따른 sql문 변경
			switch (selected_category_id.size()) {
			case 0: // 대분류 선택
				sql += "L_CATEGORY lc";
				System.out.println("[대분류를 선택해주세요]");
				break;
			case 1: // 중분류 선택
				sql += "M_CATEGORY mc WHERE mc.Lc_id='" + selected_category_id.get(0) + "'";
				System.out.println("[중분류를 선택해주세요]");
				break;
			case 2: // 소분류 선택
				sql += "S_CATEGORY sc WHERE sc.Mc_id='" + selected_category_id.get(1) + "'";
				System.out.println("[소분류를 선택해주세요]");
				break;
			}
			// SELECT Query 진행
			try {
				ResultSet rs = stmt.executeQuery(sql);
				rs.last(); // 커서를 맨 뒤로 옮김
				if (rs.getRow() == 0) { // 정상적인 상황에서 수행되지 않음.
					System.out.println("해당 카테고리에 하위 항목이 없습니다.");
				} else {
					rs.beforeFirst(); // 커서를 맨 앞으로 옮김
					while (rs.next()) {
						cnt++;
						String qC_id = rs.getString(1);
						String qName = rs.getString(2);
						current_id_list.add(qC_id);
						current_name_list.add(qName);
						System.out.printf("%2d. %s\n", cnt, qName);
					}
					System.out.printf("%2d. %s\n", 0, "돌아가기");
					rs.close();
					conn.commit();
				}
			} catch (SQLException ex) {
				System.err.println("sql error = " + ex.getMessage());
				System.out.println("카테고리 검색을 진행하는데 문제가 발생했습니다. 나중에 다시 시도해 주세요");
				menu.leave();
				return;
			}
			String input;
			// 사용자로부터 입력 받음
			while (true) {
				System.out.print("> ");
				input = sc.nextLine().trim();
				boolean input_check = true;
				String fail_reason = "";
				String pattern = "^[0-9]*$";
				// validation check: 정규식(숫자)
				if (!input.matches(pattern)) { // 정규식 불일치
					input_check = false;
					fail_reason += "\t번호를 입력해 주세요.\n";
				} else { // 정규식이 일치하는 경우
					// 나열된 번호 내에서 입력하지 않은 경우
					if (ATOI(input) > current_id_list.size() || ATOI(input) < 0) {
						input_check = false;
						fail_reason += "\t나열된 항목 내에서 번호를 선택해 주세요.\n";
					}
				}

				if (input_check) { // 선택이 가능한 경우
					break;
				} else { // 선택이 불가능한 경우
					System.out.println("잘못된 입력입니다. 다시 입력해 주세요.");
					System.out.printf("\t사유: \n" + fail_reason);
				}
			}

			if (input.equals("0")) {
				if (selected_category_id.size() > 0) { // 이전 카테고리 검색으로 돌아가는 경우
					System.out.println("이전으로 돌아갑니다.");
					selected_category_id.remove(selected_category_id.size() - 1);
					selected_category_name.remove(selected_category_name.size() - 1);
				} else { // 카테고리 별 검색을 종료하는 경우
					System.out.println("카테고리 별 검색을 취소합니다.");
					menu.leave();
					return;
				}
			} else {
				System.out.println(current_name_list.get(ATOI(input) - 1) + "를(을) 선택했습니다.");
				selected_category_id.add(current_id_list.get(ATOI(input) - 1));
				selected_category_name.add(current_name_list.get(ATOI(input) - 1));
			}
		}

		sql = "SELECT * FROM ITEM WHERE Sc_id='" + selected_category_id.get(2) + "'";
		ResultSet rs;
		// 소분류로 아이템 검색 -> 이후 showItemList 진행
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.out.println("카테고리 검색을 진행하는데 문제가 발생했습니다. 나중에 다시 시도해 주세요");
			menu.leave();
			return;
		}
		showItemList(rs);
		String input;
		// 사용자로부터 입력 받음
		while (true) {
			System.out.println();
			System.out.println("상세히 보고 싶은 상품의 연번을 입력해주세요. 미입력 시 이전 메뉴로 돌아갑니다.");
			System.out.print("> ");
			input = sc.nextLine().trim();
			if (input.length() <= 0) {
				System.out.println("이전 메뉴로 돌아갑니다.");
				break;
			}
			boolean input_check = true;
			String fail_reason = "";
			String pattern = "^[0-9]*$";
			// validation check: 정규식(숫자)
			if (!input.matches(pattern)) { // 정규식 불일치
				input_check = false;
				fail_reason += "\t번호를 입력해 주세요.\n";
			} else { // 정규식이 일치하는 경우
				// 나열된 번호 내에서 입력하지 않은 경우
				try {
					rs.last();
					if (ATOI(input) > rs.getRow() || ATOI(input) <= 0) {
						input_check = false;
						fail_reason += "\t나열된 항목 내에서 번호를 선택해 주세요.\n";
					}
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("검색 중 문제가 발생했습니다. 나중에 다시 시도해 주세요");
					menu.leave();
					return;
				}
			}

			if (input_check) { // 선택이 가능한 경우
				try {
					rs.beforeFirst();
					for (int i = 0; i < ATOI(input); i++) {
						rs.next();
					}

					String code = rs.getString(1);
					menu.enter("상품 상세정보");
					showItemInfo(code);
					showItemList(rs);
				} catch (SQLException e) {
					System.out.println("검색 중 문제가 발생했습니다. 나중에 다시 시도해 주세요");
					menu.leave();
					return;
				}
			} else { // 선택이 불가능한 경우
				System.out.println("잘못된 입력입니다. 다시 입력해 주세요.");
				System.out.printf("\t사유: \n" + fail_reason);
			}
		}

		menu.leave();
	}

	private static void searchItem() {
		System.out.println();
		System.out.println(menu.path());
		System.out.println(hr);
		System.out.println("검색할 상품의 이름을 입력해주세요. 미입력 시 이전 메뉴로 돌아갑니다.");

		while (true) {
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (input.isEmpty()) {
				break;
			} else {
				menu.enter("검색 결과");
				showItemListByName(input);
			}

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.println("검색할 상품의 이름을 입력해주세요. 미입력 시 이전 메뉴로 돌아갑니다.");
		}

		menu.leave();
	}

	private static void showItemListByName(String itemName) {
		try {
			sql = "SELECT * FROM ITEM WHERE Name LIKE '%" + itemName + "%' ORDER BY Code ASC";
			ResultSet rs = stmt.executeQuery(sql);

			rs.last();
			if (rs.getRow() == 0) {
				System.out.println();
				System.out.println("검색 결과가 없습니다.");
			} else {
				showItemList(rs);
				System.out.println();
				System.out.println("상세히 보고 싶은 상품의 연번을 입력해주세요. 미입력 시 이전 메뉴로 돌아갑니다.");
				while (true) {
					System.out.print("> ");
					String input = sc.nextLine().trim();
					rs.last();
					if (input.isEmpty()) {
						break;
					} else if (ATOI(input) > rs.getRow() || ATOI(input) <= 0) {
						System.out.println("잘못된 입력입니다.");
					} else {
						rs.beforeFirst();

						for (int i = 0; i < ATOI(input); i++) {
							rs.next();
						}

						String code = rs.getString(1);
						menu.enter("상품 상세정보");

						if (currentUser.getC_id().charAt(0) == 'A') { // 관리자 계정
							showItemInfoAdmin(code);
						} else { // 일반 사용자
							showItemInfo(code);
						}

						showItemList(rs);
						System.out.println();
						System.out.println("상세히 보고 싶은 상품의 연번을 입력해주세요. 미입력 시 이전 메뉴로 돌아갑니다.");
					}
				}
			}

			rs.close();
			conn.commit();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("상품을 검색할 수 없습니다.");
		}

		menu.leave();
	}

	private static void showItemInfoAdmin(String code) {
		Statement stmt2 = null;

		try {
			stmt2 = conn.createStatement();
			sql = "SELECT * FROM ITEM WHERE Code = '" + code + "'";
			ResultSet itrs = stmt2.executeQuery(sql);

			itrs.next();
			String name = itrs.getString(2);
			String spec = itrs.getString(3);
			int quantity = itrs.getInt(4);
			String unit = itrs.getString(5);
			int stock = itrs.getInt(6);
			int price = itrs.getInt(7);
			int min_quantity = itrs.getInt(8);

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.println("상품 코드: " + code);
			System.out.println("품명: " + name);
			System.out.println("규격: " + spec);
			System.out.println("판매 단위: " + quantity + unit);
			System.out.println("단가: " + price);
			System.out.println("최소 주문 수량: " + min_quantity);
			System.out.println("재고 수량: " + stock);
			System.out.println("가격: " + quantity * price);

			System.out.println();
			System.out.println("1. 물품 주문");
			System.out.println("0. 돌아가기");

			while (true) {
				System.out.print("> ");
				String input = sc.nextLine().trim();

				if (input.equals("0")) {
					break;
				} else if (input.equals("1")) {
					increaseItem(itrs);
					break;
				} else {
					System.out.println("잘못된 입력입니다.");
				}
			}

			stmt2.close();
			conn.commit();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("상품 상세 정보를 불러올 수 없습니다.");
		}

		menu.leave();
	}

	private static void increaseItem(ResultSet rs) {
		Statement stmt2 = null;

		try {
			String code = rs.getString(1);
			int quantity = rs.getInt(4);

			System.out.println();
			System.out.println("추가로 주문할 수량을 입력해 주세요.");
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (ATOI(input) <= 0) {
				System.out.println("잘못된 입력입니다.");
			} else {
				stmt2 = conn.createStatement();
				sql = "UPDATE ITEM SET Stock = Stock + " + ATOI(input) * quantity + " WHERE Code = '" + code + "'";
				stmt2.executeUpdate(sql);

				System.out.println();
				System.out.println("상품이 추가로 주문되었습니다.");

				stmt2.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("상품을 주문할 수 없습니다.");
		}
	}

	private static void showItemList(ResultSet rs) {
		try {
			rs.beforeFirst();
			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.format("%s\t%s\t%s\n", "연번", "품명", "규격");

			int cnt = 1;
			while (rs.next()) {
				String name = rs.getString(2);
				String spec = rs.getString(3);

				System.out.format("%d\t%s\t%s\n", cnt++, name, spec);
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("상품 목록을 불러올 수 없습니다.");
		}
	}

	private static void showItemInfo(String code) {
		Statement stmt2 = null;

		try {
			stmt2 = conn.createStatement();
			sql = "SELECT * FROM ITEM WHERE Code = '" + code + "'";
			ResultSet itrs = stmt2.executeQuery(sql);

			itrs.next();
			String name = itrs.getString(2);
			String spec = itrs.getString(3);
			int quantity = itrs.getInt(4);
			String unit = itrs.getString(5);
			int stock = itrs.getInt(6);
			int price = itrs.getInt(7);
			int min_quantity = itrs.getInt(8);

			System.out.println();
			System.out.println(menu.path());
			System.out.println(hr);
			System.out.println("품명: " + name);
			System.out.println("규격: " + spec);
			System.out.println("판매 단위: " + quantity + unit);
			System.out.println("단가: " + price);
			System.out.println("최소 주문 수량: " + min_quantity);
			System.out.println("재고 수량: " + stock);
			System.out.println("가격: " + quantity * price);

			System.out.println();
			System.out.println("1. 장바구니에 추가");
			System.out.println("0. 돌아가기");

			while (true) {
				System.out.print("> ");
				String input = sc.nextLine().trim();

				if (input.equals("0")) {
					break;
				} else if (input.equals("1")) {
					addToShoppingBag(itrs);
					break;
				} else {
					System.out.println("잘못된 입력입니다.");
				}
			}

			stmt2.close();
			conn.commit();
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("상품 상세 정보를 불러올 수 없습니다.");
		}

		menu.leave();
	}

	private static void addToShoppingBag(ResultSet rs) {
		Statement stmt2 = null;

		try {
			String code = rs.getString(1);
			int quantity = rs.getInt(4);
			int min_quantity = rs.getInt(8);

			System.out.println();
			System.out.println("장바구니에 추가할 수량을 입력해 주세요.");
			System.out.print("> ");
			String input = sc.nextLine().trim();

			if (ATOI(input) <= 0) {
				System.out.println("잘못된 입력입니다.");
			} else if (ATOI(input) * quantity < min_quantity) {
				System.out.println("주문 수량이 최소 주문 수량에 미달합니다.");
			} else {
				// 우선 사용자의 장바구니에 해당 상품이 있는지를 검색
				sql = "SELECT * FROM SHOPPINGBAG WHERE C_id = '" + currentUser.getC_id() + "' AND I_code = '" + code
						+ "'";
				stmt2 = conn.createStatement();
				ResultSet brs = stmt2.executeQuery(sql);

				brs.last();
				if (brs.getRow() == 0) { // 장바구니에 해당 상품이 없는 경우
					sql = "INSERT INTO SHOPPINGBAG VALUES ('" + currentUser.getC_id() + "', '" + code + "', "
							+ ATOI(input) * quantity + ")";
				} else { // 장바구니에 해당 상품이 있는 경우
					sql = "UPDATE SHOPPINGBAG SET Quantity = Quantity + " + ATOI(input) * quantity + " WHERE I_code = '"
							+ code + "'";
				}

				stmt2.executeUpdate(sql);

				System.out.println();
				System.out.println("장바구니에 상품이 담겼습니다.");

				brs.close();
				stmt2.close();
				conn.commit();
			}
		} catch (SQLException ex) {
			System.err.println("sql error = " + ex.getMessage());
			System.err.println("상품을 장바구니에 추가할 수 없습니다.");
		}
	}

	public static void main(String[] args) {
		LOG = Logger.getGlobal();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// LOG.info("JDBC Driver founded.");
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
