package com.fede.ct.v2.common.model._trading;


import com.fede.ct.v2.common.model.types.OrderAction;
import com.fede.ct.v2.common.model.types.OrderType;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 02/11/2017.
 */
public class OrderRequest {

	public static AddOrderIn createBuyLimitOrderIn(String pairName, BigDecimal price, double volume) {
		AddOrderIn request = new AddOrderIn(pairName, OrderAction.BUY, OrderType.LIMIT, volume);
		request.setPrice(price);
		return request;
	}

	public static AddOrderIn createSellLimitOrderIn(String pairName, BigDecimal price, double volume) {
		AddOrderIn request = new AddOrderIn(pairName, OrderAction.SELL, OrderType.LIMIT, volume);
		request.setPrice(price);
		return request;
	}


}
