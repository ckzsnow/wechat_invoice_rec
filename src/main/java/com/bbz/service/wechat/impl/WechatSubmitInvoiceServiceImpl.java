package com.bbz.service.wechat.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bbz.service.wechat.IWechatSubmitInvoiceService;
import com.bbz.utils.RedisPool;
import redis.clients.jedis.Jedis;

@Service("wechatSubmitInvoiceService")
public class WechatSubmitInvoiceServiceImpl implements IWechatSubmitInvoiceService {

	private static final Logger logger = LoggerFactory.getLogger(WechatSubmitInvoiceServiceImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Map<String, String> wechatSubmitInvoice(String userId, String userName, String userCompanyName, String billDate, int isFa, String invoiceJsonData) {
		Map<String, String> retMap = new HashMap<>();
		JSONObject jsonObject = JSONObject.parseObject(invoiceJsonData);
		jsonObject.put("user_id", userId);
		jsonObject.put("user_name", userName);
		jsonObject.put("user_company_name", userCompanyName);
		jsonObject.put("bill_date", billDate);
		jsonObject.put("is_fa", isFa);
		
		//数据库写入二维码识别基本信息
		String fpdm = jsonObject.getString("fpdm");
		String fphm = jsonObject.getString("fphm");
		String kprq = jsonObject.getString("kprq");
		String kpje = jsonObject.getString("kpje");
		String jym = jsonObject.getString("jym");
		String kjqj = jsonObject.getString("bill_date");
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try{
			jdbcTemplate.update(new PreparedStatementCreator() {  
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {  
		        	String sql = "insert into invoice(user_id, fpdm, fphm, kprq, jym, kpje, is_fa, bill_date, create_time) values (?,?,?,?,?,?,?,?,?)";
		               PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);  
		               ps.setString(1, userId);  
		               ps.setString(2, fpdm);
		               ps.setString(3, fphm);
		               ps.setString(4, kprq);
		               ps.setString(5, jym);
		               ps.setString(6, kpje);
		               ps.setInt(7, isFa);
		               ps.setString(8, kjqj);
		               ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
		               return ps;  
		        }  
		    }, keyHolder);
		} catch(Exception e) {
			logger.error("wechatSubmitInvoice, error:{}", e.toString());
			retMap.put("error_code", "100101");
			retMap.put("error_msg", "提交任务失败，发票基本信息写入数据库失败。");
			return retMap;
		}
		long invoiceId = keyHolder.getKey().longValue();
		jsonObject.put("invoice_id", invoiceId);
		
		//向redis中写入需要执行的识别任务
		String jsonData = jsonObject.toJSONString();
		logger.debug("wechatSubmitInvoice, jsonData:{}", jsonData);
		Jedis jedis = RedisPool.pool.getResource();
		if(jedis.rpush("WECHAT_TASK_INVOICE_QRCODE", jsonData)>0){
			retMap.put("error_code", "000000");
			retMap.put("error_msg", "");
			logger.debug("wechatSubmitInvoice, success");
		} else {
			retMap.put("error_code", "100102");
			retMap.put("error_msg", "提交任务失败，任务无法写入redis。");
			logger.debug("wechatSubmitInvoice, fail");
		}
		jedis.close();
		return retMap;
	}

	@Override
	public List<Map<String, Object>> wechatGetInvoice(String userId, int page, int countPerPage) {
		List<Map<String, Object>> invoiceDataList = new ArrayList<>();
		String sql = "select * from invoice where user_id=? order by create_time desc limit ?,?";
		try{
			invoiceDataList = jdbcTemplate.queryForList(sql, userId, (page-1)*countPerPage, countPerPage);
		}catch(Exception e) {
			logger.error("wechatGetAllInvoice error : {}", e.toString());
		}
		return invoiceDataList;
	}

	@Override
	public Map<String, Object> wechatGetInvoiceItem(int invoiceId) {
		Map<String, Object> retMap = new HashMap<>();
		if(invoiceId == -1) {
			retMap.put("error_code", "100301");
			retMap.put("error_msg", "发票ID为-1，不正确。");
			return retMap;
		}
		Map<String, Object> invoiceDataMap = new HashMap<>();
		List<Map<String, Object>> invoiceItemDataMap = new ArrayList<>();
		String sqlMap = "select * from invoice where id=?";
		String sqlList = "select * from invoice_item where invoice_id=?";
		try{
			invoiceDataMap = jdbcTemplate.queryForMap(sqlMap, invoiceId);
			invoiceItemDataMap = jdbcTemplate.queryForList(sqlList, invoiceId);
			retMap.put("error_code", "000000");
			retMap.put("error_msg", "");
			retMap.put("invoice_info", invoiceDataMap);
			retMap.put("invoice_item_info", invoiceItemDataMap);
		}catch(Exception e) {
			retMap.put("error_code", "100302");
			retMap.put("error_msg", "获取发票信息失败，错误信息："+e.toString());
			logger.error("wechatGetAllInvoice error : {}", e.toString());
		}
		return retMap;
	}
	
}
