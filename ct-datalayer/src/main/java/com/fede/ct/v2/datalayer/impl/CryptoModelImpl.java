package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.dao.IAssetPairsDao;
import com.fede.ct.v2.dao.IAssetsDao;
import com.fede.ct.v2.dao.ITickersDao;
import com.fede.ct.v2.datalayer.ICryptoModel;

import java.util.Collection;
import java.util.List;

/**
 * Created by f.barbano on 05/11/2017.
 */
class CryptoModelImpl implements ICryptoModel {

	private IAssetsDao assetsDao;
	private IAssetPairsDao assetPairsDao;
	private ITickersDao tickersDao;


	@Override
	public List<Asset> getAssets() {
		return null;
	}

	@Override
	public List<AssetPair> getAssetPairs() {
		return null;
	}

	@Override
	public boolean setNewAssets(Collection<Asset> assets) {
		return false;
	}

	@Override
	public boolean setNewAssetPairs(Collection<AssetPair> assetPairs) {
		return false;
	}

	@Override
	public void insertTickers(Collection<Ticker> tickers) {

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
}
