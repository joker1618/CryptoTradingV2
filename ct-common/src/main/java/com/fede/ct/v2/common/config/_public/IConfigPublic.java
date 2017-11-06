package com.fede.ct.v2.common.config._public;

import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IConfigPublic {

	// Run parameters
	int getCallAssetsSecondsFrequency();
	int getCallAssetPairsSecondsFrequency();
	int getCallTickersSecondsFrequency();

	// DB config
	String getDbUrl();
	String getDbUsername();
	String getDbPassword();

	// Logger config
	Level getConsoleLevel();
	Path getLogFolder();
}
