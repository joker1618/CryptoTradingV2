package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._public.Ticker.TickerPrice;
import com.fede.ct.v2.common.model._public.Ticker.TickerVolume;
import com.fede.ct.v2.common.model._public.Ticker.TickerWholePrice;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.ITickersDao;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class TickersDbDao extends AbstractDbDao implements ITickersDao {

	private static final String INSERT_NEW_PREFIX = "INSERT INTO TICKERS (CALL_TIME, PAIR_NAME, ASK_PRICE, ASK_WHOLE_LOT_VOLUME, ASK_LOT_VOLUME, BID_PRICE, " +
												   "BID_WHOLE_LOT_VOLUME, BID_LOT_VOLUME, LAST_CLOSED_PRICE, LAST_CLOSED_LOT_VOLUME, VOLUME_TODAY, VOLUME_LAST_24, " +
												   "VOLUME_WEIGHTED_AVERAGE_TODAY, VOLUME_WEIGHTED_AVERAGE_LAST_24, NUMBER_TRADES_TODAY, NUMBER_TRADES_LAST_24, LOW_TODAY, " +
												   "LOW_LAST_24, HIGH_TODAY, HIGH_LAST_24, OPENING_PRICE, GRAFANA_TIME) VALUES ";

	private static final String SELECT_ASK_PRICE_AVERAGE_LAST24 = "SELECT PAIR_NAME, ASK_PRICE, VOLUME_WEIGHTED_AVERAGE_LAST_24 FROM TICKERS WHERE PAIR_NAME = ? AND CALL_TIME = (SELECT MAX(CALL_TIME) FROM TICKERS)";

	public TickersDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public void insertTickers(List<Ticker> tickers, long callTime) {
		String query = INSERT_NEW_PREFIX + StreamUtil.join(tickers, ",", t -> tickerToValues(callTime, t));
		super.performUpdate(query);
	}

	@Override
	public Ticker selectAskPriceAndAverageLast24(String pairName) {
		String query = SELECT_ASK_PRICE_AVERAGE_LAST24;
		try(PreparedStatement ps = createPreparedStatement(query, pairName);
			ResultSet rs = ps.executeQuery()) {

			Ticker toRet = null;
			if(rs != null && rs.next()) {
				toRet = parseTicker(rs, query);
			}
			return toRet;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s, pairName=%s]", query, pairName);
		}
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
			OutFormat.toStringLDT(callTime, "yyyyMMddHHmmss")
		);
	}

	private Ticker parseTicker(ResultSet rs, String querySelect) {
		String pairName = null;

		try {
			String strFields = StringUtils.substringBetween(querySelect.toUpperCase(), "SELECT", "FROM");

			Ticker t = new Ticker();
			if(strFields.contains("CALL_TIME"))		t.setCallTime(rs.getLong("CALL_TIME"));
			if(strFields.contains("PAIR_NAME"))		pairName = rs.getString("PAIR_NAME");
			if(strFields.contains("OPENING_PRICE"))		t.setOpeningPrice(rs.getBigDecimal("OPENING_PRICE"));
			t.setPairName(pairName);

			TickerWholePrice ask = new TickerWholePrice();
			if(strFields.contains("ASK_PRICE"))		ask.setPrice(rs.getBigDecimal("ASK_PRICE"));
			if(strFields.contains("ASK_WHOLE_LOT_VOLUME"))		ask.setWholeLotVolume(rs.getInt("ASK_WHOLE_LOT_VOLUME"));
			if(strFields.contains("ASK_LOT_VOLUME"))		ask.setLotVolume(rs.getBigDecimal("ASK_LOT_VOLUME"));
			t.setAsk(ask);

			TickerWholePrice bid = new TickerWholePrice();
			if(strFields.contains("BID_PRICE"))		bid.setPrice(rs.getBigDecimal("BID_PRICE"));
			if(strFields.contains("BID_WHOLE_LOT_VOLUME"))		bid.setWholeLotVolume(rs.getInt("BID_WHOLE_LOT_VOLUME"));
			if(strFields.contains("BID_LOT_VOLUME"))		bid.setLotVolume(rs.getBigDecimal("BID_LOT_VOLUME"));
			t.setBid(bid);

			TickerPrice last = new TickerPrice();
			if(strFields.contains("LAST_CLOSED_PRICE"))		last.setPrice(rs.getBigDecimal("LAST_CLOSED_PRICE"));
			if(strFields.contains("LAST_CLOSED_LOT_VOLUME"))		last.setLotVolume(rs.getBigDecimal("LAST_CLOSED_LOT_VOLUME"));
			t.setLastTradeClosed(last);

			TickerVolume volume = new TickerVolume();
			if(strFields.contains("VOLUME_TODAY"))		volume.setToday(rs.getBigDecimal("VOLUME_TODAY"));
			if(strFields.contains("VOLUME_LAST_24"))		volume.setLast24Hours(rs.getBigDecimal("VOLUME_LAST_24"));
			t.setVolume(volume);

			TickerVolume volWeighted = new TickerVolume();
			if(strFields.contains("VOLUME_WEIGHTED_AVERAGE_TODAY"))		volWeighted.setToday(rs.getBigDecimal("VOLUME_WEIGHTED_AVERAGE_TODAY"));
			if(strFields.contains("VOLUME_WEIGHTED_AVERAGE_LAST_24"))		volWeighted.setLast24Hours(rs.getBigDecimal("VOLUME_WEIGHTED_AVERAGE_LAST_24"));
			t.setWeightedAverageVolume(volWeighted);

			TickerVolume tradeNum = new TickerVolume();
			if(strFields.contains("NUMBER_TRADES_TODAY"))		tradeNum.setToday(rs.getBigDecimal("NUMBER_TRADES_TODAY"));
			if(strFields.contains("NUMBER_TRADES_LAST_24"))		tradeNum.setLast24Hours(rs.getBigDecimal("NUMBER_TRADES_LAST_24"));
			t.setTradesNumber(tradeNum);

			TickerVolume low = new TickerVolume();
			if(strFields.contains("LOW_TODAY"))		low.setToday(rs.getBigDecimal("LOW_TODAY"));
			if(strFields.contains("LOW_LAST_24"))		low.setLast24Hours(rs.getBigDecimal("LOW_LAST_24"));
			t.setLow(low);

			TickerVolume high = new TickerVolume();
			if(strFields.contains("HIGH_TODAY"))		high.setToday(rs.getBigDecimal("HIGH_TODAY"));
			if(strFields.contains("HIGH_LAST_24"))		high.setLast24Hours(rs.getBigDecimal("HIGH_LAST_24"));
			t.setHigh(high);

			return t;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Unable to parse ticker");
		}
	}

}
