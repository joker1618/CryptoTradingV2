package com.fede.ct.v2.common.config;

import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Created by f.barbano on 11/11/2017.
 */
public interface ISettings {

	// DB config
	String getDbUrl();
	String getDbUser();
	String getDbPwd();

	// Logger config
	Level getLoggerLevel();
	Level getConsoleLevel();
	Path getLogErrorPath();
	Path getLogAllPath();

}
