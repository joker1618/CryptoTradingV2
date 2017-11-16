package com.fede.ct.v2.common.config.impl;

import com.fede.ct.v2.common.config.IConfigTrading;
import com.fede.ct.v2.common.config.impl.ConfigKeys.TradingKeys;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 15/11/2017.
 */
public class ConfigTradingImpl extends AbstractConfig implements IConfigTrading {

	@Override
	public void loadConfigFromFile(String configPath) {
		super.loadConfigFile(configPath);
	}

	@Override
	public String getAssetPairName() {
		String name = getString(TradingKeys.ASSET_PAIR);
		return name == null ? null : name.toUpperCase();
	}

	@Override
	public BigDecimal getNotional() {
		return getBigDecimal(TradingKeys.NOTIONAL);
	}

	@Override
	public Double getDeltaPercBuy() {
		return getDouble(TradingKeys.DELTA_PERC_BUY);
	}

	@Override
	public Double getDeltaPercSell() {
		return getDouble(TradingKeys.DELTA_PERC_SELL);
	}

	@Override
	public Double getFeesPercBuy() {
		return getDouble(TradingKeys.FEES_PERC_BUY);
	}

	@Override
	public Double getFeesPercSell() {
		return getDouble(TradingKeys.FEES_PERC_SELL);
	}
}
