package com.vyako.smarttbm.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.vyako.smarttbm.constants.Constants;
import com.vyako.smarttbm.constants.CredentialConstants;
import com.vyako.smarttbm.dao.interfac.IDcdDao;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_other.MachineCycleInfo;
import com.vyako.smarttbm.do_request.CycleRequestDo;
import com.vyako.smarttbm.do_request.HandshakeRequestDo;
import com.vyako.smarttbm.entity.Email;
import com.vyako.smarttbm.entity.Machine;
import com.vyako.smarttbm.entity.MachinePart;
import com.vyako.smarttbm.entity.MachinePartHistory;
import com.vyako.smarttbm.entity.Notification;
import com.vyako.smarttbm.entity.Request;
import com.vyako.smarttbm.entity.Response;
import com.vyako.smarttbm.utils.MailManager;

@Repository
public class DcdDaoImpl extends BaseDAOImpl implements IDcdDao {

	@Autowired
	MailManager mailManager;

	@Override
	public BaseResponseModel handShakeWithMachine(HandshakeRequestDo hardsRequestDo) {
		Machine machine = getMachineByDCDIp(hardsRequestDo.getDcd_linked_ip());
		if (machine == null) {
			return formResponseModel(false, "DCD ip is not linked with Any Machine.", null);
		} else {
			MachinePart machinePartHavingLeastLife = getMachinePartHavingLeastLife(machine.getId());
			if (machinePartHavingLeastLife == null) {
				return formResponseModel(false, "No part found for the machine " + machine.getMachine_name(), null);
			} else {

				String response = formResponse(machine.getId(), machinePartHavingLeastLife.getOff_at_count(),
						machine.getCurrent_count(), machinePartHavingLeastLife.getAlert_at_count(),
						machine.getHandshake_status(), true);
				return formResponseModel(true, "", response);

			}
		}
	}

	@Override
	public BaseResponseModel sendMachineCycleInfo(CycleRequestDo cycleRequestDo) {

		if (cycleRequestDo.getCycle_info() != null) {
			MachineCycleInfo machineCycleInfo = parseMachineCycleInfo(cycleRequestDo.getCycle_info());

			// validation for wrong input from server
			if (machineCycleInfo.getCurrentCount() > getLeastOffCountOfAMachine(machineCycleInfo.getMachineId())) {

				return formResponseModel(false, "Wrong input, Machine current count should not exceed least count",
						null);
			}

			// updating machine status and count
			updateMachineStatusAndItsCount(machineCycleInfo);

			// get machine parts
			List<MachinePart> machineParts = getMachineParts(machineCycleInfo.getMachineId());

			// update exhausted life for each part
			updateExhaustedLife(machineParts, machineCycleInfo);

			// add history, notify Admin and shoot mail if required
			performHistoryNoficationAndEmailRequestIfRequired(machineParts, machineCycleInfo);

			// get machine part having least life
			MachinePart machinePartHavingLeastLife = getMachinePartHavingLeastLife(machineCycleInfo.getMachineId());

			// forming response
			String response = formResponse(machineCycleInfo.getMachineId(),
					machinePartHavingLeastLife.getOff_at_count(), machineCycleInfo.getCurrentCount(),
					machinePartHavingLeastLife.getAlert_at_count(), 0, false);
			return formResponseModel(true, "", response);

		} else {
			return formResponseModel(false, "No Cycle info in request do", null);
		}

	}

	@Override
	public BaseResponseModel resetAllMachineCycleInfo() {
		// get list of machines.
		// Machine machine = getMachineList();
		// System.out.println(machine);

		// updating machine status and count
		resetMachineStatusAndItsCount();

		// update exhausted life for each part
		// resetExhaustedLife();

		restoreMachinePart();

		// // add history, notify Admin and shoot mail if required
		// performHistoryNoficationAndEmailRequestIfRequired(machineParts,
		// machineCycleInfo);
		//
		// // get machine part having least life
		// MachinePart machinePartHavingLeastLife =
		// getMachinePartHavingLeastLife(machineCycleInfo
		// .getMachineId());
		//
		// // forming response
		// String response = formResponse(machineCycleInfo.getMachineId(),
		// machinePartHavingLeastLife.getOff_at_count(),
		// machineCycleInfo.getCurrentCount(),
		// machinePartHavingLeastLife.getAlert_at_count(), false);
		return formResponseModel(true, "Machine Reset successfully!!!", null);

	}

	private void restoreMachinePart() {
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();

			Criteria createCriteria = session.createCriteria(MachinePart.class);

			ArrayList<MachinePart> machineParts = (ArrayList<MachinePart>) createCriteria.list();
			if (machineParts != null && machineParts.size() > 0) {
				for (int index = 0; index < machineParts.size(); index++) {
					MachinePart machinePart = machineParts.get(index);
					long life = machinePart.getLife();
					long alert_gen_count = machinePart.getAlert_gen_count();

					machinePart.setFinal_life(life);
					machinePart.setOff_at_count(life);
					machinePart.setAlert_at_count(alert_gen_count);

					// Added new code start
					machinePart.setPredicted_life(life);
					machinePart.setTemp_life(life);
					machinePart.setExcl_part(Constants.VALUE_ZERO);
					machinePart.setP1(Constants.VALUE_ZERO);
					machinePart.setP2(Constants.VALUE_ZERO);
					machinePart.setP3(Constants.VALUE_ZERO);
					machinePart.setP4(Constants.VALUE_ZERO);
					machinePart.setP5(Constants.VALUE_ZERO);
					machinePart.setP6(Constants.VALUE_ZERO);
					// Added new code end

					machinePart.setPart_replace_count(Constants.VALUE_ZERO);
					machinePart.setLife_extended(false);
					machinePart.setPart_life_extend_count(Constants.VALUE_ZERO);
					machinePart.setLife_exhausted_till_date(Constants.VALUE_ZERO);
					session.update(machinePart);
				}
			}
			transaction.commit();
			session.close();

		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}

	}

	// --------------- basic methods -----------------//

	private void performHistoryNoficationAndEmailRequestIfRequired(List<MachinePart> machineParts,
			MachineCycleInfo machineCycleInfo) {
		// adding history
		for (int j = 0; j < machineParts.size(); j++) {
			MachinePart part = machineParts.get(j);
			/**
			 * --------- MACHINE LIFE OVER -------------
			 */
			if (machineCycleInfo.getCurrentCount() >= part.getOff_at_count()) {

				if (!checkIfHistoryAlreadyExist(part.getId(), (int) part.getOff_at_count(), false)) {

					// adding history that machine part life over
					addMachinePartHistory(part, Constants.STRING_LIFE_OVER, getCurrentTimeStamp());

					// notify admin
					addNofication(machineCycleInfo.getMachineId());

					// shoot mail
					shootMail(machineCycleInfo, part, true);

				}

				/**
				 * --------- MACHINE GENERATED AN ALERT -------------
				 */
			} else if (machineCycleInfo.getCurrentCount() >= part.getAlert_at_count()) {

				if (!checkIfHistoryAlreadyExist(part.getId(), (int) part.getOff_at_count(), true)) {

					// adding history that alert generated
					addMachinePartHistory(part, Constants.STRING_ALERT_GENERATED, getCurrentTimeStamp());

					// notify admin
					addNofication(machineCycleInfo.getMachineId());

					// shoot mail
					shootMail(machineCycleInfo, part, false);

				}
			}
		}

	}

	private void shootMail(MachineCycleInfo machineCycle, MachinePart part, boolean isForLifeOver) {
		// shoot mail
		ArrayList<Email> emailList = getEmailIds(machineCycle.getMachineId());

		if (emailList != null) {
			Machine machine = getMachineById(machineCycle.getMachineId());

			SendMail sendMail = null;
			String mailSubject = "";
			String mailBody = "";

			if (!isForLifeOver) {
				mailSubject = CredentialConstants.EMAIL_SUBJECT_FOR_ALERT;
				mailSubject = mailSubject.replaceAll("#machineName#", machine.getMachine_name().toUpperCase());
				mailBody = CredentialConstants.EMAIL_BODY_FOR_ALERT;
				mailBody = mailBody.replaceAll("#machineName#", machine.getMachine_name().toUpperCase());
				mailBody = mailBody.replaceAll("#partName#", part.spare_part_name.toUpperCase());

				float percent = ((float) machineCycle.getCurrentCount() / (float) part.getOff_at_count()) * 100f;

				int remainingPercentage = (100 - (int) percent);

				mailBody = mailBody.replaceAll("#lifePercentage#", remainingPercentage + "%");

				sendMail = new SendMail(emailList, mailSubject, mailBody);

			} else {
				mailSubject = CredentialConstants.EMAIL_SUBJECT_FOR_MACHINE_OFF;
				mailSubject = mailSubject.replaceAll("#machineName#", machine.getMachine_name().toUpperCase());

				mailBody = CredentialConstants.EMAIL_BODY_FOR_MACHINE_OFF;
				mailBody = mailBody.replaceAll("#machineName#", machine.getMachine_name().toUpperCase());
				mailBody = mailBody.replaceAll("#partName#", part.spare_part_name.toUpperCase());

				if (part.life_extended == false && part.life_exten_limit > 0) {
					mailBody = mailBody.replaceAll("#possibleExtension#", part.life_exten_limit + "");
				} else {
					mailBody = mailBody.replaceAll("#possibleExtension#", "0");
				}

				sendMail = new SendMail(emailList, mailSubject, mailBody);

			}
			sendMail.start();

		}
	}

	private void addNofication(int machineId) {
		Machine machine = getMachineById(machineId);
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Notification notification = null;
		try {
			String qryString = "from Notification where machine_id =?";

			Query query = session.createQuery(qryString);
			notification = (Notification) query.setInteger(0, machineId).uniqueResult();

			// if notification not found then we need to add else update
			if (notification == null) {
				notification = new Notification();
				notification.setMachine_id(machineId);
				notification.setMachine_name(machine.getMachine_name());
				notification.setStatus(Constants.STATUS_ACTIVE);
				if (machine.getStatus().equals(Constants.KEY_MACHINE_ON)) {
					notification.setOn(true);
				} else {
					notification.setOn(false);
				}
				notification.setRead(false);
				transaction = session.beginTransaction();
				session.save(notification);

			} else {

				if (machine.getStatus().equals(Constants.KEY_MACHINE_ON)) {
					notification.setOn(true);
				} else {
					notification.setOn(false);
				}
				notification.setRead(false);
				transaction = session.beginTransaction();
				session.update(notification);
			}

			transaction.commit();
			session.close();
		} catch (Exception e) {
			transaction.rollback();
			session.close();
			e.printStackTrace();
		}

	}

	private boolean checkIfHistoryAlreadyExist(int id, int off_at_count, boolean isForAlert) {

		MachinePartHistory partHistory = null;
		Session session = sessionFactory.openSession();
		try {
			String qryString = "from MachinePartHistory where part_id =? and activity_details=? and off_at_count=?";

			Query query = session.createQuery(qryString);
			if (isForAlert) {
				partHistory = (MachinePartHistory) query.setInteger(0, id)
						.setString(1, Constants.STRING_ALERT_GENERATED).setInteger(2, off_at_count).uniqueResult();
			} else {
				partHistory = (MachinePartHistory) query.setInteger(0, id).setString(1, Constants.STRING_LIFE_OVER)
						.setInteger(2, off_at_count).uniqueResult();
			}
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}

		if (partHistory == null)
			return false;
		else
			return true;
	}

	private void updateMachineStatusAndItsCount(MachineCycleInfo machineCycleInfo) {
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();

			String qryString = "update machine set status = '" + machineCycleInfo.getMachineStatus()
					+ "', current_count = " + machineCycleInfo.getCurrentCount() + " where id = "
					+ machineCycleInfo.getMachineId();

			SQLQuery query = session.createSQLQuery(qryString);
			query.executeUpdate();
			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
	}

	private void resetMachineStatusAndItsCount() {
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();

			String qryString = "update machine set status = 'OFF', current_count = 0 ";

			SQLQuery query = session.createSQLQuery(qryString);
			query.executeUpdate();
			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
	}

	private void updateExhaustedLife(List<MachinePart> machineParts, MachineCycleInfo machineCycleInfo) {
		Session session = sessionFactory.openSession();
		try {
			for (int i = 0; i < machineParts.size(); i++) {
				Transaction transaction = session.beginTransaction();

				MachinePart machinePart = machineParts.get(i);
				// MachinePart OldmachinePart = machineParts.get(i);
				long old_life_exhausted_till_date = machinePart.getLife_exhausted_till_date();

				// System.out.println(">>>> OLD1" +
				// OldmachinePart.getLife_exhausted_till_date());
				System.out.println(">>>> NEW1" + machinePart.getLife_exhausted_till_date());

				long exhaustedLife = (machineCycleInfo.getCurrentCount() * machinePart.getMultiplying_factor());

				String qryString = "update machine_parts set life_exhausted_till_date = " + exhaustedLife
						+ " where id = " + machinePart.getId();
				SQLQuery query = session.createSQLQuery(qryString);
				query.executeUpdate();

				machinePart.setLife_exhausted_till_date(exhaustedLife);

				// Added new code for Demo start here
				if (old_life_exhausted_till_date != machinePart.getLife_exhausted_till_date()) {
					if (machinePart.getTemp_life() < exhaustedLife) {
						Request request = new Request();

						// long diff = (exhaustedLife - machinePart.getLife());

						long diff = (machinePart.getLife_exhausted_till_date() - old_life_exhausted_till_date);

						switch (i) {
						// for part 1
						case 0:
							if (machineParts.get(1).getExcl_part() == 0) {
								if (machineParts.get(1).getTemp_life() < exhaustedLife) {

								} else {
									// update part 2
									long p6 = machineParts.get(1).getP6() + diff;
									request.setPredict1(p6);

									String qryString1 = "update machine_parts set p6 = " + p6 + " where id = "
											+ machineParts.get(1).getId();

									SQLQuery query1 = session.createSQLQuery(qryString1);
									query1.executeUpdate();
								}
							}

							if (machineParts.get(2).getExcl_part() == 0) {
								if (machineParts.get(2).getTemp_life() < exhaustedLife) {

								} else {
									// update part 3
									long p4 = machineParts.get(2).getP4() + diff;
									request.setPredict2(p4);

									String qryString1 = "update machine_parts set p4 = " + p4 + " where id = "
											+ machineParts.get(2).getId();

									SQLQuery query1 = session.createSQLQuery(qryString1);
									query1.executeUpdate();
								}
							}
							break;
						// for part 2
						case 1:
							if (machineParts.get(2).getExcl_part() == 0) {
								if (machineParts.get(2).getTemp_life() < exhaustedLife) {

								} else {
									// update part 3
									long p3 = machineParts.get(2).getP3() + diff;
									request.setPredict3(p3);

									String qryString1 = "update machine_parts set p3 = " + p3 + " where id = "
											+ machineParts.get(2).getId();

									SQLQuery query1 = session.createSQLQuery(qryString1);
									query1.executeUpdate();
								}
							}

							if (machineParts.get(0).getExcl_part() == 0) {
								if (machineParts.get(0).getTemp_life() < exhaustedLife) {

								} else {
									// update part 1
									long p2 = machineParts.get(0).getP2() + diff;
									request.setPredict4(p2);

									String qryString1 = "update machine_parts set p2 = " + p2 + " where id = "
											+ machineParts.get(0).getId();

									SQLQuery query1 = session.createSQLQuery(qryString1);
									query1.executeUpdate();
								}
							}
							break;
						// for part 3
						case 2:
							if (machineParts.get(0).getExcl_part() == 0) {
								if (machineParts.get(0).getTemp_life() < exhaustedLife) {

								} else {
									// update part 1
									long p1 = machineParts.get(0).getP1() + diff;
									request.setPredict5(p1);

									String qryString1 = "update machine_parts set p1 = " + p1 + " where id = "
											+ machineParts.get(0).getId();

									SQLQuery query1 = session.createSQLQuery(qryString1);
									query1.executeUpdate();

								}
							}
							if (machineParts.get(1).getExcl_part() == 0) {
								if (machineParts.get(1).getTemp_life() < exhaustedLife) {

								} else {
									// update part 2
									long p5 = machineParts.get(1).getP5() + diff;
									request.setPredict6(p5);

									String qryString1 = "update machine_parts set p5 = " + p5 + " where id = "
											+ machineParts.get(1).getId();

									SQLQuery query1 = session.createSQLQuery(qryString1);
									query1.executeUpdate();

								}
							}
							break;
						}

						String url = "https://pythonflaskexample.herokuapp.com/api/v1/tbmpredict";
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);

						HttpEntity<Request> entity = new HttpEntity<Request>(request, headers);

						Response response = genericRequest(url, entity, HttpMethod.POST, Response.class);

						if (response.getResult1() > 0) {
							// update result in table
							String qryString2 = "update machine_parts set r1 = " + response.getResult1()
									+ " where id = " + machineParts.get(0).getId();

							SQLQuery query2 = session.createSQLQuery(qryString2);
							query2.executeUpdate();

							MachinePart machinePart1 = machineParts.get(1);
							// long p6 = machinePart1.getP6() + diff;
							long predictLife1 = (machinePart1.getLife()
									- (response.getResult1() + machineParts.get(2).getR5()));
							long offAtCount1 = (machinePart1.getFinal_life() - response.getResult1());
							// long finalLife1 = (machinePart1.getFinal_life() -
							// response.getResult1());

							String qryString1 = "update machine_parts set predicted_life = " + predictLife1
									+ " where id = " + machinePart1.getId();

							// String qryString1 = "update machine_parts set
							// predicted_life = " + predictLife1
							// + ", p6 = "+ p6 +" where id = " +
							// machinePart1.getId();

							SQLQuery query1 = session.createSQLQuery(qryString1);
							query1.executeUpdate();

							machinePart.setPredicted_life(predictLife1);

						}
						if (response.getResult2() > 0) {
							// update result in table
							String qryString2 = "update machine_parts set r2 = " + response.getResult2()
									+ " where id = " + machineParts.get(0).getId();

							SQLQuery query2 = session.createSQLQuery(qryString2);
							query2.executeUpdate();

							MachinePart machinePart1 = machineParts.get(2);
							// long p4 = machinePart1.getP4() + diff;
							long predictLife1 = (machinePart1.getLife()
									- (response.getResult2() + machineParts.get(1).getR3()));
							long offAtCount1 = (machinePart1.getFinal_life() - response.getResult2());
							// long finalLife1 = (machinePart1.getFinal_life() -
							// response.getResult2());

							String qryString1 = "update machine_parts set predicted_life = " + predictLife1
									+ " where id = " + machinePart1.getId();

							// String qryString1 = "update machine_parts set
							// predicted_life = " + predictLife1
							// + ", p4 = " +p4+ " where id = " +
							// machinePart1.getId();

							SQLQuery query1 = session.createSQLQuery(qryString1);
							query1.executeUpdate();

							machinePart.setPredicted_life(predictLife1);

						}
						if (response.getResult3() > 0) {
							// update result in table
							String qryString2 = "update machine_parts set r3 = " + response.getResult3()
									+ " where id = " + machineParts.get(1).getId();

							SQLQuery query2 = session.createSQLQuery(qryString2);
							query2.executeUpdate();

							MachinePart machinePart1 = machineParts.get(2);
							// long p3 = machinePart1.getP3() + diff;
							long predictLife1 = (machinePart1.getLife()
									- (response.getResult3() + machineParts.get(0).getR2()));
							long offAtCount1 = (machinePart1.getFinal_life() - response.getResult3());
							// long finalLife1 = (machinePart1.getFinal_life() -
							// response.getResult3());

							String qryString1 = "update machine_parts set predicted_life = " + predictLife1
									+ " where id = " + machinePart1.getId();

							// String qryString1 = "update machine_parts set
							// predicted_life = " + predictLife1
							// + ", p3 = " +p3+ " where id = " +
							// machinePart1.getId();

							SQLQuery query1 = session.createSQLQuery(qryString1);
							query1.executeUpdate();

							machinePart.setPredicted_life(predictLife1);

						}
						if (response.getResult4() > 0) {
							// update result in table
							String qryString2 = "update machine_parts set r4 = " + response.getResult4()
									+ " where id = " + machineParts.get(1).getId();

							SQLQuery query2 = session.createSQLQuery(qryString2);
							query2.executeUpdate();

							MachinePart machinePart1 = machineParts.get(0);
							// long p2 = machinePart1.getP2() + diff;
							long predictLife1 = (machinePart1.getLife()
									- (response.getResult4() + machineParts.get(2).getR6()));
							long offAtCount1 = (machinePart1.getFinal_life() - response.getResult4());
							// long finalLife1 = (machinePart1.getFinal_life() -
							// response.getResult4());

							String qryString1 = "update machine_parts set predicted_life = " + predictLife1
									+ " where id = " + machinePart1.getId();

							// String qryString1 = "update machine_parts set
							// predicted_life = " + predictLife1
							// + ", p2 = " +p2+ " where id = " +
							// machinePart1.getId();

							SQLQuery query1 = session.createSQLQuery(qryString1);
							query1.executeUpdate();

							machinePart.setPredicted_life(predictLife1);

						}
						if (response.getResult5() > 0) {
							// update result in table
							String qryString2 = "update machine_parts set r5 = " + response.getResult5()
									+ " where id = " + machineParts.get(2).getId();

							SQLQuery query2 = session.createSQLQuery(qryString2);
							query2.executeUpdate();

							MachinePart machinePart1 = machineParts.get(1);
							// long p1 = machinePart1.getP1() + diff;
							long predictLife1 = (machinePart1.getLife()
									- (response.getResult5() + machineParts.get(0).getR1()));
							long offAtCount1 = (machinePart1.getFinal_life() - response.getResult5());
							// long finalLife1 = (machinePart1.getFinal_life() -
							// response.getResult5());

							// String qryString1 = "update machine_parts set
							// predicted_life = " + predictLife1
							// + ", p1 = " +p1+ " where id = " +
							// machinePart1.getId();

							String qryString1 = "update machine_parts set predicted_life = " + predictLife1
									+ " where id = " + machinePart1.getId();

							SQLQuery query1 = session.createSQLQuery(qryString1);
							query1.executeUpdate();

							machinePart.setPredicted_life(predictLife1);

						}
						if (response.getResult6() > 0) {
							// update result in table
							String qryString2 = "update machine_parts set r6 = " + response.getResult6()
									+ " where id = " + machineParts.get(2).getId();

							SQLQuery query2 = session.createSQLQuery(qryString2);
							query2.executeUpdate();

							MachinePart machinePart1 = machineParts.get(0);
							// long p5 = machinePart1.getP5() + diff;
							long predictLife1 = (machinePart1.getLife()
									- (response.getResult6() + machineParts.get(1).getR4()));
							long offAtCount1 = (machinePart1.getFinal_life() - response.getResult6());
							// long finalLife1 = (machinePart1.getFinal_life() -
							// response.getResult6());

							String qryString1 = "update machine_parts set predicted_life = " + predictLife1
									+ " where id = " + machinePart1.getId();

							// String qryString1 = "update machine_parts set
							// predicted_life = " + predictLife1
							// + ", p5 = " +p5+ " where id = " +
							// machinePart1.getId();

							SQLQuery query1 = session.createSQLQuery(qryString1);
							query1.executeUpdate();

							machinePart.setPredicted_life(predictLife1);

						}
						// Added new code for Demo end here

					}
				}
				transaction.commit();
			}
			session.close();
		} catch (

		Exception e) {
			session.close();
			e.printStackTrace();
		}

	}

	// Added new code for Demo start here
	/**
	 * Generic request
	 * 
	 * @param url
	 * @param entity
	 * @param methodType
	 * @param responseType
	 * @return
	 */
	private static <T> T genericRequest(String url, HttpEntity<?> entity, HttpMethod methodType,
			Class<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<T> personEntity = restTemplate.exchange(url, methodType, entity, responseType, 100);
		System.out.println(">>>> Code : " + personEntity.getStatusCodeValue());

		return personEntity.getBody();
	}
	// Added new code for Demo end here

	private void resetExhaustedLife() {
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();

			long exhaustedLife = 0;

			String qryString = "update machine_parts set life_exhausted_till_date = " + exhaustedLife;
			SQLQuery query = session.createSQLQuery(qryString);
			query.executeUpdate();

			transaction.commit();

			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}

	}

	private MachineCycleInfo parseMachineCycleInfo(String cycleInfo) {
		MachineCycleInfo machineCycleInfo = new MachineCycleInfo();

		String[] mainSplit = cycleInfo.split(",");
		for (int i = 0; i < mainSplit.length; i++) {

			String[] subSplit = mainSplit[i].split("=");
			String key = subSplit[0].trim();
			String value = subSplit[1].trim();

			if (key.equalsIgnoreCase(Constants.KEY_CURRENT_COUNT)) {
				machineCycleInfo.setCurrentCount(Long.valueOf(value));
			} else if (key.equalsIgnoreCase(Constants.KEY_MACHINE_STATUS)) {
				if (value.equalsIgnoreCase(Constants.KEY_MACHINE_ON)) {
					machineCycleInfo.setMachineStatus(Constants.KEY_MACHINE_ON);
				} else {
					machineCycleInfo.setMachineStatus(Constants.KEY_MACHINE_OFF);
				}
			} else if (key.equalsIgnoreCase(Constants.KEY_ALERT)) {
				machineCycleInfo.setAlert(value);
			} else if (key.equalsIgnoreCase(Constants.KEY_MACHINE_ID)) {
				machineCycleInfo.setMachineId(Integer.valueOf(value));
			} else if (key.equalsIgnoreCase(Constants.KEY_OLD)) {
				if (value != null && value.length() > 0) {
					machineCycleInfo.setOld(true);
				} else {
					machineCycleInfo.setOld(false);
				}
			} else if (key.equalsIgnoreCase(Constants.KEY_UNIX_TIME)) {
				machineCycleInfo.setTime(value);
			}
		}
		return machineCycleInfo;
	}

	/**
	 * Method to get Machine belongs to particular dcd ip
	 * 
	 * @param dcdIP
	 * @return
	 */
	private Machine getMachineByDCDIp(String dcdIP) {
		Machine machine = null;
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "from Machine where dcd_linked_ip =?";

			Query query = session.createQuery(qryString);
			machine = (Machine) query.setString(0, dcdIP).uniqueResult();

			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		return machine;
	}

	/**
	 * Method to get Machine belongs to particular dcd ip
	 * 
	 * @param dcdIP
	 * @return
	 */
	private Machine getMachineList() {
		Machine machine = null;
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			String qryString = "from Machine";

			Query query = session.createQuery(qryString);
			// machine = (Machine) query.setString(0, dcdIP).uniqueResult();

			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		return machine;
	}

	/**
	 * Method to get machine part having least life
	 * 
	 * @param machineId
	 * @return
	 */
	private MachinePart getMachinePartHavingLeastLife(int machineId) {
		MachinePart machinePartHavingLeastLife = null;
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();
			// String qryString = "SELECT id, off_at_count, alert_at_count FROM
			// machine_parts WHERE alert_at_count="
			// + " (SELECT MIN(alert_at_count) FROM machine_parts WHERE
			// machine_id= " + machineId
			// + ") AND machine_id= " + machineId;
			// Criteria createCriteria =
			// session.createCriteria(MachinePart.class);
			// String qryString ="SELECT (SELECT id FROM machine_parts WHERE
			// alert_at_count=(SELECT MIN(alert_at_count) FROM machine_parts
			// WHERE machine_id="+machineId+")) AS id,"
			// +"(SELECT off_at_count FROM machine_parts WHERE
			// off_at_count=(SELECT MIN(off_at_count) FROM machine_parts WHERE
			// machine_id="+machineId+" LIMIT 1)) AS off_at_count,"
			// +"(SELECT alert_at_count FROM machine_parts WHERE
			// alert_at_count=(SELECT MIN(alert_at_count) FROM machine_parts
			// WHERE machine_id="+machineId+" LIMIT 1)) AS alert_at_count";

			// String qryString = "SELECT (SELECT id from machine_parts WHERE
			// machine_id=" + machineId
			// + " and alert_at_count=(SELECT min(alert_at_count) FROM
			// machine_parts where machine_id=" + machineId
			// + ") limit 1) as id,(SELECT min(off_at_count) FROM machine_parts
			// WHERE machine_id=" + machineId
			// + " limit 1) as off_at_count, (SELECT min(alert_at_count) FROM
			// machine_parts WHERE machine_id="
			// + machineId + " limit 1) as alert_at_count";

			String qryString = "SELECT (SELECT id from machine_parts WHERE machine_id=" + machineId
					+ " and alert_at_count=(SELECT min(alert_at_count) FROM machine_parts where machine_id=" + machineId
					+ ") limit 1) as id,(SELECT min(off_at_count) FROM machine_parts WHERE machine_id=" + machineId
					+ " limit 1) as off_at_count, (SELECT min(alert_at_count) FROM machine_parts WHERE machine_id="
					+ machineId + " limit 1) as alert_at_count";

			SQLQuery query = session.createSQLQuery(qryString);
			List<Object[]> rows = query.list();
			for (Object[] row : rows) {
				machinePartHavingLeastLife = new MachinePart();
				machinePartHavingLeastLife.setId(Integer.parseInt(row[0].toString()));

				System.out.print(">>row id >>" + row[0].toString());
				System.out.print(">>off >>" + row[1].toString());
				System.out.print(">>alert >>" + row[2].toString());
				machinePartHavingLeastLife.setOff_at_count(Long.parseLong(row[1].toString()));
				machinePartHavingLeastLife.setAlert_at_count(Long.parseLong(row[2].toString()));
			}
			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		return machinePartHavingLeastLife;

	}

	private long getLeastOffCountOfAMachine(int machineId) {
		long leastCount = 0;
		Session session = sessionFactory.openSession();
		try {
			Transaction transaction = session.beginTransaction();
			String qryString = "SELECT id, MIN(off_at_count) FROM machine_parts WHERE machine_id= " + machineId;

			SQLQuery query = session.createSQLQuery(qryString);
			List<Object[]> rows = query.list();
			for (Object[] row : rows) {
				leastCount = Long.parseLong(row[1].toString());

			}
			transaction.commit();
			session.close();
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
		}
		System.out.print(">>leastCount" + leastCount);
		return leastCount;

	}

	/**
	 * 
	 * Inner class run the thread which is used to and sent an email
	 * 
	 * @author sid
	 */
	private class SendMail extends Thread {

		private ArrayList<Email> emailList;
		private String emailSubject;
		private String emailbody;

		public SendMail(ArrayList<Email> emailList, String emailSubject, String emailbody) {
			this.emailList = emailList;
			this.emailSubject = emailSubject;
			this.emailbody = emailbody;
		}

		@Override
		public void run() {

			boolean emailSendStatus = false;
			try {
				if (emailList.size() > 0) {
					String[] emailIds = new String[emailList.size()];
					for (int i = 0; i < emailList.size(); i++) {
						emailIds[i] = emailList.get(i).getEmail_id();
					}
					// here we sent an email to receiver with attachment
					emailSendStatus = mailManager.sendEmail(emailIds, emailSubject, emailbody);
					System.out.print("## is email sent = " + emailSendStatus);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
