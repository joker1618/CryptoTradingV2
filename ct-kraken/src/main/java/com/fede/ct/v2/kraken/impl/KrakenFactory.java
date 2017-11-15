package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.common.context.UserCtx;
import com.fede.ct.v2.kraken.IKrakenPrivate;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.IKrakenTrading;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class KrakenFactory {

	public static IKrakenPublic getPublicCaller() {
		return new KrakenPublicImpl();
	}

	public static IKrakenPrivate getPrivateCaller(UserCtx userCtx) {
		return new KrakenPrivateImpl(userCtx.getApiKey(), userCtx.getApiSecret());
	}

	public static IKrakenTrading getTradingCaller(UserCtx userCtx) {
		return new KrakenTradingImpl(userCtx.getApiKey(), userCtx.getApiSecret());
	}

}
