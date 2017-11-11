package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model.types.OrderFlag;
import com.fede.ct.v2.common.model.types.OrderMisc;
import com.fede.ct.v2.common.model.types.OrderStatus;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IOrdersDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class OrdersDbDao extends AbstractDbDao implements IOrdersDao {

	private static final String REPLACE_ORDERS_PREFIX = "REPLACE INTO ORDERS (ORDER_TX_ID,USER_ID,REF_ID,USER_REF,STATUS,REASON,OPENTM,CLOSETM,STARTTM,EXPIRETM,VOL,VOL_EXEC,COST,FEE,AVG_PRICE,STOP_PRICE,LIMIT_PRICE,MISC,OFLAGS,TRADES_ID,DESCR_PAIR_NAME,DESCR_ORDER_ACTION,DESCR_ORDER_TYPE,DESCR_PRICE,DESCR_PRICE2,DESCR_LEVERAGE,DESCR_ORDER_DESCR,DESCR_CLOSE_DESCR) VALUES ";
	private static final String SELECT_OPEN_ORDERS = "SELECT ORDER_TX_ID FROM ORDERS WHERE STATUS = 'open' AND USER_ID = ?";
	private static final String SELECT_ORDERS_STATUS = "SELECT ORDER_TX_ID, STATUS FROM ORDERS WHERE ORDER_TX_ID IN ('%s') AND USER_ID = %d";

	public OrdersDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public void updateOrders(int userId, List<OrderInfo> orders) {
		String strValues = StreamUtil.join(orders, ",", o -> orderInfoToValueString(o, userId));
		String query = REPLACE_ORDERS_PREFIX + strValues;
		super.performUpdate(query);
	}

	@Override
	public List<String> getOpenOrders(int userId) {
		try(PreparedStatement ps = createPreparedStatement(SELECT_OPEN_ORDERS, userId);
			ResultSet rs = ps.executeQuery()) {

			List<String> txIdList = new ArrayList<>();
			if(rs != null) {
				while(rs.next()) {
					txIdList.add(rs.getString("ORDER_TX_ID"));
				}
			}

			return txIdList;
			
		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s, userId=%d]", SELECT_OPEN_ORDERS, userId);
		}
	}

	@Override
	public List<OrderInfo> getOrdersStatus(int userId, List<String> orderTxIds) {
		String query = String.format(SELECT_ORDERS_STATUS, StreamUtil.join(orderTxIds, "','"), userId);

		try(PreparedStatement ps = createPreparedStatement(query);
			ResultSet rs = ps.executeQuery()) {

			List<OrderInfo> toRet = new ArrayList<>();
			if(rs != null && rs.next()) {
				String txId = rs.getString("ORDER_TX_ID");
				String sqlRes = rs.getString("STATUS");
				OrderInfo oi = new OrderInfo();
				oi.setOrderTxID(txId);
				oi.setStatus(OrderStatus.getByLabel(sqlRes));
				toRet.add(oi);
			}

			return toRet;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", query);
		}
	}

	private String orderInfoToValueString(OrderInfo order, int userId) {
		return String.format("(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
			toJdbcString(order.getOrderTxID()),
			toJdbcString(userId),
			toJdbcString(order.getRefId()),
			toJdbcString(order.getUserRef()),
			toJdbcString(order.getStatus() == null ? null : order.getStatus().label()),
			toJdbcString(order.getReason()),
			toJdbcString(order.getOpenTm()),
			toJdbcString(order.getCloseTm()),
			toJdbcString(order.getStartTm()),
			toJdbcString(order.getExpireTm()),
			toJdbcString(order.getVol()),
			toJdbcString(order.getVolExec()),
			toJdbcString(order.getCost()),
			toJdbcString(order.getFee()),
			toJdbcString(order.getAvgPrice()),
			toJdbcString(order.getStopPrice()),
			toJdbcString(order.getLimitPrice()),
			toJdbcString(StreamUtil.join(order.getMisc(), ",", OrderMisc::label)),
			toJdbcString(StreamUtil.join(order.getOflags(), ",", OrderFlag::label)),
			toJdbcString(StreamUtil.join(order.getTradesId(), ",")),
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
	
//	private OrderInfo parseOrderInfo(ResultSet rs, String querySelect) throws SQLException {
//		String sel = StringUtils.substringBetween(querySelect, "SELECT", "FROM");
//
//		OrderInfo oi = new OrderInfo();
//		if(sel.contains("ORDER_TX_ID"))	oi.setOrderTxID(rs.getString("ORDER_TX_ID"));
//		if(sel.contains("REF_ID"))		oi.setRefId(rs.getString("REF_ID"));
//		if(sel.contains("USER_REF"))	oi.setUserRef(rs.getString("USER_REF"));
//		if(sel.contains("STATUS"))		oi.setStatus(OrderStatus.getByLabel(rs.getString("STATUS")));
//		if(sel.contains("REASON"))		oi.setReason(rs.getString("REASON"));
//		if(sel.contains("OPENTM"))		oi.setOpenTm(rs.getLong("OPENTM"));
//		if(sel.contains("CLOSETM"))		oi.setOpenTm(rs.getLong("CLOSETM"));
//		if(sel.contains("STARTTM"))		oi.setOpenTm(rs.getLong("STARTTM"));
//		if(sel.contains("EXPIRETM"))	oi.setOpenTm(rs.getLong("EXPIRETM"));
//		if(sel.contains("VOL"))			oi.setVol(rs.getBigDecimal("VOL"));
//		if(sel.contains("VOL_EXEC"))	oi.setVolExec(rs.getBigDecimal("VOL_EXEC"));
//		if(sel.contains("COST"))		oi.setCost(rs.getBigDecimal("COST"));
//		if(sel.contains("FEE"))			oi.setFee(rs.getBigDecimal("FEE"));
//		if(sel.contains("AVG_PRICE"))	oi.setAvgPrice(rs.getBigDecimal("AVG_PRICE"));
//		if(sel.contains("STOP_PRICE"))	oi.setStopPrice(rs.getBigDecimal("STOP_PRICE"));
//		if(sel.contains("LIMIT_PRICE"))	oi.setLimitPrice(rs.getBigDecimal("LIMIT_PRICE"));
//		if(sel.contains("MISC"))		oi.setMisc(StreamUtil.map(StrUtil.splitFieldsList(rs.getString("MISC"), ","), OrderMisc::getByLabel));
//		if(sel.contains("OFLAGS"))		oi.setOflags(StreamUtil.map(StrUtil.splitFieldsList(rs.getString("OFLAGS"), ","), OrderFlag::getByLabel));
//		if(sel.contains("TRADES_ID"))	oi.setTradesId(StrUtil.splitFieldsList(rs.getString("TRADES_ID"), ","));
//
//		OrderDescr od = new OrderDescr();
//		dff
//
//	}
	
}
