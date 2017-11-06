package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.dao.*;
import com.fede.ct.v2.datalayer.IDataModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by f.barbano on 05/11/2017.
 */
class DataModelImpl implements IDataModel {

	private IAssetsDao assetsDao;
	private IAssetPairsDao assetPairsDao;
	private ITickersDao tickersDao;
	private IOrdersDao ordersDao;


	@Override
	public List<Asset> getAssets() {
		return assetsDao.selectAssets();
	}

	@Override
	public List<AssetPair> getAssetPairs(boolean discardDotD) {
		return assetPairsDao.selectAssetPairs(discardDotD);
	}

	@Override
	public List<String> getAssetPairNames(boolean discardDotD) {
		return assetPairsDao.selectAssetPairNames(discardDotD);
	}

	@Override
	public boolean setNewAssets(List<Asset> assets, long callTime) {
		List<Asset> actuals = getAssets();
		List<Asset> newAssets = new ArrayList<>(assets);
		newAssets.sort(Comparator.comparing(Asset::getAssetName));

		if(actuals.equals(newAssets)) {
			return false;
		}

		assetsDao.insertNewAssets(newAssets, callTime);
		return true;
	}

	@Override
	public boolean setNewAssetPairs(List<AssetPair> assetPairs, long callTime) {
		List<AssetPair> actuals = getAssetPairs(false);
		List<AssetPair> newAssetPairs = new ArrayList<>(assetPairs);
		newAssetPairs.sort(Comparator.comparing(AssetPair::getPairName));

		if(actuals.equals(newAssetPairs)) {
			return false;
		}

		assetPairsDao.insertNewAssetPairs(newAssetPairs, callTime);
		return true;
	}

	@Override
	public void insertTickers(List<Ticker> tickers, long callTime) {
		tickersDao.insertTickers(tickers, callTime);
	}

	@Override
	public void updateOrders(List<OrderInfo> orders, int userId) {
		ordersDao.updateOrders(orders, userId);
	}


	void setAssetsDao(IAssetsDao assetsDao) {
		this.assetsDao = assetsDao;
	}
	void setAssetPairsDao(IAssetPairsDao assetPairsDao) {
		this.assetPairsDao = assetPairsDao;
	}
	void setTickersDao(ITickersDao tickersDao) {
		this.tickersDao = tickersDao;
	}
	void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
}
