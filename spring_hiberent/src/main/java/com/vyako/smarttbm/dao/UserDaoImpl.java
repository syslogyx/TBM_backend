package com.vyako.smarttbm.dao;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.vyako.smarttbm.dao.interfac.IUserDao;
import com.vyako.smarttbm.do_other.BaseResponseModel;
import com.vyako.smarttbm.do_request.UserLoginRequestDo;
import com.vyako.smarttbm.entity.UserLogin;

@Repository
public class UserDaoImpl extends BaseDAOImpl implements IUserDao {
	@Override
	public BaseResponseModel login(UserLoginRequestDo userLoginRequestDo) {
		try {
			UserLogin userLogin = getUserByIdPwd(userLoginRequestDo);

			if (userLogin != null
					&& userLogin.getPwd().equalsIgnoreCase(
							userLoginRequestDo.getPwd())) {
				return formResponseModel(true, "Login successfully !!", null);
			} else {
				return formResponseModel(false, "Wrong username or password.",
						null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return formResponseModel(false, "Something went wrong.", null);
	}

	@Override
	public BaseResponseModel changePwd(UserLoginRequestDo userLoginRequestDo) {

		UserLogin userLogin = getUserByIdPwd(userLoginRequestDo);

		if (userLogin != null
				&& userLogin.getPwd().equalsIgnoreCase(
						userLoginRequestDo.getPwd())) {

			if (userLoginRequestDo.getNewPwd() == null
					|| userLoginRequestDo.getNewPwd().trim().length() == 0) {
				return formResponseModel(
						true,
						"Wrong request body.. something wrong with new password!!",
						null);
			}

			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();

			String qryString = "update users u set u.pwd = '"
					+ userLoginRequestDo.getNewPwd() + "' where u.username = '"
					+ userLoginRequestDo.getUsername() + "'";

			SQLQuery query = session.createSQLQuery(qryString);
			int count = query.executeUpdate();
			System.out.print(">>count = " + count);
			transaction.commit();
			session.close();

			return formResponseModel(true, "Password changed successfully !!",
					null);
		} else {
			return formResponseModel(false, "Wrong username or password.", null);
		}

	}

	private UserLogin getUserByIdPwd(UserLoginRequestDo userLoginRequestDo) {
		UserLogin userLogin = null;
		Session session = sessionFactory.openSession();
		try {
			String qryString = "from UserLogin where username=:user and pwd =:pass";

			Query createQuery = session.createQuery(qryString);
			userLogin = (UserLogin) createQuery
					.setParameter("user", userLoginRequestDo.getUsername())
					.setParameter("pass", userLoginRequestDo.getPwd())
					.uniqueResult();
			session.close();

		} catch (Exception e) {
			e.printStackTrace();
			session.close();
		}
		return userLogin;
	}
}
