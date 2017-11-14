package com.fede.ct.v2.common.config._public;

import com.fede.ct.v2.common.config.AbstractConfig;
import com.fede.ct.v2.common.config.ConfigCommon;
import com.fede.ct.v2.common.constants.Const;
import static com.fede.ct.v2.common.config.ConfigKeys.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.logging.Level;

/**
 * Created by f.barbano on 03/11/2017.
 */
public class ConfigPublic extends ConfigCommon implements IConfigPublic {

	private static final IConfigPublic instance = new ConfigPublic();

	public static IConfigPublic getUniqueInstance() {
		return instance;
	}


	private ConfigPublic() {
		super();
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
