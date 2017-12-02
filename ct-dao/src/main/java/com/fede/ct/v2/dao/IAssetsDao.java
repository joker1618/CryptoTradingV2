package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._public.Asset;

import java.util.List;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IAssetsDao {

	List<Asset> selectAssets();

	Asset selectAsset(String assetName);

	void insertNewAssets(List<Asset> assets, long callTime);

}
