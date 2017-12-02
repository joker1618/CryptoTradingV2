package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._private.OrderInfo.OrderDescr;
import com.fede.ct.v2.common.model.types.*;
import com.fede.ct.v2.common.util.StrUtil;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IOrdersDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class OrdersDbDao extends AbstractDbDao implements IOrdersDao {

	private static final SimpleLog logger = LogService.getLogger(OrdersDbDao.class);

	private static final String REPLACE_ORDERS_PREFIX = "REPLACE INTO ORDERS (ORDER_TX_ID,USER_ID,REF_ID,USER_REF,STATUS,REASON,OPENTM,CLOSETM,STARTTM,EXPIRETM,VOL,VOL_EXEC,COST,FEE,AVG_PRICE,STOP_PRICE,LIMIT_PRICE,MISC,OFLAGS,TRADES_ID,DESCR_PAIR_NAME,DESCR_ORDER_ACTION,DESCR_ORDER_TYPE,DESCR_PRICE,DESCR_PRICE2,DESCR_LEVERAGE,DESCR_ORDER_DESCR,DESCR_CLOSE_DESCR) VALUES ";
	private static final String SELECT_ORDERS_BY_OPEN_TM = "SELECT ORDER_TX_ID,REF_ID,USER_REF,STATUS,REASON,OPENTM,CLOSETM,STARTTM,EXPIRETM,VOL,VOL_EXEC,COST,FEE,AVG_PRICE,STOP_PRICE,LIMIT_PRICE,MISC,OFLAGS,TRADES_ID,DESCR_PAIR_NAME,DESCR_ORDER_ACTION,DESCR_ORDER_TYPE,DESCR_PRICE,DESCR_PRICE2,DESCR_LEVERAGE,DESCR_ORDER_DESCR,DESCR_CLOSE_DESCR FROM ORDERS WHERE USER_ID = ? AND OPENTM >= ? AND OPENTM <= ?";
	private static final String SELECT_ORDERS_STATUS = "SELECT ORDER_TX_ID, STATUS FROM ORDERS WHERE USER_ID = ? AND ORDER_TX_ID IN ";

	
	public OrdersDbDao(CryptoContext ctx) {
		super(ctx);
	}


	@Override
	public void updateOrders(List<OrderInfo> orders) {
		List<Query> queries = super.createJdbcQueries(REPLACE_ORDERS_PREFIX, orders.size(), 28, orders, getOrderInfoParse());
		super.performTransaction(queries);
	}

	@Override
	public List<OrderInfo> getOrdersStatus(List<String> txIds) {
		List<Object> values = new ArrayList<>(txIds);
		values.add(0, getUserCtx().getUserId());
		List<Query> queries = super.createJdbcQueries(SELECT_ORDERS_STATUS, 3, txIds.size(), values, o -> o);
		List<OrderInfo> toRet = new ArrayList<>();
		for(Query query : queries) {
			List<InquiryResult> res = super.performInquiry(query);
			toRet.addAll(StreamUtil.map(res, this::parseOrderInfo));
		}
		return toRet;
	}

	@Override
	public List<OrderInfo> getOrdersByOpenTm(Long minOpenTm, Long maxOpenTm) {
		Query query = new Query(SELECT_ORDERS_BY_OPEN_TM, getUserCtx().getUserId(), minOpenTm, maxOpenTm);
		List<InquiryResult> results = super.performInquiry(query);
		return StreamUtil.map(results, this::parseOrderInfo);
	}

	private List<Function<OrderInfo, Object>> getOrderInfoParse() {
		List<Function<OrderInfo, Object>> functions = new ArrayList<>();
		functions.add(OrderInfo::getOrderTxID);
		functions.add(oi -> getUserCtx().getUserId());
		functions.add(OrderInfo::getRefId);
		functions.add(OrderInfo::getUserRef);
		functions.add(oi -> oi.getStatus().label());
		functions.add(OrderInfo::getReason);
		functions.add(OrderInfo::getOpenTm);
		functions.add(OrderInfo::getCloseTm);
		functions.add(OrderInfo::getStartTm);
		functions.add(OrderInfo::getExpireTm);
		functions.add(OrderInfo::getVol);
		functions.add(OrderInfo::getVolExec);
		functions.add(OrderInfo::getCost);
		functions.add(OrderInfo::getFee);
		functions.add(OrderInfo::getAvgPrice);
		functions.add(OrderInfo::getStopPrice);
		functions.add(OrderInfo::getLimitPrice);
		functions.add(oi -> StreamUtil.join(oi.getMisc(), ",", OrderMisc::label));
		functions.add(oi -> StreamUtil.join(oi.getOflags(), ",", OrderFlag::label));
		functions.add(oi -> StreamUtil.join(oi.getTradesId(), ","));
		functions.add(oi -> oi.getDescr().getPairName());
		functions.add(oi -> oi.getDescr().getOrderAction().label());
		functions.add(oi -> oi.getDescr().getOrderType().label());
		functions.add(oi -> oi.getDescr().getPrimaryPrice());
		functions.add(oi -> oi.getDescr().getSecondaryPrice());
		functions.add(oi -> oi.getDescr().getLeverage());
		functions.add(oi -> oi.getDescr().getOrderDescription());
		functions.add(oi -> oi.getDescr().getCloseDescription());
		return functions;
	}


	private OrderInfo parseOrderInfo(InquiryResult ir) {
		OrderInfo oi = new OrderInfo();
		oi.setOrderTxID(ir.getString("ORDER_TX_ID"));
		oi.setRefId(ir.getString("REF_ID"));
		oi.setUserRef(ir.getString("USER_REF"));
		oi.setStatus(OrderStatus.getByLabel(ir.getString("STATUS")));
		oi.setReason(ir.getString("REASON"));
		oi.setOpenTm(ir.getLong("OPENTM"));
		oi.setCloseTm(ir.getLong("CLOSETM"));
		oi.setStartTm(ir.getLong("STARTTM"));
		oi.setExpireTm(ir.getLong("EXPIRETM"));
		oi.setVol(ir.getBigDecimal("VOL"));
		oi.setVolExec(ir.getBigDecimal("VOL_EXEC"));
		oi.setCost(ir.getBigDecimal("COST"));
		oi.setFee(ir.getBigDecimal("FEE"));
		oi.setAvgPrice(ir.getBigDecimal("AVG_PRICE"));
		oi.setStopPrice(ir.getBigDecimal("STOP_PRICE"));
		oi.setLimitPrice(ir.getBigDecimal("LIMIT_PRICE"));

		List<String> strMisc = StrUtil.splitFieldsList(ir.getString("MISC"), ",");
		oi.setMisc(StreamUtil.map(strMisc, OrderMisc::getByLabel));

		List<String> strOFlags = StrUtil.splitFieldsList(ir.getString("OFLAGS"), ",");
		oi.setOflags(StreamUtil.map(strOFlags, OrderFlag::getByLabel));

		List<String> tradeIds = StrUtil.splitFieldsList(ir.getString("TRADES_ID"), ",");
		oi.setTradesId(tradeIds);

		OrderDescr od = new OrderDescr();
		od.setPairName(ir.getString("DESCR_PAIR_NAME"));
		od.setOrderAction(OrderAction.getByLabel(ir.getString("DESCR_ORDER_ACTION")));
		od.setOrderType(OrderType.getByLabel(ir.getString("DESCR_ORDER_TYPE")));
		od.setPrimaryPrice(ir.getBigDecimal("DESCR_PRICE"));
		od.setSecondaryPrice(ir.getBigDecimal("DESCR_PRICE2"));
		od.setLeverage(ir.getInteger("DESCR_LEVERAGE"));
		od.setOrderDescription(ir.getString("DESCR_ORDER_DESCR"));
		od.setCloseDescription(ir.getString("DESCR_CLOSE_DESCR"));

		oi.setDescr(od);

		return oi;
	}



}
