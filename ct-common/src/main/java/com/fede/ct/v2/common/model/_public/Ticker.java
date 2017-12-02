package com.fede.ct.v2.common.model._public;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 13/09/2017.
 */
public class Ticker {

	private Long callTime;	// not server timestamp, but system timestamp just before call
	private String pairName;
	private TickerWholePrice ask;
	private TickerWholePrice bid;
	private TickerPrice lastTradeClosed;
	private TickerVolume volume;
	private TickerVolume weightedAverageVolume;
	private TickerVolume tradesNumber;
	private TickerVolume low;
	private TickerVolume high;
	private BigDecimal openingPrice;

	@Override
	public String toString() {
		return pairName;
	}


	public Long getCallTime() {
		return callTime;
	}
	public void setCallTime(Long callTime) {
		this.callTime = callTime;
	}
	public String getPairName() {
		return pairName;
	}
	public void setPairName(String pairName) {
		this.pairName = pairName;
	}
	public TickerWholePrice getAsk() {
		return ask;
	}
	public void setAsk(TickerWholePrice ask) {
		this.ask = ask;
	}
	public TickerWholePrice getBid() {
		return bid;
	}
	public void setBid(TickerWholePrice bid) {
		this.bid = bid;
	}
	public TickerPrice getLastTradeClosed() {
		return lastTradeClosed;
	}
	public void setLastTradeClosed(TickerPrice lastTradeClosed) {
		this.lastTradeClosed = lastTradeClosed;
	}
	public TickerVolume getVolume() {
		return volume;
	}
	public void setVolume(TickerVolume volume) {
		this.volume = volume;
	}
	public TickerVolume getWeightedAverageVolume() {
		return weightedAverageVolume;
	}
	public void setWeightedAverageVolume(TickerVolume weightedAverageVolume) {
		this.weightedAverageVolume = weightedAverageVolume;
	}
	public TickerVolume getTradesNumber() {
		return tradesNumber;
	}
	public void setTradesNumber(TickerVolume tradesNumber) {
		this.tradesNumber = tradesNumber;
	}
	public TickerVolume getLow() {
		return low;
	}
	public void setLow(TickerVolume low) {
		this.low = low;
	}
	public TickerVolume getHigh() {
		return high;
	}
	public void setHigh(TickerVolume high) {
		this.high = high;
	}
	public BigDecimal getOpeningPrice() {
		return openingPrice;
	}
	public void setOpeningPrice(BigDecimal openingPrice) {
		this.openingPrice = openingPrice;
	}

	public static class TickerPrice {
		private BigDecimal price;
		private BigDecimal lotVolume;

		public BigDecimal getPrice() {
			return price;
		} 
		public void setPrice(BigDecimal price) {
			this.price = price;
		} 
		public BigDecimal getLotVolume() {
			return lotVolume;
		} 
		public void setLotVolume(BigDecimal lotVolume) {
			this.lotVolume = lotVolume;
		}
	}

	public static class TickerWholePrice extends TickerPrice {
		private Integer wholeLotVolume;

		public TickerWholePrice() {
		}
		public TickerWholePrice(TickerPrice tickerPrice) {
			super.setPrice(tickerPrice.getPrice());
			super.setLotVolume(tickerPrice.getLotVolume());
		}

		public Integer getWholeLotVolume() {
			return wholeLotVolume;
		}
		public void setWholeLotVolume(Integer wholeLotVolume) {
			this.wholeLotVolume = wholeLotVolume;
		}
	}

	public static class TickerVolume {
		private BigDecimal today;
		private BigDecimal last24Hours;

		public BigDecimal getToday() {
			return today;
		} 
		public void setToday(BigDecimal today) {
			this.today = today;
		} 
		public BigDecimal getLast24Hours() {
			return last24Hours;
		} 
		public void setLast24Hours(BigDecimal last24Hours) {
			this.last24Hours = last24Hours;
		}
	}

}
