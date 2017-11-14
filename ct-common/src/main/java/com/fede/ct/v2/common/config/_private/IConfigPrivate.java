package com.fede.ct.v2.common.config._private;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IConfigPrivate {

	// Api token
	String getKrakenApiKey();
	String getKrakenApiSecret();

	// Configs
	int getDownloadSecondsFrequencyOrders();
}
