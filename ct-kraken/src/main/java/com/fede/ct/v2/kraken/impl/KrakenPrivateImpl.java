package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.kraken.IKrakenPrivate;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;
import com.fede.ct.v2.kraken.impl.api.KrakenApi;
import com.fede.ct.v2.kraken.impl.api.KrakenMethod;

import java.util.List;

/**
 * Created by f.barbano on 04/11/2017.
 */
class KrakenPrivateImpl extends AbstractKrakenCaller implements IKrakenPrivate {

	KrakenPrivateImpl(String key, String secret) {
		super(new KrakenApi(key, secret));
	}

	@Override
	public List<OrderInfo> getOpenOrders() throws KrakenException, KrakenCallError {
		JsonToModel jm = super.performKrakenCall(KrakenMethod.OPEN_ORDERS);
		return jm.parseOpenOrders();
	}

	@Override
	public List<OrderInfo> getClosedOrders() throws KrakenException, KrakenCallError {
		JsonToModel jm = super.performKrakenCall(KrakenMethod.CLOSED_ORDERS);
		return jm.parseClosedOrders();
	}

	@Override
	public List<AccountBalance> getAccountBalances() throws KrakenException, KrakenCallError {
		long callTime = System.currentTimeMillis();
		JsonToModel jm = super.performKrakenCall(KrakenMethod.BALANCE);
		return jm.parseAccountBalance(callTime);
	}


//	@Override
//	public List<Asset> getAssets() throws KrakenCallError, KrakenException {
//		JsonToModel jm = super.performKrakenCall(KrakenMethod.ASSETS);
//		return jm.parseAssets();
//	}
//
//	@Override
//	public List<AssetPair> getAssetPairs() throws KrakenCallError, KrakenException {
//		JsonToModel jm = super.performKrakenCall(KrakenMethod.ASSET_PAIRS);
//		return jm.parseAssetPairs();
//	}
//
//	@Override
//	public List<Ticker> getTickers(Collection<String> pairNames) throws KrakenCallError, KrakenException {
//		ApiParam pairParam = new ApiParam("pair", StreamUtil.join(pairNames, ","));
//		long callTime = System.currentTimeMillis();
//		JsonToModel jm = performKrakenCall(KrakenMethod.TICKER, pairParam);
//		return jm.parseTickers(callTime);
//	}

}
