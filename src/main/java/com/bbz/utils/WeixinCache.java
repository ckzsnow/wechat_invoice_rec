package com.bbz.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeixinCache {

	private static final Logger logger = LoggerFactory
			.getLogger(WeixinCache.class);

	private static String jsTicket = "";

	private static String accessToken = "";

	private static long jsTicketExpire = 0;

	private static long accessTokenExpire = 0;
	
	public static String getJsTicket() {
		if ((System.currentTimeMillis() / 1000) > jsTicketExpire) {
			logger.debug("jsTicket has expired, refresh the ticket from weixin server.");
			getJsTicketFromWeixinServer();
		}
		return jsTicket;
	}

	public static String getAccessToken() {
		if ((System.currentTimeMillis() / 1000) > accessTokenExpire) {
			logger.debug("accessToken has expired, refresh the accessToken from weixin server.");
			getAccessTokenFromWeixinServer();
		}
		return accessToken;
	}

	private static void getJsTicketFromWeixinServer() {
		String url = WeixinConstEnum.COMPANY_JS_TICKE_TURL.toString();
		url = url.replace("ACCESSTOKEN", getAccessToken());
		Map<Object, Object> map = WeixinTools.httpGet(url);
		if (map != null && map.containsKey("ticket")
				&& map.containsKey("expires_in")) {
			jsTicket = (String) map.get("ticket");
			jsTicketExpire = System.currentTimeMillis() / 1000
					+ (Integer) map.get("expires_in") - 180;
		} else {
			jsTicket = "";
			jsTicketExpire = 0;
		}
	}

	private static void getAccessTokenFromWeixinServer() {
		String url = WeixinConstEnum.COMPANY_ACCESS_TOKEN_URL.toString();
		url = url.replace("APPID", WeixinConstEnum.COMPANY_APP_ID.toString())
				.replace("APPSECRET",
						WeixinConstEnum.COMPANY_APP_SECRET.toString());
		Map<Object, Object> map = WeixinTools.httpGet(url);
		if (map != null && map.containsKey("access_token")
				&& map.containsKey("expires_in")) {
			accessToken = (String) map.get("access_token");
			accessTokenExpire = System.currentTimeMillis() / 1000
					+ (Integer) map.get("expires_in") - 180;
		} else {
			accessToken = "";
			accessTokenExpire = 0;
		}
	}
}
