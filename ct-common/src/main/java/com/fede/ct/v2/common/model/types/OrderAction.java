package com.fede.ct.v2.common.model.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 13/09/2017.
 */
public enum OrderAction {

	BUY("buy", "b"),
	SELL("sell", "s")
	;

	private List<String> labels;

	OrderAction(String label, String shortLabel) {
		this.labels = new ArrayList<>();
		labels.add(label);
		labels.add(shortLabel);
	}

	public String label() {
		return labels.get(0);
	}
	public String shortLabel() {
		return labels.get(1);
	}

	public static OrderAction getByLabel(String toFind) {
		return Arrays.stream(values())
				   .filter(at -> at.labels.contains(toFind.trim()))
				   .findAny()
				   .orElse(null);
	}
}
