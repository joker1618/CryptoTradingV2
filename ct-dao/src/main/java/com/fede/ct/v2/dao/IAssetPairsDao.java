package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._public.AssetPair;

import java.util.List;

/**
 * Created by f.barbano on 30/09/2017.
 */
public interface IAssetPairsDao {

	List<AssetPair> selectAssetPairs(boolean onlyTradables);
	List<String> selectAssetPairNames(boolean onlyTradables);

	void insertNewAssetPairs(List<AssetPair> assetPairs, long callTime);

}
