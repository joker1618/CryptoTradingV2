package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config.IConfigPrivate;
import com.fede.ct.v2.common.config.impl.ConfigService;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._private.AccountBalance;
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

/**
 * Created by f.barbano on 04/11/2017.
 */
public class ServicePrivate extends AbstractService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServicePrivate.class);
	private static final int THREAD_POOL_SIZE = 1; // open orders, closed orders

	private final IKrakenPrivate krakenPrivate;
	private final IConfigPrivate configPrivate = ConfigService.getConfigPrivate();
	private final IModelPrivate modelPrivate;

	private DaemonState daemonState;

	public ServicePrivate(CryptoContext ctx) {
		super(ctx);
		this.krakenPrivate = KrakenFactory.getPrivateCaller(ctx.getUserCtx());
		this.modelPrivate = ModelFactory.createModelPrivate(ctx);
	}

	@Override
	public synchronized void startEngine() {
		logger.debug("Start Kraken private engine");

		int autostopOpen = configPrivate.getOrdersAutostopOpen();
		int autostopClosed = configPrivate.getOrdersAutostopClosed();
		long freqDwnOrders = configPrivate.getCallRateOrders();
		int numLogIdle = (int)(60 / freqDwnOrders);
		this.daemonState = new DaemonState(autostopOpen, autostopClosed, numLogIdle);

		callAccountBalance();

		// Schedule jobs
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleWithFixedDelay(this::downloadOrders, freqDwnOrders, freqDwnOrders, TimeUnit.SECONDS);

	}

	private void downloadOrders() {
		if(modelPrivate.isDownloadOrdersEnabled()) {
			try {
				List<OrderInfo> openOrders = krakenPrivate.getOpenOrders();
				List<OrderInfo> closedOrders = krakenPrivate.getClosedOrders();
				List<OrderInfo> orders = new ArrayList<>();
				orders.addAll(openOrders);
				orders.addAll(closedOrders);
				modelPrivate.updateOrders(orders);
				logger.info("Orders downloaded: %d open, %d closed", openOrders.size(), closedOrders.size());

				daemonState.updateStateActive(openOrders.size(), closedOrders.size());

				if(daemonState.isNeedAccountBalanceCall()) {
					callAccountBalance();
				}

				// auto-stop management
				if(daemonState.isAutoStopDownloadOrders()) {
					daemonState.reset();
					modelPrivate.setDownloadOrdersEnabled(false);
					logger.info("Stop download orders: no open for %d, no closed for %d", daemonState.numNoOpen, daemonState.numEqualsClosed);
				}

			} catch (Exception ex) {
				daemonState.reset();
				logger.error(ex, "Exception caught while downloading open/closed orders");
			}
			
		} else {
			daemonState.updateStateIdle();
			if(daemonState.isLogIdle()) {
				daemonState.reset();
				logger.info("Idle");
			}
		}
	}

	private void callAccountBalance() {
		try {
			List<AccountBalance> abList = krakenPrivate.getAccountBalances();
			if(!abList.isEmpty()) {
				modelPrivate.addAccountBalance(abList);
			}
			logger.info("Account balance downloaded: %d assets", abList.size());

		} catch(Exception ex) {
			logger.error(ex, "Exception caught while downloading open/closed orders");
		}
	}

	private static class DaemonState {
		int numLogIdle;
		int counterLogIdle;
		int numNoOpen;
		int numEqualsClosed;
		int counterNoOpen;
		int counterEqualsClosed;
		int prevOpenNum;
		int prevClosedNum;
		boolean needAccountBalanceCall;

		private DaemonState(int numNoOpen, int numEqualsClosed, int numLogIdle) {
			this.numNoOpen = numNoOpen;
			this.numEqualsClosed = numEqualsClosed;
			this.numLogIdle = numLogIdle;
			reset();
		}

		private synchronized void updateStateActive(int numOpen, int numClosed) {
			counterLogIdle = 0;

			boolean resOpen = prevOpenNum != -1 && prevOpenNum != numOpen;
			boolean resClosed = prevClosedNum != -1 && prevClosedNum != numClosed;
			needAccountBalanceCall = resOpen || resClosed;

			if(numOpen == 0 && numClosed == prevClosedNum){
				counterNoOpen++;
				counterEqualsClosed++;
			} else {
				counterNoOpen = 0;
				counterEqualsClosed = 0;
			}

			prevOpenNum = numOpen;
			prevClosedNum = numClosed;
		}

		private synchronized void updateStateIdle() {
			counterLogIdle++;
			counterNoOpen = 0;
			counterEqualsClosed = 0;
			prevOpenNum = -1;
			prevClosedNum = -1;
			needAccountBalanceCall = false;
		}

		private synchronized boolean isAutoStopDownloadOrders() {
			return counterNoOpen >= numNoOpen && counterEqualsClosed >= numEqualsClosed;
		}

		private synchronized boolean isLogIdle() {
			return counterLogIdle >= numLogIdle;
		}

		private synchronized void reset() {
			this.counterNoOpen = 0;
			this.counterEqualsClosed = 0;
			this.prevOpenNum = -1;
			this.prevClosedNum = -1;
			this.counterLogIdle = 0;
			this.needAccountBalanceCall = false;
		}

		private boolean isNeedAccountBalanceCall() {
			return needAccountBalanceCall;
		}

		@Override
		public String toString() {
			return String.format("DaemonState [open=%d/%d, closed=%d/%d, last closed size=%d]", counterNoOpen, numNoOpen, counterEqualsClosed, numEqualsClosed, prevClosedNum);
		}
	}

}
