package com.fede.ct.v2.common.config.impl;

import com.fede.ct.v2.common.config.IConfigThresold;
import com.fede.ct.v2.common.config.impl.ConfigKeys.SimpleThresoldKeys;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 15/11/2017.
 */
public class ConfigThresoldImpl extends AbstractConfig implements IConfigThresold {

	@Override
	public void loadConfigFromFile(String configPath) {
		super.loadConfigFile(configPath);
	}

	@Override
	public String getAssetPairName() {
		String name = getString(SimpleThresoldKeys.ASSET_PAIR);
		return name == null ? null : name.toUpperCase();
	}

	@Override
	public BigDecimal getNotional() {
		return getBigDecimal(SimpleThresoldKeys.NOTIONAL);
	}

	@Override
	public Double getDeltaPercBuy() {
		return getDouble(SimpleThresoldKeys.DELTA_PERC_BUY);
	}

	@Override
	public Double getDeltaPercSell() {
		return getDouble(SimpleThresoldKeys.DELTA_PERC_SELL);
	}

	@Override
	public Double getFeesPercBuy() {
		return getDouble(SimpleThresoldKeys.FEES_PERC_BUY);
	}

	@Override
	public Double getFeesPercSell() {
		return getDouble(SimpleThresoldKeys.FEES_PERC_SELL);
	}

	@Override
	public int getDataValidSeconds() {
		return getInt(SimpleThresoldKeys.DATA_VALID_SECONDS);
	}

	@Override
	public int getNumberOfTryToRetrieveOrders() {
		return getInt(SimpleThresoldKeys.RETRIEVE_TX_NUMBER_TRY);
	}
}
