package com.bbz.controller.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bbz.service.web.IGenVoucherService;

@Controller
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	@Autowired
	private IGenVoucherService genVoucherService;
	
	@RequestMapping("/web/genVoucher")
	@ResponseBody
	public List<Map<String, Object>> genVoucher(HttpServletRequest request) {
		List<Map<String, Object>> retList = new ArrayList<>();
		String userId = (String) request.getSession().getAttribute("user_id");
		return retList;
	}
}
