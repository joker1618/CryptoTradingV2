package com.fede.ct.v2.common.config;

/**
 * Created by f.barbano on 11/11/2017.
 */
public class ConfigKeys {

	/* Common keys */
	public static class CommonKeys {
		public static final String DB_URL = "ct.dao.db.URL";
		public static final String DB_USERNAME = "ct.dao.db.username";
		public static final String DB_PASSWORD = "ct.dao.db.password";

		public static final String LOG_FOLDER = "logsFolder";
		public static final String LOGGER_LEVEL = "ct.logger.level";
		public static final String CONSOLE_LEVEL = "ct.logger.console.level";
		public static final String LOG_ERROR_FILENAME = "ct.logger.file.error.name";
		public static final String LOG_ALL_FILENAME = "ct.logger.file.all.name";
	}

	/* Public keys */
	public static class PublicKeys {
		public static final String CALL_RATE_ASSETS = "ct.public.kraken.call.rate.assets";
		public static final String CALL_RATE_ASSET_PAIRS = "ct.public.kraken.call.rate.assetPairs";
		public static final String CALL_RATE_TICKERS = "ct.public.kraken.call.rate.tickers";
	}

	/* Private keys */
	public static class PrivateKeys {
		public static final String CALL_RATE_ORDERS = "ct.private.kraken.call.rate.orders";
		public static final String ORDERS_DOWNLOAD_AUTOSTOP_OPEN = "ct.private.orders.download.autostop.open.after";
		public static final String ORDERS_DOWNLOAD_AUTOSTOP_CLOSED = "ct.private.orders.download.autostop.closed.after";
	}
}
