package com.fede.ct.v2.common.config._public;

import com.fede.ct.v2.common.config.AbstractConfig;

import java.math.BigDecimal;
import java.util.List;



/**
 * Created by f.barbano on 07/11/2017.
 */
public class ConfigStrategy extends AbstractConfig implements IConfigStrategy {

	public ConfigStrategy(String configPath) {
		super(configPath);
	}

	@Override
	public List<String> getAssetPairNames() {
		return getCommaSeparatedList(KeysStrategy.ASSET_PAIR_NAMES);
	}

	@Override
	public BigDecimal getBuyAmount() {
		return getBigDecimal(KeysStrategy.BUY_AMOUNT);
	}

	@Override
	public double getBuyLowerPerc() {
		return getDouble(KeysStrategy.BUY_LOWER_PERC);
	}

	@Override
	public double getSellHigherPerc() {
		return getDouble(KeysStrategy.SELL_HIGHER_PERC);
	}
}
