package com.fede.ct.v2.service;

import com.fede.ct.v2.common.config.Config;
import com.fede.ct.v2.common.config.IConfig;
import com.fede.ct.v2.datalayer.ICryptoModel;
import com.fede.ct.v2.datalayer.impl.CryptoModelFactory;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.impl.KrakenFactory;

/**
 * Created by f.barbano on 04/11/2017.
 */
public class PublicService implements IPublicService {

	private static final IPublicService instance = new PublicService();
	private static final IConfig config = Config.getUniqueInstance();

	private final IKrakenPublic krakenCaller;
	private final ICryptoModel model;

	private PublicService() {
		krakenCaller = KrakenFactory.getPublicCaller();
		model = CryptoModelFactory.getModel();
	}

	public static IPublicService getService() {
		return instance;
	}

	public synchronized void startPublicEngine() {

	}




}
