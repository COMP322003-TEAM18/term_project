package db_term_phase_3;

import java.sql.*;
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
		// TODO 비밀번호 입력 암호화.
		/*Console cons = System.console();
		String password = new String(cons.readPassword());*/

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
				currentUser = null;	// 로그아웃 시 
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
				accountSetting();
			} else if (input.equals("2")) {

			} else if (input.equals("3")) {

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
			System.out.println("메인 메뉴");
			System.out.println(hr);
			System.out.println("1. 계정 관리");
			System.out.println("2. 상품 조회");
			System.out.println("3. 장바구니");
			System.out.println("4. 구매내역 조회");
			System.out.println("0. 로그아웃");
		}
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
			System.err.println("Cannot get a connection: " + ex.getMessage());
			System.exit(1);
		}
	}
}
