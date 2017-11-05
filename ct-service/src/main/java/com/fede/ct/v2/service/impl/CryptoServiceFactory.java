package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.service.ICryptoService;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class CryptoServiceFactory {

	private static PublicService publicService;

	public static synchronized ICryptoService getPublicService() {
		if(publicService == null) {
			publicService = new PublicService();
		}
		return publicService;
	}
}
