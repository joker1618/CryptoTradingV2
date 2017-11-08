package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.IUserIdDao;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class UserIdDbDao extends AbstractDbDao implements IUserIdDao {

	private static final SimpleLog logger = LogService.getLogger(UserIdDbDao.class);

	private static final String SELECT_USER_ID = "SELECT USER_ID FROM CT_USERS WHERE API_KEY = ? AND API_SECRET = ?";
	private static final String GET_NEXT_FREE_USER_ID = "SELECT MAX(USER_ID) AS NEXT_ID FROM CT_USERS";
	private static final String INSERT_NEW_USER_ID = "INSERT INTO CT_USERS (API_KEY, API_SECRET, USER_ID) VALUES (?,?,?)";


	public UserIdDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public Integer getUserId(String apiKey, String apiSecret) {
		// select user id from CT_USERS
		Integer userId = selectUserId(apiKey, apiSecret);
		if(userId != null) {
			return userId;
		}

		// if user id does not exists for input key/secret, create new one
		Integer nextId = getNextFreeUserId();
		super.performUpdate(INSERT_NEW_USER_ID, apiKey, apiSecret, nextId);

		return nextId;
	}

	private Integer selectUserId(String apiKey, String apiSecret) {
		try (PreparedStatement ps = createPreparedStatement(SELECT_USER_ID, apiKey, apiSecret);
			 ResultSet rs = ps.executeQuery()){
			Integer toRet = null;
			if(rs != null && rs.next()) {
				toRet = rs.getInt("USER_ID");
			}
			return toRet;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s] for [apiKey=%s, apiSecret=%d]", SELECT_USER_ID, apiKey, apiSecret);
		}
	}

	private Integer getNextFreeUserId() {
		try (PreparedStatement ps = createPreparedStatement(GET_NEXT_FREE_USER_ID);
			 ResultSet rs = ps.executeQuery()){
			Integer toRet = 1;
			if(rs != null && rs.next()) {
				String str = rs.getString("NEXT_ID");
				if(StringUtils.isNotBlank(str)) {
					toRet = Integer.parseInt(str);
				}
			}
			return toRet;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", GET_NEXT_FREE_USER_ID);
		}
	}
}
