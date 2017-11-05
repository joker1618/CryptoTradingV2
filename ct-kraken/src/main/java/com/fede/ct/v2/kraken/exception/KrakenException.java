package com.fede.ct.v2.kraken.exception;

import com.fede.ct.v2.kraken.impl.api.KrakenMethod;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class KrakenException extends Exception {

	public KrakenException(KrakenMethod method, Throwable t) {
		super(String.format("Unable to call kraken for method %s", method.getName()), t);
	}

}
