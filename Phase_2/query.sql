-- Q1
SELECT COUNT(*) Count_item FROM ITEM I WHERE Stock<=20;

-- Q2
SELECT COUNT(*) Count_customer FROM CUSTOMER;

-- Q3
SELECT *
FROM CUSTOMER 
WHERE C_id IN (
    SELECT C_id 
    FROM SHIPPINGORDER 
    GROUP BY C_ID 
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