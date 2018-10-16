�ذ������̸� �Բ� ÷�ε� README.docx Word ������ �о��ּ���
Database Term Project Phase 1

18�� - ������(2013105047), ��â��(2013105063)


<<Entity ����>>
������ ���õ� Entity
    1. ITEM: ���θ� ��X���� ��ϵ� ��ǰ�� ����

    2. BRAND: ITEM�� �귣�� ����

    3. CATEGORY: 
    ITEM�� �з� ����. ��/��/�Һз� ���� ���԰��踦 ǥ���ϱ� ���� ����
    L_CATEGORY, M_CATEGORY, S_CATEGORY 
    ��� 3���� ��ƼƼ�� ������ ������.

    4. SUPPLIER: ���޾�ü�� ����

    5. SHIPPINGCOMPANY: ��۾�ü�� ����

    6. CUSTOMER: ���� ����

    7. SHIPPINGBAG: ��ٱ����� ����

�߰��� ������ Entity
    1. SHIPPINGORDER: �ֹ��� ���� ������ ����. 

<<Relation ����>>
    1. MADE_BY: between ITEM(T) and BRAND(T)
    ITEM�� BRAND���� �������.

    2. SUPPLIED_BY: between BRAND(T) and SUPPLIER(T)
    BRAND ��ǰ�� SUPPLIER�� ������.

    3. BELONGS_TO: between ITEM(T) and S_CATEGORY(T)
    ITEM�� S_CATEGORY�� ����.

    4. BELONGS_TO: between S_CATEGORY(T) and M_CATEGORY(T)
    S_CATEGORY�� M_CATEGORY�� ����.

    5. BELONGS_TO: between M_CATEGORY(T) and L_CATEGORY(T)
    M_CATEGORY�� L_CATEGORY�� ����.

    6. INCLUDED_IN: between ITEM(P) and SHIPPINGORDER(T)
    ITEM�� SHIPPINGORDER�� ���Ե�, 
    �ֹ��� ITEM�� Code�� ������ { (I_code, Quantity) , (I_code, Quantity) , ...} �� ���·� ����.

    7. INCLUDED_IN: between ITEM(P) and SHOPPINGBAG(T)
    ITEM�� SHOPPINGBAG�� ���Ե�, 
    ��ٱ��Ͽ� ���� ITEM�� Code�� ������ { (I_code, Quantity) , (I_code, Quantity) , ...} �� ���·� ����.

    8. ASSIGNED_TO: between SHOPPINGBAG(T) and CUSTOMER(T)
    SHOPPINGBAG�� CUSTOMER���� �Ҵ��.

    9. ORDERS: between CUSTOMER(P) and SHIPPINGORDER(T)
    CUSTOMER�� SHIPPINGORDER�� �ֹ���.

    10. HANDLED_BY: between SHIPPINGORDER(T) and SHIPPINGCOMPANY(P)
    SHIPPINGORDER�� SHIPPINGCOMPANY���� ó����.

*(T): Total Participation, (P): Partial Participation

<<Attribute ����>>
    1. ITEM
        a. Code  (Primary Key): ��ǰ�� ���� �ڵ�. 
        b. Name: ��ǰ��
        c. Spec: ��ǰ �԰�
        d. Quantity: ��� ����(������ �ִ� ��ǰ ��)
        e. Unit: ����
        f. Stock: ��� ����
        g. Price: �ܰ�
        h. Min_quantity: �ּ� �ֹ� ����

    2. SUPPLIER
        a. S_id  (Primary Key): ���޾�ü ID
        b. Name: ���޾�ü��

    3. BRAND
        a. B_id  (Primary Key): �귣�� ID
        b. Name: �귣���

    4. L_CATEGORY
        a. Lc_id  (Primary Key): ��з� ID
        b. Name: ��з���

    5. M_CATEGORY
        a. Mc_id  (Primary Key): �ߺз� ID
        b. Name: �ߺз���

    6. S_CATEGORY
        a. Sc_id  (Primary Key): �Һз� ID
        b. Name: �Һз���

    7. CUSTOMER
        a. C_id  (Primary Key): �� ID
        b. Username: ���̵�
        c. Password: ��й�ȣ
        d. Address: �ּ�
        e. Tel: ��ȭ��ȣ
        f. Sex: ����
        g. Age: ����
        h. Name (Firstname, Lastname): �̸� (Composite Attribute)
        i. Job: ����
        j. Type: Ÿ�� (�Ҹž�, ���ž�, ��Ÿ)

    8. SHIPPINGCOMPANY
        a. Sh_id  (Primary Key): ��۾�ü ID
        b. Name: ��۾�ü��
        c. Tel: ��۾�ü ����ó
        d. Region: ��ü �� ��� ���� (������, ������ ��)

    9. SHIPPINGORDER
        a. So_id  (Primary Key): ��� �� �ֹ� ID
        b. Address: ����� �ּ�
        c. Receiver (Name, Tel): �������� (�̸�, ����ó)  (Composite Attribute)
        d. Time: �ֹ� �ð�
