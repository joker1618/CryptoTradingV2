package com.fede.ct.v2.common.config.impl;

import com.fede.ct.v2.common.config.AbstractConfig;
import com.fede.ct.v2.common.config.IConfigPublic;
import com.fede.ct.v2.common.exception.TechnicalException;

import java.io.IOException;

import static com.fede.ct.v2.common.config.ConfigKeys.*;

/**
 * Created by f.barbano on 03/11/2017.
 */
class ConfigPublicImpl extends AbstractConfig implements IConfigPublic {

	ConfigPublicImpl(String filePath) {
		super(filePath);
	}


	@Override
	public int getCallRateAssets() {
		return getInt(PublicKeys.CALL_RATE_ASSETS);
	}

	@Override
	public int getCallRateAssetPairs() {
		return getInt(PublicKeys.CALL_RATE_ASSET_PAIRS);
	}

	@Override
	public int getCallRateTickers() {
		return getInt(PublicKeys.CALL_RATE_TICKERS);
	}
}
