package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config.IConfigTrading;
import com.fede.ct.v2.common.config.impl.ConfigService;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._trading.AddOrderIn;
import com.fede.ct.v2.common.model._trading.AddOrderOut;
import com.fede.ct.v2.common.model._trading.OrderRequest;
import com.fede.ct.v2.common.model.types.OrderAction;
import com.fede.ct.v2.common.util.AltMath;
import com.fede.ct.v2.common.util.Func;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.datalayer.IModelTrading;
import com.fede.ct.v2.datalayer.impl.ModelFactory;
import com.fede.ct.v2.kraken.IKrakenTrading;
import com.fede.ct.v2.kraken.impl.KrakenFactory;
import com.fede.ct.v2.service.ICryptoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by f.barbano on 07/11/2017.
 */
public class ServiceTrading extends AbstractService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServiceTrading.class);
	private static final int THREAD_POOL_SIZE = 1;
	private static final int SECOND_SLEEP_RETRIEVE_ORDER = 5;
	private static final int DB_POLL_RATE = 5;

	private final IConfigTrading configTrading = ConfigService.getConfigTrading();
	private final IKrakenTrading krakenTrading;
	private final IModelTrading modelTrading;

	private AssetPair tradedAssetPair = null;
	private OrderInProgress orderInProgress = null;

	public ServiceTrading(CryptoContext ctx) {
		super(ctx);
		this.krakenTrading = KrakenFactory.getTradingCaller(ctx.getUserCtx());
		this.modelTrading = ModelFactory.createModelTrading(ctx);
		initTradedAssetPair();
	}

	@Override
	public void startEngine() {
		logger.debug("Start Kraken public engine");

		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleWithFixedDelay(this::doTradingStrategy, DB_POLL_RATE, DB_POLL_RATE, TimeUnit.SECONDS);

	}

	private void doTradingStrategy() {

		if(orderInProgress == null) {
			BigDecimal buyPrice = computeBuyPrice();
			logger.info("Buy price = %s", OutFormat.toStringNum(buyPrice));
			if (buyPrice != null) {
				modelTrading.turnOnDownloadOrders();
				emitBuyOrder(buyPrice);
			}

		} else {
			List<OrderInfo> orders = modelTrading.getOrdersStatus(orderInProgress.out.getTxIDs());
			orders.removeIf(o -> !o.isOrderActive());

			logger.info("Open orders found: %d", orders.size());

			if(orders.isEmpty()) {
				// No active orders found
				AddOrderIn req = orderInProgress.in;
				if(req.getOrderAction() == OrderAction.BUY) {
					AddOrderIn sellReq = createSellOrderRequest(req);
					logger.info("Sell order = %s", toStringOrderIn(sellReq));
					modelTrading.turnOnDownloadOrders();
					emitSellOrder(sellReq);
				} else if(req.getOrderAction() == OrderAction.SELL) {
					logger.info("Sell order %s got away", orderInProgress.out.getTxIDs());
					orderInProgress = null;
				}
			}
		}

	}

	private BigDecimal computeBuyPrice() {
		Ticker ticker = modelTrading.getTickerAskPriceAndAvgLast24(tradedAssetPair.getPairName());
		BigDecimal askPrice = ticker.getAsk().getPrice();
		BigDecimal avgPriceLast24 = ticker.getWeightedAverageVolume().getLast24Hours();
		Double percBuy = 1d - configTrading.getDeltaPercBuy();
		BigDecimal buyPrice = AltMath.mult(avgPriceLast24, percBuy);
		logger.debug("ask= %s, last24= %s, buyPrice=%s", OutFormat.toStringNum(askPrice), OutFormat.toStringNum(avgPriceLast24), OutFormat.toStringNum(buyPrice));
		return askPrice.compareTo(buyPrice) <= 0 ? buyPrice : null;
	}

	private void emitBuyOrder(BigDecimal buyPrice) {

		if(buyPrice.compareTo(BigDecimal.ZERO) <= 0) {
			logger.warning("Unable to emit order: buyPrice equals to 0");
			return;
		}

		BigDecimal assetBalance = getAssetBalance();
		logger.debug("Account balance for %s = %s", tradedAssetPair.getPairName(), OutFormat.toStringNum(assetBalance));
		assetBalance = assetBalance == null ? BigDecimal.ZERO : assetBalance;
		if(assetBalance == null || assetBalance.compareTo(BigDecimal.ZERO) <= 0) {
			logger.warning("Unable to emit order: no money in account");
			return;
		}

		BigDecimal notional = configTrading.getNotional();
		if(assetBalance.compareTo(notional) < 0) {
			logger.info("Notional reduced from %s to %s: not enough money in account", OutFormat.toStringNum(buyPrice), OutFormat.toStringNum(assetBalance));
			notional = assetBalance;
		}

		BigDecimal volume = AltMath.divide(notional, buyPrice);
		AddOrderIn orderIn = OrderRequest.createBuyLimitOrderIn(tradedAssetPair.getPairName(), buyPrice, volume);
		AddOrderOut orderOut;
		logger.debug("%s", toStringOrderIn(orderIn));
		Long beforeCall = System.currentTimeMillis();
		try {
			orderOut = krakenTrading.emitOrder(orderIn);
			logger.info("Order emitted: tx id = %s", orderOut.getTxIDs());
		} catch (Exception ex) {
			logger.info("Emit order raise exception... Try to retrieve order tx if exists...");
			orderOut = retrieveOrderResponse(orderIn, beforeCall);
			if(orderOut != null) {
				logger.info("Order emitted (retrieved): tx id = %s", orderOut.getTxIDs());
			} else {
				logger.info("Order buy not emitted on Kraken");
			}
		}

		if(orderOut != null) {
			orderInProgress = new OrderInProgress(orderIn, orderOut);
		}
	}

	private BigDecimal getAssetBalance() {
		AccountBalance assetBalance = modelTrading.getAssetBalance(tradedAssetPair.getQuote());
		return assetBalance == null ? BigDecimal.ZERO : assetBalance.getBalance();
	}

	private String toStringOrderIn(AddOrderIn orderIn) {
		return String.format("[%s, %s, assetPair=%s, price=%s, volume=%s]",
			orderIn.getOrderAction().label(),
			orderIn.getOrderType().label(),
			orderIn.getPairName(),
			OutFormat.toStringNum(orderIn.getPrice()),
			OutFormat.toStringNum(orderIn.getVolume())
		);
	}

	private AddOrderOut retrieveOrderResponse(AddOrderIn request, Long minOpenTm) {
		Long maxOpenTm = System.currentTimeMillis();
		modelTrading.turnOnDownloadOrders();
		while(modelTrading.isDownloadOrdersEnabled()) {
			Func.sleep(1000L * SECOND_SLEEP_RETRIEVE_ORDER);
			List<OrderInfo> orders = modelTrading.getOrders(minOpenTm, maxOpenTm);
			orders.removeIf(o -> o.getDescr().getOrderAction() != request.getOrderAction());
			orders.removeIf(o -> !o.getDescr().getPairName().equalsIgnoreCase(request.getPairName()));
			orders.removeIf(o -> o.getDescr().getPrimaryPrice().compareTo(request.getPrice()) != 0);
			if (!orders.isEmpty()) {
				List<String> txIds = StreamUtil.map(orders, OrderInfo::getOrderTxID);
				AddOrderOut orderOut = new AddOrderOut();
				orderOut.setTxIDs(txIds);
				return orderOut;
			}
		}
		return null;
	}

	private AddOrderIn createSellOrderRequest(AddOrderIn in) {
		Double feesBuy = configTrading.getFeesPercBuy();
		Double feesSell = configTrading.getFeesPercSell();
		Double percSell = configTrading.getDeltaPercSell();

		BigDecimal buyPrice = in.getPrice();
		Double buyVolume = in.getVolume();

		BigDecimal buyNotional = AltMath.mult(buyPrice, buyVolume);
		Double multiplier = (1 + feesBuy + percSell) * (1 + feesSell);

		BigDecimal sellNotional = AltMath.mult(buyNotional, multiplier);
		BigDecimal sellPrice = AltMath.divide(sellNotional, buyVolume);

		return OrderRequest.createSellLimitOrderIn(tradedAssetPair.getPairName(), sellPrice, buyVolume);
	}

	private void emitSellOrder(AddOrderIn request) {
		AddOrderOut orderOut;
		long callTime = System.currentTimeMillis();

		try {
			orderOut = krakenTrading.emitOrder(request);
			logger.info("Sell order emitted: %s", orderOut.getTxIDs());
		} catch (Exception ex) {
			logger.info("Exception raised emitting sell order");
			orderOut = retrieveOrderResponse(request, callTime);
			if(orderOut != null) {
				logger.info("Sell order retrieved: %s", orderOut.getTxIDs());
			} else {
				logger.info("Order sell not emitted on Kraken");
			}
		}

		if(orderOut != null) {
			orderInProgress = new OrderInProgress(request, orderOut);
		}
	}

	private void initTradedAssetPair() {
		String apName = configTrading.getAssetPairName();

		List<AssetPair> apKnown = modelTrading.getAssetPairs(true);
		AssetPair found = null;
		for(AssetPair ap : apKnown) {
			if(ap.getPairName().equalsIgnoreCase(apName)) {
				found = ap;
			}
		}

		if(found == null) {
			throw new TechnicalException("Asset pair %s does not exists (see trading config file)", apName);
		}

		tradedAssetPair = found;
	}

	private static class OrderInProgress {
		private AddOrderIn in;
		private AddOrderOut out;

		private OrderInProgress(AddOrderIn in, AddOrderOut out) {
			this.in = in;
			this.out = out;
		}
	}
}
