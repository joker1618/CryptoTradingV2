package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.kraken.IKrakenPrivate;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.IKrakenStrategy;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class KrakenFactory {

	public static IKrakenPublic getPublicCaller() {
		return new KrakenPublicImpl();
	}

	public static IKrakenPrivate getPrivateCaller(String key, String secret) {
		return new KrakenPrivateImpl(key, secret);
	}

	public static IKrakenStrategy getStrategyCaller(String key, String secret) {
		return new KrakenStrategyImpl(key, secret);
	}

}
