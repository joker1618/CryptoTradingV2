package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._private.OrderInfo;

import java.util.List;

/**
 * Created by f.barbano on 03/11/2017.
 */
public interface IOrdersDao {

	void updateOrders(List<OrderInfo> orders);

	List<OrderInfo> getOrdersStatus(List<String> txIds);

	List<OrderInfo> getOrdersByOpenTm(Long minOpenTm, Long maxOpenTm);

	List<OrderInfo> getOpenOrders();

}
