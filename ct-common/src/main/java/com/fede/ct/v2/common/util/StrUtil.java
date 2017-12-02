package com.fede.ct.v2.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class StrUtil {

	public static String[] splitAllFields(String source, String separatorString) {
		return splitAllFields(source, separatorString, false);
	}
	public static String[] splitAllFields(String source, String separatorString, boolean trimValues) {
		return splitAllFields(source, separatorString, false, true);
	}
	public static String[] splitAllFields(String source, String separatorString, boolean trimValues, boolean removeSeparator) {
		if(source == null || source.isEmpty()) {
			return new String[0];
		}

		String[] splitted = source.split(Pattern.quote(separatorString));
		int numFields = StringUtils.countMatches(source, separatorString) + 1;

		String[] toRet = new String[numFields];

		int pos = 0;
		for(; pos < splitted.length; pos++) {
			String str = removeSeparator ? splitted[pos] : splitted[pos] + separatorString;
			toRet[pos] = trimValues ? str.trim() : str;
		}
		for(; pos < numFields; pos++) {
			toRet[pos] = "";
		}

		return toRet;
	}

	public static List<String> splitFieldsList(String source, String separatorString) {
		return splitFieldsList(source, separatorString, false);
	}
	public static List<String> splitFieldsList(String source, String separatorString, boolean trimValues) {
		return splitFieldsList(source, separatorString, trimValues, true);
	}
	public static List<String> splitFieldsList(String source, String separatorString, boolean trimValues, boolean removeSeparator) {
		return Arrays.asList(splitAllFields(source, separatorString, trimValues, removeSeparator));
	}

}
