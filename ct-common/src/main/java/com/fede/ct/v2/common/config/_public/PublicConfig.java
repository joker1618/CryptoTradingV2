package com.fede.ct.v2.common.config._public;

import com.fede.ct.v2.common.config.AbstractConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Created by f.barbano on 03/11/2017.
 */
public class PublicConfig extends AbstractConfig implements IPublicConfig {

	private static final IPublicConfig instance = new PublicConfig();

	public static IPublicConfig getUniqueInstance() {
		return instance;
	}


	private PublicConfig() {
		super();
		try {
			loadConfigFile("config/publicConfig.properties");
		} catch (IOException e) {
			throw new RuntimeException("Unable to init config", e);
		}
	}

	@Override
	public String getKrakenApiKey() {
		return getString(PublicKeys.API_KEY);
	}

	@Override
	public String getKrakenApiSecret() {
		return getString(PublicKeys.API_SECRET);
	}

	@Override
	public int getCallAssetsSecondsFrequency() {
		return getInt(PublicKeys.CALL_RATE_ASSETS, 86400);
	}

	@Override
	public int getCallAssetPairsSecondsFrequency() {
		return getInt(PublicKeys.CALL_RATE_ASSET_PAIRS, 86400);
	}

	@Override
	public int getCallTickersSecondsFrequency() {
		return getInt(PublicKeys.CALL_RATE_TICKERS, 20);
	}

	@Override
	public String getDbUrl() {
		return getString(PublicKeys.DB_URL);
	}

	@Override
	public String getDbUsername() {
		return getString(PublicKeys.DB_USERNAME);
	}

	@Override
	public String getDbPassword() {
		return getString(PublicKeys.DB_PASSWORD);
	}

	@Override
	public Level getConsoleLevel() {
		return getLoggerLevel(PublicKeys.CONSOLE_LEVEL, Level.ALL);
	}

	@Override
	public Path getLogFolder() {
		return Paths.get("logs");
	}
}
