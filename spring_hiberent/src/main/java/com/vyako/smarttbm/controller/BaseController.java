package com.vyako.smarttbm.controller;

import com.vyako.smarttbm.do_other.BaseResponseModel;

public class BaseController {
	protected BaseResponseModel formResponseModel(boolean isSuccess,
			String msg, Object responseObj) {
		BaseResponseModel baseResponseModel = new BaseResponseModel();
		baseResponseModel.setSuccess(isSuccess);
		baseResponseModel.setMsg(msg);
		baseResponseModel.setResponse(responseObj);
		return baseResponseModel;
	}

}
