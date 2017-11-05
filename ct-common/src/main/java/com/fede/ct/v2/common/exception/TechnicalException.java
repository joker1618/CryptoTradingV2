package com.fede.ct.v2.common.exception;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class TechnicalException extends RuntimeException {

	public TechnicalException(String mexFormat, Object... params) {
		super(String.format(mexFormat, params));
	}
	
	public TechnicalException(Throwable t, String mexFormat, Object... params) {
		super(String.format(mexFormat, params), t);
	}

}
