package com.fede.ct.v2.kraken.exception;

import com.fede.ct.v2.kraken.impl.api.KrakenMethod;

import java.util.List;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class KrakenCallError extends Exception {

	public KrakenCallError(KrakenMethod method, List<String> errorList) {
		super(String.format("Kraken call, method %s: errors found in response json %s", method.getName(), errorList));
	}

}
