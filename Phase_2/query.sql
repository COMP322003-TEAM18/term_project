-- Q1
SELECT COUNT(*) Count_item FROM ITEM I WHERE Stock<=20;

-- Q2
SELECT COUNT(*) Count_customer FROM CUSTOMER;

-- Q3
SELECT C_id, Lname, Fname
FROM CUSTOMER
WHERE C_id IN (
    SELECT C_id 
    FROM SHIPPINGORDER 
    GROUP BY C_id
    HAVING COUNT(*) >= 7
    );

-- Q4
SELECT b.Name AS Brand_name, s.Name AS Supplier_name, topbrand.Count_sold
FROM SUPPLIER s, BRAND b
INNER JOIN (
    SELECT i.B_id, COUNT(i.B_id) AS Count_sold
    FROM ORDER_LIST ol, ITEM i
    WHERE ol.So_id IN (
        SELECT So_id 
        FROM SHIPPINGORDER 
        WHERE Otime > DATE_SUB(now(), INTERVAL 6 MONTH)
        ) 
    AND ol.I_code = i.Code 
    GROUP BY i.B_id 
    ORDER BY Count_sold DESC LIMIT 3
) AS topbrand
ON b.B_id = topbrand.B_id
WHERE b.S_id = s.S_id
ORDER BY Count_sold DESC;

-- Q6
-- Top 5 매출을 낸 사람을 찾는 경우:
SELECT c.C_id, c.Fname, c.Lname, SUM(i.Price * ol.Quantity) AS sum_amount
FROM SHIPPINGORDER so, ORDER_LIST ol, CUSTOMER c, ITEM i
WHERE ol.So_id IN (
    -- 그 고객이 주문한 과거 SHIPPINGORDER들
    SELECT So_id
    FROM SHIPPINGORDER
    WHERE C_id IN (
        -- 장바구니에 상품이 하나도 없는 고객
        SELECT C_id
        FROM CUSTOMER
        WHERE C_id NOT IN (
            SELECT C_id
            FROM SHOPPINGBAG
            GROUP BY C_id
        )
    )
)
AND ol.So_id = so.So_id
AND so.C_id = c.C_id
AND ol.I_code = i.Code
GROUP BY c.C_id
ORDER BY sum_amount DESC
LIMIT 5;

-- Top 5 매출인 제품을 찾는 경우:
SELECT i.Code, i.Name, i.Spec, SUM(i.Price * ol.Quantity) AS sum_amount
FROM ITEM i, ORDER_LIST ol
WHERE ol.So_id IN (
    -- 그 고객이 주문한 과거 SHIPPINGORDER들
    SELECT So_id
    FROM SHIPPINGORDER
    WHERE C_id IN (
        -- 장바구니에 상품이 하나도 없는 고객
        SELECT C_id
        FROM CUSTOMER
        WHERE C_id NOT IN (
            SELECT C_id
            FROM SHOPPINGBAG
            GROUP BY C_id
        )
    )
)
AND i.Code = ol.I_code
GROUP BY i.Code
ORDER BY sum_amount DESC
LIMIT 5;

-- Q8
-- 그 중분류와, 그 중분류의 제품을 주문한 CUSTOMER들의 타입중 가장 많은 것
SELECT m_c.Mc_id, m_c.Name, c.Type, COUNT(c.Type) AS type_cnt
FROM ITEM i, S_CATEGORY s_c, ORDER_LIST ol, SHIPPINGORDER so, CUSTOMER c, M_CATEGORY m_c
INNER JOIN (
    -- 그 배송업체가 가장 많이 배송한 중분류
    SELECT m_c.Mc_id, m_c.Name, COUNT(m_c.Mc_id) AS mc_cnt
    FROM S_CATEGORY s_c, M_CATEGORY m_c, SHIPPINGORDER so, ITEM i, ORDER_LIST ol, SHIPPINGCOMPANY sh
    INNER JOIN (
        -- 가장 많은 배송을 진행한 배송업체
        SELECT Sh_id, COUNT(*) AS sh_cnt
        FROM SHIPPINGORDER
        GROUP BY Sh_id
        ORDER BY sh_cnt DESC
        LIMIT 1
    ) AS topsh
    ON sh.Sh_id = topsh.Sh_id
    WHERE ol.So_id = so.So_id
    AND ol.I_code = i.Code
    AND i.Sc_id = s_c.Sc_id
    AND s_c.Mc_id = m_c.Mc_id
    GROUP BY m_c.Mc_id
    ORDER BY mc_cnt DESC
    LIMIT 1
) AS max_mc
ON max_mc.Mc_id = m_c.Mc_id
WHERE ol.I_code = i.Code
AND i.Sc_id = s_c.Sc_id
AND m_c.Mc_id = s_c.Mc_id
AND so.So_id = ol.So_id
AND c.C_id = so.C_id
GROUP BY m_c.Mc_id, c.Type
ORDER BY type_cnt DESC
LIMIT 1;

-- Q10
-- 그걸 장바구니에 담고 있는 고객
SELECT c.C_id, c.Lname, c.Fname
FROM CUSTOMER c, SHOPPINGBAG sb, ITEM i
INNER JOIN (
    -- 현재까지 가장 높은 매출을 낸 제품
    SELECT i.Code, SUM(i.Price * ol.Quantity) AS sum_amount
    FROM ITEM i, ORDER_LIST ol
    WHERE ol.I_code = i.Code
    GROUP BY i.Code
    ORDER BY sum_amount DESC
    LIMIT 1
) AS max_it
ON max_it.Code = i.Code
WHERE sb.C_id = c.C_id
AND sb.I_code = i.Code
GROUP BY c.C_id
ORDER BY c.C_id ASC;

-- 그걸 장바구니에 담고 있는 고객
SELECT c.C_id, c.Lname, c.Fname
FROM CUSTOMER c, SHOPPINGBAG sb, ITEM i
INNER JOIN (
    -- 그것과 같은 카테고리의 제품
    SELECT i.Code
    FROM S_CATEGORY s_c, ITEM i
    WHERE s_c.Sc_id IN (
        SELECT max_it.Sc_id
        FROM (
            -- 현재까지 가장 높은 매출을 낸 제품
            SELECT i.Code, i.Sc_id, SUM(i.Price * ol.Quantity) AS sum_amount
            FROM ITEM i, ORDER_LIST ol
            WHERE ol.I_code = i.Code
            GROUP BY i.Code
            ORDER BY sum_amount DESC
            LIMIT 1
        ) AS max_it
    )
    AND i.Sc_id = s_c.Sc_id
    ORDER BY i.Code ASC
) AS max_it
ON max_it.Code = i.Code
WHERE sb.C_id = c.C_id
AND sb.I_code = i.Code
GROUP BY c.C_id
ORDER BY c.C_id ASC;