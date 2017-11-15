package com.fede.ct.v2.datalayer;

import com.fede.ct.v2.common.model._private.OrderInfo;

import java.util.List;

/**
 * Created by f.barbano on 14/11/2017.
 */
public interface IModelPrivate {

	boolean isDownloadOrdersEnabled();
	void setDownloadOrdersEnabled(boolean enabled);

	void updateOrders(List<OrderInfo> orders);


}
