package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.config.IConfigPublic;
import com.fede.ct.v2.common.config.impl.ConfigService;
import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.util.Converter;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.datalayer.IModelPublic;
import com.fede.ct.v2.datalayer.impl.ModelFactory;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.impl.KrakenFactory;
import com.fede.ct.v2.service.ICryptoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by f.barbano on 04/11/2017.
 */
public class ServicePublic extends AbstractService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServicePublic.class);
	private static final int THREAD_POOL_SIZE = 3; // assets, asset pairs, tickers

	private final IKrakenPublic krakenPublic;
	private final IModelPublic modelPublic;
	private final IConfigPublic configPublic = ConfigService.getConfigPublic();

	private List<String> assetPairsNames = Collections.synchronizedList(new ArrayList<>());

	public ServicePublic(CryptoContext ctx) {
		super(ctx);
		krakenPublic = KrakenFactory.getPublicCaller();
		modelPublic = ModelFactory.createModelPublic(ctx);
	}

	@Override
	public synchronized void startEngine() {
		logger.debug("Start Kraken public engine");

		// Download first assets and assets pairs
		downloadAssets();
		downloadAssetPairs();

		// Schedule jobs
		long delay = getNextMidnightDelay();
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleAtFixedRate(this::downloadAssets, delay, configPublic.getCallRateAssets(), TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::downloadAssetPairs, delay, configPublic.getCallRateAssetPairs(), TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::downloadTickers, 10, configPublic.getCallRateTickers(), TimeUnit.SECONDS);

	}

	private long getNextMidnightDelay() {
		LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
		long delayMilli = Converter.localDateTimeToMillis(tomorrow) - System.currentTimeMillis();
		return (delayMilli / 1000L);
	}

	private void downloadAssets() {
		try {
			long callTime = System.currentTimeMillis();
			List<Asset> assets = krakenPublic.getAssets();
			boolean changed = modelPublic.setNewAssets(assets, callTime);
			logger.info("%d assets downloaded --> %s", assets.size(), (changed ? "data changed (new valid startTime=" + callTime + ")" : "no changes"));
			
		} catch (Exception ex) {
			logger.error(ex, "Exception caught while downloading assets");
		}
	}

	private void downloadAssetPairs() {
		try {
			long callTime = System.currentTimeMillis();
			List<AssetPair> assetPairs = krakenPublic.getAssetPairs();
			boolean changed = modelPublic.setNewAssetPairs(assetPairs, callTime);
			if(changed || assetPairsNames.isEmpty()) {
				List<String> names = StreamUtil.map(assetPairs, AssetPair::getPairName);
				synchronized (assetPairsNames) {
					assetPairsNames.clear();
					assetPairsNames.addAll(names);
				}
			}
			logger.info("%d asset pairs downloaded --> %s", assetPairs.size(), (changed ? "data changed (new valid startTime is " + callTime + ")" : "no changes"));

		} catch (Exception ex) {
			logger.error(ex, "Exception caught while downloading asset pairs");
		}
	}

	private void downloadTickers() {
		try {
			long callTime = System.currentTimeMillis();
			List<String> names;
			synchronized (assetPairsNames)	{
				names = new ArrayList<>(assetPairsNames);
			}
			List<Ticker> tickers = krakenPublic.getTickers(names);
			modelPublic.insertTickers(tickers, callTime);
			logger.info("%d tickers downloaded", tickers.size());

		} catch (Exception ex) {
			logger.error(ex, "Exception caught while downloading tickers");
		}
	}

}
