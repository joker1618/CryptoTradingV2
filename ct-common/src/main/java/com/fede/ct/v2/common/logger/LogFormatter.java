package com.fede.ct.v2.common.logger;

/**
 * Created by f.barbano on 01/10/2017.
 */
import com.fede.ct.v2.common.util.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private static final String SEP = " - ";

	private boolean showClass;
	private boolean showThread;
	private boolean showLevel;

	public LogFormatter(boolean showClass, boolean showThread, boolean showLevel) {
		this.showClass = showClass;
		this.showThread = showThread;
		this.showLevel = showLevel;
	}

	@Override
	public String format(LogRecord record) {
		String methodName = "";
		String lineNumber = "";
		Thread currentThread = Thread.currentThread();

		if(showClass) {
			StackTraceElement[] stackTrace = currentThread.getStackTrace();
			for (int i = 0; i < stackTrace.length && methodName.isEmpty(); i++) {
				StackTraceElement elem = stackTrace[i];
				if (elem.getClassName().startsWith(record.getLoggerName())) {
					methodName = elem.getMethodName();
					lineNumber = "(" + elem.getLineNumber() + ")";
				}
			}
		}

		String message = super.formatMessage(record);

		StringBuilder sb = new StringBuilder();
		LocalDateTime ldt = Converter.millisToLocalDateTime(record.getMillis());
		sb.append(DATE_TIME_FORMATTER.format(ldt)).append(SEP);

		if(showClass) {
			sb.append(record.getLoggerName()).append(lineNumber);
			sb.append("\t").append(SEP);
		}

		if(showThread) {
			sb.append(String.format("<%s-%d>\t%s", currentThread.getName(), currentThread.getId(), SEP));
		}

		if(showLevel) {
			sb.append(String.format("%-7s", record.getLevel())).append(SEP);
		}

		sb.append(message).append("\n");

		return sb.toString();
	}
}