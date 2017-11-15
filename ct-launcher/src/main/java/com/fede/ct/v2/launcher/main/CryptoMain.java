package com.fede.ct.v2.launcher.main;

import com.fede.ct.v2.common.config.ISettings;
import com.fede.ct.v2.common.config.impl.ConfigService;
import com.fede.ct.v2.common.constants.Const;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.context.RunType;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogFormatter;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.util.CheckUtils;
import com.fede.ct.v2.common.util.Converter;
import com.fede.ct.v2.login.LoginService;
import com.fede.ct.v2.service.ICryptoService;
import com.fede.ct.v2.service.impl.ServicePrivate;
import com.fede.ct.v2.service.impl.ServicePublic;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	private static final ISettings settings = ConfigService.getSettings();

	private static final String USAGE;
	static {
		String jarName = CryptoMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String usage = "USAGE\n";
		usage += String.format("   java -jar %s REGISTER_USER <username> <file containing kraken api & secret\n", jarName);
		usage += String.format("   java -jar %s [PUBLIC]\n", jarName);
		usage += String.format("   java -jar %s PRIVATE <user ID> [<private config file path>]\n", jarName);
//		usage += String.format("   java -jar %s STRATEGY  <strategy config file path>", jarName);
		USAGE = usage;
	}

	public static void main(String[] args) {
		try {
			initLogger();
			logger.config("SETTINGS:\n%s", settings);

			parseInputAndManage(args);
			
		} catch(TechnicalException ex) {
			logger.error("Error found: %s", ex.getMessage());
		}
	}

	private static void parseInputAndManage(String[] args) {

		RunType runType = RunType.PUBLIC;
		if(args.length > 0) {
			try {
				runType = RunType.valueOf(args[0].toUpperCase());
			} catch (Exception e) {
				showUsageAndExit();
				// exit
			}
		}
		logger.info("Run type: %s", runType);

		if(runType == RunType.PUBLIC) {
			managePublic(args);
		} else if(runType == RunType.PRIVATE) {
			managePrivate(args);
		} else if(runType == RunType.REGISTER_USER) {
			manageRegisterUser(args);
		} else {
			throw new TechnicalException("Unchecked run type %s", runType.name());
		}

	}

	private static void manageRegisterUser(String[] args) {
		if(args.length != 3 || !Files.exists(Paths.get(args[2]))) {
			showUsageAndExit();
		}
		manageUserRegistration(args[1], args[2]);
	}
	private static void managePublic(String[] args) {
		if(args.length != 1) {
			showUsageAndExit();
		}

		CryptoContext ctx = LoginService.createContext();
		ICryptoService service = new ServicePublic(ctx);
		service.startEngine();
	}
	private static void managePrivate(String[] args) {
		if((args.length != 2 && args.length != 3) || !CheckUtils.isInteger(args[1])) {
			showUsageAndExit();
		}
		if(args.length == 3 && !Files.exists(Paths.get(args[2]))) {
			exit("File %s does not exists", args[2]);
		}

		String configPath = args.length == 2 ? Const.CONFIG_PRIVATE_PATH : args[2];
		ConfigService.getConfigPrivate().loadConfigFromFile(configPath);

		CryptoContext ctx = LoginService.createContext(PRIVATE, Converter.stringToInteger(args[1]));
		ICryptoService service = new ServicePrivate(ctx);
		service.startEngine();
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
				throw new TechnicalException("No 'key' property found in file %s", krakenConfigPath);
			}
			if(StringUtils.isBlank(apiSecret)) {
				throw new TechnicalException("No 'secret' property found in file %s", krakenConfigPath);
			}

			CryptoContext ctx = LoginService.registerNewUser(userName, apiKey, apiSecret);
			logger.info("New user registered %s", ctx.getUserCtx());
			return ctx;

		} catch (IOException e) {
			throw new TechnicalException(e, "Unable to read file %s", krakenConfigPath);
		}
	}

	private static void showUsageAndExit() {
		exit(USAGE);
	}
	private static void exit(String mex, Object... params) {
		System.out.println(String.format(mex, params));
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
				return settings.getConsoleLevel();
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
