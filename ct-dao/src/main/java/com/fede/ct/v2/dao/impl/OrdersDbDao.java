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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by f.barbano on 06/11/2017.
 */
public class OrdersDbDao extends AbstractDbDao implements IOrdersDao {

	private static final SimpleLog logger = LogService.getLogger(OrdersDbDao.class);

	private static final String REPLACE_ORDER = "REPLACE INTO ORDERS (ORDER_TX_ID,USER_ID,REF_ID,USER_REF,STATUS,REASON,OPENTM,CLOSETM,STARTTM,EXPIRETM,VOL,VOL_EXEC,COST,FEE,AVG_PRICE,STOP_PRICE,LIMIT_PRICE,MISC,OFLAGS,TRADES_ID,DESCR_PAIR_NAME,DESCR_ORDER_ACTION,DESCR_ORDER_TYPE,DESCR_PRICE,DESCR_PRICE2,DESCR_LEVERAGE,DESCR_ORDER_DESCR,DESCR_CLOSE_DESCR) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SELECT_ORDERS_BY_OPEN_TM = "SELECT ORDER_TX_ID,REF_ID,USER_REF,STATUS,REASON,OPENTM,CLOSETM,STARTTM,EXPIRETM,VOL,VOL_EXEC,COST,FEE,AVG_PRICE,STOP_PRICE,LIMIT_PRICE,MISC,OFLAGS,TRADES_ID,DESCR_PAIR_NAME,DESCR_ORDER_ACTION,DESCR_ORDER_TYPE,DESCR_PRICE,DESCR_PRICE2,DESCR_LEVERAGE,DESCR_ORDER_DESCR,DESCR_CLOSE_DESCR FROM ORDERS WHERE USER_ID = ? AND OPENTM >= ? AND OPENTM <= ?";
	private static final String SELECT_ORDERS_STATUS = "SELECT ORDER_TX_ID, STATUS FROM ORDERS WHERE USER_ID = ? AND ORDER_TX_ID IN ";
	private static final String SELECT_OPEN_ORDERS = "SELECT ORDER_TX_ID FROM ORDERS WHERE USER_ID = ? AND STATUS = ? ";

	
	public OrdersDbDao(CryptoContext ctx) {
		super(ctx);
	}


	@Override
	public void updateOrders(List<OrderInfo> orders) {
		List<Query> queries = StreamUtil.map(orders, this::createQueryIns);
		super.performTransaction(queries);
	}

	@Override
	public List<OrderInfo> getOrdersStatus(List<String> txIds) {
		if(txIds.isEmpty())	return new ArrayList<>();

		String inMarks = String.format("(?%s)", StringUtils.repeat(",?", txIds.size() - 1));
		Query query = new Query(SELECT_ORDERS_STATUS + inMarks);
		query.addParams(getUserCtx().getUserId());
		txIds.forEach(query::addParams);
		List<InquiryResult> res = super.performInquiry(query);
		return StreamUtil.map(res, this::parseOrderInfo);
	}

	@Override
	public List<OrderInfo> getOrdersByOpenTm(Long minOpenTm, Long maxOpenTm) {
		Query query = new Query(SELECT_ORDERS_BY_OPEN_TM, getUserCtx().getUserId(), minOpenTm, maxOpenTm);
		List<InquiryResult> results = super.performInquiry(query);
		return StreamUtil.map(results, this::parseOrderInfo);
	}

	@Override
	public List<OrderInfo> getOpenOrders() {
		Query query = new Query(SELECT_OPEN_ORDERS, getUserCtx().getUserId(), OrderStatus.OPEN.label());
		List<InquiryResult> results = super.performInquiry(query);
		return StreamUtil.map(results, this::parseOrderInfo);
	}

	private Query createQueryIns(OrderInfo orderInfo) {
		Query query = new Query(REPLACE_ORDER);
		query.addParams(orderInfo.getOrderTxID());
		query.addParams(getUserCtx().getUserId());
		query.addParams(orderInfo.getRefId());
		query.addParams(orderInfo.getUserRef());
		query.addParams(orderInfo.getStatus().label());
		query.addParams(orderInfo.getReason());
		query.addParams(orderInfo.getOpenTm());
		query.addParams(orderInfo.getCloseTm());
		query.addParams(orderInfo.getStartTm());
		query.addParams(orderInfo.getExpireTm());
		query.addParams(orderInfo.getVol());
		query.addParams(orderInfo.getVolExec());
		query.addParams(orderInfo.getCost());
		query.addParams(orderInfo.getFee());
		query.addParams(orderInfo.getAvgPrice());
		query.addParams(orderInfo.getStopPrice());
		query.addParams(orderInfo.getLimitPrice());
		query.addParams(StreamUtil.join(orderInfo.getMisc(), ",", OrderMisc::label));
		query.addParams(StreamUtil.join(orderInfo.getOflags(), ",", OrderFlag::label));
		query.addParams(StreamUtil.join(orderInfo.getTradesId(), ","));
		query.addParams(orderInfo.getDescr().getPairName());
		query.addParams(orderInfo.getDescr().getOrderAction().label());
		query.addParams(orderInfo.getDescr().getOrderType().label());
		query.addParams(orderInfo.getDescr().getPrimaryPrice());
		query.addParams(orderInfo.getDescr().getSecondaryPrice());
		query.addParams(orderInfo.getDescr().getLeverage());
		query.addParams(orderInfo.getDescr().getOrderDescription());
		query.addParams(orderInfo.getDescr().getCloseDescription());
		return query;
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
