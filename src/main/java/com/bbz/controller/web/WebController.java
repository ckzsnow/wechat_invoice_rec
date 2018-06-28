package com.bbz.controller.web;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bbz.service.web.IGenVoucherService;

@Controller
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	@Autowired
	private IGenVoucherService genVoucherService;
	
	@RequestMapping("/web/downloadVoucher")
	@ResponseBody
	public ResponseEntity<byte[]> downloadVoucher(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("user_id");
		String userName = (String) request.getSession().getAttribute("user_name");
		String billDate = (String) request.getSession().getAttribute("bill_date");
		String isDetail = (String) request.getSession().getAttribute("is_detail");
		boolean isDetail_ = ("0").equals(isDetail) ? false:true;
		logger.debug("genVoucher, bill_date:{}, isDetail:{}", billDate, isDetail);
		Map<String, String> retMap = genVoucherService.genVoucher(userId, userName, billDate, isDetail_);
		if(retMap.get("error_code").isEmpty()) {
			ResponseEntity<byte[]> re = null;
			File file = new File(retMap.get("xls_path"));
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentDispositionFormData("attachment", new String(("凭证_" + billDate).getBytes("UTF-8"), "iso-8859-1"));
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				re = new ResponseEntity<byte[]>(
						FileUtils.readFileToByteArray(file), headers,
						HttpStatus.OK);
			} catch (IOException e) {
				logger.error("downloadVoucher exception : {}", e.toString());
			}
			return re;
		} else {
			return null;
		}
	}
}
