package com.fede.ct.v2.kraken;

import com.fede.ct.v2.common.model._trading.AddOrderIn;
import com.fede.ct.v2.common.model._trading.AddOrderOut;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;

/**
 * Created by f.barbano on 06/11/2017.
 */
public interface IKrakenStrategy {

	AddOrderOut emitOrder(AddOrderIn orderRequest) throws KrakenException, KrakenCallError;


}
