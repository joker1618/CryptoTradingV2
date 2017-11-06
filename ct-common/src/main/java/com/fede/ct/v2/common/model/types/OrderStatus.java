package com.fede.ct.v2.common.model.types;

import java.util.Arrays;

/**
 * Created by f.barbano on 14/09/2017.
 */
public enum OrderStatus {

	PENDING("pending"),
	OPEN("open"),
	CLOSED("closed"),
	CANCELED("canceled"),
	EXPIRED("expired")
	;

	private String label;

	OrderStatus(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}

	public static OrderStatus getByLabel(String toFind) {
		return Arrays.stream(values())
				   .filter(at -> at.label.equals(toFind.trim()))
				   .findAny()
				   .orElse(null);
	}
}
