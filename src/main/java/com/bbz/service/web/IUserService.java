package com.bbz.service.web;

import java.util.List;
import java.util.Map;

public interface IUserService {

	public boolean addUser(String unionId);

	public Map<String, Object> getUserAccountByUserId(String unionId);

	public List<Map<String, Object>> getAllInvoiceByUserId(String unionId, String index);

	public Map<String, Object> getInvoiceById(String invoice_id);
}
