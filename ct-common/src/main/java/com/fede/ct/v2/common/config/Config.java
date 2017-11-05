package com.fede.ct.v2.common.config;

import java.io.IOException;
import java.util.logging.Level;

import static com.fede.ct.v2.common.config.ConfigKeys.*;

/**
 * Created by f.barbano on 03/11/2017.
 */
public class Config extends AbstractConfig implements IConfig {

	private static final IConfig instance = new Config();

	public static IConfig getUniqueInstance() {
		return instance;
	}


	private Config() {
		super();
		try {
			loadConfigFile("config/publicConfig.properties");
		} catch (IOException e) {
			throw new RuntimeException("Unable to init config", e);
		}
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
	public int getCallAssetsSecondsFrequency() {
		return getInt(CALL_RATE_ASSETS, 86400);
	}

	@Override
	public int getCallAssetPairsSecondsFrequency() {
		return getInt(CALL_RATE_ASSET_PAIRS, 86400);
	}

	@Override
	public int getCallTickersSecondsFrequency() {
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

	@Override
	public Level getConsoleLevel() {
		return getLoggerLevel(CONSOLE_LEVEL, Level.ALL);
	}
}
