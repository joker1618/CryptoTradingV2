package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._public.Ticker.TickerPrice;
import com.fede.ct.v2.common.model._public.Ticker.TickerVolume;
import com.fede.ct.v2.common.model._public.Ticker.TickerWholePrice;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.dao.ITickersDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class TickersDbDao extends AbstractDbDao implements ITickersDao {

	private static final String UPDATE_VALIDS = "UPDATE TICKERS SET VALID = 0 WHERE VALID = 1";
	private static final String INSERT_NEW_PREFIX = "INSERT INTO TICKERS (CALL_TIME, PAIR_NAME, ASK_PRICE, ASK_WHOLE_LOT_VOLUME, ASK_LOT_VOLUME, BID_PRICE, " +
												   "BID_WHOLE_LOT_VOLUME, BID_LOT_VOLUME, LAST_CLOSED_PRICE, LAST_CLOSED_LOT_VOLUME, VOLUME_TODAY, VOLUME_LAST_24, " +
												   "VOLUME_WEIGHTED_AVERAGE_TODAY, VOLUME_WEIGHTED_AVERAGE_LAST_24, NUMBER_TRADES_TODAY, NUMBER_TRADES_LAST_24, LOW_TODAY, " +
												   "LOW_LAST_24, HIGH_TODAY, HIGH_LAST_24, OPENING_PRICE, GRAFANA_TIME, VALID) VALUES ";

//	private static final String SELECT_ASK_PRICE_AVERAGE_LAST24 = "SELECT PAIR_NAME, ASK_PRICE, VOLUME_WEIGHTED_AVERAGE_LAST_24 FROM TICKERS WHERE PAIR_NAME = ? AND CALL_TIME = (SELECT MAX(CALL_TIME) FROM TICKERS)";

	public TickersDbDao(CryptoContext ctx) {
		super(ctx);
	}

	@Override
	public void insertTickers(List<Ticker> tickers, long callTime) {
		Query updateQuery = new Query(UPDATE_VALIDS);
		List<Query> queries = createJdbcQueries(INSERT_NEW_PREFIX, tickers.size(), 23, tickers, getTickerFunctions(callTime));
		queries.add(0, updateQuery);
		super.performTransaction(queries);
	}
//
//	@Override
//	public Ticker selectAskPriceAndAverageLast24(String pairName) {
//		Query query = new Query(SELECT_ASK_PRICE_AVERAGE_LAST24, pairName);
//		List<InquiryResult> results = super.performInquiry(query);
//		return results.isEmpty() ? null : parseTicker(results.get(0));
//	}

	private List<Function<Ticker, Object>> getTickerFunctions(Long callTime) {
		List<Function<Ticker, Object>> functions = new ArrayList<>();
		functions.add(t -> callTime);
		functions.add(t -> t.getPairName());
		functions.add(t -> t.getAsk().getPrice());
		functions.add(t -> t.getAsk().getWholeLotVolume());
		functions.add(t -> t.getAsk().getLotVolume());
		functions.add(t -> t.getBid().getPrice());
		functions.add(t -> t.getBid().getWholeLotVolume());
		functions.add(t -> t.getBid().getLotVolume());
		functions.add(t -> t.getLastTradeClosed().getPrice());
		functions.add(t -> t.getLastTradeClosed().getLotVolume());
		functions.add(t -> t.getVolume().getToday());
		functions.add(t -> t.getVolume().getLast24Hours());
		functions.add(t -> t.getWeightedAverageVolume().getToday());
		functions.add(t -> t.getWeightedAverageVolume().getLast24Hours());
		functions.add(t -> t.getTradesNumber().getToday());
		functions.add(t -> t.getTradesNumber().getLast24Hours());
		functions.add(t -> t.getLow().getToday());
		functions.add(t -> t.getLow().getLast24Hours());
		functions.add(t -> t.getHigh().getToday());
		functions.add(t -> t.getHigh().getLast24Hours());
		functions.add(t -> t.getOpeningPrice());
		functions.add(t -> OutFormat.toStringLDT(callTime, "yyyyMMddHHmmss"));
		functions.add(t -> 1);
		return functions;
	}

	private Ticker parseTicker(InquiryResult res) {
		Ticker t = new Ticker();
		t.setCallTime(res.getLong("CALL_TIME"));
		t.setPairName(res.getString("PAIR_NAME"));
		t.setOpeningPrice(res.getBigDecimal("OPENING_PRICE"));

		TickerWholePrice ask = new TickerWholePrice();
		ask.setPrice(res.getBigDecimal("ASK_PRICE"));
		ask.setWholeLotVolume(res.getInteger("ASK_WHOLE_LOT_VOLUME"));
		ask.setLotVolume(res.getBigDecimal("ASK_LOT_VOLUME"));
		t.setAsk(ask);

		TickerWholePrice bid = new TickerWholePrice();
		bid.setPrice(res.getBigDecimal("BID_PRICE"));
		bid.setWholeLotVolume(res.getInteger("BID_WHOLE_LOT_VOLUME"));
		bid.setLotVolume(res.getBigDecimal("BID_LOT_VOLUME"));
		t.setBid(bid);

		TickerPrice last = new TickerPrice();
		last.setPrice(res.getBigDecimal("LAST_CLOSED_PRICE"));
		last.setLotVolume(res.getBigDecimal("LAST_CLOSED_LOT_VOLUME"));
		t.setLastTradeClosed(last);

		TickerVolume volume = new TickerVolume();
		volume.setToday(res.getBigDecimal("VOLUME_TODAY"));
		volume.setLast24Hours(res.getBigDecimal("VOLUME_LAST_24"));
		t.setVolume(volume);

		TickerVolume volWeighted = new TickerVolume();
		volWeighted.setToday(res.getBigDecimal("VOLUME_WEIGHTED_AVERAGE_TODAY"));
		volWeighted.setLast24Hours(res.getBigDecimal("VOLUME_WEIGHTED_AVERAGE_LAST_24"));
		t.setWeightedAverageVolume(volWeighted);

		TickerVolume tradeNum = new TickerVolume();
		tradeNum.setToday(res.getBigDecimal("NUMBER_TRADES_TODAY"));
		tradeNum.setLast24Hours(res.getBigDecimal("NUMBER_TRADES_LAST_24"));
		t.setTradesNumber(tradeNum);

		TickerVolume low = new TickerVolume();
		low.setToday(res.getBigDecimal("LOW_TODAY"));
		low.setLast24Hours(res.getBigDecimal("LOW_LAST_24"));
		t.setLow(low);

		TickerVolume high = new TickerVolume();
		high.setToday(res.getBigDecimal("HIGH_TODAY"));
		high.setLast24Hours(res.getBigDecimal("HIGH_LAST_24"));
		t.setHigh(high);

		return t;
	}

}
