package com.fede.ct.v2.common.model._trading;

import com.fede.ct.v2.common.model.types.OrderAction;
import com.fede.ct.v2.common.model.types.OrderFlag;
import com.fede.ct.v2.common.model.types.OrderType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by f.barbano on 07/11/2017.
 */
public class AddOrderIn {

	// pair = asset pair
	private String pairName;
	// type = type of order (buy/sell)
	private OrderAction orderAction;
	// ordertype = order type:
	private OrderType orderType;
	// price = price (optional. dependent upon ordertype)
	private BigDecimal price;
	// price2 = secondary price (optional. dependent upon ordertype)
	private BigDecimal price2;
	// volume = order volume in lots
	private Double volume;
	// leverage = amount of leverage desired (optional. default = none)
	private Integer leverage;
	// oflags = comma delimited list of order flags (optional):
	private List<OrderFlag> oflags;
	// starttm = scheduled start time (optional):
	// 	0 = now (default)
	// 	+<n> = schedule start time <n> seconds from now
	// 	<n> = unix timestamp of start time
	private String starttm;
	// expiretm = expiration time (optional):
	// 	0 = no expiration (default)
	// 	+<n> = expire <n> seconds from now
	// 	<n> = unix timestamp of expiration time
	private String expiretm;
	// userref = user reference id. 32-bit signed number. (optional)
	private String userRef;
	// validate = validate inputs only. do not submit order (optional)
	private boolean validate;
	// REVIEW follow field not managed
	// optional closing order to add to system when order gets filled:
	// 	close[ordertype] = order type
	// 	close[price] = price
	// 	close[price2] = secondary price


	AddOrderIn(String pairName, OrderAction orderAction, OrderType orderType, Double volume) {
		this.pairName = pairName;
		this.orderAction = orderAction;
		this.orderType = orderType;
		this.volume = volume;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getPrice2() {
		return price2;
	}
	public void setPrice2(BigDecimal price2) {
		this.price2 = price2;
	}
	public Double getVolume() {
		return volume;
	}
	public void Double(Double volume) {
		this.volume = volume;
	}
	public Integer getLeverage() {
		return leverage;
	}
	public void setLeverage(Integer leverage) {
		this.leverage = leverage;
	}
	public List<OrderFlag> getOflags() {
		return oflags;
	}
	public void setOflags(List<OrderFlag> oflags) {
		this.oflags = oflags;
	}
	public String getStarttm() {
		return starttm;
	}
	public void setStarttm(String starttm) {
		this.starttm = starttm;
	}
	public String getExpiretm() {
		return expiretm;
	}
	public void setExpiretm(String expiretm) {
		this.expiretm = expiretm;
	}
	public String getUserRef() {
		return userRef;
	}
	public void setUserRef(String userRef) {
		this.userRef = userRef;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
}
