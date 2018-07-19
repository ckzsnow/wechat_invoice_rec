package com.bbz.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAppCache {

	private static final Logger logger = LoggerFactory.getLogger(WebAppCache.class);
	private static String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
	//private static String access_token = "";
	
	public static String getUserInfoByOpenId(String openId){
		String ret = "";
		URL url;
		try {
			url = new URL("https://api.weixin.qq.com/sns/userinfo?access_token="+WeixinCache.getAccessToken()+"&openid="+openId+"&lang=zh_CN");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			ret = new String(jsonBytes, "UTF-8");
			logger.debug("push result : {}", ret);
		} catch (Exception e) {
			logger.error("exception : {}", e.toString());
		}
		return ret;	
	}
	
	public static Map<Object, Object> getUserInfoMap(String code) {
		logger.debug("getUserInfoMap code : {}", code);
		String access_token = "";
		String openid = "";
		Map<Object, Object> accessTokenMap = getAccessTokenMap(code);
		if (accessTokenMap.containsKey("access_token"))
			access_token = (String) accessTokenMap.get("access_token");
		if (accessTokenMap.containsKey("openid"))
			openid = (String) accessTokenMap.get("openid");
		String url = userInfoUrl.replace("ACCESS_TOKEN", access_token)
				.replace("OPENID", openid);
		logger.debug("getUserInfoMap getUnionidUrl : {}", url);
		Map<Object, Object> userInfoMap = WeixinTools.httpGet(url);
		logger.debug("getUserInfoMap userInfoMap : {}", userInfoMap.toString());
		return userInfoMap;
	}
	
	public static Map<Object, Object> getAccessTokenMap(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		url = url.replace("APPID", WebAppConfig.appId)
				.replace("SECRET", WebAppConfig.appSecret)
				.replace("CODE", code);
		logger.info("GetOpenId URL : {}", url);
		Map<Object, Object> map = WeixinTools.httpGet(url);
		return map;
	}
	
	
	public static String generateQrcodeUrl(String redirect) {
		String state = String.valueOf((int)((Math.random()*9+1)*100000));
		logger.debug("generate state : {}", state);
		String url = WebAppConfig.originUrl.replace("APPID", WebAppConfig.appId)
				.replace("REDIRECT_URI", WebAppConfig.redirectUri + redirect)
				.replace("STATE", state);
		logger.debug("generate qrcodeUrl : {}", url);
		return url;
	}
	
}
