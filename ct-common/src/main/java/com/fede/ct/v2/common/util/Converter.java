package com.fede.ct.v2.common.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by f.barbano on 04/11/2017.
 */
public class Converter {

	public static LocalDateTime millisToLocalDateTime(long millis) {
		return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static long localDateTimeToMillis(LocalDateTime ldt) {
		return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static BigDecimal stringToBigDecimal(String str) {
		double num = Double.parseDouble(str.trim());
		return BigDecimal.valueOf(num);
	}

	public static Integer stringToInteger(String str) {
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException ex) {
			return null;
		}
	}
}
