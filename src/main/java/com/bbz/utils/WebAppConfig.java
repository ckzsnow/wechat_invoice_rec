package com.bbz.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAppConfig {
	
	public static final String appId = "wx855af37f9b4b4048";
	public static final String appSecret = "ad60a543723586ea0d555231f7fe0258";
	public static final String redirectUri = "http%3A%2F%2Fwww.udiyclub.com%2FwxLoginSuccess%3F";
	public static final String originUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
	public static final String refresh_token_url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
}
