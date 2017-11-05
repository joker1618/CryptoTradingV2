package com.fede.ct.v2.launcher.main;

import com.fede.ct.v2.common.config.Config;
import com.fede.ct.v2.common.config.IConfig;
import com.fede.ct.v2.common.logger.LogFormatter;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.service.IPublicService;
import com.fede.ct.v2.service.PublicService;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;

import static com.fede.ct.v2.common.logger.LogService.LogServiceConfig;

/**
 * Created by f.barbano on 04/11/2017.
 */
public class Main {

	private static final SimpleLog logger = LogService.getLogger(Main.class);
	private static final IConfig config = Config.getUniqueInstance();

	public static void main(String[] args) {
		// check user input
		checkInputArgs(args);

		// init logger
		initLogger();

		// log configs
		logger.config("CONFIGS:\n%s", config);

		IPublicService publicService = PublicService.getService();
		publicService.startPublicEngine();

	}

	public static void checkInputArgs(String[] args) {
		if(args.length != 0) {
			String jarName = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String usage = String.format("USAGE:\t\tjava -jar %s", jarName);
			System.out.println(usage);
			System.exit(1);
		}
	}

	public static void initLogger() {
		try {
			LogService.configure(getLoggerConfig());
		} catch (IOException e) {
			logger.error(e, "Unable to init logger");
			System.exit(2);
		}
	}

	public static LogServiceConfig getLoggerConfig() {
		return new LogServiceConfig() {
			@Override
			public String getRootLoggerName() {
				return "com.fede.ct.v2";
			}

			@Override
			public Level getConsoleLevel() {
				return config.getConsoleLevel();
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
