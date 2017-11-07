package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config._public.IConfigPrivate;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.datalayer.IContextModel;
import com.fede.ct.v2.datalayer.impl.ModelFactory;
import com.fede.ct.v2.kraken.IKrakenPrivate;
import com.fede.ct.v2.kraken.impl.KrakenFactory;
import com.fede.ct.v2.service.ICryptoService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by f.barbano on 04/11/2017.
 */
class ServicePrivate extends AbstractCryptoService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServicePrivate.class);
	private static final int THREAD_POOL_SIZE = 1; // open orders, closed orders
	private static final int AUTOSTOP_COUNTER_SIZE = 5; // open orders, closed orders

	private final IKrakenPrivate privateCaller;
	private final IConfigPrivate configPrivate;
	private final IContextModel contextModel;

	// this var contains the milli at which the order downloads has to autostop
	private AtomicInteger autoStopCounter = new AtomicInteger(AUTOSTOP_COUNTER_SIZE);

	ServicePrivate(IConfigPrivate configPrivate) {
		super();
		this.configPrivate = configPrivate;
		privateCaller = KrakenFactory.getPrivateCaller(configPrivate.getKrakenApiKey(), configPrivate.getKrakenApiSecret());
		this.contextModel = ModelFactory.getContextModel(configPrivate.getKrakenApiKey(), configPrivate.getKrakenApiSecret());
	}

	@Override
	public synchronized void startEngine() {
		logger.debug("Start Kraken private engine");

		// Schedule jobs
		long freqSec = configPrivate.getDownloadSecondsFrequencyOrders();
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleAtFixedRate(this::downloadOrders, freqSec, freqSec, TimeUnit.SECONDS);

		while(true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				executorService.shutdownNow();
				logger.warning("Interrupt received, stop cycle");
				throw new TechnicalException(e, "Interrupt received, stop cycle");
			}
		}
	}

	private void downloadOrders() {
		if(contextModel.isDownloadOrdersEnabled()) {
			try {
				List<OrderInfo> openOrders = privateCaller.getOpenOrders();
				List<OrderInfo> closedOrders = privateCaller.getClosedOrders();
				List<OrderInfo> orders = new ArrayList<>();
				orders.addAll(openOrders);
				orders.addAll(closedOrders);
				dataModel.updateOrders(orders, contextModel.getUserId());
				logger.info("Orders downloaded: %d open, %d closed", openOrders.size(), closedOrders.size());

				manageAutoStop(openOrders.size());

			} catch (Exception ex) {
				logger.error(ex, "Exception caught while downloading open/closed orders");
			}
		}
	}

	private void manageAutoStop(int openOrderSize) {
		if(openOrderSize == 0) {
			if(autoStopCounter.decrementAndGet() == 0) {
				contextModel.setDownloadOrdersEnabled(false);
				autoStopCounter.set(AUTOSTOP_COUNTER_SIZE);
				logger.info("Stop downloading open/closed orders: open orders not found for last %d calls", AUTOSTOP_COUNTER_SIZE);
			}
		} else {
			autoStopCounter.set(AUTOSTOP_COUNTER_SIZE);
		}
	}

}
