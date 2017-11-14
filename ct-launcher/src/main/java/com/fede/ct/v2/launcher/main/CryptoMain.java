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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;


import static com.fede.ct.v2.common.context.RunType.*;
import static com.fede.ct.v2.common.logger.LogService.LogServiceConfig;

/**
 * Created by f.barbano on 04/11/2017.
 */
public final class CryptoMain {

	private static final SimpleLog logger = LogService.getLogger(CryptoMain.class);

	private static final IConfigPublic configPublic = ConfigPublic.getUniqueInstance();

	private static final String USAGE;
	static {
		String jarName = CryptoMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String usage = "USAGE\n";
		usage += String.format("   java -jar %s [PUBLIC]\n", jarName);
		usage += String.format("   java -jar %s PRIVATE  <private config file path>\n", jarName);
		usage += String.format("   java -jar %s STRATEGY  <strategy config file path>", jarName);
		USAGE = usage;
	}

	public static void main(String[] args) {
		long startMain = System.currentTimeMillis();

		configPublic.loadConfigFile(Const.DEFAULT_PUBLIC_CONFIG_PATH);

		// check user input
		RunType runType = checkInputArgs(args);

		if(runType == REGISTER_USER) {
			manageUserRegistration(args[1].toUpperCase(), args[2]);

		} else {
			// review now only public context
			CryptoContext ctx = LoginService.createContext();

			// init logger
			initLogger();

			// log configs
			logger.config("CONFIGURATION:\n%s", configPublic);

			ICryptoService service;

			switch (runType) {
				case PUBLIC:
					service = CryptoServiceFactory.getServicePublic(ctx);
					break;
				default:
					throw new TechnicalException("Service not yet implemented for run typology = %s", runType);
			}

			service.startEngine();
		}

		logger.info("End %s run. Elapsed: %s", runType, OutFormat.toStringElapsed(startMain, System.currentTimeMillis(), false));
	}

	private static CryptoContext manageUserRegistration(String userName, String krakenConfigPath) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(krakenConfigPath));
			List<String> configs = lines.stream().map(String::trim).filter(line -> StringUtils.startsWithAny(line, "key=", "secret=")).collect(Collectors.toList());

			String apiKey = "";
			String apiSecret = "";
			for(String line : configs) {
				String[] split = line.split("=");
				if(split[0].trim().equals("key")) 		apiKey = split[1].trim();
				if(split[0].trim().equals("secret")) 	apiSecret = split[1].trim();
			}

			if(StringUtils.isBlank(apiKey)) {
				throw new TechnicalException("Kraken Api key blank in file %s", krakenConfigPath);
			}
			if(StringUtils.isBlank(apiSecret)) {
				throw new TechnicalException("Kraken Api secret blank in file %s", krakenConfigPath);
			}

			CryptoContext ctx = LoginService.registerNewUser(userName, apiKey, apiSecret);
			return ctx;

		} catch (IOException e) {
			throw new TechnicalException(e, "Unable to read file %s", krakenConfigPath);
		}
	}

	private static RunType checkInputArgs(String[] args) {
		if(args.length == 0) {
			return RunType.PUBLIC;
		}
		
		RunType runType;
		try {
			runType = RunType.valueOf(args[0].toUpperCase());
		} catch(Exception e) {
			showUsageAndExit();
			return null;
		}

		boolean showUsage;

		if(runType == RunType.PUBLIC) {
			showUsage = args.length != 1;
		} else if(runType == RunType.PRIVATE || runType == RunType.STRATEGY) {
			showUsage = args.length != 2 || !Files.exists(Paths.get(args[1]));
		} else if(runType == RunType.REGISTER_USER) {
			showUsage = args.length != 3 || !Files.exists(Paths.get(args[2]));
		} else {
			throw new TechnicalException("Unchecked run type %s", runType.name());
		}

		if(showUsage) {
			showUsageAndExit();
		}

		return runType;
	}

	private static void showUsageAndExit() {
		System.out.println(USAGE);
		System.exit(1);
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
