package com.fede.ct.v2.datalayer;

import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model.types.OrderStatus;

import java.util.List;

/**
 * Created by f.barbano on 03/11/2017.
 */
public interface IDataModel {

	List<Asset> getAssets();
	List<AssetPair> getAssetPairs(boolean discardDotD);
	List<String> getAssetPairNames(boolean discardDotD);

	boolean setNewAssets(List<Asset> assets, long callTime);
	boolean setNewAssetPairs(List<AssetPair> assetPairs, long callTime);

	void insertTickers(List<Ticker> tickers, long callTime);

	void updateOrders(List<OrderInfo> orders, int userId);


	// Trading methods
	Ticker retrieveAskPriceAverageLast24(String pairName);

	List<String> getOpenOrderTxIds(int userId);
	List<OrderInfo> getOrderStatus(int userId, List<String> orderTxIds);
}
