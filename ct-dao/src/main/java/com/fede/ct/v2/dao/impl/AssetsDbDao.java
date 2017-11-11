package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IAssetsDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class AssetsDbDao extends AbstractDbDao implements IAssetsDao {

	private static final String SELECT_VALIDS = "SELECT ASSET_NAME, A_CLASS, ALT_NAME, DECIMALS, DISPLAY_DECIMALS FROM ASSETS WHERE EXPIRE_TIME = 0 ORDER BY ASSET_NAME";
	private static final String UPDATE_EXPIRE_TIME = "UPDATE ASSETS SET EXPIRE_TIME = %d WHERE EXPIRE_TIME = 0";
	private static final String INSERT_NEW_PREFIX = "INSERT INTO ASSETS (ASSET_NAME, A_CLASS, ALT_NAME, DECIMALS, DISPLAY_DECIMALS, START_TIME, EXPIRE_TIME) VALUES ";


	public AssetsDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public List<Asset> selectAssets() {
		try (PreparedStatement ps = createPreparedStatement(SELECT_VALIDS);
			 ResultSet rs = ps.executeQuery()){

			List<Asset> assets = new ArrayList<>();
			if(rs != null) {
				while(rs.next()) {
					Asset asset = new Asset();
					asset.setAssetName(rs.getString("ASSET_NAME"));
					asset.setAClass(rs.getString("A_CLASS"));
					asset.setAltName(rs.getString("ALT_NAME"));
					asset.setDecimals(rs.getInt("DECIMALS"));
					asset.setDisplayDecimals(rs.getInt("DISPLAY_DECIMALS"));
					assets.add(asset);
				}
			}
			return assets;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", SELECT_VALIDS);
		}
	}

	@Override
	public void insertNewAssets(List<Asset> assets, long callTime) {
		String qUpdate = String.format(UPDATE_EXPIRE_TIME, callTime);
		String qInsert = INSERT_NEW_PREFIX + StreamUtil.join(assets, ",", a -> assetToValues(a, callTime));
		super.performUpdateBatch(qUpdate, qInsert);
	}

	private String assetToValues(Asset asset, Long callTime) {
		return String.format("('%s', '%s', '%s', %d, %d, %d, 0)",
			asset.getAssetName(),
			asset.getAClass(),
			asset.getAltName(),
			asset.getDecimals(),
			asset.getDisplayDecimals(),
			callTime
		);
	}
}
