package com.bbz.controller.wechat;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bbz.service.wechat.IWechatSubmitInvoiceService;

@Controller
public class WechatController {

	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);
	
	@Autowired
	private IWechatSubmitInvoiceService wechatSubmitInvoiceService;
	
	@RequestMapping("/wechat/submitInvoice")
	@ResponseBody
	public Map<String, String> submitInvoice(HttpServletRequest request) {
		String userId = (String)request.getSession().getAttribute("user_id");
		String userName = (String)request.getSession().getAttribute("user_name");
		String userCompanyName = (String)request.getSession().getAttribute("user_company_name");
		String invoiceJsonData = request.getParameter("invoice_json_data");
		Map<String, String> retMap = wechatSubmitInvoiceService.wechatSubmitInvoice(userId, userName, userCompanyName, invoiceJsonData);
		logger.debug("submitInvoice, ret:{}", retMap.toString());
		return retMap;
	}
	
	@RequestMapping("/wechat/getInvoice")
	@ResponseBody
	public List<Map<String, Object>> getInvoice(HttpServletRequest request) {
		String userId = (String)request.getSession().getAttribute("user_id");
		String page = request.getParameter("page");
		String countPerPage = request.getParameter("countPerPage");
		int page_ = 1;
		int countPerPage_ = 5;
		try {
			page_ = Integer.valueOf(page);
			countPerPage_ = Integer.valueOf(countPerPage);
		} catch(Exception e) {
			logger.error("getInvoice, error:{}", e.toString());
		}
		List<Map<String, Object>> retList = wechatSubmitInvoiceService.wechatGetInvoice(userId, page_, countPerPage_);
		logger.debug("getInvoice, ret:{}", retList.toString());
		return retList;
	}
	
	@RequestMapping("/wechat/getInvoiceItem")
	@ResponseBody
	public Map<String, Object> getInvoiceItem(HttpServletRequest request) {
		String invoiceId = request.getParameter("invoiceId");
		int invoiceId_ = -1;
		try {
			invoiceId_ = Integer.valueOf(invoiceId);
		} catch(Exception e) {
			logger.error("getInvoiceItem, error:{}", e.toString());
		}
		Map<String, Object> retMap = wechatSubmitInvoiceService.wechatGetInvoiceItem(invoiceId_);
		logger.debug("getInvoiceItem, ret:{}", retMap.toString());
		return retMap;
	}
}
