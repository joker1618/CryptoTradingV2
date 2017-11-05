package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.config.Config;
import com.fede.ct.v2.common.config.IConfig;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.impl.AssetPairsDbDao;
import com.fede.ct.v2.dao.impl.AssetsDbDao;
import com.fede.ct.v2.dao.impl.TickersDbDao;
import com.fede.ct.v2.datalayer.ICryptoModel;
import com.mysql.jdbc.ConnectionImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class CryptoModelFactory {

	private static final SimpleLog logger = LogService.getLogger(CryptoModelFactory.class);
	private static final IConfig config = Config.getUniqueInstance();

	public static ICryptoModel getModel() {
		Connection conn = createConnection();

		CryptoModelImpl model = new CryptoModelImpl();
		model.setAssetsDao(new AssetsDbDao(conn));
		model.setAssetPairsDao(new AssetPairsDbDao(conn));
		model.setTickersDao(new TickersDbDao(conn));
		return model;
	}

	private static Connection createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
		} catch (Exception e) {
			logger.error(e, "Unable to create DB connection [URL=%s, user=%s, pwd=%s]", config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
			throw new TechnicalException(e, "Unable to create DB connection [URL=%s, user=%s, pwd=%s]", config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
		}
	}
}
