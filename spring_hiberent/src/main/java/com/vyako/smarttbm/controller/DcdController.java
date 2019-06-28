package com.vyako.smarttbm.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.Gson;
import com.vyako.smarttbm.dao.interfac.IDcdDao;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_request.CycleRequestDo;
import com.vyako.smarttbm.do_request.HandshakeRequestDo;

@RestController
@RequestMapping(value = "api/dcd")
@EnableWebMvc
public class DcdController extends BaseController {
	@Autowired
	private IDcdDao dcdDao;

	/**
	 * API to get Dr specialization list
	 */
	@RequestMapping(value = "/handshake", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel handShakeWithMachine(@RequestBody String requestJSONString) {
		try {
			Gson gson = new Gson();
			HandshakeRequestDo hardsRequestDo = gson.fromJson(requestJSONString, HandshakeRequestDo.class);
			return dcdDao.handShakeWithMachine(hardsRequestDo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formResponseModel(false, "Wrong Request Body.", null);

	}

	/**
	 * API to get cycleinfo
	 */
	@RequestMapping(value = "/cycleinfo", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel sendMachineCycleInfo(@RequestBody String requestJSONString) {
		try {
			Gson gson = new Gson();
			CycleRequestDo cycleRequestDo = gson.fromJson(requestJSONString, CycleRequestDo.class);
			return dcdDao.sendMachineCycleInfo(cycleRequestDo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formResponseModel(false, "Wrong Request Body.", null);

	}

	/**
	 * API to Reset all machine cycle info.
	 */
	@RequestMapping(value = "/reset_all", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel resetAllMachineCycleInfo(@RequestBody String requestJSONString) {
		try {
			return dcdDao.resetAllMachineCycleInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formResponseModel(false, "Wrong Request Body.", null);

	}

}
