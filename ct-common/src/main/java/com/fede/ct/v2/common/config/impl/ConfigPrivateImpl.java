package com.fede.ct.v2.common.config.impl;

import com.fede.ct.v2.common.config.AbstractConfig;
import com.fede.ct.v2.common.config.ConfigKeys.PrivateKeys;
import com.fede.ct.v2.common.config.IConfigPrivate;

/**
 * Created by f.barbano on 06/11/2017.
 */
class ConfigPrivateImpl extends AbstractConfig implements IConfigPrivate {

	ConfigPrivateImpl() {
		super();
	}

	@Override
	public void loadConfigFromFile(String configPath) {
		super.loadConfigFile(configPath);
	}

	@Override
	public int getCallRateOrders() {
		return getInt(PrivateKeys.CALL_RATE_ORDERS);
	}

	@Override
	public int getOrdersAutostopOpen() {
		return getInt(PrivateKeys.ORDERS_DOWNLOAD_AUTOSTOP_OPEN);
	}

	@Override
	public int getOrdersAutostopClosed() {
		return getInt(PrivateKeys.ORDERS_DOWNLOAD_AUTOSTOP_CLOSED);
	}
}
