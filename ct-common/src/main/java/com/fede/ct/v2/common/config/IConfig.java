package com.fede.ct.v2.common.config;

import java.util.logging.Level;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IConfig {

	// Api token
	String getKrakenApiKey();
	String getKrakenApiSecret();

	// Run parameters
	int getCallSecondsRateAssets();
	int getCallSecondsRateAssetPairs();
	int getCallSecondsRateTickers();

	// DB config
	String getDbUrl();
	String getDbUsername();
	String getDbPassword();

	// Logger config
	Level getConsoleLevel();
}
