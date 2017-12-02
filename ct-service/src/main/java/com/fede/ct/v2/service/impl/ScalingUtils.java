package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._private.OrderInfo.OrderDescr;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._public.Ticker.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by f.barbano on 29/11/2017.
 */
class ScalingUtils {

	static Ticker getScaledTicker(Ticker ticker, AssetPair assetPair) {
		Ticker scaled = new Ticker();
		scaled.setCallTime(ticker.getCallTime());
		scaled.setPairName(ticker.getPairName());
		scaled.setAsk(scaleTickerWholePrice(ticker.getAsk(), assetPair));
		scaled.setBid(scaleTickerWholePrice(ticker.getBid(), assetPair));
		scaled.setLastTradeClosed(scaleTickerPrice(ticker.getLastTradeClosed(), assetPair));
		scaled.setVolume(scaleTickerVolume(ticker.getVolume(), assetPair));
		scaled.setWeightedAverageVolume(scaleTickerVolume(ticker.getWeightedAverageVolume(), assetPair));
		scaled.setTradesNumber(scaleTickerVolume(ticker.getTradesNumber(), assetPair));
		scaled.setLow(scaleTickerVolume(ticker.getLow(), assetPair));
		scaled.setHigh(scaleTickerVolume(ticker.getHigh(), assetPair));
		scaled.setOpeningPrice(scaleBigDecimal(ticker.getOpeningPrice(), assetPair.getPairDecimals()));
		return scaled;
	}

	static AccountBalance getScaledAccountBalance(AccountBalance accountBalance, Asset asset) {
		if(accountBalance == null || asset == null) {
			return null;
		}

		AccountBalance scaled = new AccountBalance();
		scaled.setCallTime(accountBalance.getCallTime());
		scaled.setAssetName(accountBalance.getAssetName());
		scaled.setBalance(scaleBigDecimal(accountBalance.getBalance(), asset.getDecimals()));
		return scaled;
	}

	static OrderInfo getScaledOrderInfo(OrderInfo order, AssetPair assetPair) {
		Integer pairDec = assetPair.getPairDecimals();
		Integer lotDec = assetPair.getLotDecimals();

		OrderInfo scaled = new OrderInfo();
		scaled.setOrderTxID(order.getOrderTxID());
		scaled.setRefId(order.getRefId());
		scaled.setUserRef(order.getUserRef());
		scaled.setStatus(order.getStatus());
		scaled.setReason(order.getReason());
		scaled.setOpenTm(order.getOpenTm());
		scaled.setCloseTm(order.getCloseTm());
		scaled.setStartTm(order.getStartTm());
		scaled.setExpireTm(order.getExpireTm());

		scaled.setVol(scaleBigDecimal(order.getVol(), lotDec));
		scaled.setVolExec(scaleBigDecimal(order.getVolExec(), lotDec));
		scaled.setCost(scaleBigDecimal(order.getCost(), pairDec));
		scaled.setFee(scaleBigDecimal(order.getFee(), pairDec));
		scaled.setAvgPrice(scaleBigDecimal(order.getAvgPrice(), pairDec));
		scaled.setStopPrice(scaleBigDecimal(order.getStopPrice(), pairDec));
		scaled.setLimitPrice(scaleBigDecimal(order.getLimitPrice(), pairDec));

		scaled.setMisc(order.getMisc());
		scaled.setOflags(order.getOflags());
		scaled.setTradesId(order.getTradesId());

		OrderDescr od = new OrderDescr();
		OrderDescr descr = order.getDescr();
		od.setPairName(descr.getPairName());
		od.setOrderAction(descr.getOrderAction());
		od.setOrderType(descr.getOrderType());
		od.setPrimaryPrice(scaleBigDecimal(descr.getPrimaryPrice(), pairDec));
		od.setSecondaryPrice(scaleBigDecimal(descr.getSecondaryPrice(), pairDec));
		od.setLeverage(descr.getLeverage());
		od.setOrderDescription(descr.getOrderDescription());
		od.setCloseDescription(descr.getCloseDescription());

		scaled.setDescr(od);

		return scaled;
	}


	private static TickerPrice scaleTickerPrice(TickerPrice tp, AssetPair ap) {
		TickerPrice scaled = new TickerPrice();
		scaled.setLotVolume(scaleBigDecimal(tp.getLotVolume(), ap.getLotDecimals()));
		scaled.setPrice(scaleBigDecimal(tp.getPrice(), ap.getPairDecimals()));
		return scaled;
	}
	private static TickerWholePrice scaleTickerWholePrice(TickerWholePrice twp, AssetPair ap) {
		TickerWholePrice scaled = new TickerWholePrice(scaleTickerPrice(twp, ap));
		scaled.setWholeLotVolume(twp.getWholeLotVolume());
		return scaled;
	}
	private static TickerVolume scaleTickerVolume(TickerVolume tv, AssetPair ap) {
		TickerVolume scaled = new TickerVolume();
		scaled.setToday(scaleBigDecimal(tv.getToday(), ap.getLotDecimals()));
		scaled.setLast24Hours(scaleBigDecimal(tv.getLast24Hours(), ap.getLotDecimals()));
		return scaled;
	}

	private static BigDecimal scaleBigDecimal(BigDecimal num, int scale) {
		return num == null ? num : num.setScale(scale, RoundingMode.HALF_EVEN);
	}
}
