package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._public.IConfigPublic;
import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.datalayer.IDataModel;
import com.fede.ct.v2.datalayer.impl.ModelFactory;

/**
 * Created by f.barbano on 05/11/2017.
 */
abstract class AbstractCryptoService {

	protected final IConfigPublic configPublic = ConfigPublic.getUniqueInstance();

	protected final IDataModel dataModel;


	protected AbstractCryptoService() {
		this.dataModel = ModelFactory.getDataModel();
	}
}
