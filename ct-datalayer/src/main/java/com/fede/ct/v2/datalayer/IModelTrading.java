package com.fede.ct.v2.datalayer;

import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;

import java.util.List;

/**
 * Created by f.barbano on 15/11/2017.
 */
public interface IModelTrading {

	List<AssetPair> getAssetPairs(boolean onlyTradables);

	Ticker getTickerAskPriceAndAvgLast24(String pairName);

	AccountBalance getAssetBalance(String assetName);

	void turnOnDownloadOrders();
	boolean isDownloadOrdersEnabled();

	List<OrderInfo> getOrdersStatus(List<String> txIds);
	List<OrderInfo> getOrders(Long minOpenTm, Long maxOpenTm);

}
