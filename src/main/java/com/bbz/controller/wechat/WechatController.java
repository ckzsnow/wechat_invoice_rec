package com.bbz.controller.wechat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbz.service.web.IUserService;
import com.bbz.service.wechat.IWechatSubmitInvoiceService;
import com.bbz.utils.AesCbcUtil;
import com.bbz.utils.Constants;
import com.bbz.utils.MySessionContext;
import com.bbz.utils.WeixinTools;

import net.sf.json.JSONObject;

@Controller
public class WechatController {

	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);
	
	@Autowired
	private IWechatSubmitInvoiceService wechatSubmitInvoiceService;
	
	@Autowired
	private IUserService userService;
	
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
	
	@RequestMapping("/wechat/getUserInfo") 
	@ResponseBody
	public Map<String, Object> getUserInfo(HttpServletRequest request) {
		Map<String, Object> retMap = new HashMap<>();
		int error_code = 0;
		String encryptedData = request.getParameter("encryptedData");
		String iv = request.getParameter("iv");
		String code = request.getParameter("code");
		String url = Constants.url.replace("APPID", Constants.AppID)
				.replace("APPSECRET", Constants.AppSecret)
				.replace("CODE", code);
		logger.debug("getUserInfo url : {}", url);
		Map<Object, Object> map = WeixinTools.httpGet(url);
		String session_key = map.get("session_key").toString();
//		String openid = (String) map.get("openid");
		try {
			String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
			if (null != result && result.length() > 0) {
				JSONObject userInfoJSON = JSONObject.fromObject(result);
				/*Map<String, Object> userInfo = new HashMap<>();
				userInfo.put("openId", userInfoJSON.get("openId"));
				userInfo.put("nickName", userInfoJSON.get("nickName"));
				userInfo.put("gender", userInfoJSON.get("gender"));
				userInfo.put("city", userInfoJSON.get("city"));
				userInfo.put("province", userInfoJSON.get("province"));
				userInfo.put("country", userInfoJSON.get("country"));
				userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
				userInfo.put("unionId", userInfoJSON.get("unionId"));*/
				logger.debug("getUserInfo user detail info : {}", userInfoJSON.toString());
				request.getSession().setAttribute("user_openId", userInfoJSON.get("openId"));
				request.getSession().setAttribute("user_unionId", userInfoJSON.get("unionId"));
				if(userInfoJSON.get("unionId") != null && !((String)userInfoJSON.get("unionId")).isEmpty()) {
					if(userService.addUser((String)userInfoJSON.get("unionId"))) {
						error_code = 1;
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sessionId = request.getSession().getId();
		logger.debug("getUserInfo sessionId", sessionId);
		retMap.put("error_code", error_code);
		retMap.put("sessionId", sessionId);
		return retMap;
	}
	
	@RequestMapping("/wechat/getUserAccount")
	@ResponseBody
	public Map<String, Object> getUserAccount(HttpServletRequest request) {
		String sessionId = (String)request.getAttribute("sessionId");
		HttpSession session = MySessionContext.getSession(sessionId);
		String unionId = (String)session.getAttribute("uinonId");
		Map<String, Object> accountMap = userService.getUserAccountByUserId(unionId);
		return accountMap;
	}
}
