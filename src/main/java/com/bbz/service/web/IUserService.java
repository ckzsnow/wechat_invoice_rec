package com.bbz.service.web;

import java.util.Map;

public interface IUserService {

	public boolean addUser(String unionId);

	public Map<String, Object> getUserAccountByUserId(String unionId);
}
