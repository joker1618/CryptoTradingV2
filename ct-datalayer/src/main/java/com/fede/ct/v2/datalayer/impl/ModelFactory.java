package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.impl.*;
import com.fede.ct.v2.datalayer.IContextModel;
import com.fede.ct.v2.datalayer.IDataModel;
import com.fede.ct.v2.datalayer.IModelPublic;

import java.sql.Connection;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class ModelFactory {

	private static final SimpleLog logger = LogService.getLogger(ModelFactory.class);

	public static IModelPublic getModelPublic(Connection conn) {
		ModelPublicImpl model = new ModelPublicImpl();
		model.setAssetsDao(new AssetsDbDao(conn));
		model.setAssetPairsDao(new AssetPairsDbDao(conn));
		model.setTickersDao(new TickersDbDao(conn));
		return model;
	}


}
