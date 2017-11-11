package com.fede.ct.v2.common.model.types;

import java.util.Arrays;

/**
 * Created by f.barbano on 13/09/2017.
 */
public enum LeverageType {

	BUY("buy"),
	SELL("sell")
	;

	private String label;

	LeverageType(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}

	public static LeverageType getByLabel(String toFind) {
		return Arrays.stream(values())
				   .filter(at -> at.label.equals(toFind.trim()))
				   .findAny()
				   .orElse(null);
	}
}
