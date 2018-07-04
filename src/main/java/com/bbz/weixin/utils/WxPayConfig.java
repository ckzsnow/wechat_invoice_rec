package com.bbz.weixin.utils;

public class WxPayConfig {
	
	// 小程序appid
	public static final String appid = "wxb72422e9a541f7d1";
	// 微信支付的商户id
	public static final String mch_id = "1509019491";
	// 微信支付的商户密钥
	public static final String key = "prlimecm835w08zjdyyawk9ytephs25f";
	// 支付成功后的服务器回调url
	public static final String notify_url = "http://127.0.0.1/wechat/wxNotify";
	// 签名方式，固定值
	public static final String SIGNTYPE = "MD5";
	// 交易类型，小程序支付的固定值为JSAPI
	public static final String TRADETYPE = "JSAPI";
	// 微信统一下单接口地址
	public static final String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
}
