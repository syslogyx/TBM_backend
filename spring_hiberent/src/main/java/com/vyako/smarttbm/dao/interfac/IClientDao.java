package com.vyako.smarttbm.dao.interfac;

import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.entity.EmailSetting;
import com.vyako.smarttbm.entity.Location;
import com.vyako.smarttbm.entity.Machine;

public interface IClientDao {
	public BaseResponseModel getMachinesList();
	
	public BaseResponseModel getMailSettings();
	
	public BaseResponseModel saveEmailSettings(EmailSetting emailSettings);
	
	public BaseResponseModel getDepartmentList();

	public BaseResponseModel getMachinePartsByMachineId(int machineId);

	public BaseResponseModel addMachineAndItsParts(Machine machine);

	public BaseResponseModel updateMachineAndItsParts(Machine machine);

	public BaseResponseModel deleteMachine(int machineId);

	public BaseResponseModel getEmails(int machineId);

	public BaseResponseModel saveEmailIds(int machine_id, String[] emailArray);

	public BaseResponseModel getMachinePartHistoryByPartId(int machine_part_id);

	public BaseResponseModel addLocation(Location locObj);

	public BaseResponseModel fetcheNoficationCount();

	public BaseResponseModel fetcheAllNofications();

	public BaseResponseModel fetchAllNotificationsOfAMachine(int machineId);

	public BaseResponseModel setAllNotificationAsRead();

	public BaseResponseModel replaceMachinePart(int part_id,int current_count, String action);

	public BaseResponseModel extendMachinePartLife(int part_id, String action);

	public BaseResponseModel resetMachine(int part_id);

	public BaseResponseModel updateIp(String dcd_linked_ip, int machineId);

}
