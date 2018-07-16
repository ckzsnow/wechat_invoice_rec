package com.bbz.service.web;

import java.util.List;
import java.util.Map;

public interface IUserService {

	public boolean addUser(String unionId);

	public Map<String, Object> getUserAccountByUserId(String unionId);

	public List<Map<String, Object>> getAllInvoiceByUserId(String unionId, String index);

	public Map<String, Object> getInvoiceById(String invoice_id);

	public boolean addPayRecord(String unionId, String orderNo, String money);

	public boolean updatePayRecord(String out_trade_no, String transaction_id, String bank_type, String mch_id);

	public boolean updateUser(String openid, String fee);

	public boolean addUnionOpendId(String openId, String unionId);

	public Map<String, Object> getUserByUserId(String user_id);
}
