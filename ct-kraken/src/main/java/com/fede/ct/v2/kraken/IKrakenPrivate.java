package com.fede.ct.v2.kraken;

import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;

import java.util.List;

/**
 * Created by f.barbano on 06/11/2017.
 */
public interface IKrakenPrivate {

	List<OrderInfo> getOpenOrders() throws KrakenException, KrakenCallError;

	List<OrderInfo> getClosedOrders() throws KrakenException, KrakenCallError;

	List<AccountBalance> getAccountBalances() throws KrakenException, KrakenCallError;
	
}
