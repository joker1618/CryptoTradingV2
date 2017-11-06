package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model.types.OrderFlag;
import com.fede.ct.v2.common.model.types.OrderMisc;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IOrdersDao;

import java.sql.Connection;
import java.util.List;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class OrdersDbDao extends AbstractDbDao implements IOrdersDao {

	private static final String REPLACE_ORDERS_PREFIX = "REPLACE INTO ORDERS (ORDER_TX_ID,USER_ID,REF_ID,USER_REF,STATUS,REASON,OPENTM,CLOSETM,STARTTM,EXPIRETM,VOL,VOL_EXEC,COST,FEE,AVERAGE_PRICE,STOP_PRICE,LIMIT_PRICE,MISC,OFLAGS,TRADES_IDX,DESCR_PAIR_NAME,DESCR_ORDER_ACTION,DESCR_ORDER_TYPE,DESCR_PRICE,DESCR_PRICE2,DESCR_LEVERAGE,DESCR_ORDER_DESCR,DESCR_CLOSE_DESCR) VALUES ";

	public OrdersDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public void updateOrders(List<OrderInfo> orders, int userId) {
		String strValues = StreamUtil.join(orders, ",", o -> orderInfoToValueString(o, userId));
		String query = REPLACE_ORDERS_PREFIX + strValues;
		super.performUpdate(query);
	}

	private String orderInfoToValueString(OrderInfo order, int userId) {
		return String.format("(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
			toJdbcString(order.getOrderTxID()),
			toJdbcString(userId),
			toJdbcString(order.getRefId()),
			toJdbcString(order.getUserRef()),
			toJdbcString(order.getStatus() == null ? null : order.getStatus().label()),
			toJdbcString(order.getReason()),
			toJdbcString(order.getOpenTimestamp()),
			toJdbcString(order.getCloseTimestamp()),
			toJdbcString(order.getStartTimestamp()),
			toJdbcString(order.getExpireTimestamp()),
			toJdbcString(order.getVolume()),
			toJdbcString(order.getVolumeExecuted()),
			toJdbcString(order.getCost()),
			toJdbcString(order.getFee()),
			toJdbcString(order.getAveragePrice()),
			toJdbcString(order.getStopPrice()),
			toJdbcString(order.getLimitPrice()),
			toJdbcString(StreamUtil.join(order.getMisc(), ",", OrderMisc::label)),
			toJdbcString(StreamUtil.join(order.getFlags(), ",", OrderFlag::label)),
			toJdbcString(StreamUtil.join(order.getTrades(), ",")),
			toJdbcString(order.getDescr().getPairName()),
			toJdbcString(order.getDescr().getOrderAction().label()),
			toJdbcString(order.getDescr().getOrderType().label()),
			toJdbcString(order.getDescr().getPrimaryPrice()),
			toJdbcString(order.getDescr().getSecondaryPrice()),
			toJdbcString(order.getDescr().getLeverage()),
			toJdbcString(order.getDescr().getOrderDescription()),
			toJdbcString(order.getDescr().getCloseDescription())
		);
	}
}
