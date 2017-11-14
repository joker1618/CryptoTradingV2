package com.fede.ct.v2.common.config._private;

import com.fede.ct.v2.common.config.AbstractConfig;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class ConfigPrivate extends AbstractConfig implements IConfigPrivate {

	public ConfigPrivate(String configPath) {
		super();
	}

	@Override
	public String getKrakenApiKey() {
		return getString(KeysPrivate.API_KEY);
	}

	@Override
	public String getKrakenApiSecret() {
		return getString(KeysPrivate.API_SECRET);
	}

	@Override
	public int getDownloadSecondsFrequencyOrders() {
		return getInt(KeysPrivate.DOWNLOAD_SECONDS_FREQUENCY_ORDERS, 10);
	}

}
