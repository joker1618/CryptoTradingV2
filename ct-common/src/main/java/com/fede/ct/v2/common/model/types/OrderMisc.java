package com.fede.ct.v2.common.model.types;

import java.util.Arrays;

/**
 * Created by f.barbano on 24/09/2017.
 */
public enum OrderMisc {
	//	misc = comma delimited list of miscellaneous info
	//		stopped = triggered by stop price
	//		touched = triggered by touch price
	//		liquidated = liquidation
	//		partial = partial fill

	STOPPED("stopped"),
	TOUCHED("touched"),
	LIQUIDATED("liquidated"),
	PARTIAL("partial"),
	;

	private String label;

	OrderMisc(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}

	public static OrderMisc getByLabel(String label) {
		return Arrays.stream(values())
				   .filter(at -> at.label.equals(label))
				   .findAny()
				   .orElse(null);
	}
}
