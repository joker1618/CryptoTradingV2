package com.fede.ct.v2.common.config.impl;

import com.fede.ct.v2.common.config.IConfigStrategy;
import com.fede.ct.v2.common.config.impl.ConfigKeys.StrategyKeys;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 15/11/2017.
 */
public class ConfigStrategyImpl extends AbstractConfig implements IConfigStrategy {

	@Override
	public String getAssetPairName() {
		return getString(StrategyKeys.ASSET_PAIR);
	}

	@Override
	public BigDecimal getNotional() {
		return getBigDecimal(StrategyKeys.NOTIONAL);
	}

	@Override
	public Double getDeltaPercBuy() {
		return getDouble(StrategyKeys.DELTA_PERC_BUY);
	}

	@Override
	public Double getDeltaPercSell() {
		return getDouble(StrategyKeys.DELTA_PERC_SELL);
	}

	@Override
	public Double getFeesPercBuy() {
		return getDouble(StrategyKeys.FEES_PERC_BUY);
	}

	@Override
	public Double getFeesPercSell() {
		return getDouble(StrategyKeys.FEES_PERC_SELL);
	}
}
