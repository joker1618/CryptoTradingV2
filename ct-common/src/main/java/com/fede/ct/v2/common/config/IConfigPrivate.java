package com.fede.ct.v2.common.config;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IConfigPrivate {

	void loadConfigFromFile(String configPath);

	// Call rates in seconds
	int getCallRateOrders();

	// Autostop orders download configs
	int getOrdersAutostopOpen();
	int getOrdersAutostopClosed();
}
