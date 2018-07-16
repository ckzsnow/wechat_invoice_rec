package com.bbz.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WeixinTools {
	
	private static final Logger logger = LoggerFactory
			.getLogger(WeixinTools.class);
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> httpGet(String url) {
		logger.debug("sending http get request, url : {}", url);
		Map<Object, Object> ret = null;
		StringBuffer bufferRes = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setConnectTimeout(25000);
			conn.setReadTimeout(25000);
			HttpURLConnection.setFollowRedirects(true);
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
			conn.setRequestProperty("Referer", "https://api.weixin.qq.com/");
			conn.connect();
			InputStream in = conn.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String valueString = null;
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			ObjectMapper mapper = new ObjectMapper();
			ret = mapper.readValue(bufferRes.toString(), Map.class);
			in.close();
			if (conn != null) {
				conn.disconnect();
			}
		} catch (MalformedURLException e) {
			logger.warn(e.toString());
		} catch (IOException e) {
			logger.warn(e.toString());
		}
		logger.debug("finish in sending http get request, response : {}",
				bufferRes.toString());
		return ret;
	}
}
