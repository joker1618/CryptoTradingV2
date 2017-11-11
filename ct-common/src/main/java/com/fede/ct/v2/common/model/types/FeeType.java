package com.fede.ct.v2.common.model.types;

import java.util.Arrays;

/**
 * Created by f.barbano on 10/11/2017.
 */
public enum FeeType {

	FEES("fees"),
	FEES_MAKER("fees_maker")
	;

	private String label;

	FeeType(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}

	public static FeeType getByLabel(String toFind) {
		return Arrays.stream(values())
				   .filter(at -> at.label.equals(toFind.trim()))
				   .findAny()
				   .orElse(null);
	}
}
