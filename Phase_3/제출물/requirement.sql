-- 관리자 계정 추가
INSERT INTO CUSTOMER VALUES ('A000000001', 'admin', 'admin', '대구광역시 북구 산격3동 1370번지 경북대학교(대구캠퍼스) 공대9호관', '01041971483', NULL, NULL, NULL, NULL, NULL, NULL);

-- Secondary Index 생성
CREATE INDEX customer_username_idx ON CUSTOMER(Username);
CREATE INDEX item_scid_idx ON ITEM(Sc_id);
CREATE INDEX item_name_idx ON ITEM(Name);
CREATE INDEX shippingorder_cid_idx ON SHIPPINGORDER(C_id);
CREATE INDEX shippingorder_otime_idx ON SHIPPINGORDER(Otime);
CREATE INDEX mcategory_lcid_idx ON M_CATEGORY(Lc_id);
CREATE INDEX scategory_mcid_idx ON S_CATEGORY(Mc_id);
