package com.fede.ct.v2.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by f.barbano on 15/11/2017.
 */
public class AltMath {

	public static BigDecimal mult(BigDecimal num, Double d) {
		return num.multiply(BigDecimal.valueOf(d));
	}

	public static BigDecimal min(BigDecimal... nums) {
		BigDecimal min = null;
		for(BigDecimal num : nums) {
			if(min == null || num.compareTo(min) < 0) {
				min = num;
			}
		}
		return min;
	}

	public static BigDecimal max(BigDecimal... nums) {
		BigDecimal max = null;
		for(BigDecimal num : nums) {
			if(max == null || num.compareTo(max) > 0) {
				max = num;
			}
		}
		return max;
	}

	public static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
		return numerator.divide(denominator, 10, RoundingMode.FLOOR);
	}

	public static BigDecimal divide(BigDecimal numerator, Double denominator) {
		return divide(numerator, BigDecimal.valueOf(denominator));
	}
}
