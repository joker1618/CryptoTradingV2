package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.impl.*;
import com.fede.ct.v2.datalayer.IModelPrivate;
import com.fede.ct.v2.datalayer.IModelPublic;
import com.fede.ct.v2.datalayer.IModelTrading;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class ModelFactory {

	private static final SimpleLog logger = LogService.getLogger(ModelFactory.class);

	public static IModelPublic createModelPublic(CryptoContext ctx) {
		ModelPublicImpl model = new ModelPublicImpl();
		model.setAssetsDao(new AssetsDbDao(ctx));
		model.setAssetPairsDao(new AssetPairsDbDao(ctx));
		model.setTickersDao(new TickersDbDao(ctx));
		return model;
	}

	public static IModelPrivate createModelPrivate(CryptoContext ctx) {
		ModelPrivateImpl model = new ModelPrivateImpl();
		model.setPropertiesDao(new PropertiesDbDao(ctx));
		model.setOrdersDao(new OrdersDbDao(ctx));
		model.setAccountBalanceDao(new AccountBalanceDbDao(ctx));
		return model;
	}

	public static IModelTrading createModelTrading(CryptoContext ctx) {
		ModelTradingImpl model = new ModelTradingImpl();
		model.setAssetsDao(new AssetsDbDao(ctx));
		model.setAssetPairsDao(new AssetPairsDbDao(ctx));
		model.setPropertiesDao(new PropertiesDbDao(ctx));
		model.setTickersDao(new TickersDbDao(ctx));
		model.setAccountBalanceDao(new AccountBalanceDbDao(ctx));
		model.setOrdersDao(new OrdersDbDao(ctx));
		return model;
	}


}
