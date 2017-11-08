package com.fede.ct.v2.common.model._trading;

import java.util.List;

/**
 * Created by f.barbano on 28/09/2017.
 */
public class AddOrderOut {

	private List<String> txIDs;
	private String orderDescr;
	private String closeDescr;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AddOrderOut)) return false;

		AddOrderOut that = (AddOrderOut) o;

		if (txIDs != null ? !txIDs.equals(that.txIDs) : that.txIDs != null) return false;
		if (orderDescr != null ? !orderDescr.equals(that.orderDescr) : that.orderDescr != null) return false;
		return closeDescr != null ? closeDescr.equals(that.closeDescr) : that.closeDescr == null;
	}

	@Override
	public int hashCode() {
		int result = txIDs != null ? txIDs.hashCode() : 0;
		result = 31 * result + (orderDescr != null ? orderDescr.hashCode() : 0);
		result = 31 * result + (closeDescr != null ? closeDescr.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AddOrderOut{" +
				   "txIDs=" + txIDs +
				   ", orderDescr='" + orderDescr + '\'' +
				   ", closeDescr='" + closeDescr + '\'' +
				   '}';
	}


	public List<String> getTxIDs() {
		return txIDs;
	}
	public void setTxIDs(List<String> txIDs) {
		this.txIDs = txIDs;
	}
	public String getOrderDescr() {
		return orderDescr;
	}
	public void setOrderDescr(String orderDescr) {
		this.orderDescr = orderDescr;
	}
	public String getCloseDescr() {
		return closeDescr;
	}
	public void setCloseDescr(String closeDescr) {
		this.closeDescr = closeDescr;
	}
}
