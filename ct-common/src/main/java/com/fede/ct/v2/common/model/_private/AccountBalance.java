package com.fede.ct.v2.common.model._private;

import java.math.BigDecimal;

/**
 * Created by f.barbano on 14/09/2017.
 */
public class AccountBalance {

	private Long callTime;
	private String assetName;
	private BigDecimal balance;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AccountBalance)) return false;

		AccountBalance that = (AccountBalance) o;

		if (callTime != null ? !callTime.equals(that.callTime) : that.callTime != null) return false;
		if (assetName != null ? !assetName.equals(that.assetName) : that.assetName != null) return false;
		return balance != null ? balance.equals(that.balance) : that.balance == null;
	}

	@Override
	public int hashCode() {
		int result = callTime != null ? callTime.hashCode() : 0;
		result = 31 * result + (assetName != null ? assetName.hashCode() : 0);
		result = 31 * result + (balance != null ? balance.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AccountBalance{" +
				   "callTime=" + callTime +
				   ", assetClass='" + assetName + '\'' +
				   ", balance=" + balance +
				   '}';
	}


	public Long getCallTime() {
		return callTime;
	}
	public void setCallTime(Long callTime) {
		this.callTime = callTime;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
