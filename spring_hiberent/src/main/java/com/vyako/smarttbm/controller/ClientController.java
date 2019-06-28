package com.vyako.smarttbm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.Gson;
import com.vyako.smarttbm.constants.CredentialConstants;
import com.vyako.smarttbm.dao.interfac.IClientDao;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_other.EmailBody;
import com.vyako.smarttbm.entity.EmailSetting;
import com.vyako.smarttbm.entity.Location;
import com.vyako.smarttbm.entity.Machine;
import com.vyako.smarttbm.utils.MailManager;

@RestController
@RequestMapping(value = "api/client")
@EnableWebMvc
public class ClientController extends BaseController {
	@Autowired
	private IClientDao iClientDao;

	@Autowired
	private MailManager mailManager;

	/**
	 * API to get Machine list
	 * 
	 */
	@RequestMapping(value = "/machines", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public BaseResponseModel getMachinesList() {
		try {
			return iClientDao.getMachinesList();
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to get mail settings
	 * 
	 */
	@RequestMapping(value = "/mails/settings", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public BaseResponseModel getMailSettings() {
		try {
			return iClientDao.getMailSettings();
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to send mail to configured mail id.
	 * 
	 */
	@RequestMapping(value = "/mail/test", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel testMailConfiguration(@RequestBody String mailJSON) {
		try {
			System.out.println(">>Mail send setting json=" + mailJSON);
			Gson gson = new Gson();
			EmailSetting emailSettings = gson.fromJson(mailJSON, EmailSetting.class);
			System.out.println(emailSettings);

			// mailManager.initJavaMailSender();

			// here we sent an email to receiver with attachment
			return mailManager.testEmail(emailSettings);

		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing..Email setting details not filled properly !!", null);
		}
	}

	/**
	 * API to save mail settings.
	 * 
	 */
	@RequestMapping(value = "/mail/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel savetMailConfiguration(@RequestBody String mailJSON) {
		try {
			System.out.println(">>Mail send setting json=" + mailJSON);
			Gson gson = new Gson();
			EmailSetting emailSettings = gson.fromJson(mailJSON, EmailSetting.class);
			// System.out.println(emailSettings);
			return iClientDao.saveEmailSettings(emailSettings);

		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing.. Email setting details are not filled properly.!!",
					null);
		}
	}

	@RequestMapping(value = "/mail", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public BaseResponseModel sendMail() {
		try {

			String[] emailIds = new String[1];
			// emailIds[0] = "siddharthdv80@gmail.com";
			emailIds[0] = "monica.jha@vyako.com";
			// here we sent an email to receiver with attachment

			// mailManager.initJavaMailSender();

			String mailSubject = CredentialConstants.EMAIL_SUBJECT_FOR_MACHINE_OFF;
			mailSubject = mailSubject.replaceAll("#machineName#", "bike".toUpperCase());

			String mailBody = CredentialConstants.EMAIL_BODY_FOR_MACHINE_OFF;
			mailBody = mailBody.replaceAll("#machineName#", "bike".toUpperCase());
			mailBody = mailBody.replaceAll("#partName#", "abc".toLowerCase());

			// if (part.life_extended == false && part.life_exten_limit > 0) {
			mailBody = mailBody.replaceAll("#possibleExtension#", "20");
			// } else {
			// mailBody.replaceAll("*possibleExtension*", "0");
			// }

			SendMail sendMail = new SendMail(emailIds, mailSubject, mailBody);
			sendMail.start();
			return formResponseModel(true, "Mail status", "In progress");
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to get machine parts by machine id
	 */
	@RequestMapping(value = "/machine/{machine_id}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel getMachinePartsByMachineId(@PathVariable(value = "machine_id") int machineId) {
		try {
			return iClientDao.getMachinePartsByMachineId(machineId);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to get machine parts history by part_id
	 * 
	 */
	@RequestMapping(value = "/machine/history/{machine_part_id}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel getMachinePartHistoryByPartId(
			@PathVariable(value = "machine_part_id") int machine_part_id) {
		try {
			return iClientDao.getMachinePartHistoryByPartId(machine_part_id);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to get departments
	 */
	@RequestMapping(value = "/departments", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel getDepartmentList() {
		try {
			return iClientDao.getDepartmentList();
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to add machines and its parts
	 * 
	 */
	@RequestMapping(value = "/machine/add", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel addMachineAndItsParts(@RequestBody String machineJSON) {
		try {
			System.out.println(">>Added machine json=" + machineJSON);
			Gson gson = new Gson();
			Machine machine = gson.fromJson(machineJSON, Machine.class);
			return iClientDao.addMachineAndItsParts(machine);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing.. Machine or it's part details not filled properly !!",
					null);
		}
	}

	/**
	 * API to update machines and its parts
	 * 
	 */
	@RequestMapping(value = "/machine/update", method = RequestMethod.PUT)
	@ResponseBody
	public BaseResponseModel updateMachineAndItsParts(@RequestBody String machineJSON) {
		try {
			Gson gson = new Gson();
			Machine machine = gson.fromJson(machineJSON, Machine.class);
			return iClientDao.updateMachineAndItsParts(machine);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing.. Machine or it's part details not filled properly !!",
					null);
		}
	}
	
	/**
	 * API to update DCDIP
	 * 
	 */
	@RequestMapping(value = "/machine/update_ip", method = RequestMethod.PUT)
	@ResponseBody
	public BaseResponseModel updateMachineIP(@RequestBody String machineJSON) {
		try {
			Gson gson = new Gson();
			Machine machine = gson.fromJson(machineJSON, Machine.class);
			int machineId = machine.getId();
			String dcd_linked_ip = machine.getDcd_linked_ip();
			
			return iClientDao.updateIp(dcd_linked_ip,machineId);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing.. Machine or it's part details not filled properly !!",
					null);
		}
	}

	/**
	 * API get reset machine
	 */
	@RequestMapping(value = "/machine/reset/{machine_id}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel resetMachine(@PathVariable(value = "machine_id") int machineId) {
		return iClientDao.resetMachine(machineId);
	}

	/**
	 * API to delete machine and it's associated parts
	 */
	@RequestMapping(value = "/machine/delete/{machine_id}", method = RequestMethod.DELETE)
	@ResponseBody
	public BaseResponseModel deleteMachine(@PathVariable(value = "machine_id") int machineId) {
		return iClientDao.deleteMachine(machineId);
	}

	// --------------------------- EMAIL APIS-------------------------------//

	/**
	 * API get email list for machines
	 */
	@RequestMapping(value = "/machine/emails/{machine_id}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel getEmails(@PathVariable(value = "machine_id") int machineId) {
		return iClientDao.getEmails(machineId);
	}

	/**
	 * API to update email-ids
	 */
	@RequestMapping(value = "/machine/emails/save/{machine_id}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel saveEmailIds(@PathVariable(value = "machine_id") int machineId,
			@RequestBody String emailsJSON) {

		try {
			Gson gson = new Gson();
			String[] emailArray = gson.fromJson(emailsJSON, String[].class);
			return iClientDao.saveEmailIds(machineId, emailArray);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing.. Machine or it's part details not filled properly !!",
					null);
		}
	}
	
	

	/**
	 * API to add machines and its parts
	 * 
	 */
	@RequestMapping(value = "/location", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseModel addLocation(@RequestBody String location) {
		try {
			Gson gson = new Gson();
			Location locObj = gson.fromJson(location, Location.class);
			return iClientDao.addLocation(locObj);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, "Error while parsing.. Machine or it's part details not filled properly !!",
					null);
		}
	}

	/**
	 * API to get notification count
	 */
	@RequestMapping(value = "/machine/notification/count", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel fetcheNoficationCount() {
		return iClientDao.fetcheNoficationCount();
	}

	/**
	 * API to set all notification status as read
	 */
	@RequestMapping(value = "/machine/notification/read", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel setAllNotificationAsRead() {
		return iClientDao.setAllNotificationAsRead();
	}

	/**
	 * API to get all notifications
	 */
	@RequestMapping(value = "/machine/notifications", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel fetcheAllNofications() {
		return iClientDao.fetcheAllNofications();
	}

	/**
	 * API to get all Machine parts which needs attention
	 */
	@RequestMapping(value = "/machine/notifications/{machine_id}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel fetchAllNotificationsOfAMachine(@PathVariable(value = "machine_id") int machineId) {
		return iClientDao.fetchAllNotificationsOfAMachine(machineId);
	}

	/**
	 * API to replace the machine part
	 */
	@RequestMapping(value = "/machine/replace/part/{part_id}/current_count/{current_count}/action/{action}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel replaceMachinePart(@PathVariable(value = "part_id") int part_id,@PathVariable(value = "current_count") int current_count,@PathVariable(value = "action") String action) {
		try {
			return iClientDao.replaceMachinePart(part_id,current_count,action);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * API to extend the life of a machine part
	 */
	@RequestMapping(value = "/machine/extend/part/{part_id}/action/{action}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResponseModel extendMachinePartLife(@PathVariable(value = "part_id") int part_id, @PathVariable(value = "action") String action) {
		try {
			return iClientDao.extendMachinePartLife(part_id, action);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	/**
	 * 
	 * Inner class run the thread which is used to and sent an email
	 * 
	 * @author sid
	 */
	private class SendMail extends Thread {

		private String[] emailIds;
		private String emailSubject;
		private String emailbody;

		public SendMail(String[] emailIds, String emailSubject, String emailbody) {
			this.emailIds = emailIds;
			this.emailSubject = emailSubject;
			this.emailbody = emailbody;
		}

		@Override
		public void run() {

			boolean emailSendStatus = false;
			try {
				// here we sent an email to receiver with attachment
				emailSendStatus = mailManager.sendEmail(emailIds, emailSubject, emailbody);
				System.out.print("## is email sent = " + emailSendStatus);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
