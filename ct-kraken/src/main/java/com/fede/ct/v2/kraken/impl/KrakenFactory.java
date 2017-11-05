package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.kraken.IKrakenPublic;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class KrakenFactory {

	public static IKrakenPublic getPublicCaller() {
		return new KrakenPublicImpl();
	}

}
