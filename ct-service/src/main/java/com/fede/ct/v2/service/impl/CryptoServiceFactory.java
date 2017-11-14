package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._private.IConfigPrivate;
import com.fede.ct.v2.common.config._trading.IConfigStrategy;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.service.ICryptoService;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class CryptoServiceFactory {

	private static ServicePublic servicePublic;

	public static synchronized ICryptoService getServicePublic(CryptoContext ctx) {
		if(servicePublic == null) {
			servicePublic = new ServicePublic(ctx);
		}
		return servicePublic;
	}

//	public static synchronized ICryptoService getServicePrivate(IConfigPrivate configPrivate) {
//		return new ServicePrivate(configPrivate);
//	}
//
//	public static synchronized ICryptoService getServiceStrategy(IConfigPrivate configPrivate, IConfigStrategy configStrategy) {
//		return new ServiceStrategy(configPrivate, configStrategy);
//	}
}
