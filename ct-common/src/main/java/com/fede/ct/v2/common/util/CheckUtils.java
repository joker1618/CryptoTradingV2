package com.fede.ct.v2.common.util;

/**
 * Created by f.barbano on 15/11/2017.
 */
public class CheckUtils {

	public static boolean isInteger(String str) {
		try {
			new Integer(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

}
