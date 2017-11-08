package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._trading.AddOrderIn;
import com.fede.ct.v2.common.model._trading.AddOrderOut;
import com.fede.ct.v2.kraken.IKrakenPrivate;
import com.fede.ct.v2.kraken.IKrakenStrategy;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;
import com.fede.ct.v2.kraken.impl.api.KrakenApi;
import com.fede.ct.v2.kraken.impl.api.KrakenMethod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 04/11/2017.
 */
class KrakenStrategyImpl extends AbstractKrakenCaller implements IKrakenStrategy {

	KrakenStrategyImpl(String key, String secret) {
		super(new KrakenApi(key, secret));
	}

	@Override
	public AddOrderOut emitOrder(AddOrderIn orderRequest) throws KrakenException, KrakenCallError {
		List<ApiParam> apiParams = Arrays.asList(
			new ApiParam("pair", orderRequest.getPairName()),
			new ApiParam("type", orderRequest.getOrderAction().label()),
			new ApiParam("ordertype", orderRequest.getOrderType().label()),
			new ApiParam("price", String.valueOf(orderRequest.getPrice().doubleValue())),
			new ApiParam("volume", String.valueOf(orderRequest.getVolume()))
		);

		JsonToModel jm = super.performKrakenCall(KrakenMethod.ADD_ORDER, apiParams);
		return jm.parseOrderOut();
	}


}
