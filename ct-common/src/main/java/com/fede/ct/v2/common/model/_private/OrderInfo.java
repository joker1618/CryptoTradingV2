package com.fede.ct.v2.common.model._private;

import com.fede.ct.v2.common.model.types.*;
import model.types.*;

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
	//	starttm = unix timestamp of order start time (or 0 if not set)
	// closetm = unix timestamp of when order was closed
	private Long closeTimestamp;
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

		if (orderTxID != null ? !orderTxID.equals(orderInfo.orderTxID) : orderInfo.orderTxID != null) return false;
		if (refId != null ? !refId.equals(orderInfo.refId) : orderInfo.refId != null) return false;
		if (userRef != null ? !userRef.equals(orderInfo.userRef) : orderInfo.userRef != null) return false;
		if (status != orderInfo.status) return false;
		if (reason != null ? !reason.equals(orderInfo.reason) : orderInfo.reason != null) return false;
		if (openTimestamp != null ? !openTimestamp.equals(orderInfo.openTimestamp) : orderInfo.openTimestamp != null)
			return false;
		if (closeTimestamp != null ? !closeTimestamp.equals(orderInfo.closeTimestamp) : orderInfo.closeTimestamp != null)
			return false;
		if (startTimestamp != null ? !startTimestamp.equals(orderInfo.startTimestamp) : orderInfo.startTimestamp != null)
			return false;
		if (expireTimestamp != null ? !expireTimestamp.equals(orderInfo.expireTimestamp) : orderInfo.expireTimestamp != null)
			return false;
		if (descr != null ? !descr.equals(orderInfo.descr) : orderInfo.descr != null) return false;
		if (volume != null ? !volume.equals(orderInfo.volume) : orderInfo.volume != null) return false;
		if (volumeExecuted != null ? !volumeExecuted.equals(orderInfo.volumeExecuted) : orderInfo.volumeExecuted != null)
			return false;
		if (cost != null ? !cost.equals(orderInfo.cost) : orderInfo.cost != null) return false;
		if (fee != null ? !fee.equals(orderInfo.fee) : orderInfo.fee != null) return false;
		if (averagePrice != null ? !averagePrice.equals(orderInfo.averagePrice) : orderInfo.averagePrice != null)
			return false;
		if (stopPrice != null ? !stopPrice.equals(orderInfo.stopPrice) : orderInfo.stopPrice != null) return false;
		if (limitPrice != null ? !limitPrice.equals(orderInfo.limitPrice) : orderInfo.limitPrice != null) return false;
		if (misc != null ? !misc.equals(orderInfo.misc) : orderInfo.misc != null) return false;
		if (flags != null ? !flags.equals(orderInfo.flags) : orderInfo.flags != null) return false;
		return trades != null ? trades.equals(orderInfo.trades) : orderInfo.trades == null;
	}

	@Override
	public int hashCode() {
		int result = orderTxID != null ? orderTxID.hashCode() : 0;
		result = 31 * result + (refId != null ? refId.hashCode() : 0);
		result = 31 * result + (userRef != null ? userRef.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (reason != null ? reason.hashCode() : 0);
		result = 31 * result + (openTimestamp != null ? openTimestamp.hashCode() : 0);
		result = 31 * result + (closeTimestamp != null ? closeTimestamp.hashCode() : 0);
		result = 31 * result + (startTimestamp != null ? startTimestamp.hashCode() : 0);
		result = 31 * result + (expireTimestamp != null ? expireTimestamp.hashCode() : 0);
		result = 31 * result + (descr != null ? descr.hashCode() : 0);
		result = 31 * result + (volume != null ? volume.hashCode() : 0);
		result = 31 * result + (volumeExecuted != null ? volumeExecuted.hashCode() : 0);
		result = 31 * result + (cost != null ? cost.hashCode() : 0);
		result = 31 * result + (fee != null ? fee.hashCode() : 0);
		result = 31 * result + (averagePrice != null ? averagePrice.hashCode() : 0);
		result = 31 * result + (stopPrice != null ? stopPrice.hashCode() : 0);
		result = 31 * result + (limitPrice != null ? limitPrice.hashCode() : 0);
		result = 31 * result + (misc != null ? misc.hashCode() : 0);
		result = 31 * result + (flags != null ? flags.hashCode() : 0);
		result = 31 * result + (trades != null ? trades.hashCode() : 0);
		return result;
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
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof OrderDescr)) return false;

			OrderDescr that = (OrderDescr) o;

			if (pairName != null ? !pairName.equals(that.pairName) : that.pairName != null) return false;
			if (orderAction != that.orderAction) return false;
			if (orderType != that.orderType) return false;
			if (primaryPrice != null ? !primaryPrice.equals(that.primaryPrice) : that.primaryPrice != null)
				return false;
			if (secondaryPrice != null ? !secondaryPrice.equals(that.secondaryPrice) : that.secondaryPrice != null)
				return false;
			if (leverage != null ? !leverage.equals(that.leverage) : that.leverage != null) return false;
			if (orderDescription != null ? !orderDescription.equals(that.orderDescription) : that.orderDescription != null)
				return false;
			return closeDescription != null ? closeDescription.equals(that.closeDescription) : that.closeDescription == null;
		}

		@Override
		public int hashCode() {
			int result = pairName != null ? pairName.hashCode() : 0;
			result = 31 * result + (orderAction != null ? orderAction.hashCode() : 0);
			result = 31 * result + (orderType != null ? orderType.hashCode() : 0);
			result = 31 * result + (primaryPrice != null ? primaryPrice.hashCode() : 0);
			result = 31 * result + (secondaryPrice != null ? secondaryPrice.hashCode() : 0);
			result = 31 * result + (leverage != null ? leverage.hashCode() : 0);
			result = 31 * result + (orderDescription != null ? orderDescription.hashCode() : 0);
			result = 31 * result + (closeDescription != null ? closeDescription.hashCode() : 0);
			return result;
		}

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
