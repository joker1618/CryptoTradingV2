package com.fede.ct.v2.common.util;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

	public static String toString(long millis, String pattern) {
		return toString(Converter.millisToLocalDateTime(millis), pattern);
	}
	public static String toString(LocalDateTime ldt, String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(ldt);
	}
}
