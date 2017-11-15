package com.fede.ct.v2.common.config;

import com.fede.ct.v2.common.model._public.AssetPair;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by f.barbano on 07/11/2017.
 */
public interface IConfigStrategy {

	String getAssetPairName();
	
	BigDecimal getNotional();

	Double getDeltaPercBuy();
	Double getDeltaPercSell();

	Double getFeesPercBuy();
	Double getFeesPercSell();

}
