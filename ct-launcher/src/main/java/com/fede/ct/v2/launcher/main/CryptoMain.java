package com.fede.ct.v2.launcher.main;

import com.fede.ct.v2.common.config._public.IPublicConfig;
import com.fede.ct.v2.common.config._public.PublicConfig;
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

	private static final IPublicConfig publicConfig = PublicConfig.getUniqueInstance();

	private enum EngineTypology { PUBLIC, PRIVATE, STRATEGY }

	public static void main(String[] args) {
		long startMain = System.currentTimeMillis();

		// check user input
		EngineTypology runTypology = checkInputArgs(args);

		// init logger
		initLogger();

		// log configs
		logger.config("PUBLIC CONFIGS:\n%s", publicConfig);

		ICryptoService service;

		switch (runTypology) {
			case PUBLIC:	service = CryptoServiceFactory.getPublicService(); break;
			case PRIVATE:
			case STRATEGY:
			default:
				throw new TechnicalException("Service not yet implemented for run typology = %s", runTypology);
		}

		service.startEngine();

		logger.info("End %s run. Elapsed: %s", runTypology, OutFormat.toStringElapsed(startMain, System.currentTimeMillis(), false));
	}

	public static EngineTypology checkInputArgs(String[] args) {
		EngineTypology toRun = EngineTypology.PUBLIC;
		boolean showUsage = false;

		if(args.length > 1) {
			showUsage = true;
		} else if(args.length == 1) {
			try {
				toRun = EngineTypology.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException ex) {
				showUsage = true;
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

	private static void initLogger() {
		try {
			LogService.configure(getLoggerConfig());
		} catch (IOException e) {
			logger.error(e, "Unable to init logger");
			System.exit(2);
		}
	}

	private static LogServiceConfig getLoggerConfig() {
		return new LogServiceConfig() {
			@Override
			public String getRootLoggerName() {
				return "com.fede.ct.v2";
			}

			@Override
			public Level getConsoleLevel() {
				return publicConfig.getConsoleLevel();
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
				Path errPublicPath = publicConfig.getLogFolder().resolve("cryptotrading.public.warn");
				Pair<Level, LogFormatter> pair = Pair.of(Level.WARNING, new LogFormatter(true, true, true));
				return Collections.singletonMap(errPublicPath, pair);
			}
		};
	}
}
