package com.fede.ct.v2.common.model._private;

import com.fede.ct.v2.common.model.types.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by f.barbano on 03/11/2017.
 */
public class OrderInfo {
	
	private String orderTxID;
	//	refid = Referral order transaction id that created this order
	private String refId;
	//	userref = user reference id
	private String userRef;
	//	status = status of order:
//		pending = order pending book entry
//		open = open order
//		closed = closed order
//		canceled = order canceled
//		expired = order expired
	private OrderStatus status;
	// reason = additional info on status (if any)
	private String reason;
	//	opentm = unix timestamp of when order was placed
	private Long openTimestamp;
	// closetm = unix timestamp of when order was closed
	private Long closeTimestamp;
	//	starttm = unix timestamp of order start time (or 0 if not set)
	private Long startTimestamp;
	//	expiretm = unix timestamp of order end time (or 0 if not set)
	private Long expireTimestamp;
	//	descr = order description info
	private OrderDescr descr;
	//	vol = volume of order (base currency unless viqc set in oflags)
	private BigDecimal volume;
	//	vol_exec = volume executed (base currency unless viqc set in oflags)
	private BigDecimal volumeExecuted;
	//	cost = total cost (quote currency unless unless viqc set in oflags)
	private BigDecimal cost;
	//	fee = total fee (quote currency)
	private BigDecimal fee;
	//	price = average price (quote currency unless viqc set in oflags)
	private BigDecimal averagePrice;
	//	stopprice = stop price (quote currency, for trailing stops)
	private BigDecimal stopPrice;
	//	limitprice = triggered limit price (quote currency, when limit based order type triggered)
	private BigDecimal limitPrice;
	//	misc = comma delimited list of miscellaneous info
//		stopped = triggered by stop price
//		touched = triggered by touch price
//		liquidated = liquidation
//		partial = partial fill
	private List<OrderMisc> misc;
	//	oflags = comma delimited list of order flags
//		viqc = volume in quote currency
//		fcib = prefer fee in base currency (default if selling)
//		fciq = prefer fee in quote currency (default if buying)
//		nompp = no market price protection
	private List<OrderFlag> flags;
	//	trades = array of trade ids related to order (if trades info requested and data available)
	private List<String> trades;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderInfo)) return false;

		OrderInfo orderInfo = (OrderInfo) o;

		return orderTxID != null ? orderTxID.equals(orderInfo.orderTxID) : orderInfo.orderTxID == null;
	}

	@Override
	public int hashCode() {
		return orderTxID != null ? orderTxID.hashCode() : 0;
	}

	public String getOrderTxID() {
		return orderTxID;
	}
	public void setOrderTxID(String orderTxID) {
		this.orderTxID = orderTxID;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getUserRef() {
		return userRef;
	}
	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Long getOpenTimestamp() {
		return openTimestamp;
	}
	public void setOpenTimestamp(Long openTimestamp) {
		this.openTimestamp = openTimestamp;
	}
	public Long getCloseTimestamp() {
		return closeTimestamp;
	}
	public void setCloseTimestamp(Long closeTimestamp) {
		this.closeTimestamp = closeTimestamp;
	}
	public Long getStartTimestamp() {
		return startTimestamp;
	}
	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	public Long getExpireTimestamp() {
		return expireTimestamp;
	}
	public void setExpireTimestamp(Long expireTimestamp) {
		this.expireTimestamp = expireTimestamp;
	}
	public OrderDescr getDescr() {
		return descr;
	}
	public void setDescr(OrderDescr descr) {
		this.descr = descr;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getVolumeExecuted() {
		return volumeExecuted;
	}
	public void setVolumeExecuted(BigDecimal volumeExecuted) {
		this.volumeExecuted = volumeExecuted;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public BigDecimal getAveragePrice() {
		return averagePrice;
	}
	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}
	public BigDecimal getStopPrice() {
		return stopPrice;
	}
	public void setStopPrice(BigDecimal stopPrice) {
		this.stopPrice = stopPrice;
	}
	public BigDecimal getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(BigDecimal limitPrice) {
		this.limitPrice = limitPrice;
	}
	public List<OrderMisc> getMisc() {
		return misc;
	}
	public void setMisc(List<OrderMisc> misc) {
		this.misc = misc;
	}
	public List<OrderFlag> getFlags() {
		return flags;
	}
	public void setFlags(List<OrderFlag> flags) {
		this.flags = flags;
	}
	public List<String> getTrades() {
		return trades;
	}
	public void setTrades(List<String> trades) {
		this.trades = trades;
	}

	@Override
	public String toString() {
		return orderTxID;
	}


	public static class OrderDescr {
		// pair = asset pair
		private String pairName;
		// type = type of order (buy/sell)
		private OrderAction orderAction;
		// ordertype = order type (See Add standard order)
		private OrderType orderType;
		// price = primary price
		private BigDecimal primaryPrice;
		// price2 = secondary price
		private BigDecimal secondaryPrice;
		// leverage = amount of leverage
		private Integer leverage;
		// order = order description
		private String orderDescription;
		// close = conditional close order description (if conditional close set)
		private String closeDescription;

		@Override
		public String toString() {
			return "OrderDescr{" +
					   "pairName='" + pairName + '\'' +
					   ", orderAction=" + orderAction +
					   ", orderType=" + orderType +
					   ", primaryPrice=" + primaryPrice +
					   ", secondaryPrice=" + secondaryPrice +
					   ", leverage=" + leverage +
					   ", orderDescription='" + orderDescription + '\'' +
					   ", closeDescription='" + closeDescription + '\'' +
					   '}';
		}

		public String getPairName() {
			return pairName;
		}
		public void setPairName(String pairName) {
			this.pairName = pairName;
		}
		public OrderAction getOrderAction() {
			return orderAction;
		}
		public void setOrderAction(OrderAction orderAction) {
			this.orderAction = orderAction;
		}
		public OrderType getOrderType() {
			return orderType;
		}
		public void setOrderType(OrderType orderType) {
			this.orderType = orderType;
		}
		public BigDecimal getPrimaryPrice() {
			return primaryPrice;
		}
		public void setPrimaryPrice(BigDecimal primaryPrice) {
			this.primaryPrice = primaryPrice;
		}
		public BigDecimal getSecondaryPrice() {
			return secondaryPrice;
		}
		public void setSecondaryPrice(BigDecimal secondaryPrice) {
			this.secondaryPrice = secondaryPrice;
		}
		public Integer getLeverage() {
			return leverage;
		}
		public void setLeverage(Integer leverage) {
			this.leverage = leverage;
		}
		public String getOrderDescription() {
			return orderDescription;
		}
		public void setOrderDescription(String orderDescription) {
			this.orderDescription = orderDescription;
		}
		public String getCloseDescription() {
			return closeDescription;
		}
		public void setCloseDescription(String closeDescription) {
			this.closeDescription = closeDescription;
		}
	}
}
