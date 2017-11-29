package com.fede.ct.v2.common.config;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 07/11/2017.
 */
public interface IConfigThresold {

	void loadConfigFromFile(String configPath);

	String getAssetPairName();
	
	BigDecimal getNotional();

	Double getDeltaPercBuy();
	Double getDeltaPercSell();

	Double getFeesPercBuy();
	Double getFeesPercSell();

	int getDataValidSeconds();

	int getNumberOfTryToRetrieveOrders();

}
