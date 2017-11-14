package com.fede.ct.v2.datalayer;

import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;

import java.util.List;

/**
 * Created by f.barbano on 11/11/2017.
 */
public interface IModelPublic {

	List<Asset> getAssets();
	boolean setNewAssets(List<Asset> assets, long callTime);

	List<AssetPair> getAssetPairs(boolean onlyTradables);
	boolean setNewAssetPairs(List<AssetPair> assetPairs, long callTime);

	void insertTickers(List<Ticker> tickers, long callTime);

}
