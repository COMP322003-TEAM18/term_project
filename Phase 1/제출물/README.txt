※가급적이면 함께 첨부된 README.docx Word 문서를 읽어주세요
Database Term Project Phase 1

18팀 - 박정현(2013105047), 유창재(2013105063)


<<Entity 설명>>
기존에 제시된 Entity
    1. ITEM: 쇼핑몰 ‘X’에 등록될 제품의 정보

    2. BRAND: ITEM의 브랜드 정보

    3. CATEGORY: 
    ITEM의 분류 정보. 대/중/소분류 간의 포함관계를 표현하기 위해 각각
    L_CATEGORY, M_CATEGORY, S_CATEGORY 
    라는 3개의 엔티티로 나누어 생성함.

    4. SUPPLIER: 공급업체의 정보

    5. SHIPPINGCOMPANY: 배송업체의 정보

    6. CUSTOMER: 고객의 정보

    7. SHIPPINGBAG: 장바구니의 정보

추가로 생성한 Entity
    1. SHIPPINGORDER: 주문에 대한 정보를 가짐. 

<<Relation 설명>>
    1. MADE_BY: between ITEM(T) and BRAND(T)
    ITEM은 BRAND에서 만들어짐.

    2. SUPPLIED_BY: between BRAND(T) and SUPPLIER(T)
    BRAND 제품을 SUPPLIER가 공급함.

    3. BELONGS_TO: between ITEM(T) and S_CATEGORY(T)
    ITEM은 S_CATEGORY에 속함.

    4. BELONGS_TO: between S_CATEGORY(T) and M_CATEGORY(T)
    S_CATEGORY는 M_CATEGORY에 속함.

    5. BELONGS_TO: between M_CATEGORY(T) and L_CATEGORY(T)
    M_CATEGORY는 L_CATEGORY에 속함.

    6. INCLUDED_IN: between ITEM(P) and SHIPPINGORDER(T)
    ITEM은 SHIPPINGORDER에 포함됨, 
    주문한 ITEM의 Code와 수량을 { (I_code, Quantity) , (I_code, Quantity) , ...} 의 형태로 가짐.

    7. INCLUDED_IN: between ITEM(P) and SHOPPINGBAG(T)
    ITEM은 SHOPPINGBAG에 포함됨, 
    장바구니에 담은 ITEM의 Code와 수량을 { (I_code, Quantity) , (I_code, Quantity) , ...} 의 형태로 가짐.

    8. ASSIGNED_TO: between SHOPPINGBAG(T) and CUSTOMER(T)
    SHOPPINGBAG은 CUSTOMER에게 할당됨.

    9. ORDERS: between CUSTOMER(P) and SHIPPINGORDER(T)
    CUSTOMER는 SHIPPINGORDER를 주문함.

    10. HANDLED_BY: between SHIPPINGORDER(T) and SHIPPINGCOMPANY(P)
    SHIPPINGORDER는 SHIPPINGCOMPANY에서 처리함.

*(T): Total Participation, (P): Partial Participation

<<Attribute 설명>>
    1. ITEM
        a. Code  (Primary Key): 제품의 고유 코드. 
        b. Name: 제품명
        c. Spec: 제품 규격
        d. Quantity: 출고 수량(묶여져 있는 제품 수)
        e. Unit: 단위
        f. Stock: 재고 수량
        g. Price: 단가
        h. Min_quantity: 최소 주문 수량

    2. SUPPLIER
        a. S_id  (Primary Key): 공급업체 ID
        b. Name: 공급업체명

    3. BRAND
        a. B_id  (Primary Key): 브랜드 ID
        b. Name: 브랜드명

    4. L_CATEGORY
        a. Lc_id  (Primary Key): 대분류 ID
        b. Name: 대분류명

    5. M_CATEGORY
        a. Mc_id  (Primary Key): 중분류 ID
        b. Name: 중분류명

    6. S_CATEGORY
        a. Sc_id  (Primary Key): 소분류 ID
        b. Name: 소분류명

    7. CUSTOMER
        a. C_id  (Primary Key): 고객 ID
        b. Username: 아이디
        c. Password: 비밀번호
        d. Address: 주소
        e. Tel: 전화번호
        f. Sex: 성별
        g. Age: 나이
        h. Name (Firstname, Lastname): 이름 (Composite Attribute)
        i. Job: 직업
        j. Type: 타입 (소매업, 도매업, 기타)

    8. SHIPPINGCOMPANY
        a. Sh_id  (Primary Key): 배송업체 ID
        b. Name: 배송업체명
        c. Tel: 배송업체 연락처
        d. Region: 업체 별 담당 지역 (수도권, 영남권 등)

    9. SHIPPINGORDER
        a. So_id  (Primary Key): 배송 및 주문 ID
        b. Address: 배송지 주소
        c. Receiver (Name, Tel): 수취인의 (이름, 연락처)  (Composite Attribute)
        d. Time: 주문 시간
