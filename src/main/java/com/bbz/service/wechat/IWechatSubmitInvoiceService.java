package com.bbz.service.wechat;

import java.util.List;
import java.util.Map;

public interface IWechatSubmitInvoiceService {
	
	public Map<String, String> wechatSubmitInvoice(String userId, String userName, String userCompanyName, String invoiceJsonData);
	
	public List<Map<String, Object>> wechatGetInvoice(String userId, int page, int countPerPage);
	
	public Map<String, Object> wechatGetInvoiceItem(int invoiceId);
	
}