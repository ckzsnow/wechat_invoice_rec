package com.bbz.service.web.impl;

import java.sql.Timestamp;
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
		String sql = "replace into user set user_id=?, balance=?, create_time=?";
		try {
			affectedRows = jdbcTemplate.update(sql, unionId, 0.5, new Timestamp(System.currentTimeMillis()));
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
	public List<Map<String, Object>> getAllInvoiceByUserId(String unionId, String index, String date) {
		List<Map<String, Object>> invoiceList = new ArrayList<>();
		String sql = "select * from invoice where user_id=? and bill_date=? limit 0,?";
		try {
			invoiceList = jdbcTemplate.queryForList(sql, unionId, date, Integer.valueOf(index)*5);
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

	@Override
	public boolean addPayRecord(String unionId, String orderNo, String money) {
		String sql = "insert into pay_record (user_id, out_trade_no, fee, status, create_time) values (?, ?, ?, ?, ?)";
		int affectedRows = 0;
		try {
			affectedRows = jdbcTemplate.update(sql, unionId, orderNo, money, 0, new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public boolean updatePayRecord(String out_trade_no, String transaction_id, String bank_type, String mch_id) {
		String sql = "update pay_record set transaction_id=?, bank_type=?, mch_id=?, status=?, modify_time=? where out_trade_no=?";
		int affectedRows = 0;
		try {
			affectedRows = jdbcTemplate.update(sql, transaction_id, bank_type, mch_id, 1, new Timestamp(System.currentTimeMillis()), out_trade_no);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public boolean updateUser(String openid, String fee) {
		String sql = "update user set balance=(balance+?) where user_id=(select union_id from open_union_id where open_id=?)";
		int affectedRows = 0;
		try {
			affectedRows = jdbcTemplate.update(sql, Integer.valueOf(fee)/100, openid);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public boolean addUnionOpendId(String openId, String unionId) {
		String sql = "replace into open_union_id (open_id, union_id, create_time) values (?, ?, ?)";
		int affectedRows = 0;
		try {
			affectedRows = jdbcTemplate.update(sql, openId, unionId, new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public Map<String, Object> getUserByUserId(String user_id) {
		String sql = "select * from user where user_id=?";
		Map<String, Object> retMap = new HashMap<>();
		try {
			retMap = jdbcTemplate.queryForMap(sql, user_id);
		} catch(Exception e) {
			logger.error(e.toString());
		}
		return retMap;
	}

	@Override
	public boolean updateUserInfo(String unionId, String user_name, String user_company_name) {
		String sql = "update user set user_name=?, user_company_name=? where user_id=?";
		int affectedRows = 0;
		try {
			affectedRows = jdbcTemplate.update(sql, user_name, user_company_name, unionId);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public boolean deduct(String unionId) {
		String sql = "update user set balance=(balance-0.1) where user_id=?";
		int affectedRows = 0;
		try {
			affectedRows = jdbcTemplate.update(sql, unionId);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
		return affectedRows != 0;
	}

	@Override
	public Map<String, Object> getUserInfoForWeb(String userId) {
		String sql = "select user.*, count(invoice.id) as total from user inner join invoice on invoice.user_id=? and user.user_id=?";
		Map<String, Object> retMap = new HashMap<>();
		try {
			retMap = jdbcTemplate.queryForMap(sql, userId, userId);
		} catch(Exception e) {
			logger.error(e.toString());
		}
		return retMap;
	}

	@Override
	public Map<String, Object> getAllInvoiceForWeb(String userId, String billDate, int currentPage,
			int countPrePage) {
		List<Map<String, Object>> invoiceList = new ArrayList<>();
		Map<String, Object> retMap = new HashMap<>();
		String sqlTotal = "select count(id) from invoice where user_id=? and bill_date=?";
		String sqlRecorde = "select * from invoice where user_id=? and bill_date=? order by create_time desc limit ?,?";
		int total = 0;
		try {
			total = jdbcTemplate.queryForObject(sqlTotal, new Object[]{userId, billDate}, Integer.class);
			retMap.put("total", total);
			if(total != 0) {
				invoiceList = jdbcTemplate.queryForList(sqlRecorde, userId, billDate, (currentPage-1) * countPrePage, countPrePage);
				retMap.put("data", invoiceList);
			} else {
				retMap.put("data", null);
			}			
		} catch(Exception e) {
			logger.error(e.toString());
			retMap.put("total", 0);
			retMap.put("data", null);
		}
		return retMap;
	}

	@Override
	public void deleteInvoiceForSingle(long invoice_id) {
		String sql = "delete from invoice where id=?";
		try {
			jdbcTemplate.update(sql, invoice_id);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
	}

	@Override
	public void deleteInvoiceAll(String ids) {
		String[] invoiceIds = ids.split("#");
		StringBuilder sb = new StringBuilder();
		Object[] args = new Object[invoiceIds.length];
		sb.append("delete from invoice where ");
		int index = 0;
		for(String id : invoiceIds){
			sb.append("id=? or ");
			args[index++] = Long.valueOf(id);
		}
		String sql = sb.toString().substring(0, sb.length()-3);
		try {
			jdbcTemplate.update(sql, args);
		} catch (Exception e) {
			logger.debug(e.toString());
		}
	}
	
}
