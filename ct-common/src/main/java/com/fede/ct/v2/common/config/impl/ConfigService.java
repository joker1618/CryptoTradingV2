package com.fede.ct.v2.common.config.impl;

import com.fede.ct.v2.common.config.IConfigPrivate;
import com.fede.ct.v2.common.config.IConfigPublic;
import com.fede.ct.v2.common.config.ISettings;
import com.fede.ct.v2.common.constants.Const;

/**
 * Created by f.barbano on 14/11/2017.
 */
public class ConfigService {

	private static ISettings settings;
	private static IConfigPublic configPublic;
	private static IConfigPrivate configPrivate;

	public static synchronized ISettings getSettings() {
		if(settings == null) {
			settings = new SettingsImpl(Const.CONFIG_SETTINGS_PATH);
		}
		return settings;
	}

	public static synchronized IConfigPublic getConfigPublic() {
		if(configPublic == null) {
			configPublic = new ConfigPublicImpl(Const.CONFIG_PUBLIC_PATH);
		}
		return configPublic;
	}

	public static synchronized IConfigPrivate getConfigPrivate() {
		if(configPrivate == null) {
			configPrivate = new ConfigPrivateImpl();
		}
		return configPrivate;
	}
}
