package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IAssetsDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class AssetsDbDao extends AbstractDbDao implements IAssetsDao {

	private static final String SELECT_VALID_ASSETS = "SELECT ASSET_NAME, A_CLASS, ALT_NAME, DECIMALS, DISPLAY_DECIMALS FROM ASSETS WHERE EXPIRE_TIME = 0 ORDER BY ASSET_NAME";
	private static final String UPDATE_EXPIRE_TIME = "UPDATE ASSETS SET EXPIRE_TIME = ? WHERE EXPIRE_TIME = 0";
	private static final String INSERT_NEW_PREFIX = "INSERT INTO ASSETS (ASSET_NAME, A_CLASS, ALT_NAME, DECIMALS, DISPLAY_DECIMALS, START_TIME, EXPIRE_TIME) VALUES ";


	public AssetsDbDao(CryptoContext ctx) {
		super(ctx);
	}

	@Override
	public List<Asset> selectAssets() {
		List<InquiryResult> results = super.performInquiry(new Query(SELECT_VALID_ASSETS));
		return StreamUtil.map(results, this::parseAsset);
	}

	@Override
	public void insertNewAssets(List<Asset> assets, long callTime) {
		List<Query> queries = new ArrayList<>();
		queries.add(new Query(UPDATE_EXPIRE_TIME, callTime));
		queries.addAll(createQueryInsertNew(assets, callTime));
		super.performTransaction(queries);
	}

	private List<Query> createQueryInsertNew(List<Asset> assets, long callTime) {
		List<Function<Asset, Object>> functions = new ArrayList<>();
		functions.add(Asset::getAssetName);
		functions.add(Asset::getAClass);
		functions.add(Asset::getAltName);
		functions.add(Asset::getDecimals);
		functions.add(Asset::getDisplayDecimals);
		functions.add(asset -> callTime);
		functions.add(asset -> 0L);
		return super.createJdbcQueries(INSERT_NEW_PREFIX, assets.size(), 7, assets, functions);
	}

	private Asset parseAsset(InquiryResult res) {
		Asset asset = new Asset();
		asset.setAssetName(res.getString("ASSET_NAME"));
		asset.setAClass(res.getString("A_CLASS"));
		asset.setAltName(res.getString("ALT_NAME"));
		asset.setDecimals(res.getInteger("DECIMALS"));
		asset.setDisplayDecimals(res.getInteger("DISPLAY_DECIMALS"));
		return asset;
	}

}
