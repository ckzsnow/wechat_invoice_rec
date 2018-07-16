package com.bbz.controller.wechat;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.bbz.weixin.utils.IpUtils;
import com.bbz.weixin.utils.PayUtil;
import com.bbz.weixin.utils.StringUtils;
import com.bbz.weixin.utils.WxPayConfig;

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
				logger.debug("getUserInfo session id : {}", request.getSession().getId());
				request.getSession().setAttribute("user_openId", userInfoJSON.get("openId"));
				request.getSession().setAttribute("user_unionId", userInfoJSON.get("unionId"));
				retMap.put("openId", userInfoJSON.get("openId"));
				retMap.put("unionId", userInfoJSON.get("unionId"));
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
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("error_code", "10001");
		retMap.put("error_msg", "授权时效，请重新登陆！");
		String sessionId = (String)request.getParameter("sessionId");
		logger.debug("getUserAccount sessionId : {}", sessionId);
		if(sessionId != null && !sessionId.isEmpty()) {
			HttpSession session = MySessionContext.getSession(sessionId);
			if(session != null) {
				String unionId = (String)session.getAttribute("user_unionId");
				if(unionId != null && !unionId.isEmpty()) {
					Map<String, Object> accountMap = userService.getUserAccountByUserId(unionId);
					if(!accountMap.isEmpty()) {
						retMap.put("error_code", "10000");
						retMap.put("balance", accountMap.get("balance"));
					}
				}
			}
		}
		return retMap;
	}
	
	@RequestMapping("/wechat/getAllInvoice")
	@ResponseBody
	public Map<String, Object> getAllInvoice(HttpServletRequest request) {
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("error_code", "10001");
		retMap.put("error_msg", "授权时效，请重新登陆！");
		String sessionId = (String)request.getParameter("sessionId");
		String union_id = (String)request.getParameter("unionId");
		String index = (String)request.getParameter("index");
		logger.debug("getUserAccount sessionId : {}", sessionId);
		if(sessionId != null && !sessionId.isEmpty()) {
			HttpSession session = MySessionContext.getSession(sessionId);
			if(session != null) {
				String unionId = (String)session.getAttribute("user_unionId");
				if(unionId != null && !unionId.isEmpty()) {
					List<Map<String, Object>> invoiceList = userService.getAllInvoiceByUserId(unionId, index);
					if(!invoiceList.isEmpty()) {
						retMap.put("error_code", "10000");
						retMap.put("data", invoiceList);
					}
				}
			}
		}
		return retMap;
	}
	
	@RequestMapping("/wechat/getInvoiceById")
	@ResponseBody
	public Map<String, Object> getInvoiceById(HttpServletRequest request) {
		Map<String, Object> retMap = new HashMap<>();
		String invoice_id = request.getParameter("invoiceId");
		Map<String, Object> invoiceDetail = userService.getInvoiceById(invoice_id);
		retMap.put("detail", invoiceDetail);
		return retMap;
	}
	
	@RequestMapping("/wechat/wxPay")
	@ResponseBody
	public Map<String, Object> wxPay(HttpServletRequest request) {
		Map<String, Object> retMap = new HashMap<>();
		String openId = request.getParameter("openId");
		String money = request.getParameter("money");//支付金额，单位：分，这边需要转成字符串类型，否则后面的签名会失败
        try{
            //生成的随机字符串
            String nonce_str = StringUtils.getRandomStringByLength(32);
            //商品名称
            String body = "测试商品名称";
            //获取本机的ip地址
            String spbill_create_ip = IpUtils.getIpAddr(request);

            String orderNo = StringUtils.getRandomStringByLength(20);

            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.appid);
            packageParams.put("mch_id", WxPayConfig.mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", orderNo);//商户订单号
            packageParams.put("total_fee", money);//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.notify_url);
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);
            packageParams.put("openid", openId);

            // 除去数组中的空值和签名参数
            packageParams = PayUtil.paraFilter(packageParams);
            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = PayUtil.sign(prestr, WxPayConfig.key, "utf-8").toUpperCase();
            logger.info("=======================第一次签名：" + mysign + "=====================");

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + WxPayConfig.appid + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + WxPayConfig.mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + WxPayConfig.notify_url + "</notify_url>"
                    + "<openid>" + openId + "</openid>"
                    + "<out_trade_no>" + orderNo + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + money + "</total_fee>"
                    + "<trade_type>" + WxPayConfig.TRADETYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            System.out.println("调试模式_统一下单接口 请求XML数据：" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest(WxPayConfig.pay_url, "POST", xml);

            System.out.println("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);

            String return_code = (String) map.get("return_code");//返回状态码

            //返回给移动端需要的参数
            Map<String, Object> response = new HashMap<String, Object>();
            if(return_code == "SUCCESS" || return_code.equals(return_code)){
                // 业务结果
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误

                String stringSignTemp = "appId=" + WxPayConfig.appid + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id+ "&signType=" + WxPayConfig.SIGNTYPE + "&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtil.sign(stringSignTemp, WxPayConfig.key, "utf-8").toUpperCase();
                logger.info("=======================第二次签名：" + paySign + "=====================");

                response.put("paySign", paySign);

                //更新订单信息
                //业务逻辑代码
            }

            response.put("appid", WxPayConfig.appid);
            retMap.put("data", response);
        }catch(Exception e){
            logger.error(e.toString());
        }
        return retMap;
	}
	
	@RequestMapping(value="/wxNotify")
    @ResponseBody
    public void wxNotify(HttpServletRequest request,HttpServletResponse response) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null){
            sb.append(line);
        }
        br.close();
        //sb为微信返回的xml
        String notityXml = sb.toString();
        String resXml = "";
        System.out.println("接收到的报文：" + notityXml);
 
        Map map = PayUtil.doXMLParse(notityXml);
 
        String returnCode = (String) map.get("return_code");
        if("SUCCESS".equals(returnCode)){
            //验证签名是否正确
            Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
            String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            String sign = PayUtil.sign(validStr, WxPayConfig.key, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
            //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
            if(sign.equals(map.get("sign"))){
                /**此处添加自己的业务逻辑代码start**/
 
 
                /**此处添加自己的业务逻辑代码end**/
                //通知微信服务器已经支付成功
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            }
        }else{
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        System.out.println(resXml);
        System.out.println("微信支付回调数据结束");
 
 
        BufferedOutputStream out = new BufferedOutputStream(
                response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }
	
}
