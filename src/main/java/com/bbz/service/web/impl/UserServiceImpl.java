package com.bbz.service.web.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bbz.service.web.IUserService;

@Service("userService")
public class UserServiceImpl implements IUserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean addUser(String unionId) {
		int affectedRows = 0;
		String sql = "insert into user set user_id=?";
		try {
			affectedRows = jdbcTemplate.update(sql, unionId);
		} catch(Exception e) {
			logger.error(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public Map<String, Object> getUserAccountByUserId(String unionId) {
		Map<String, Object> accountMap = new HashMap<>();
		String sql = "select * from account where user_id=?";
		try {
			accountMap = jdbcTemplate.queryForMap(sql, unionId);
		} catch(Exception e) {
			logger.error(e.toString());
		}
		return accountMap;
	}

	@Override
	public List<Map<String, Object>> getAllInvoiceByUserId(String unionId, String index) {
		List<Map<String, Object>> invoiceList = new ArrayList<>();
		String sql = "select * from invoice where user_id=? limit 0,?";
		try {
			invoiceList = jdbcTemplate.queryForList(sql, unionId, Integer.valueOf(index)*5);
		} catch(Exception e) {
			logger.error(e.toString());
		}
		return invoiceList;
	}

	@Override
	public Map<String, Object> getInvoiceById(String invoice_id) {
		Map<String, Object> resMap = new HashMap<>();
		String sql = "select * from invoice where id=?";
		try {
			resMap = jdbcTemplate.queryForMap(sql, invoice_id);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return resMap;
	}
	
}
