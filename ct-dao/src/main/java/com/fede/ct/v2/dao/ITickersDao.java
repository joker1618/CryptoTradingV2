package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._public.Ticker;

import java.util.Collection;

/**
 * Created by f.barbano on 10/10/2017.
 */
public interface ITickersDao {

	void insertTickers(Collection<Ticker> tickers, long callTime);

	Ticker selectAskPriceAndAverageLast24(String pairName);


}
