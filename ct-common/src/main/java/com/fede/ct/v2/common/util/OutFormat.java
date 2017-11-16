package com.fede.ct.v2.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.*;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class OutFormat {

	public static NumberFormat getEnglishFormat() {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setMinimumFractionDigits(1);
		nf.setMaximumFractionDigits(12);
		nf.setGroupingUsed(false);
		return nf;
	}

	public static String toStringLDT(long millis, String pattern) {
		return toStringLDT(Converter.millisToLocalDateTime(millis), pattern);
	}
	public static String toStringLDT(LocalDateTime ldt, String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(ldt);
	}

	public static String toStringNum(BigDecimal num) {
		return getEnglishFormat().format(num);
	}


	public static String toStringElapsed(long start, long end, boolean showMilli) {
		return toStringElapsed(end-start, showMilli);
	}
	public static String toStringElapsed(long elapsed, boolean showMilli) {
		WTime td = new WTime(elapsed);

		String strMilli = showMilli ? String.format(".%03d", td.getMilli()) : "";

		if(td.getHour() > 0) {
			return String.format("%02d:%02d:%02d%s", td.getHour(), td.getMinute(), td.getSecond(), strMilli);
		} else if(td.getMinute() > 0) {
			return String.format("%02d:%02d%s", td.getMinute(), td.getSecond(), strMilli);
		} else {
			return String.format("%d%s", td.getSecond(), strMilli);
		}
	}


	private static class WTime {

		private long totalMillis;

		private long day;
		private long hour;
		private long minute;
		private long second;
		private long milli;


		public WTime() {
			this(System.currentTimeMillis());
		}

		public WTime(long totalMillis) {
			this.totalMillis = totalMillis;
			this.milli = totalMillis % 1000;

			long rem = (totalMillis - this.milli) / 1000;

			long daySec = DAYS.getDuration().getSeconds();
			this.day = rem / daySec;
			rem -= daySec * this.day;

			long hourSec = HOURS.getDuration().getSeconds();
			this.hour = rem / hourSec;
			rem -= hourSec * this.hour;

			long minuteSec = MINUTES.getDuration().getSeconds();
			this.minute = rem / minuteSec;
			rem -= minuteSec * this.minute;

			this.second =  rem;
		}

		/* GETTERS and SETTERS */
		public long getTotalMillis() {
			return totalMillis;
		}
		public long getDay() {
			return day;
		}
		public long getHour() {
			return hour;
		}
		public long getMinute() {
			return minute;
		}
		public long getSecond() {
			return second;
		}
		public long getMilli() {
			return milli;
		}
		public long getTotalHour() {
			return hour + 24 * day;
		}
		public long getTotalMinute() {
			return minute + 60 * getTotalHour();
		}
		public long getTotalSecond() {
			return second + 60 * getTotalMinute();
		}

	}
}
