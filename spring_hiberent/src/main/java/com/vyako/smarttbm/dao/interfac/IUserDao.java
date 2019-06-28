package com.vyako.smarttbm.dao.interfac;

import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_request.CycleRequestDo;
import com.vyako.smarttbm.do_request.UserLoginRequestDo;

public interface IUserDao {
	public BaseResponseModel login(UserLoginRequestDo userLoginRequestDo);

	public BaseResponseModel changePwd(UserLoginRequestDo userLoginRequestDo);
}
