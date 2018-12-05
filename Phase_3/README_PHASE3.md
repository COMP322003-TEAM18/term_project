# Phase 3 README


## 1. 선행 작업
    requirement.sql - Admin 계정 추가 및 Index 생성

    ※Phase_2/제출물/Team18-P2.sql - Phase_2에서 제출한 .sql 파일


### Secondary Index 생성
Index를 생성할 필드와 해당 Index가 자주 사용될 것으로 예상되는 method

    1. CUSTOMER
        + Username 
            + loginScreen()
            + getCurrentUserInfo()
            + accountCheck()
    2. ITEM
       + Sc_id
            + showItemListByCategory()
       + Name
            + showItemListByName()
    3. SHIPPINGORDER
       + C_id
            + showOrderLog()
       + Otime
            + getSale()
    4. M_CATEGORY
       + Lc_id
            + showItemListByCategory()
    5. S_CATEGORY
       + Mc_id
            + showItemListByCategory()

## 2. Issue 목록
쇼핑몰 필수 기능들을 Issue로 나누어 분담하여 개발을 진행했음

### 공통
    1. 로그인->메인메뉴 띄우기(일반, 관리자), 종료->User Class 수정, 로그아웃 및 UTF-8 Encoding 설정 추가


### 일반 회원 관련
    2. 회원가입(유창재)
    3. 계정 관리(유창재)
    4. 장바구니(박정현)
    5. 구매 내역 조회(박정현)
    6. 상품 조회
        6-1. 카테고리 별 조회(유창재)
        6-2. 검색(박정현)


### 관리자 회원 관련
    7. 물품 주문(박정현)
    8. 재고 부족 상품 조회(유창재)
    9. 매출 확인(유창재)
    10. 배송횟수 조회(박정현)


### 기타
    11. 현재 메뉴 위치 표시