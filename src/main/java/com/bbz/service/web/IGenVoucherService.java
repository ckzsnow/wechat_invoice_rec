package com.bbz.service.web;

import java.util.Map;

public interface IGenVoucherService {
	
	public Map<String, String> genVoucher(String userId, String userName, String billDate, boolean isDetail);
	
}