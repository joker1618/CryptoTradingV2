package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config.IConfigPrivate;
import com.fede.ct.v2.common.config.impl.ConfigService;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.datalayer.IModelPrivate;
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
class ServicePrivate extends AbstractService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServicePrivate.class);
	private static final int THREAD_POOL_SIZE = 1; // open orders, closed orders

	private final IKrakenPrivate privateCaller;
	private final IConfigPrivate configPrivate = ConfigService.getConfigPrivate();
	private final IModelPrivate modelPrivate;

	private DaemonState daemonState;
	private int numIdleNoLog;
	private int counterIdleNoLog;

	ServicePrivate(CryptoContext ctx) {
		super(ctx);
		this.privateCaller = KrakenFactory.getPrivateCaller(ctx.getUserCtx());
		this.modelPrivate = ModelFactory.createModelPrivate(ctx);
		this.daemonState = new DaemonState(configPrivate.getOrdersAutostopOpen(), configPrivate.getOrdersAutostopClosed());
	}

	@Override
	public synchronized void startEngine() {
		logger.debug("Start Kraken private engine");


		// Schedule jobs
		long freqDwnOrders = configPrivate.getCallRateOrders();

		numIdleNoLog = (int)(30 / freqDwnOrders);
		counterIdleNoLog = 0;

		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleAtFixedRate(this::downloadOrders, freqDwnOrders, freqDwnOrders, TimeUnit.SECONDS);

	}

	private void downloadOrders() {
		logger.info("%s", daemonState);
		
		if(modelPrivate.isDownloadOrdersEnabled()) {
			try {
				List<OrderInfo> openOrders = privateCaller.getOpenOrders();
				List<OrderInfo> closedOrders = privateCaller.getClosedOrders();
				List<OrderInfo> orders = new ArrayList<>();
				orders.addAll(openOrders);
				orders.addAll(closedOrders);
				modelPrivate.updateOrders(orders);
				logger.info("Orders downloaded: %d open, %d closed", openOrders.size(), closedOrders.size());

				// auto-stop management
				daemonState.updateState(openOrders.size(), closedOrders.size());
				if(daemonState.isAutostopDownloadOrders()) {
					daemonState.reset();
					modelPrivate.setDownloadOrdersEnabled(false);
					logger.info("Stop download orders: no open for %d, no closed for %d", daemonState.numNoOpen, daemonState.numEqualsClosed);
				}

			} catch (Exception ex) {
				daemonState.reset();
				logger.error(ex, "Exception caught while downloading open/closed orders");
			}
		} else {
				logger.info("else");
			counterIdleNoLog++;
			if(counterIdleNoLog == numIdleNoLog) {
				logger.info("Idle");
				counterIdleNoLog = 0;
			}
		}
	}

	private static class DaemonState {
		int numNoOpen;
		int numEqualsClosed;
		int counterNoOpen;
		int counterEqualsClosed;
		int prevClosedNum;

		private DaemonState(int numNoOpen, int numEqualsClosed) {
			this.numNoOpen = numNoOpen;
			this.numEqualsClosed = numEqualsClosed;
			reset();
		}

		private synchronized void updateState(int numOpen, int numClosed) {
			if(numOpen == 0 && numClosed == prevClosedNum){
				counterNoOpen++;
				counterEqualsClosed++;
			} else {
				counterNoOpen = 0;
				counterEqualsClosed = 0;
				prevClosedNum = numClosed;
			}
		}

		private synchronized boolean isAutostopDownloadOrders() {
			return counterNoOpen >= numNoOpen && counterEqualsClosed >= numEqualsClosed;
		}

		private synchronized void reset() {
			this.counterNoOpen = 0;
			this.counterEqualsClosed = 0;
			this.prevClosedNum = -1;
		}

		@Override
		public String toString() {
			return String.format("DaemonState [open=%d/%d, closed=%d/%d, last closed siez=%d]", counterNoOpen, numNoOpen, counterEqualsClosed, numEqualsClosed, prevClosedNum);
		}
	}

}
