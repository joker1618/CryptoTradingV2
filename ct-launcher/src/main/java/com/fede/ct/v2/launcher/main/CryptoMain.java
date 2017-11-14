package com.fede.ct.v2.launcher.main;

import com.fede.ct.v2.common.config._private.ConfigPrivate;
import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.common.config._trading.ConfigStrategy;
import com.fede.ct.v2.common.config._public.IConfigPublic;
import com.fede.ct.v2.common.constants.Const;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.context.RunType;
import com.fede.ct.v2.common.context.RunType.*;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogFormatter;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.login.LoginService;
import com.fede.ct.v2.service.ICryptoService;
import com.fede.ct.v2.service.impl.CryptoServiceFactory;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;


import static com.fede.ct.v2.common.context.RunType.*;
import static com.fede.ct.v2.common.logger.LogService.LogServiceConfig;

/**
 * Created by f.barbano on 04/11/2017.
 */
public final class CryptoMain {

	private static final SimpleLog logger = LogService.getLogger(CryptoMain.class);

	private static final IConfigPublic configPublic = ConfigPublic.getUniqueInstance();

	public static void main(String[] args) {
		long startMain = System.currentTimeMillis();

		configPublic.loadConfigFile(Const.DEFAULT_PUBLIC_CONFIG_PATH);

		// check user input
		RunType runType = checkInputArgs(args);

		// review now only public context
		CryptoContext ctx = LoginService.createContext(runType);

		// init logger
		initLogger();

		// log configs
		logger.config("CONFIGURATION:\n%s", configPublic);

		ICryptoService service;

		switch (runType) {
			case PUBLIC:	service = CryptoServiceFactory.getServicePublic(ctx); break;
//			case PRIVATE:   service = CryptoServiceFactory.getServicePrivate(new ConfigPrivate(args[1])); break;
//			case STRATEGY:  service = CryptoServiceFactory.getServiceStrategy(new ConfigPrivate(args[1]), new ConfigStrategy(args[2])); break;
			default:
				throw new TechnicalException("Service not yet implemented for run typology = %s", runType);
		}

		service.startEngine();

		logger.info("End %s run. Elapsed: %s", runType, OutFormat.toStringElapsed(startMain, System.currentTimeMillis(), false));
	}

	private static RunType checkInputArgs(String[] args) {
		if(args.length == 0) {
			return RunType.PUBLIC;
		}

		RunType runType = RunType.valueOf(args[0].toUpperCase());
		boolean showUsage;

		if(runType == RunType.PUBLIC) {
			showUsage = args.length != 1;
		} else if(runType == RunType.PRIVATE || runType == RunType.STRATEGY) {
			showUsage = args.length != 2 || !Files.exists(Paths.get(args[1]));
		} else {
			throw new TechnicalException("Unchecked run type %s", runType.name());
		}

		if(showUsage) {
			String jarName = CryptoMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String usage = "USAGE\n";
			usage += String.format("   java -jar %s [PUBLIC]\n", jarName);
			usage += String.format("   java -jar %s PRIVATE  <private config file path>\n", jarName);
			usage += String.format("   java -jar %s STRATEGY  <strategy config file path>", jarName);
			System.out.println(usage);
			System.exit(1);
		}

		return runType;
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
				return null;
			}
		};
	}
}
