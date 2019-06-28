package com.vyako.smarttbm.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vyako.smarttbm.constants.Constants;
import com.vyako.smarttbm.dao.interfac.IClientDao;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_other.NotificationCountDo;
import com.vyako.smarttbm.entity.Departments;
import com.vyako.smarttbm.entity.Email;
import com.vyako.smarttbm.entity.EmailSetting;
import com.vyako.smarttbm.entity.Location;
import com.vyako.smarttbm.entity.Machine;
import com.vyako.smarttbm.entity.MachinePart;
import com.vyako.smarttbm.entity.MachinePartCopy;
import com.vyako.smarttbm.entity.MachinePartHistory;
import com.vyako.smarttbm.entity.Notification;

@Repository
public class ClientDaoImpl extends BaseDAOImpl implements IClientDao {

	@Override
	public BaseResponseModel getMachinesList() {
		Session session = sessionFactory.openSession();
		List<Machine> machineList = new ArrayList<Machine>();
		try {

			String qryString = "from Machine";
			machineList = session.createQuery(qryString).list();

			// adding emails in machine list
			addEmailsInMachineList(machineList);

			// adding notification alert in machine list
			calculateMachinePartsWhichNeedsAttention(machineList);
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}

		if (machineList.size() == 0) {
			return formResponseModel(false, "No Machine Found", null);
		}

		return formResponseModel(true, "List of Machine", machineList);
	}

	@Override
	public BaseResponseModel getMailSettings() {
		Session session = sessionFactory.openSession();
		// List<EmailSetting> emailSettingList = new ArrayList<EmailSetting>();
		EmailSetting emailSetting;
		try {

			// String qryString = "from EmailSetting";
			// emailSettingList = session.createQuery(qryString).list();

			Criteria createCriteria = session.createCriteria(EmailSetting.class);
			createCriteria.setMaxResults(1);
			emailSetting = (EmailSetting) createCriteria.uniqueResult();
			// Criteria createCriteria =
			// session.createCriteria(EmailSetting.class);
			// emailSettingList = createCriteria.list();

			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}

		if (emailSetting == null) {
			return formResponseModel(false, "No Data Found", null);
		}

		return formResponseModel(true, "Mail setting Details", emailSetting);
	}

	public BaseResponseModel saveEmailSettings(EmailSetting emailSettings) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			if (emailSettings != null) {

				// String qryString = "DELETE FROM mail_settings WHERE id =" +
				// emailSettings.getId();
				//
				// SQLQuery query = session.createSQLQuery(qryString);

				emailSettings.setHost(emailSettings.getHost());
				emailSettings.setPort(emailSettings.getPort());
				emailSettings.setEmail(emailSettings.getEmail());
				emailSettings.setEmail_pwd(emailSettings.getEmail_pwd());

				session.saveOrUpdate(emailSettings);
				transaction.commit();
				session.close();
			} else {
				return formResponseModel(false, "Something Went Wrong", null);
			}

		} catch (Exception e) {
			transaction.rollback();
			session.close();
			emailSettings = null;
		}
		return formResponseModel(true, "Settings Saved Successfully !!", emailSettings);
	}

	private void addEmailsInMachineList(List<Machine> machineList) {
		if (machineList != null) {
			for (int i = 0; i < machineList.size(); i++) {

				ArrayList<Email> emailList = getEmailIds(machineList.get(i).getId());
				if (emailList != null) {
					String[] emails = new String[emailList.size()];
					for (int j = 0; j < emailList.size(); j++) {
						emails[j] = emailList.get(j).getEmail_id();
					}
					machineList.get(i).setEmails(emails);
				}

			}
		}
	}

	@Override
	public BaseResponseModel getDepartmentList() {
		Session session = sessionFactory.openSession();
		List<Departments> departmentList = new ArrayList<Departments>();
		try {

			String qryString = "from Departments";
			departmentList = session.createQuery(qryString).list();
			session.close();
		} catch (Exception e) {
			session.close();
			return formResponseModel(false, e.getMessage(), null);
		}

		if (departmentList.size() == 0) {
			return formResponseModel(false, "No Department Found", null);
		}

		return formResponseModel(true, "List of Departments", departmentList);
	}

	@Override
	public BaseResponseModel getMachinePartsByMachineId(int machineId) {
		Machine machine = getMachineById(machineId);
		if (machine == null) {
			return formResponseModel(false, "Machine not found", null);
		}
		machine.setParts(getMachineParts(machineId));
		return formResponseModel(true, "Machine with Parts", machine);
	}

	@Override
	public BaseResponseModel addMachineAndItsParts(Machine machine) {
		if (machine.getParts() != null && machine.parts.size() > 0) {
			// checking if machine already exist
			BaseResponseModel existingMachineResponse = checkIfMachineAlreadyExist(machine);

			if (existingMachineResponse != null) {
				return existingMachineResponse;
			} else {
				// checking if dcd ip is already linked
				BaseResponseModel dcdIPLinkedResponse = checkIfDCDAlreadyExist(machine);

				if (dcdIPLinkedResponse != null) {
					return dcdIPLinkedResponse;
				} else {
					// going to add machine and it's parts
					Machine addedMachine = addOrUpdateMachine(machine, false);
					if (addedMachine != null) {
						// adding each parts
						for (int i = 0; i < addedMachine.getParts().size(); i++) {

							MachinePart machinePartToBeAdded = machine.getParts().get(i);

							// setting machine id
							machinePartToBeAdded.setMachine_id(addedMachine.getId());

							// calculating off count and alert count
							calculateOffAndAlertCount(machinePartToBeAdded);

							// set final life as life
							machinePartToBeAdded.setFinal_life(machinePartToBeAdded.getLife());
							
							// set temp life as life
							machinePartToBeAdded.setTemp_life(machinePartToBeAdded.getLife());
							
							// set predicted life as life
							machinePartToBeAdded.setPredicted_life(machinePartToBeAdded.getLife());

							// adding machine part
							MachinePart addedMachinePart = addOrUpdateMachinePart(machinePartToBeAdded, false);

							if (addedMachinePart == null) {
								// deleting/rollbacking added machine and it's
								// parts
								deleteMachineAndItsParts(addedMachine);

								return formResponseModel(false, "Unable to Added machine part " + i + 1, null);
							}
						}

						// adding emails
						saveEmailIds(addedMachine.getId(), addedMachine.getEmails());

					} else {
						return formResponseModel(false, "Failed to Add machine.", null);
					}
				}
			}

		} else {
			return formResponseModel(false, "Please Add Machine parts.", null);
		}
		return formResponseModel(true, "Machine Added Successfully !!", machine);
	}

	private MachinePart findMachinePart(List<MachinePart> existingMachineParts, int partId) {
		MachinePart existingPart = null;
		if (existingMachineParts != null) {
			for (int i = 0; i < existingMachineParts.size(); i++) {
				if (existingMachineParts.get(i).getId() == partId) {
					existingPart = existingMachineParts.get(i);
					return existingPart;
				}
			}
		}
		return existingPart;
	}

	@Override
	public BaseResponseModel updateMachineAndItsParts(Machine machine) {
		if (machine.getParts() != null && machine.parts.size() > 0) {
			// Monica added if condition
			// checking if machine id is present in notification table or not

			BaseResponseModel machineIdInNotificationResponse = checkMachineIdIsPresentOnNotificationOrNot(machine);
			if (machineIdInNotificationResponse != null) {
				return machineIdInNotificationResponse;
			} else {
				// Surabhi added if condition
				// checking if dcd ip is already exist Except Self Machine

				BaseResponseModel dcdIPLinkedResponse = checkIfDCDAlreadyExistExceptSelfMachine(machine);

				if (dcdIPLinkedResponse != null) {
					return dcdIPLinkedResponse;
				} else {

					machine = addOrUpdateMachine(machine, true);
					if (machine == null) {
						return formResponseModel(false, "Machine not found !!", null);
					} else {

						// first will get the existing list of machine parts
						// from db
						List<MachinePart> existingMachineParts = getMachineParts(machine.getId());

						// delete part which has been deleted from front-end
						if (existingMachineParts.size() > 0) {

							for (int i = 0; i < existingMachineParts.size(); i++) {

								boolean isExistingPartFoundInNew = false;

								MachinePart existingPart = existingMachineParts.get(i);

								for (int j = 0; j < machine.getParts().size(); j++) {
									MachinePart newPart = machine.getParts().get(j);
									if (newPart.getId() == existingPart.getId()) {
										isExistingPartFoundInNew = true;
										break;
									}
								}
								if (!isExistingPartFoundInNew) {
									deleteMachinePart(existingPart);
								}
							}
						}

						// add or update machine parts
						// adding each parts
						for (int i = 0; i < machine.getParts().size(); i++) {
							MachinePart machinePartToBeAdded = machine.getParts().get(i);

							// setting machine id
							machinePartToBeAdded.setMachine_id(machine.getId());

							// if machine part already exist then we need to add
							// the
							// updated
							// off_at_count and alert_count
							if (machinePartToBeAdded.getId() != 0) {
								MachinePart existingPart = findMachinePart(existingMachineParts,
										machinePartToBeAdded.getId());
								// calculating off count and alert count
								calculateOffAlertandFinalLifeCountForExistingMachinePart(machinePartToBeAdded,
										existingPart);
							} else {
								calculateOffAndAlertCount(machinePartToBeAdded);
							}

							// machinePartToBeAdded.setPart_replace_count(machinePartToBeAdded.getPart_replace_count());
							// machinePartToBeAdded
							// .setPart_life_extend_count(machinePartToBeAdded.getPart_life_extend_count());

							// adding machine part
							MachinePart addedOrUpdatedMachinePart = addOrUpdateMachinePart(machinePartToBeAdded, true);

							if (addedOrUpdatedMachinePart == null) {
								return formResponseModel(false, "Updating part "
										+ addedOrUpdatedMachinePart.getSpare_part_name() + " Failed !!", null);
							}
						}

						// adding emails
						saveEmailIds(machine.getId(), machine.getEmails());
					}
				}
			}
		} else {
			return formResponseModel(false, "Please Add Machine parts.", null);
		}
		return formResponseModel(true, "Machine updated Successfully.", machine);

	}

	@Override
	public BaseResponseModel deleteMachine(int machineId) {
		Machine machineToBeDeleted = getMachineById(machineId);

		if (machineToBeDeleted == null) {
			return formResponseModel(false, "Machine not found.", null);
		} else {
			machineToBeDeleted.setParts(getMachineParts(machineId));
			if (!deleteMachineAndItsParts(machineToBeDeleted)) {
				return formResponseModel(true, "Error while deleting machine or it's part", null);
			}
		}
		// return formResponseModel(true, "Machine and It's part deleted
		// successfully !!", null);
		// Surabhi Change the msg
		return formResponseModel(true, "Machine and it's part/s deleted successfully !!!", null);
	}

	@Override
	public BaseResponseModel getEmails(int machineId) {
		ArrayList<Email> emailList = getEmailIds(machineId);
		if (emailList == null || emailList.size() == 0) {
			return formResponseModel(false, "No Email Associated", null);
		}
		return formResponseModel(true, "Email List", emailList);
	}

	@Override
	public BaseResponseModel saveEmailIds(int machine_id, String[] emailArray) {
		// deleting previous email list
		deleteEmails(machine_id);

		// adding emails
		if (emailArray != null) {
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			try {
				for (int i = 0; i < emailArray.length; i++) {
					Email email = new Email();
					email.setMachine_id(machine_id);
					email.setEmail_id(emailArray[i]);
					session.save(email);
				}
				transaction.commit();
				session.close();
			} catch (Exception e) {
				e.printStackTrace();
				transaction.commit();
				session.close();
				return formResponseModel(false, "Adding Email failed !!", null);
			}

		}
		// return formResponseModel(true, "Email list saved successfully !!",
		// null);
		// Surabhi Change the msg
		return formResponseModel(true, "Email saved successfully !!!", null);
	}

	@Override
	public BaseResponseModel getMachinePartHistoryByPartId(int machine_part_id) {
		Machine machine = null;
		Session session = sessionFactory.openSession();
		try {
			String qryString = "from MachinePartHistory where part_id=" + machine_part_id;
			ArrayList<MachinePartHistory> partHistoryList = (ArrayList<MachinePartHistory>) session
					.createQuery(qryString).list();
			machine = getMachineById(partHistoryList.get(0).getMachine_id());
			machine.setPart_history(partHistoryList);

			if (partHistoryList != null && partHistoryList.size() > 0) {

				List<MachinePart> machineParts = new ArrayList<>();
				machineParts.add(getMachinePartById(partHistoryList.get(0).getPart_id()));
				machine.setParts(machineParts);
			}
			session.close();
		} catch (Exception e) {
			session.close();
		}

		if (machine == null) {
			return formResponseModel(false, "Machine not found.", null);
		}
		return formResponseModel(true, "History of parts.", machine);
	}

	// ---------------------- basic methods----------------------------//

	// public void calculateOffAndAlertCount(MachinePart machinePart) {
	//
	// long offCount = machinePart.getLife() /
	// machinePart.getMultiplying_factor();
	//
	// long AlertCount = machinePart.getAlert_gen_count() /
	// machinePart.getMultiplying_factor();
	// machinePart.setOff_at_count(offCount);
	// machinePart.setAlert_at_count(AlertCount);
	// machinePart.setFinal_life(machinePart.getLife());
	//
	// }

	public void calculateOffAndAlertCount(MachinePart machinePart) {
		double getLife = machinePart.getLife();
		double multiplayingFactor = machinePart.getMultiplying_factor();

		double offCountDouble = Math.round(getLife / multiplayingFactor);

		long offCount = (long) offCountDouble;

		// long offCount = machinePart.getLife() /
		// machinePart.getMultiplying_factor();

		double alertGneCount = machinePart.getAlert_gen_count();

		double alertCountInDouble = Math.round(alertGneCount / multiplayingFactor);

		long AlertCount = (long) alertCountInDouble;

		machinePart.setOff_at_count(offCount);
		machinePart.setAlert_at_count(AlertCount);
		machinePart.setFinal_life(machinePart.getLife());

	}

	// ---------------------- basic methods----------------------------//

	// public void
	// calculateOffAlertandFinalLifeCountForExistingMachinePart(MachinePart
	// newPart,
	// MachinePart existingPart) {
	//
	// // if any of the part is already replaced
	// if (existingPart.getPart_replace_count() > 0 ||
	// existingPart.getPart_life_extend_count() > 0) {
	//
	// // it means part life increased
	// if (newPart.getLife() != existingPart.getLife()) {
	//
	// // resetting off_at_count
	// long off_at_count = existingPart.getOff_at_count()
	// - (existingPart.getLife() / existingPart.getMultiplying_factor());
	//
	// off_at_count = off_at_count + (newPart.getLife() /
	// newPart.getMultiplying_factor());
	//
	// newPart.setOff_at_count(off_at_count);
	//
	// // resetting alert_at_count
	// long alert_at_count = existingPart.getAlert_at_count()
	// - (existingPart.getLife() / existingPart.getMultiplying_factor());
	//
	// alert_at_count = alert_at_count + (newPart.getLife() /
	// newPart.getMultiplying_factor());
	//
	// newPart.setAlert_at_count(alert_at_count);
	//
	// // resetting final_life
	// long final_life = existingPart.getFinal_life() - existingPart.getLife();
	//
	// final_life = final_life + newPart.getLife();
	//
	// newPart.setFinal_life(final_life);
	// } else {
	// // if life has not been changed then need to do nothing
	// calculateOffAndAlertCount(newPart);
	// }
	//
	// } else {
	// // if no part replace yet then need to do nothing
	// calculateOffAndAlertCount(newPart);
	// }
	//
	// }

	public void calculateOffAlertandFinalLifeCountForExistingMachinePart(MachinePart newPart,
			MachinePart existingPart) {

		// if any of the part is already replaced
		if (existingPart.getPart_replace_count() > 0 || existingPart.getPart_life_extend_count() > 0) {

			// it means part life increased
			if (newPart.getLife() != existingPart.getLife()) {

				// resetting off_at_count
				double getOff_at_count_ex = existingPart.getOff_at_count();
//				double getOff_at_count_ex = existingPart.getPredicted_life();
				double getLife_ex = existingPart.getLife();
				double getMultiplying_factor_ex = existingPart.getMultiplying_factor();
				double getAlert_at_count_ex = existingPart.getAlert_at_count();

				double getOff_at_count_new = newPart.getOff_at_count();
//				double getOff_at_count_new = newPart.getPredicted_life();
				double getLife_new = newPart.getLife();
				double getMultiplying_factor_new = newPart.getMultiplying_factor();

				double off_at_count_double = getOff_at_count_ex - (Math.round(getLife_ex / getMultiplying_factor_ex));

				off_at_count_double = off_at_count_double + (Math.round(getLife_new / getMultiplying_factor_new));

				long off_at_count = (long) off_at_count_double;

				newPart.setOff_at_count(off_at_count);

				// resetting alert_at_count
				double alert_at_count_double = getAlert_at_count_ex
						- (Math.round(getLife_ex / getMultiplying_factor_ex));

				alert_at_count_double = alert_at_count_double + (Math.round(getLife_new / getMultiplying_factor_new));

				long alert_at_count = (long) alert_at_count_double;

				newPart.setAlert_at_count(alert_at_count);

				// resetting final_life
				long final_life = existingPart.getFinal_life() - existingPart.getLife();

				final_life = final_life + newPart.getLife();

				newPart.setFinal_life(final_life);
			} else {
				// if life has not been changed then need to do nothing
				// Monica commented below line.
				// calculateOffAndAlertCount(newPart);
			}

		} else {
			// if no part replace yet then need to do nothing
			calculateOffAndAlertCount(newPart);
		}

	}

	public boolean deleteEmails(int machineId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			ArrayList<Email> emailList = getEmailIds(machineId);
			if (emailList != null) {
				for (int i = 0; i < emailList.size(); i++) {
					try {
						session.delete(emailList.get(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			transaction.commit();
			session.close();
			return true;
		} catch (Exception e) {
			transaction.rollback();
			session.close();
		}
		return false;
	}

	public boolean deleteNoficiations(int machineId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {

			String qryString = "delete from notification where machine_id = " + machineId;
			SQLQuery query = session.createSQLQuery(qryString);
			int count = query.executeUpdate();

			transaction.commit();
			session.close();
			return true;
		} catch (Exception e) {
			transaction.rollback();
			session.close();
		}
		return false;
	}

	public boolean deleteMachineAndItsParts(Machine machine) {
		Session session = null;
		Transaction transaction = null;
		try {

			// deleting machine parts and it's history
			for (int i = 0; i < machine.getParts().size(); i++) {
				deleteMachinePart(machine.getParts().get(i));
			}

			// delete emails
			deleteEmails(machine.getId());

			// delete notification entry belonging to that machine
			deleteNoficiations(machine.getId());

			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			// deleting machine
			session.delete(machine);

			transaction.commit();
			session.close();
			return true;
		} catch (Exception e) {
			transaction.rollback();
			session.close();
			return false;
		}

	}

	/**
	 * method to delete machine part.. Here make sure machine part having it's
	 * id
	 * 
	 * @param machinePart
	 */
	public void deleteMachinePart(MachinePart machinePart) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			ArrayList<MachinePartHistory> machinePartHistoryList = getMachinePartHistoryByMachinePartId(
					machinePart.getId());
			// first we need to delete its history
			for (int i = 0; i < machinePartHistoryList.size(); i++) {
				session.delete(machinePartHistoryList.get(i));
			}
			// delete the part now
			session.delete(machinePart);
			transaction.commit();
			session.close();
		} catch (Exception e) {
			transaction.rollback();
			session.close();
		}
	}

	public MachinePart addOrUpdateMachinePart(MachinePart machinePart, boolean isUpdating) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			if (isUpdating) {
				session.saveOrUpdate(machinePart);
				transaction.commit();
				session.close();

			} else {
				int id = (Integer) session.save(machinePart);
				machinePart.setId(id);
				transaction.commit();
				session.close();
				addMachinePartHistory(machinePart, Constants.STRING_NEW_PART_INSTALLED, getCurrentTimeStamp());
			}

		} catch (Exception e) {
			transaction.rollback();
			session.close();
			machinePart = null;
		}
		return machinePart;

	}

	public ArrayList<MachinePartHistory> getMachinePartHistoryByMachinePartId(int machinePartId) {
		ArrayList<MachinePartHistory> machinePartHistoryList = new ArrayList<>();
		Session session = sessionFactory.openSession();
		try {
			String qryString = "from MachinePartHistory where part_id=" + machinePartId;
			machinePartHistoryList = (ArrayList<MachinePartHistory>) session.createQuery(qryString).list();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return machinePartHistoryList;

	}

	public Machine addOrUpdateMachine(Machine machine, boolean isUpdating) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			if (isUpdating) {
				session.saveOrUpdate(machine);
			} else {
				int id = (Integer) session.save(machine);
				machine.setId(id);
			}
			transaction.commit();
			session.close();

		} catch (Exception e) {
			transaction.rollback();
			session.close();
			machine = null;
		}
		return machine;

	}

	public BaseResponseModel checkIfMachineAlreadyExist(Machine machine) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "from Machine where machine_name =?";

			Query query = session.createQuery(qryString);
			Machine existingMachine = (Machine) query.setString(0, machine.getMachine_name()).uniqueResult();

			if (existingMachine != null) {
				return formResponseModel(false, "Machine with this name already exist.", null);
			}

			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return null;
	}

	public BaseResponseModel checkIfDCDAlreadyExistExceptSelfMachine(Machine machine) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "FROM Machine WHERE dcd_linked_ip = ? AND id != ?";

			Query query = session.createQuery(qryString);
			Machine existingMachine = (Machine) query.setString(0, machine.getDcd_linked_ip())
					.setInteger(1, machine.getId()).uniqueResult();

			if (existingMachine != null) {
				return formResponseModel(false, "DCD ip already assinged to some other machine.", null);
			}

			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return null;
	}

	public BaseResponseModel checkMachineIdIsPresentOnNotificationOrNot(Machine machine) {

		Session session = sessionFactory.openSession();

		try {

			String qryString = "select * from notification where machine_id = " + machine.getId();
			SQLQuery query = session.createSQLQuery(qryString);
			// int count = query.executeUpdate();
			// List<Notification> list = query.setBoolean(0, false).list();
			List list = query.list();

			// Machine existingMachine = (Machine) query.setString(0,
			// machine.getDcd_linked_ip()).uniqueResult();

			if (!list.isEmpty()) {
				return formResponseModel(false,
						"Please take the action of this Machine from Notification first, then you would be able to make the changes.",
						null);
			}

			session.close();
		} catch (Exception e) {
			session.close();
		}
		return null;
	}

	public BaseResponseModel checkIfDCDAlreadyExist(Machine machine) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "from Machine where dcd_linked_ip =?";

			Query query = session.createQuery(qryString);
			Machine existingMachine = (Machine) query.setString(0, machine.getDcd_linked_ip()).uniqueResult();

			if (existingMachine != null) {
				return formResponseModel(false, "DCD ip already assinged to some other machine.", null);
			}

			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return null;
	}

	@Override
	public BaseResponseModel addLocation(Location locObj) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {

			int id = (Integer) session.save(locObj);
			locObj.setId(id);
			transaction.commit();
			session.close();

		} catch (Exception e) {
			transaction.rollback();
			session.close();
		}
		return formResponseModel(true, "Location Added successfully", locObj);
	}

	@Override
	public BaseResponseModel fetcheNoficationCount() {
		NotificationCountDo notificationCountDo = new NotificationCountDo();
		Session session = sessionFactory.openSession();
		try {

			// get new notification count which are not read yet
			String qryString = "from Notification where notfi_read =?";

			Query query = session.createQuery(qryString);
			List<Notification> notificationsList = query.setBoolean(0, false).list();

			// get machine names which are not read and turned off
			if (notificationsList != null && notificationsList.size() > 0) {
				notificationCountDo.setCount(notificationsList.size());
				ArrayList<String> turnedOffMachines = new ArrayList<>();
				for (int i = 0; i < notificationsList.size(); i++) {
					if (!notificationsList.get(i).isOn()) {
						turnedOffMachines.add(notificationsList.get(i).getMachine_name());
					}
				}
				notificationCountDo.setTurned_off_machine_names(turnedOffMachines);
			}
			session.close();
			return formResponseModel(true, "Notification Count", notificationCountDo);
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		return formResponseModel(false, "Something went wrong !!", null);
	}

	@Override
	public BaseResponseModel fetcheAllNofications() {
		Session session = sessionFactory.openSession();
		try {
			// get all the notification
			String qryString = "from Notification";
			Query query = session.createQuery(qryString);
			List<Notification> notificationsList = query.list();
			session.close();

			ArrayList<Machine> machineList = new ArrayList<>();
			// getting machines by machine id
			if (notificationsList != null && notificationsList.size() > 0) {
				for (int i = 0; i < notificationsList.size(); i++) {
					machineList.add(getMachineById(notificationsList.get(i).getMachine_id()));
				}

				calculateMachinePartsWhichNeedsAttention(machineList);
			}

			if (machineList.size() > 0) {
				return formResponseModel(true, "Machines need your attention", machineList);
			} else {
				return formResponseModel(false, "No notification found", null);
			}

		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		return formResponseModel(false, "Something went wrong !!", null);
	}

	private void calculateMachinePartsWhichNeedsAttention(List<Machine> machineList) {
		for (int i = 0; i < machineList.size(); i++) {
			List<MachinePart> machineParts = getMachineParts(machineList.get(i).getId());

			int noOfPartsNeedsAttention = 0;

			for (int j = 0; j < machineParts.size(); j++) {
				MachinePart part = machineParts.get(j);
				if (machineList.get(i).getCurrent_count() >= part.getOff_at_count()) {
					noOfPartsNeedsAttention++;
				} else if (machineList.get(i).getCurrent_count() >= part.getAlert_at_count()) {
					noOfPartsNeedsAttention++;
				}
			}

			if (noOfPartsNeedsAttention == 0) {
				if (machineList.get(i).getStatus().equalsIgnoreCase(Constants.KEY_MACHINE_ON)) {
					machineList.get(i).setAlert_details("Running Fine");
				} else {
					machineList.get(i).setAlert_details("Stopped with no warning");
				}
			} else if (noOfPartsNeedsAttention > 1) {
				machineList.get(i).setAlert_details(noOfPartsNeedsAttention + " parts need your attention");
			} else {
				machineList.get(i).setAlert_details(noOfPartsNeedsAttention + " part need your attention");
			}

		}
	}

	@Override
	public BaseResponseModel fetchAllNotificationsOfAMachine(int machineId) {
		try {
			Machine machine = getMachineWithItsPartRequiredAttention(machineId);

			return formResponseModel(true, "Machines parts need your attention", machine);
		} catch (Exception e) {
			e.printStackTrace();
			return formResponseModel(false, e.getMessage(), null);
		}
	}

	private Machine getMachineWithItsPartRequiredAttention(int machineId) {
		Machine machine = getMachineById(machineId);
		List<MachinePart> allMachineParts = getMachineParts(machineId);

		ArrayList<MachinePart> machinePartsNeedAttention = new ArrayList<>();

		for (int j = 0; j < allMachineParts.size(); j++) {
			MachinePart part = allMachineParts.get(j);
			if (machine.getCurrent_count() >= part.getOff_at_count()) {
				machinePartsNeedAttention.add(part);
			} else if (machine.getCurrent_count() >= part.getAlert_at_count()) {
				machinePartsNeedAttention.add(part);
			}
			
//			MachinePart part1 = allMachineParts.get(0);
//			MachinePart part2 = allMachineParts.get(1);
//			MachinePart part3 = allMachineParts.get(2);
			
//			if(part1.getOff_at_count() > part2.getOff_at_count()){
//				System.out.println("inside IF");
//			}
		}

		machine.setParts(machinePartsNeedAttention);
		return machine;
	}

	@Override
	public BaseResponseModel setAllNotificationAsRead() {
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();
			String qryString = "update notification n set n.notfi_read = true";
			SQLQuery query = session.createSQLQuery(qryString);
			int count = query.executeUpdate();
			System.out.print(">>count = " + count);
			transaction.commit();
			session.close();
			return formResponseModel(true, "Read count set successfully", null);
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
			return formResponseModel(false, "Something went wrong", e.getMessage());
		}
	}

	@Override
	public BaseResponseModel replaceMachinePart(int part_id, int current_count, String action) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();

			if (action.equals("update")) {
				MachinePart machinePart = getMachinePartById(part_id);
				MachinePartCopy machinePartCopy = setMachinePartCopy(machinePart);

				double alertCount = machinePart.getAlert_gen_count();
				double offCount = machinePart.getLife();
				long replace_count = (machinePart.getPart_replace_count() + 1);
				long final_life = ((current_count * machinePart.getMultiplying_factor()) + (machinePart.getLife()));
				long off_at_count = (Math.round(offCount / machinePart.getMultiplying_factor()) + current_count);
				
				long alert_at_count = (Math.round(alertCount / machinePart.getMultiplying_factor()) + current_count);
				
				String qryString = "update machine_parts machinepart set machinepart.life_exhausted_till_date = "
						+ machinePart.getLife_exhausted_till_date() + "" + ", machinepart.life_extended = false,"
						+ " machinepart.part_replace_count = " + replace_count + ", machinepart.final_life = "
						+ final_life + ", machinepart.off_at_count = " + off_at_count
						+ ", machinepart.alert_at_count = " + alert_at_count + ", machinepart.temp_life = "+ final_life +", machinepart.predicted_life = "+ offCount +", excl_part = 0 where machinepart.id = " + part_id;

				SQLQuery query = session.createSQLQuery(qryString);
				query.executeUpdate();
				//Added new code start
				if(part_id == 1){
					String qryString1 = "update machine_parts machinepart set machinepart.p6 = 0, machinepart.r4 = 0 where machinepart.id = 2" ;

					SQLQuery query1 = session.createSQLQuery(qryString1);
					query1.executeUpdate();
					
					String qryString2 = "update machine_parts machinepart set machinepart.p4 = 0, machinepart.r6 = 0 where machinepart.id = 3" ;

					SQLQuery query2 = session.createSQLQuery(qryString2);
					query2.executeUpdate();
				}
				if(part_id == 2){
					String qryString1 = "update machine_parts machinepart set machinepart.p2 = 0, machinepart.r1 = 0 where machinepart.id = 1" ;

					SQLQuery query1 = session.createSQLQuery(qryString1);
					query1.executeUpdate();
					
					String qryString2 = "update machine_parts machinepart set machinepart.p3 = 0,machinepart.r5 = 0 where machinepart.id = 3" ;

					SQLQuery query2 = session.createSQLQuery(qryString2);
					query2.executeUpdate();
				}
				if(part_id == 3){
					String qryString1 = "update machine_parts machinepart set machinepart.p1 = 0, machinepart.r2 = 0 where machinepart.id = 1" ;

					SQLQuery query1 = session.createSQLQuery(qryString1);
					query1.executeUpdate();
					
					String qryString2 = "update machine_parts machinepart set machinepart.p5 = 0, machinepart.r3 = 0 where machinepart.id = 2" ;

					SQLQuery query2 = session.createSQLQuery(qryString2);
					query2.executeUpdate();
				}
				//Added new code end
				
				transaction.commit();
				// session.close();

				// give response of machine parts needed attention if any still
				// needs
				Machine machine = getMachineWithItsPartRequiredAttention(machinePart.getMachine_id());

				if (machine.getPartsCopy() != null && machine.getPartsCopy().size() > 0) {
					
					//update predict value
					
					return formResponseModel(true, "Part Replaced Successfully, " + machine.getPartsCopy().size()
							+ " more part needs your attention", machine);
				} else {

					// update status of notification table to in progress
					updateMachineNotifiction(machine.getId(), Constants.STATUS_INPROGRESS);

					return formResponseModel(true,
							"Part Replaced Successfully, No more part of this machine requires your attention !!",
							null);
				}
			}
			if (action.equals("resolve")) {
				// fetch machinePartCopy data
				MachinePartCopy machinePartsCopy = getMachinePartsCopyData(part_id);

				if (machinePartsCopy != null) {
					session.delete(machinePartsCopy);
					transaction.commit();
				}

				MachinePart machinePart = getMachinePartById(part_id);

				// adding history that new part installed
				addMachinePartHistory(machinePart, Constants.STRING_NEW_PART_INSTALLED, getCurrentTimeStamp());

				// give response of machine parts needed attention if any
				// still needs
				Machine machine = getMachineWithItsPartRequiredAttention(machinePart.getMachine_id());

				if (machine.getParts() != null && machine.getParts().size() > 0) {
					return formResponseModel(true, "Part Replaced Successfully, " + machine.getParts().size()
							+ " more part needs your attention", machine);
				} else {

					// // Delete entry from notification table
					deleteMachineNotifiction(machine.getId());
					//
					machinePart.setExcl_part(0);
					return formResponseModel(true,
							"Part Replaced Successfully, No more part of this machine requires your attention !!",
							null);
				}
			}
			if (action.equals("rollback")) {
				// fetch machinePartCopy data
				MachinePartCopy machinePartsCopy = getMachinePartsCopyData(part_id);

				if (machinePartsCopy != null) {

					// set machinePart
					MachinePart machinePart = setMachinePart(machinePartsCopy, part_id);
					//session.delete(machinePartsCopy);
					//transaction.commit();

					// update status of notification table to active
					updateMachineNotifiction(machinePart.getMachine_id(), Constants.STATUS_ACTIVE);

					return formResponseModel(true, "Rollbacked Successfully !!", null);
				}
			}

			return formResponseModel(false, "Ivalid Action !!", null);

		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			session.close();
			e.printStackTrace();
			return formResponseModel(false, "Something went wrong", e.getMessage());
		}
	}

	@Override
	public BaseResponseModel extendMachinePartLife(int part_id, String action) {
		MachinePart machinePart = getMachinePartById(part_id);

		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		try {

			if (action.equals("update")) {
				if (machinePart.getLife_exten_limit() <= 0 || machinePart.isLife_extended() == true) {
					return formResponseModel(false, "Have no extension to increase life.", null);
				} else {

					// MachinePart machinePart = getMachinePartById(part_id);
					MachinePartCopy machinePartCopy = setMachinePartCopy(machinePart);

					transaction = session.beginTransaction();
					double offAtCount = machinePart.getLife_exten_limit();
					double alertAtCount = (machinePart.getFinal_life() + machinePart.getExten_life_alert_count());

					long life_extn_count = (machinePart.getPart_life_extend_count() + 1);
					long final_life = (machinePart.getFinal_life() + machinePart.getLife_exten_limit());
					long off_at_count = (machinePart.getOff_at_count()
							+ (Math.round(offAtCount / machinePart.getMultiplying_factor())));
					long alert_at_count = (Math.round(alertAtCount / machinePart.getMultiplying_factor()));

					String qryString = "update machine_parts machinepart set machinepart.life_extended = true,"
							+ " machinepart.part_life_extend_count = " + life_extn_count + ", machinepart.final_life = "
							+ final_life + ", machinepart.off_at_count = " + off_at_count
							+ ", machinepart.alert_at_count = " + alert_at_count + ", excl_part = 1  where machinepart.id = " + part_id;

					SQLQuery query = session.createSQLQuery(qryString);
					query.executeUpdate();
					transaction.commit();
					// session.close();

					// give response of machine parts needed attention if any
					// still
					// needs
					Machine machine = getMachineWithItsPartRequiredAttention(machinePart.getMachine_id());

					if (machine.getParts() != null && machine.getParts().size() > 0) {
						return formResponseModel(true, "Life extended successfully, " + machine.getParts().size()
								+ " more part needs your attention", machine);
					} else {

						// update status of notification table to in progress
						updateMachineNotifiction(machine.getId(), Constants.STATUS_INPROGRESS);
//						//
//						machinePart.setExcl_part(1);
						return formResponseModel(true,
								"Life extended successfully, No more part of this machine requires your attention !!",
								null);
					}
				}
			}
			if (action.equals("resolve")) {
				// fetch machinePartCopy data
				MachinePartCopy machinePartsCopy = getMachinePartsCopyData(part_id);

				if (machinePartsCopy != null) {
					transaction = session.beginTransaction();
					session.delete(machinePartsCopy);
					transaction.commit();
				}

				// adding history that life a part extended
				addMachinePartHistory(machinePart, Constants.STRING_LIFE_EXTENDED, getCurrentTimeStamp());

				// give response of machine parts needed attention if any
				// still needs
				Machine machine = getMachineWithItsPartRequiredAttention(machinePart.getMachine_id());

				if (machine.getParts() != null && machine.getParts().size() > 0) {
					return formResponseModel(true, "Life extended successfully, " + machine.getParts().size()
							+ " more part needs your attention", machine);
				} else {

					// Delete entry from notification table
					deleteMachineNotifiction(machine.getId());

					return formResponseModel(true,
							"Life extended successfully, No more part of this machine requires your attention !!",
							null);
				}
			}
			if (action.equals("rollback")) {
				// fetch machinePartCopy data
				MachinePartCopy machinePartsCopy = getMachinePartsCopyData(part_id);

				if (machinePartsCopy != null) {

					// set machinePart
					MachinePart machinePart2 = setMachinePart(machinePartsCopy, part_id);

					// update status of notification table to active
					updateMachineNotifiction(machinePart2.getMachine_id(), Constants.STATUS_ACTIVE);

					return formResponseModel(true, "Rollbacked Successfully !!", null);
				}
			}
			return formResponseModel(false, "Ivalid Action !!", null);

		} catch (Exception e) {
			session.close();
			e.printStackTrace();
			return formResponseModel(false, "Something went wrong", e.getMessage());
		}

	}

	public void deleteMachineNotifiction(int machineId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "DELETE FROM notification WHERE machine_id =" + machineId;
			// String qryString = "update notification notification set
			// notification.status = "
			// + Constants.STATUS_DEACTIVE + " WHERE machine_id =" + machineId;

			SQLQuery query = session.createSQLQuery(qryString);
			int count = query.executeUpdate();
			System.out.print(">>count = " + count);
			transaction.commit();
			session.close();
		} catch (Exception e) {
			transaction.rollback();
			session.close();
			e.printStackTrace();
		}
	}

	public void updateMachineNotifiction(int machineId, int status) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "update notification notifications set notifications.status = " + status
					+ " WHERE notifications.machine_id =" + machineId;

			SQLQuery query = session.createSQLQuery(qryString);
			int count = query.executeUpdate();
			System.out.print(">>count = " + count);
			transaction.commit();
			session.close();
		} catch (Exception e) {
			transaction.rollback();
			session.close();
			e.printStackTrace();
		}
	}

	/**
	 * Resetting machine and it's parts
	 */
	@Override
	public BaseResponseModel resetMachine(int machineId) {

		try {
			// reset machine life count
			resetMachineById(machineId);

			// reset each parts exhausted life
			resetMachineParts(machineId);

			// delete machine history
			deleteMachinePartHistory(machineId);

			// delete notification of that machine if any exist
			deleteMachineNotifiction(machineId);

		} catch (Exception e) {
			return formResponseModel(false, "Something went wrong >>error>>" + e.getMessage(), e.getMessage());
		}

		// sending fresh machine after resetting
		Machine machine = getMachineById(machineId);
		List<MachinePart> machineParts = getMachineParts(machineId);
		machine.setParts(machineParts);

		// add new history as machine reset for each part
		for (int i = 0; i < machineParts.size(); i++) {
			addMachinePartHistory(machineParts.get(i), Constants.STRING_NEW_PART_INSTALLED + " (Machine Reset)",
					getCurrentTimeStamp());
		}

		return formResponseModel(true, "Machine Reset Successfully", machine);
	}

	private void deleteMachinePartHistory(int machineId) throws Exception {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "delete from machine_parts_history where machine_id = " + machineId;
			SQLQuery query = session.createSQLQuery(qryString);
			int count = query.executeUpdate();

			System.out.print(">> history delete count >> " + count);
			transaction.commit();
			session.close();
		} catch (Exception e) {
			transaction.rollback();
			session.close();
			throw e;
		}
	}

	private void resetMachineById(int machineId) throws Exception {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "UPDATE machine SET current_count = 0 WHERE id = " + machineId;

			SQLQuery query = session.createSQLQuery(qryString);
			query.executeUpdate();
			transaction.commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
			session.close();
			throw e;
		}

	}

	private void resetMachineParts(int machineId) throws Exception {
		List<MachinePart> machineParts = getMachineParts(machineId);

		for (int i = 0; i < machineParts.size(); i++) {
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			try {
				int off_at_count = (int) (Math
						.round(machineParts.get(i).getLife() / machineParts.get(i).getMultiplying_factor()));

				int atert_at_count = (int) (Math
						.round(machineParts.get(i).getAlert_gen_count() / machineParts.get(i).getMultiplying_factor()));

				String qryString = "UPDATE machine_parts SET life_exhausted_till_date = 0, off_at_count ="
						+ off_at_count + ", alert_at_count =" + atert_at_count + ", final_life ="
						+ machineParts.get(i).getLife() + ", "
						+ "life_extended =0, part_replace_count =0, part_life_extend_count =0 WHERE id="
						+ machineParts.get(i).getId();

				SQLQuery query = session.createSQLQuery(qryString);
				query.executeUpdate();
				transaction.commit();
				session.close();
			} catch (Exception e) {
				e.printStackTrace();
				transaction.rollback();
				session.close();
				throw e;
			}
		}

	}

	/**
	 * Update the IP of selected machine
	 */
	@Override
	public BaseResponseModel updateIp(String dcd_linked_ip, int machineId) {

		BaseResponseModel dcdIPLinkedResponse = checkIfDCDAlreadyExistIncludeSelfMachine(dcd_linked_ip, machineId);

		if (dcdIPLinkedResponse != null) {
			return dcdIPLinkedResponse;
		} else {
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			try {

				String qryString = "UPDATE machine SET dcd_linked_ip ='" + dcd_linked_ip
						+ "', handshake_status = 1  WHERE id=" + machineId;

				SQLQuery query = session.createSQLQuery(qryString);
				query.executeUpdate();
				transaction.commit();
				session.close();
				return formResponseModel(true, "Machine IP updated successfully", null);
			} catch (Exception e) {
				e.printStackTrace();
				transaction.rollback();
				session.close();
				return formResponseModel(false, "Machine IP updationn get failed", null);
			}
		}
	}

	public BaseResponseModel checkIfDCDAlreadyExistIncludeSelfMachine(String dcd_linked_ip, int machineId) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			// Criteria machineCriteria = session.createCriteria(Machine.class);
			// machineCriteria.add(Restrictions.eq("dcd_linked_ip",
			// dcd_linked_ip));
			// Machine existingMachine = (Machine)
			// machineCriteria.uniqueResult();

			boolean isDcdLinkIpEsist = session.createCriteria(Machine.class)
					.add(Restrictions.eq("dcd_linked_ip", dcd_linked_ip)).uniqueResult() != null;

			if (isDcdLinkIpEsist) {
				return formResponseModel(false, "DCD IP already assinged to another machine.", null);
			}

			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return null;
	}

}
