package com.fede.ct.v2.common.config._public;

import com.fede.ct.v2.common.config.AbstractConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Created by f.barbano on 03/11/2017.
 */
public class ConfigPublic extends AbstractConfig implements IConfigPublic {

	private static final IConfigPublic instance = new ConfigPublic();

	public static IConfigPublic getUniqueInstance() {
		return instance;
	}


	private ConfigPublic() {
		super("config/publicConfig.properties");
	}

	@Override
	public int getCallAssetsSecondsFrequency() {
		return getInt(KeysPublic.CALL_RATE_ASSETS, 86400);
	}

	@Override
	public int getCallAssetPairsSecondsFrequency() {
		return getInt(KeysPublic.CALL_RATE_ASSET_PAIRS, 86400);
	}

	@Override
	public int getCallTickersSecondsFrequency() {
		return getInt(KeysPublic.CALL_RATE_TICKERS, 20);
	}

	@Override
	public String getDbUrl() {
		return getString(KeysPublic.DB_URL);
	}

	@Override
	public String getDbUsername() {
		return getString(KeysPublic.DB_USERNAME);
	}

	@Override
	public String getDbPassword() {
		return getString(KeysPublic.DB_PASSWORD);
	}

	@Override
	public Level getConsoleLevel() {
		return getLoggerLevel(KeysPublic.CONSOLE_LEVEL, Level.ALL);
	}

	@Override
	public Path getLogFolder() {
		return Paths.get("logs");
	}
}
