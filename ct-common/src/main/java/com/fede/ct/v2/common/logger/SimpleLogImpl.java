package com.fede.ct.v2.common.logger;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.fede.ct.v2.common.logger.LogService.LogLevel;

/**
 * Created by f.barbano on 01/10/2017.
 */
class SimpleLogImpl implements SimpleLog {

	private Logger logger;
	private boolean showStackTrace;

	SimpleLogImpl(Logger logger, boolean showStackTrace) {
		this.logger = logger;
		this.showStackTrace = showStackTrace;
	}

	@Override
	public synchronized void error(Throwable t) {
		error(t, null);
	}
	@Override
	public synchronized void error(String mex, Object... params) {
		error(null, mex, params);
	}
	@Override
	public synchronized void error(Throwable t, String mex, Object... params) {
		doLogThrowable(t, LogLevel.ERROR, mex, params);
	}

	@Override
	public synchronized void warning(Throwable t) {
		warning(t, null);
	}

	@Override
	public synchronized void warning(String mex, Object... params) {
		warning(null, mex, params);
	}

	@Override
	public void warning(Throwable t, String mex, Object... params) {
		doLogThrowable(t, Level.WARNING, mex, params);
	}

	@Override
	public synchronized void info(String mex, Object... params) {
		doLog(Level.INFO, String.format(mex, params));
	}

	@Override
	public void debug(String mex, Object... params) {
		doLog(LogLevel.DEBUG, String.format(mex, params));
	}

	@Override
	public synchronized void config(String mex, Object... params) {
		doLog(Level.CONFIG, String.format(mex, params));
	}
	@Override
	public synchronized void fine(String mex, Object... params) {
		doLog(Level.FINE, String.format(mex, params));
	}
	@Override
	public synchronized void finer(String mex, Object... params) {
		doLog(Level.FINER, String.format(mex, params));
	}
	@Override
	public synchronized void finest(String mex, Object... params) {
		doLog(Level.FINEST, String.format(mex, params));
	}


	private synchronized void doLog(Level level, String mex, Object... params) {
		if(LogService.getMinLevel().intValue() <= level.intValue()) {
			logger.log(level, String.format(mex, params));
		}
	}

	private synchronized void doLogThrowable(Throwable t, Level level, String mex, Object... params) {
		if(LogService.getMinLevel().intValue() <= level.intValue()) {
			String logMex = StringUtils.isNotBlank(mex) ? String.format(mex, params) : "";
			String tMex = t == null ? "" : (showStackTrace ? toStringStackTrace(t) : String.format("(%s)", t));
			String pattern = showStackTrace ? "%s\n%s" : "%s\t%s";
			String finalMex = String.format(pattern, logMex, tMex);
			logger.log(level, finalMex.trim());
		}
	}

	private synchronized String toStringStackTrace(Throwable t) {
		StringBuilder sb = new StringBuilder();
		Throwable selected = t;
		boolean isCause = false;
		while (selected != null) {
			sb.append(isCause ? "Caused by:  " : "").append(selected).append("\n");
			Arrays.stream(selected.getStackTrace()).forEach(el -> sb.append("\tat ").append(el).append("\n"));
			selected = selected.getCause();
			isCause = true;
		}
		return sb.toString();
	}
	
}