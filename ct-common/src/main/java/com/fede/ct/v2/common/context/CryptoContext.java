package com.fede.ct.v2.common.context;

import java.sql.Connection;

/**
 * Created by f.barbano on 11/11/2017.
 */
public class CryptoContext {

	private RunType runType;
	private UserCtx userCtx;
	private Connection dbConn;

	public CryptoContext(RunType runType, Connection dbConn) {
		this.runType = runType;
		this.dbConn = dbConn;
	}

	public RunType getRunType() {
		return runType;
	}

	public void setRunType(RunType runType) {
		this.runType = runType;
	}

	public UserCtx getUserCtx() {
		return userCtx;
	}

	public void setUserCtx(UserCtx userCtx) {
		this.userCtx = userCtx;
	}

	public Connection getDbConn() {
		return dbConn;
	}

	public void setDbConn(Connection dbConn) {
		this.dbConn = dbConn;
	}

}
