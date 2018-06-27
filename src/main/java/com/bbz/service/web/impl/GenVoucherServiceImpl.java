package com.bbz.service.web.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.bbz.service.web.IGenVoucherService;

@Service("genVoucherService")
public class GenVoucherServiceImpl implements IGenVoucherService {
	
	private static final Logger logger = LoggerFactory.getLogger(GenVoucherServiceImpl.class);
	
	@Override
	public void genVoucher(String billDate, boolean isDetail) {
		
	}	
}