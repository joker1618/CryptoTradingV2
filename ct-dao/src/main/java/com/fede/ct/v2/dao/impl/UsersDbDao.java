package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.UserCtx;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.IUsersDao;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class UsersDbDao extends AbstractDbDao2 implements IUsersDao {

	private static final SimpleLog logger = LogService.getLogger(UsersDbDao.class);

	private static final String SELECT_USER_BY_ID = "SELECT USER_ID, USER_NAME, API_KEY, API_SECRET FROM CT_USERS WHERE USER_ID = ?";
	private static final String SELECT_USER_BY_NAME = "SELECT USER_ID, USER_NAME, API_KEY, API_SECRET FROM CT_USERS WHERE USER_NAME = ?";
	private static final String GET_NEXT_FREE_USER_ID = "SELECT MAX(USER_ID) AS MAX_ID FROM CT_USERS";
	private static final String INSERT_NEW_ENTRY = "INSERT INTO CT_USERS (USER_ID, USER_NAME, API_KEY, API_SECRET) VALUES (?,?,?,?)";


	public UsersDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public UserCtx getByUserId(int userId) {
		Query query = new Query(SELECT_USER_BY_ID, userId);
		List<InquiryResult> results = super.performInquiry(query);
		UserCtx toRet = null;
		if(!results.isEmpty()) {
			InquiryResult ir = results.get(0);
			toRet = new UserCtx();
			toRet.setUserId(ir.getInteger("USER_ID"));
			toRet.setUserName(ir.getString("USER_NAME"));
			toRet.setApiKey(ir.getString("API_KEY"));
			toRet.setApiSecret(ir.getString("API_SECRET"));
		}
		return toRet;
	}

	@Override
	public int createNewUserId(String userName, String apiKey, String apiSecret) {
		if(userNameExists(userName)) {
			throw new TechnicalException("Username %s already exists", userName);
		}

		Integer nextId = getNextFreeUserId();

		Query insQuery = new Query(INSERT_NEW_ENTRY, nextId, userName, apiKey, apiSecret);
		super.performUpdate(insQuery);
		return nextId;
	}

	private boolean userNameExists(String userName) {
		List<InquiryResult> results = super.performInquiry(new Query(SELECT_USER_BY_NAME, userName));
		return results.isEmpty();
	}

	private Integer getNextFreeUserId() {
		Query query = new Query(GET_NEXT_FREE_USER_ID);
		List<InquiryResult> results = super.performInquiry(query);
		Integer nextId = 1;
		if(!results.isEmpty()) {
			Integer maxId = results.get(0).getInteger("MAX_ID");
			nextId = maxId + 1;
		}
		return nextId;
	}
}
