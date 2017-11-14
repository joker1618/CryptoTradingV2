package com.fede.ct.v2.login;

import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.common.config._public.IConfigPublic;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.context.RunType;
import com.fede.ct.v2.common.context.UserCtx;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.dao.IAssetsDao;
import com.fede.ct.v2.dao.IUsersDao;
import com.fede.ct.v2.dao.impl.AssetsDbDao;
import com.fede.ct.v2.dao.impl.UsersDbDao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by f.barbano on 14/11/2017.
 */
public class LoginService {

	private static final IConfigPublic configPublic = ConfigPublic.getUniqueInstance();

	public static CryptoContext registerNewUser(String userName, String apiKey, String apiSecret) {
		Connection dbConn = createConnection();
		IUsersDao usersDao = new UsersDbDao(dbConn);
		UserCtx userCtx = usersDao.createNewUserId(userName, apiKey, apiSecret);
		CryptoContext ctx = new CryptoContext(RunType.REGISTER_USER, dbConn);
		ctx.setUserCtx(userCtx);
		return ctx;
	}

	public static CryptoContext createContext() {
		return createContext(RunType.PUBLIC, null);
	}
	public static CryptoContext createContext(RunType runType, Integer userId) {
		if(runType == RunType.PUBLIC || userId == null) {
			return new CryptoContext(RunType.PUBLIC, createConnection());
		}

		if(runType == RunType.PRIVATE || runType == RunType.STRATEGY) {
			Connection dbConn = createConnection();
			CryptoContext ctx = new CryptoContext(runType, createConnection());
			IUsersDao usersDao = new UsersDbDao(dbConn);
			UserCtx userCtx = usersDao.getByUserId(userId);
			if(userCtx == null) {
				throw new TechnicalException("User ID %d not registered", userId);
			}
			ctx.setUserCtx(userCtx);
			return ctx;
		}

		return null;
	}

	private static Connection createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(configPublic.getDbUrl(), configPublic.getDbUser(), configPublic.getDbPwd());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
