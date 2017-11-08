package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._public.IConfigPrivate;
import com.fede.ct.v2.common.config._public.IConfigStrategy;
import com.fede.ct.v2.service.ICryptoService;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class CryptoServiceFactory {

	private static ServicePublic servicePublic;

	public static synchronized ICryptoService getServicePublic() {
		if(servicePublic == null) {
			servicePublic = new ServicePublic();
		}
		return servicePublic;
	}

	public static synchronized ICryptoService getServicePrivate(IConfigPrivate configPrivate) {
		return new ServicePrivate(configPrivate);
	}

	public static synchronized ICryptoService getServiceStrategy(IConfigPrivate configPrivate, IConfigStrategy configStrategy) {
		return new ServiceStrategy(configPrivate, configStrategy);
	}
}
