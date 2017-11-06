package com.fede.ct.v2.launcher.main;

import com.fede.ct.v2.common.config._public.ConfigPrivate;
import com.fede.ct.v2.common.config._public.IConfigPublic;
import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogFormatter;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.service.ICryptoService;
import com.fede.ct.v2.service.impl.CryptoServiceFactory;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

import static com.fede.ct.v2.common.logger.LogService.LogServiceConfig;

/**
 * Created by f.barbano on 04/11/2017.
 */
public final class CryptoMain {

	private static final SimpleLog logger = LogService.getLogger(CryptoMain.class);
	// todo remove
	private static final String PRIVATE_CONFIG_PATH = "config/privatePianta.properties";

	private static final IConfigPublic configPublic = ConfigPublic.getUniqueInstance();

	private enum EngineTypology { PUBLIC, PRIVATE, STRATEGY }

	public static void main(String[] args) {
		long startMain = System.currentTimeMillis();

		// check user input
		EngineTypology runTypology = checkInputArgs(args);

		// init logger
		initLogger(runTypology);

		// log configs
		logger.config("PUBLIC CONFIGS:\n%s", configPublic);

		ICryptoService service;

		switch (runTypology) {
			case PUBLIC:	service = CryptoServiceFactory.getServicePublic(); break;
			case PRIVATE:   service = CryptoServiceFactory.getServicePrivate(new ConfigPrivate(PRIVATE_CONFIG_PATH)); break;
			case STRATEGY:
			default:
				throw new TechnicalException("Service not yet implemented for run typology = %s", runTypology);
		}

		service.startEngine();

		logger.info("End %s run. Elapsed: %s", runTypology, OutFormat.toStringElapsed(startMain, System.currentTimeMillis(), false));
	}

	private static EngineTypology checkInputArgs(String[] args) {
		EngineTypology toRun = EngineTypology.PUBLIC;

		boolean showUsage = true;
		if(args.length == 1) {
			try {
				toRun = EngineTypology.valueOf(args[0].toUpperCase());
				showUsage = false;
			} catch (IllegalArgumentException ex) {
			}
		}

		if(showUsage) {
			String jarName = CryptoMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String usage = String.format("USAGE:\njava -jar %s [PUBLIC|PRIVATE|STRATEGY]\n* default = PUBLIC", jarName);
			System.out.println(usage);
			System.exit(1);
		}

		return toRun;
	}

	private static void initLogger(EngineTypology runTypology) {
		try {
			LogService.configure(getLoggerConfig(runTypology));
		} catch (IOException e) {
			logger.error(e, "Unable to init logger");
			System.exit(2);
		}
	}

	private static LogServiceConfig getLoggerConfig(EngineTypology runTypology) {
		return new LogServiceConfig() {
			@Override
			public String getRootLoggerName() {
				return "com.fede.ct.v2";
			}

			@Override
			public Level getConsoleLevel() {
				return configPublic.getConsoleLevel();
			}

			@Override
			public LogFormatter getConsoleFormatter() {
				return new LogFormatter(false, false, true);
			}

			@Override
			public boolean isShowStackTrace() {
				return true;
			}

			@Override
			public Map<Path, Pair<Level, LogFormatter>> getFileHandlers() {
				Path errPublicPath = configPublic.getLogFolder().resolve(String.format("ct.%s.warn", runTypology.name().toLowerCase()));
				Pair<Level, LogFormatter> pair = Pair.of(Level.WARNING, new LogFormatter(true, true, true));
				return Collections.singletonMap(errPublicPath, pair);
			}
		};
	}
}
