package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.ITickersDao;

import java.sql.Connection;
import java.util.Collection;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class TickersDbDao extends AbstractDbDao implements ITickersDao {

	private static final String INSERT_NEW_PREFIX = "INSERT INTO TICKERS (CALL_TIME, PAIR_NAME, ASK_PRICE, ASK_WHOLE_LOT_VOLUME, ASK_LOT_VOLUME, BID_PRICE, " +
												   "BID_WHOLE_LOT_VOLUME, BID_LOT_VOLUME, LAST_CLOSED_PRICE, LAST_CLOSED_LOT_VOLUME, VOLUME_TODAY, VOLUME_LAST_24, " +
												   "VOLUME_WEIGHTED_AVERAGE_TODAY, VOLUME_WEIGHTED_AVERAGE_LAST_24, NUMBER_TRADES_TODAY, NUMBER_TRADES_LAST_24, LOW_TODAY, " +
												   "LOW_LAST_24, HIGH_TODAY, HIGH_LAST_24, OPENING_PRICE, GRAFANA_TIME) VALUES ";

	public TickersDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public void insertTickers(Collection<Ticker> tickers, long callTime) {
		String query = INSERT_NEW_PREFIX + StreamUtil.join(tickers, ",", t -> tickerToValues(callTime, t));
		super.performUpdate(query);
	}

	private String tickerToValues(Long callTime, Ticker ticker) {
		return String.format("(%d, '%s', %s, %d, %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
			callTime,
			ticker.getPairName(),
			toJdbcString(ticker.getAsk().getPrice()),
			ticker.getAsk().getWholeLotVolume(),
			toJdbcString(ticker.getAsk().getLotVolume()),
			toJdbcString(ticker.getBid().getPrice()),
			ticker.getBid().getWholeLotVolume(),
			toJdbcString(ticker.getBid().getLotVolume()),
			toJdbcString(ticker.getLastTradeClosed().getPrice()),
			toJdbcString(ticker.getLastTradeClosed().getLotVolume()),
			toJdbcString(ticker.getVolume().getToday()),
			toJdbcString(ticker.getVolume().getLast24Hours()),
			toJdbcString(ticker.getWeightedAverageVolume().getToday()),
			toJdbcString(ticker.getWeightedAverageVolume().getLast24Hours()),
			toJdbcString(ticker.getTradesNumber().getToday()),
			toJdbcString(ticker.getTradesNumber().getLast24Hours()),
			toJdbcString(ticker.getLow().getToday()),
			toJdbcString(ticker.getLow().getLast24Hours()),
			toJdbcString(ticker.getHigh().getToday()),
			toJdbcString(ticker.getHigh().getLast24Hours()),
			toJdbcString(ticker.getOpeningPrice()),
			OutFormat.toString(callTime, "yyyyMMddHHmmss")
		);
	}
}
