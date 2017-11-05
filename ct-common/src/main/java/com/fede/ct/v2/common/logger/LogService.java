package com.fede.ct.v2.common.logger;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by f.barbano on 01/10/2017.
 */
public class LogService {

	private static final LogService INSTANCE = new LogService();

	private Logger rootLogger;
	private Level minLevel;
	private boolean showStackTrace;
//	private ConsoleHandler consoleHandler;
//	private Map<Path,FileHandler> fileHandlerMap;


	private LogService() {
//		this.fileHandlerMap = new HashMap<>();
		minLevel = Level.ALL;

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if(rootLogger != null && rootLogger.getHandlers() != null) {
				Arrays.stream(rootLogger.getHandlers()).forEach(fh -> {
					if(fh != null) {
						fh.flush();
						fh.close();
					}
					rootLogger.removeHandler(fh);
				});
			}
//			if(fileHandlerMap != null)	fileHandlerMap.values().forEach(FileHandler::close);
//			if(consoleHandler != null) 	consoleHandler.close();
		}));
	}

	public static void configure(LogServiceConfig config) throws IOException {
		synchronized (INSTANCE) {
			// Turn off global configs
			Logger global = Logger.getLogger("");
			global.setLevel(Level.OFF);
			Arrays.stream(global.getHandlers()).forEach(global::removeHandler);

			INSTANCE.rootLogger = Logger.getLogger(config.getRootLoggerName());
			INSTANCE.rootLogger.setUseParentHandlers(false);
			INSTANCE.rootLogger.setLevel(Level.ALL);

			Level consoleLevel = config.getConsoleLevel();
			if(consoleLevel != Level.OFF) {
//				INSTANCE.consoleHandler = new ConsoleHandler();
//				INSTANCE.consoleHandler.setLevel(Level.ALL);
//				INSTANCE.consoleHandler.setFormatter(config.getConsoleFormatter());
//				INSTANCE.rootLogger.addHandler(INSTANCE.consoleHandler);
				ConsoleHandler ch = new ConsoleHandler();
				ch.setLevel(consoleLevel);
				ch.setFormatter(config.getConsoleFormatter());
				INSTANCE.rootLogger.addHandler(ch);
			}

			Level min = consoleLevel;
			Map<Path, Pair<Level, LogFormatter>> fhMap = config.getFileHandlers();
			if(fhMap != null && !fhMap.isEmpty()) {
				for(Map.Entry<Path, Pair<Level, LogFormatter>> entry : fhMap.entrySet()) {
					Files.createDirectories(entry.getKey().toAbsolutePath().getParent());
					FileHandler fh = new FileHandler(entry.getKey().toString(), true);
					Level fhLevel = entry.getValue().getKey();
					fh.setLevel(fhLevel);
					fh.setFormatter(entry.getValue().getValue());
					INSTANCE.rootLogger.addHandler(fh);
					if(fhLevel.intValue() < min.intValue())		min = fhLevel;
				}
			}

			INSTANCE.minLevel = min;
			INSTANCE.showStackTrace = config.isShowStackTrace();
		}
	}

	public static SimpleLog getLogger(String loggerName) {
		Logger logger = Logger.getLogger(loggerName);
		return new SimpleLogImpl(logger, INSTANCE.showStackTrace);
	}
	public static SimpleLog getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Level getMinLevel() {
		return INSTANCE.minLevel;
	}

	public interface LogServiceConfig {
		String getRootLoggerName();
		Level getConsoleLevel();
		LogFormatter getConsoleFormatter();
		boolean isShowStackTrace();
		Map<Path, Pair<Level, LogFormatter>> getFileHandlers();
	}

	public static class LogLevel extends Level {
		/**
		 * Duplicate of SEVERE level (same value = 1000)
		 */
		public static final Level ERROR = new LogLevel("ERROR", 1000);

		/**
		 * DEBUG level value between INFO and CONFIG
		 */
		public static final Level DEBUG = new LogLevel("DEBUG", 750);

		protected LogLevel(String name, int value) {
			super(name, value);
		}
	}
}
