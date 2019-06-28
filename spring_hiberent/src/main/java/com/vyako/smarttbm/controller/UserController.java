package com.vyako.smarttbm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.Gson;
import com.vyako.smarttbm.dao.interfac.IUserDao;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_request.UserLoginRequestDo;

@RestController
@RequestMapping(value = "api/user")
@EnableWebMvc
public class UserController extends BaseController {

	@Autowired
	private IUserDao iUserDao;

	/**
	 * API to add machines and its parts
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel loginUser(@RequestBody String userLoginJsonString) {
		try {
			Gson gson = new Gson();
			UserLoginRequestDo userLoginRequestDo = gson.fromJson(
					userLoginJsonString, UserLoginRequestDo.class);
			return iUserDao.login(userLoginRequestDo);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false,
					"Error while parsing.. Please check the request format !!",
					null);
		}
	}

	/**
	 * API to add machines and its parts
	 */
	@RequestMapping(value = "/pwd/change", method = RequestMethod.PUT)
	@ResponseBody
	public BaseResponseModel changePwd(@RequestBody String changePwdJsonString) {
		try {
			Gson gson = new Gson();
			UserLoginRequestDo userLoginRequestDo = gson.fromJson(
					changePwdJsonString, UserLoginRequestDo.class);
			return iUserDao.changePwd(userLoginRequestDo);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false,
					"Error while parsing.. Please check the request format !!",
					null);
		}
	}

}
