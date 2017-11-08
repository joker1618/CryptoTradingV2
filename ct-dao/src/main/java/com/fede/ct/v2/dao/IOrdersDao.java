package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model.types.OrderStatus;

import java.util.List;

/**
 * Created by f.barbano on 03/11/2017.
 */
public interface IOrdersDao {

	void updateOrders(int userId, List<OrderInfo> orders);

	List<String> getOpenOrders(int userId);
	List<OrderInfo> getOrdersStatus(int userId, List<String> orderTxId);

}
