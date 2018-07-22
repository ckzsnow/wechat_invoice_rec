package com.bbz.controller.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
import com.bbz.service.web.IUserService;
import com.bbz.service.wechat.IWechatSubmitInvoiceService;

@Controller
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	@Autowired
	private IGenVoucherService genVoucherService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IWechatSubmitInvoiceService wechatSubmitInvoiceService;
	
	@RequestMapping("/")
	public String getRootHtml() {
		return "redirect:/views/admin/home.html";
	}
	
	@RequestMapping("/admin/getUserInfo")
	@ResponseBody
	public Map<String, Object> getUserInfo(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("user_id");
		userId = "oZEuwxBPWrDkHjFL1Q8VTHv_o_II";
		return userService.getUserInfoForWeb(userId);
	}
	
	@RequestMapping("/admin/getAllInvoiceForWeb")
	@ResponseBody
	public Map<String, Object> getAllInvoiceForWeb(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("user_id");
		String billDate = request.getParameter("bill_date");
		String currentPage = request.getParameter("current_page");
		String countPrePage = request.getParameter("count_pre_page");
		int currentPage_ = 1;
		int countPrePage_ = 5;
		try {
			currentPage_ = Integer.valueOf(currentPage);
			countPrePage_ = Integer.valueOf(countPrePage);
		} catch(Exception ex) {
			logger.error(ex.toString());
		}
		userId = "oZEuwxBPWrDkHjFL1Q8VTHv_o_II";
		return userService.getAllInvoiceForWeb(userId, billDate, currentPage_, countPrePage_);
	}
	
	@RequestMapping("/admin/getInvoiceDetailByIdForWeb")
	@ResponseBody
	public Map<String, Object> getInvoiceDetailByIdForWeb(HttpServletRequest request) {
		int invoiceId = Integer.valueOf(request.getParameter("invoice_id"));
		return wechatSubmitInvoiceService.wechatGetInvoiceItem(invoiceId);
	}
	
	@RequestMapping("/admin/deleteInvoiceForSingle")
	@ResponseBody
	public Map<String, Object> deleteInvoiceForSingle(HttpServletRequest request) {
		long invoiceId = Long.valueOf(request.getParameter("invoice_id"));
		userService.deleteInvoiceForSingle(invoiceId);
		return null;
	}
	
	@RequestMapping("/admin/deleteInvoiceAll")
	@ResponseBody
	public Map<String, Object> deleteInvoiceAll(HttpServletRequest request) {
		String invoiceIds = request.getParameter("invoice_id");
		userService.deleteInvoiceAll(invoiceIds);
		return null;
	}
	
	@RequestMapping("/admin/downloadVoucherPath")
	@ResponseBody
	public Map<String, String> downloadVoucherPath(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("user_id");
		String userName = (String) request.getSession().getAttribute("user_name");
		userId = "oZEuwxBPWrDkHjFL1Q8VTHv_o_II";
		userName = "测试员";
		String billDate = request.getParameter("bill_date");
		String isDetail = request.getParameter("is_detail");;
		boolean isDetail_ = ("0").equals(isDetail) ? false:true;
		logger.debug("genVoucher, bill_date:{}, isDetail:{}", billDate, isDetail);
		Map<String, String> retMap = genVoucherService.genVoucher(userId, userName, billDate, isDetail_);
		return retMap;
	}
	
	@RequestMapping("/admin/downloadVoucher")
	@ResponseBody
	public ResponseEntity<byte[]> downloadVoucher(HttpServletRequest request) {
		ResponseEntity<byte[]> re = null;
		String excelPath = request.getParameter("excel_path");
		String billDate = request.getParameter("bill_date");
		File file = new File(excelPath);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDispositionFormData("attachment", new String(("凭证_" + billDate + ".xls").getBytes("UTF-8"), "iso-8859-1"));
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			re = new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(file), headers,
					HttpStatus.OK);
		} catch (IOException e) {
			logger.error("downloadVoucher exception : {}", e.toString());
		}
		return re;
	}
}
