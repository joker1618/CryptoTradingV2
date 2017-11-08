package com.fede.ct.v2.common.config._public;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by f.barbano on 07/11/2017.
 */
public interface IConfigStrategy {

	List<String> getAssetPairNames();
	
	BigDecimal getBuyAmount();

	double getBuyLowerPerc();
	double getSellHigherPerc();

}
