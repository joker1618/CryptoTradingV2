package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.service.ICryptoService;

/**
 * Created by f.barbano on 07/11/2017.
 */
public class ServiceTrading extends AbstractService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServiceTrading.class);
	private static final int THREAD_POOL_SIZE = 1;


	public ServiceTrading(CryptoContext ctx) {
		super(ctx);
	}

	@Override
	public void startEngine() {

	}
}
