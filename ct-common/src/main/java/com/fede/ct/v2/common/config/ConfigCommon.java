package com.fede.ct.v2.common.config;

import com.fede.ct.v2.common.exception.TechnicalException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import static com.fede.ct.v2.common.config.ConfigKeys.*;


/**
 * Created by f.barbano on 11/11/2017.
 */
public abstract class ConfigCommon extends AbstractConfig implements IConfigCommon {

	protected ConfigCommon() {
		super();
	}

	@Override
	public void loadConfigFile(String filePath) {
		try {
			super.loadConfigFile(filePath);
		} catch (IOException e) {
			throw new TechnicalException(e, "Unable to load config from file %s", filePath);
		}
	}

	@Override
	public String getDbUrl() {
		return getString(CommonKeys.DB_URL);
	}
	@Override
	public String getDbUser() {
		return getString(CommonKeys.DB_USERNAME);
	}
	@Override
	public String getDbPwd() {
		return getString(CommonKeys.DB_PASSWORD);
	}

	@Override
	public Level getLoggerLevel() {
		return getLoggerLevel(CommonKeys.LOGGER_LEVEL);
	}
	@Override
	public Level getConsoleLevel() {
		return getLoggerLevel(CommonKeys.CONSOLE_LEVEL);
	}
	@Override
	public Path getLogErrorPath() {
		String fileName = getString(CommonKeys.LOG_ERROR_FILENAME);
		return StringUtils.isBlank(fileName) ? null : Paths.get(getLogFolder(), fileName);
	}
	@Override
	public Path getLogAllPath() {
		String fileName = getString(CommonKeys.LOG_ALL_FILENAME);
		return StringUtils.isBlank(fileName) ? null : Paths.get(getLogFolder(), fileName);
	}

	private String getLogFolder() {
		return getString(CommonKeys.LOG_FOLDER, "");
	}
}
