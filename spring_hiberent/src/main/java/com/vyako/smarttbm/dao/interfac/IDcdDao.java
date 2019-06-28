package com.vyako.smarttbm.dao.interfac;

import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_request.CycleRequestDo;
import com.vyako.smarttbm.do_request.HandshakeRequestDo;

public interface IDcdDao {
	public BaseResponseModel handShakeWithMachine(
			HandshakeRequestDo hardsRequestDo);

	public BaseResponseModel sendMachineCycleInfo(CycleRequestDo cycleRequestDo);
	
	public BaseResponseModel resetAllMachineCycleInfo();

}
