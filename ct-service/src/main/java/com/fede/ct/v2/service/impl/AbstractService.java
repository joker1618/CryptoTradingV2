package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.context.CryptoContext;

/**
 * Created by f.barbano on 05/11/2017.
 */
abstract class AbstractService {

	private final CryptoContext ctx;


	protected AbstractService(CryptoContext ctx) {
		this.ctx = ctx;
	}

	protected CryptoContext getCtx() {
		return ctx;
	}

}
