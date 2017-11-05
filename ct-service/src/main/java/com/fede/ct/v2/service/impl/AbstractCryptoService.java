package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._public.IPublicConfig;
import com.fede.ct.v2.common.config._public.PublicConfig;
import com.fede.ct.v2.datalayer.ICryptoModel;
import com.fede.ct.v2.datalayer.impl.CryptoModelFactory;

/**
 * Created by f.barbano on 05/11/2017.
 */
abstract class AbstractCryptoService {

	protected final IPublicConfig config = PublicConfig.getUniqueInstance();

	protected final ICryptoModel model;


	protected AbstractCryptoService() {
		this.model = CryptoModelFactory.getModel();
	}
}
