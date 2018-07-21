package com.bbz.service.web;

import java.util.List;
import java.util.Map;

public interface IUserService {

	public boolean addUser(String unionId);

	public Map<String, Object> getUserAccountByUserId(String unionId);
	
	public Map<String, Object> getUserInfoForWeb(String userId);

	public List<Map<String, Object>> getAllInvoiceByUserId(String unionId, String index, String date);
	
	public Map<String, Object> getAllInvoiceForWeb(String userId, String billDate, int currentPage, int countPrePage);

	public Map<String, Object> getInvoiceById(String invoice_id);
	
	public void deleteInvoiceForSingle(long invoice_id);
	
	public void deleteInvoiceAll(String ids);

	public boolean addPayRecord(String unionId, String orderNo, String money);

	public boolean updatePayRecord(String out_trade_no, String transaction_id, String bank_type, String mch_id);

	public boolean updateUser(String openid, String fee);

	public boolean addUnionOpendId(String openId, String unionId);

	public Map<String, Object> getUserByUserId(String user_id);

	public boolean updateUserInfo(String unionId, String user_name, String user_company_name);

	public boolean deduct(String unionId);
}
