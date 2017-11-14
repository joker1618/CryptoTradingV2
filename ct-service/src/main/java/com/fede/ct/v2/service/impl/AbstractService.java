package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.common.config._public.IConfigPublic;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.datalayer.IDataModel;
import com.fede.ct.v2.datalayer.impl.ModelFactory;

/**
 * Created by f.barbano on 05/11/2017.
 */
abstract class AbstractService {

	private final CryptoContext ctx;


	protected AbstractService(CryptoContext ctx) {
		this.ctx = ctx;
	}

	protected CryptoContext getContext() {
		return ctx;
	}

}
