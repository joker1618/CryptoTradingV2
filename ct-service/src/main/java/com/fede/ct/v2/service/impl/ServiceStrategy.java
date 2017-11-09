package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._public.IConfigPrivate;
import com.fede.ct.v2.common.config._public.IConfigStrategy;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._trading.AddOrderIn;
import com.fede.ct.v2.common.model._trading.AddOrderOut;
import com.fede.ct.v2.common.model._trading.OrderRequest;
import com.fede.ct.v2.common.model.types.OrderStatus;
import com.fede.ct.v2.datalayer.IContextModel;
import com.fede.ct.v2.datalayer.impl.ModelFactory;
import com.fede.ct.v2.kraken.IKrakenStrategy;
import com.fede.ct.v2.kraken.exception.KrakenException;
import com.fede.ct.v2.kraken.impl.KrakenFactory;
import com.fede.ct.v2.service.ICryptoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by f.barbano on 07/11/2017.
 */
public class ServiceStrategy extends AbstractCryptoService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServiceStrategy.class);

	private final IKrakenStrategy callerStrategy;
	private final IConfigPrivate configPrivate;
	private final IConfigStrategy configStrategy;
	private final IContextModel contextModel;

	public ServiceStrategy(IConfigPrivate configPrivate, IConfigStrategy configStrategy) {
		super();
		this.configPrivate = configPrivate;
		this.configStrategy = configStrategy;
		this.callerStrategy = KrakenFactory.getStrategyCaller(configPrivate.getKrakenApiKey(), configPrivate.getKrakenApiSecret());
		this.contextModel = ModelFactory.getContextModel(configPrivate.getKrakenApiKey(), configPrivate.getKrakenApiSecret());
	}

	@Override
	public void startEngine() {
		logger.debug("Start Kraken strategy engine");

		// Schedule jobs
		List<String> apNames = configStrategy.getAssetPairNames();
		ExecutorService executorService = Executors.newFixedThreadPool(apNames.size());
		for(String pairName : apNames) {
			executorService.submit(() -> this.doStrategy(pairName));
		}
		executorService.shutdownNow();

		while(!executorService.isTerminated()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e);
				throw new TechnicalException(e, "Interrupt received, stop cycle");
			}
		}
	}

	private void doStrategy(String pairName) {

		while(true) {

			try {
				Ticker ticker = dataModel.retrieveAskPriceAverageLast24(pairName);

				if (ticker != null) {
					BigDecimal buyPrice = computeThresold(ticker);
					BigDecimal actualAskPrice = ticker.getAsk().getPrice();

					if (actualAskPrice.doubleValue() <= buyPrice.doubleValue()) {
						// Buy condition
						double volume = buyPrice.doubleValue() / actualAskPrice.doubleValue();
						AddOrderOut addOrderOut;
						AddOrderIn addOrderIn = OrderRequest.createBuyLimitOrderIn(pairName, buyPrice, volume);
						try {
							contextModel.setDownloadOrdersEnabled(true);
							addOrderOut = callerStrategy.emitOrder(addOrderIn);
						} catch (KrakenException e) {
							addOrderOut = retrieveOrderIxIdLost(addOrderIn);
						}

						if (addOrderOut != null) {
							logger.info("Order emitted for pair %s [txId=%s]", pairName, addOrderOut.getTxIDs());
							waitOrderSell(addOrderOut);
						}
					}
				}

				Thread.sleep(3000);

			} catch(Exception e) {
				logger.error(e, "Exception caught while perform strategy");
			}
		}


	}

	private BigDecimal computeThresold(Ticker ticker) {
		BigDecimal buyLowerPerc = BigDecimal.valueOf(configStrategy.getBuyLowerPerc());
		BigDecimal last24Hours = ticker.getWeightedAverageVolume().getLast24Hours();
		BigDecimal toSubtract = last24Hours.multiply(buyLowerPerc);
		return last24Hours.subtract(toSubtract);
	}

	private AddOrderOut retrieveOrderIxIdLost(AddOrderIn addOrderIn) {
		// todo impl
		return null;
	}

	private void waitOrderSell(AddOrderOut addOrderOut) throws InterruptedException {
		boolean go = true;
		int numCycles = 5;
		int counter = numCycles;
		while(go) {
			List<OrderInfo> orders = dataModel.getOrderStatus(contextModel.getUserId(), addOrderOut.getTxIDs());
			orders.removeIf(o -> o.getStatus() != OrderStatus.OPEN && o.getStatus() != OrderStatus.PENDING);
			counter = orders.isEmpty() ? counter-1 : numCycles;

			if(counter == 0) {
				return;
			}

			Thread.sleep(3000L);
		}
	}
}
