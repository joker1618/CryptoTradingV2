package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.config._public.IConfigPublic;
import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.impl.*;
import com.fede.ct.v2.datalayer.IContextModel;
import com.fede.ct.v2.datalayer.IDataModel;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class ModelFactory {

	private static final SimpleLog logger = LogService.getLogger(ModelFactory.class);
	private static Connection conn;
	private static final IConfigPublic config = ConfigPublic.getUniqueInstance();

	public static IDataModel getDataModel() {
		Connection conn = createConnection();

		DataModelImpl model = new DataModelImpl();
		model.setAssetsDao(new AssetsDbDao(conn));
		model.setAssetPairsDao(new AssetPairsDbDao(conn));
		model.setTickersDao(new TickersDbDao(conn));
		model.setOrdersDao(new OrdersDbDao(conn));
		return model;
	}

	public static IContextModel getContextModel(String apiKey, String apiSecret) {
		Connection conn = createConnection();

		ContextModelImpl ctxModelImpl = new ContextModelImpl();
		ctxModelImpl.setUserIdDao(new UserIdDbDao(conn));
		ctxModelImpl.setPropertiesDao(new PropertiesDbDao(conn));
		ctxModelImpl.initialize(apiKey, apiSecret);
		return ctxModelImpl;
	}

	private static Connection createConnection() {
		if(conn == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
			} catch (Exception e) {
				logger.error(e, "Unable to create DB connection [URL=%s, user=%s, pwd=%s]", config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
				throw new TechnicalException(e, "Unable to create DB connection [URL=%s, user=%s, pwd=%s]", config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
			}
		}
		return conn;
	}
}
