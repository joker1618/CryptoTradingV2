package com.fede.ct.v2.datalayer;

import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;

import java.util.Collection;
import java.util.List;

/**
 * Created by f.barbano on 03/11/2017.
 */
public interface ICryptoModel {

	List<Asset> getAssets();
	List<AssetPair> getAssetPairs();

	boolean setNewAssets(Collection<Asset> assets);
	boolean setNewAssetPairs(Collection<AssetPair> assetPairs);

	void insertTickers(Collection<Ticker> tickers);

}
