package com.vyako.smarttbm.dao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.vyako.smarttbm.constants.Constants;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.entity.Email;
import com.vyako.smarttbm.entity.Machine;
import com.vyako.smarttbm.entity.MachinePart;
import com.vyako.smarttbm.entity.MachinePartCopy;
import com.vyako.smarttbm.entity.MachinePartHistory;

public class BaseDAOImpl {

	@Autowired
	protected SessionFactory sessionFactory;

	protected BaseResponseModel formResponseModel(boolean isSuccess, String msg, Object responseObj) {
		BaseResponseModel baseResponseModel = new BaseResponseModel();
		baseResponseModel.setSuccess(isSuccess);
		baseResponseModel.setMsg(msg);
		baseResponseModel.setResponse(responseObj);
		return baseResponseModel;
	}

	protected String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	protected String getUnixTime() {
		return (System.currentTimeMillis() / 1000L) + "";
	}

	protected String formResponse(int machineId, long mainCount, long currentCount, long alertCount,
			int handshakeStatus, boolean giveStartCmd) {

		String responseString = Constants.KEY_MACHINE_ID + "=" + machineId + "," + Constants.KEY_MAIN_COUNT + "="
				+ mainCount + "," + Constants.KEY_CURRENT_COUNT + "=" + currentCount + "," + Constants.KEY_ALERT_COUNT
				+ "=" + alertCount;

		if (giveStartCmd) {
			if (handshakeStatus == 1) {
				responseString = responseString + ("," + Constants.KEY_CMD + "=REPLACE");
				updateHandShakeStatus(machineId, 0);
			} else {
				responseString = responseString + ("," + Constants.KEY_CMD + "=START");
			}

			responseString = responseString + ("," + Constants.KEY_UNIX_TIME + "=" + getUnixTime());
		} else {
			responseString = responseString + ("," + Constants.KEY_CMD + "=NULL");
			responseString = responseString + ("," + Constants.KEY_UNIX_TIME + "=NULL");
		}
		return responseString;
	}

	public void updateHandShakeStatus(int machineId, int value) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {

			Query updateQuery = session
					.createQuery("update Machine set handshake_status = :status" + " where id = :id");
			updateQuery.setParameter("status", value);
			updateQuery.setParameter("id", machineId);
			updateQuery.executeUpdate();
			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
		}
	}

	public List<MachinePart> getMachineParts(int machineId) {
		Session session = sessionFactory.openSession();
		List<MachinePart> machineParts = new ArrayList<MachinePart>();
		try {

			String qryString = "from MachinePart where machine_id=" + machineId;
			machineParts = session.createQuery(qryString).list();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return machineParts;
	}

	// public List<MachinePartCopy> getMachinePartsCopy(int machineId) {
	// Session session = sessionFactory.openSession();
	// List<MachinePartCopy> machinePartsCopy = new
	// ArrayList<MachinePartCopy>();
	// try {
	//
	// String qryString = "from MachinePartCopy where machine_id=" + machineId;
	// machinePartsCopy = session.createQuery(qryString).list();
	// session.close();
	// } catch (Exception e) {
	// session.close();
	// }
	// return machinePartsCopy;
	// }

	public MachinePart getMachinePartById(int partId) {
		Session session = sessionFactory.openSession();
		MachinePart machinePart = null;
		try {

			String qryString = "from MachinePart where id=" + partId;
			machinePart = (MachinePart) session.createQuery(qryString).uniqueResult();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return machinePart;
	}

	public void addMachinePartHistory(MachinePart machinePart, String statusMsg, String historyTime) {
		MachinePartHistory machinePartHistory = new MachinePartHistory();
		machinePartHistory.setPart_id(machinePart.id);
		machinePartHistory.setMachine_id(machinePart.machine_id);
		machinePartHistory.setSpare_part_name(machinePart.spare_part_name);
		machinePartHistory.setLife(machinePart.life);
		machinePartHistory.setMultiplying_factor(machinePart.multiplying_factor);
		machinePartHistory.setAlert_gen_count(machinePart.alert_gen_count);
		machinePartHistory.setProvision_of_life_exten(machinePart.provision_of_life_exten);
		machinePartHistory.setLife_exhausted_till_date(machinePart.life_exhausted_till_date);
		machinePartHistory.setLife_exten_limit(machinePart.life_exten_limit);
		machinePartHistory.setExten_life_alert_count(machinePart.exten_life_alert_count);
		machinePartHistory.setFinal_life(machinePart.final_life);
		machinePartHistory.setOff_at_count(machinePart.off_at_count);
		machinePartHistory.setAlert_at_count(machinePart.alert_at_count);
		machinePartHistory.setActivity_details(statusMsg);
		machinePartHistory.setAdded_on(historyTime);
		machinePartHistory.setPart_replace_count(machinePart.part_replace_count);
		machinePartHistory.setPart_life_extend_count(machinePart.part_life_extend_count);

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.save(machinePartHistory);
			transaction.commit();
			session.close();

		} catch (Exception e) {
			transaction.rollback();
			session.close();
		}

	}

	// public void removeMachinePartHistory() {
	//
	// Session session = sessionFactory.openSession();
	// Transaction transaction = session.beginTransaction();
	//
	// try {
	// Query query = session.createQuery("from MachinePartHistory order by id
	// DESC");
	// query.setMaxResults(1);
	// MachinePartHistory uniqueResult = (MachinePartHistory)
	// query.uniqueResult();
	// // System.out.println(uniqueResult);
	// session.delete(uniqueResult);
	// transaction.commit();
	// session.close();
	//
	// } catch (Exception e) {
	// transaction.rollback();
	// session.close();
	// }
	//
	// }

	public MachinePartCopy setMachinePartCopy(MachinePart machinePart) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();

		MachinePartCopy machinePartCopy = new MachinePartCopy();

		machinePartCopy.setMachine_id(machinePart.getMachine_id());
		machinePartCopy.setPart_id(machinePart.getId());
		machinePartCopy.setSpare_part_name(machinePart.getSpare_part_name());
		machinePartCopy.setLife(machinePart.getLife());
		machinePartCopy.setMultiplying_factor(machinePart.getMultiplying_factor());
		machinePartCopy.setAlert_gen_count(machinePart.getAlert_gen_count());
		machinePartCopy.setProvision_of_life_exten(machinePart.getProvision_of_life_exten());
		machinePartCopy.setLife_exhausted_till_date(machinePart.getLife_exhausted_till_date());
		machinePartCopy.setLife_exten_limit(machinePart.getLife_exten_limit());
		machinePartCopy.setExten_life_alert_count(machinePart.getExten_life_alert_count());
		machinePartCopy.setFinal_life(machinePart.getFinal_life());
		machinePartCopy.setOff_at_count(machinePart.getOff_at_count());
		machinePartCopy.setAlert_at_count(machinePart.getAlert_at_count());
		machinePartCopy.setPart_replace_count(machinePart.getPart_replace_count());
		machinePartCopy.setPart_life_extend_count(machinePart.getPart_life_extend_count());
		machinePartCopy.setLife_extended(machinePart.isLife_extended());

		// Added new code start
		machinePartCopy.setPredicted_life(machinePart.getPredicted_life());
		machinePartCopy.setTemp_life(machinePart.getTemp_life());
		machinePartCopy.setExcl_part(machinePart.getExcl_part());
		machinePartCopy.setP1(machinePart.getP1());
		machinePartCopy.setP2(machinePart.getP2());
		machinePartCopy.setP3(machinePart.getP1());
		machinePartCopy.setP4(machinePart.getP1());
		machinePartCopy.setP5(machinePart.getP1());
		machinePartCopy.setP6(machinePart.getP1());
		
		machinePartCopy.setR1(machinePart.getR1());
		machinePartCopy.setR2(machinePart.getR2());
		machinePartCopy.setR3(machinePart.getR3());
		machinePartCopy.setR4(machinePart.getR4());
		machinePartCopy.setR5(machinePart.getR5());
		machinePartCopy.setR6(machinePart.getR6());
		// Added new code end

		session.save(machinePartCopy);
		transaction.commit();

		return machinePartCopy;
	}

	public MachinePart setMachinePart(MachinePartCopy machinePartCopy, int part_id) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();

		MachinePart machinePart = getMachinePartById(part_id);

		machinePart.setLife_exhausted_till_date(machinePartCopy.getLife_exhausted_till_date());
		machinePart.setFinal_life(machinePartCopy.getFinal_life());
		machinePart.setOff_at_count(machinePartCopy.getOff_at_count());
		machinePart.setAlert_at_count(machinePartCopy.getAlert_at_count());
		machinePart.setLife_extended(machinePartCopy.isLife_extended());

		machinePart.setPart_replace_count((machinePartCopy.getPart_replace_count()));
		machinePart.setPart_life_extend_count((machinePartCopy.getPart_life_extend_count()));

		// Added new code start
		machinePart.setPredicted_life(machinePartCopy.getPredicted_life());
		machinePart.setTemp_life(machinePartCopy.getTemp_life());
		machinePart.setExcl_part(machinePartCopy.getExcl_part());
		machinePart.setP1(machinePartCopy.getP1());
		machinePart.setP2(machinePartCopy.getP2());
		machinePart.setP3(machinePartCopy.getP3());
		machinePart.setP4(machinePartCopy.getP4());
		machinePart.setP5(machinePartCopy.getP5());
		machinePart.setP6(machinePartCopy.getP6());
		machinePart.setR1(machinePartCopy.getR1());
		machinePart.setR2(machinePartCopy.getR2());
		machinePart.setR3(machinePartCopy.getR3());
		machinePart.setR4(machinePartCopy.getR4());
		machinePart.setR5(machinePartCopy.getR5());
		machinePart.setR6(machinePartCopy.getR6());
		// Added new code end

		session.update(machinePart);
		session.delete(machinePartCopy);
		transaction.commit();
		return machinePart;
	}

	public MachinePartCopy getMachinePartsCopyData(int partId) {
		Session session = sessionFactory.openSession();
		MachinePartCopy machinePartsCopy = null;
		try {
			String qryString = "from MachinePartCopy where part_id=" + partId;
			machinePartsCopy = (MachinePartCopy) session.createQuery(qryString).uniqueResult();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return machinePartsCopy;
	}

	public ArrayList<Email> getEmailIds(int machineId) {
		ArrayList<Email> emailList = null;
		Session session = sessionFactory.openSession();
		try {
			String qryString = "from Email where machine_id=" + machineId;
			emailList = (ArrayList<Email>) session.createQuery(qryString).list();
			session.close();
		} catch (Exception e) {
			session.close();
		}
		return emailList;
	}

	public Machine getMachineById(int machineId) {
		Machine machine = null;
		Session session = sessionFactory.openSession();
		try {
			String qryString = "from Machine where id=" + machineId;
			machine = (Machine) session.createQuery(qryString).uniqueResult();

			// adding emails
			ArrayList<Email> emailList = getEmailIds(machine.getId());
			if (emailList != null) {
				String[] emails = new String[emailList.size()];
				for (int j = 0; j < emailList.size(); j++) {
					emails[j] = emailList.get(j).getEmail_id();
				}
				machine.setEmails(emails);
			}

			session.close();
		} catch (Exception e) {
			session.close();
		}
		return machine;
	}

}
