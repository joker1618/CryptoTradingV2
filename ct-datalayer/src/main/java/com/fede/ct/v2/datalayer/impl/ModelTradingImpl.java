package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.*;
import com.fede.ct.v2.datalayer.IModelTrading;

import java.util.List;

/**
 * Created by f.barbano on 15/11/2017.
 */
class ModelTradingImpl implements IModelTrading {

	private IAssetPairsDao assetPairsDao;
	private IPropertiesDao propertiesDao;
	private ITickersDao tickersDao;
	private IAccountBalanceDao accountBalanceDao;
	private IOrdersDao ordersDao;


	@Override
	public List<AssetPair> getAssetPairs(boolean onlyTradables) {
		return assetPairsDao.selectAssetPairs(onlyTradables);
	}

	@Override
	public Ticker getTickerAskPriceAndAvgLast24(String pairName) {
		return tickersDao.selectAskPriceAndAvgLast24(pairName);
	}

	@Override
	public AccountBalance getAssetBalance(String assetName) {
		List<AccountBalance> abList = accountBalanceDao.getAccountBalance();
		abList.removeIf(ab -> assetName.equalsIgnoreCase(ab.getAssetName()));
		return abList.isEmpty() ? null : abList.get(0);
	}

	@Override
	public void turnOnDownloadOrders() {
		propertiesDao.setDownloadOrdersEnabled(true);
	}

	@Override
	public boolean isDownloadOrdersEnabled() {
		return propertiesDao.isDownloadOrdersEnabled();
	}

	@Override
	public List<OrderInfo> getOrdersStatus(List<String> txIds) {
		return ordersDao.getOrdersStatus(txIds);
	}

	@Override
	public List<OrderInfo> getOrders(Long minOpenTm, Long maxOpenTm) {
		return ordersDao.getOrdersByOpenTm(minOpenTm, maxOpenTm);
	}


	void setAssetPairsDao(IAssetPairsDao assetPairsDao) {
		this.assetPairsDao = assetPairsDao;
	}
	void setPropertiesDao(IPropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}
	void setTickersDao(ITickersDao tickersDao) {
		this.tickersDao = tickersDao;
	}
	void setAccountBalanceDao(IAccountBalanceDao accountBalanceDao) {
		this.accountBalanceDao = accountBalanceDao;
	}
 	void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
}
