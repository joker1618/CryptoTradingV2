package com.fede.ct.v2.common.config;

import java.io.IOException;

import static com.fede.ct.v2.common.config.ConfigKey.*;

/**
 * Created by f.barbano on 03/11/2017.
 */
public class Config extends AbstractConfig implements IConfig {

	private static final IConfig instance = new Config();

	public static IConfig getInstance() {
		return instance;
	}


	private Config() {
		super();
		try {
			loadConfigFile("config/config.properties");
			configureLogger();
		} catch (IOException e) {
			throw new RuntimeException("Unable to init config", e);
		}
	}
	private void configureLogger() {
		// todo
	}

	@Override
	public String getKrakenApiKey() {
		return getString(API_KEY);
	}

	@Override
	public String getKrakenApiSecret() {
		return getString(API_SECRET);
	}

	@Override
	public int getCallSecondsRateAssets() {
		return getInt(CALL_RATE_ASSETS, 86400);
	}

	@Override
	public int getCallSecondsRateAssetPairs() {
		return getInt(CALL_RATE_ASSET_PAIRS, 86400);
	}

	@Override
	public int getCallSecondsRateTickers() {
		return getInt(CALL_RATE_TICKERS, 20);
	}

	@Override
	public String getDbUrl() {
		return getString(DB_URL);
	}

	@Override
	public String getDbUsername() {
		return getString(DB_USERNAME);
	}

	@Override
	public String getDbPassword() {
		return getString(DB_PASSWORD);
	}
}
