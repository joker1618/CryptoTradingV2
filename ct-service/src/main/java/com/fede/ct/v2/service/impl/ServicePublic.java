package com.fede.ct.v2.service.impl;

import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.util.Converter;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.impl.KrakenFactory;
import com.fede.ct.v2.service.ICryptoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by f.barbano on 04/11/2017.
 */
class ServicePublic extends AbstractCryptoService implements ICryptoService {

	private static final SimpleLog logger = LogService.getLogger(ServicePublic.class);
	private static final int THREAD_POOL_SIZE = 3; // assets, asset pairs, tickers

	private final IKrakenPublic krakenCaller;

	ServicePublic() {
		super();
		krakenCaller = KrakenFactory.getPublicCaller();
	}

	@Override
	public synchronized void startEngine() {
		logger.debug("Start Kraken public engine");

		// Download first assets and assets pairs
		downloadAssets();
		downloadAssetPairs();

		// Schedule jobs
		long nextMidnightDelay = getNextMidnightDelay();
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleAtFixedRate(this::downloadAssets, nextMidnightDelay, configPublic.getCallAssetsSecondsFrequency(), TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::downloadAssetPairs, nextMidnightDelay, configPublic.getCallAssetPairsSecondsFrequency(), TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::downloadTickers, 10, configPublic.getCallTickersSecondsFrequency(), TimeUnit.SECONDS);

	}

	private long getNextMidnightDelay() {
		LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
		long delayMilli = Converter.localDateTimeToMillis(tomorrow) - System.currentTimeMillis();
		return (delayMilli / 1000L);
	}

	private void downloadAssets() {
		try {
			long callTime = System.currentTimeMillis();
			List<Asset> assets = krakenCaller.getAssets();
			boolean changed = dataModel.setNewAssets(assets, callTime);
			logger.info("%d assets downloaded --> %s", assets.size(), (changed ? "data changed (new valid startTime=" + callTime + ")" : "no changes"));
			
		} catch (Exception ex) {
			logger.error(ex, "Exception caught while downloading assets");
		}
	}

	private void downloadAssetPairs() {
		try {
			long callTime = System.currentTimeMillis();
			List<AssetPair> assetPairs = krakenCaller.getAssetPairs();
			boolean changed = dataModel.setNewAssetPairs(assetPairs, callTime);
			logger.info("%d asset pairs downloaded --> %s", assetPairs.size(), (changed ? "data changed (new valid startTime is " + callTime + ")" : "no changes"));

		} catch (Exception ex) {
			logger.error(ex, "Exception caught while downloading asset pairs");
		}
	}

	private void downloadTickers() {
		try {
			List<String> assetPairNames = dataModel.getAssetPairNames(true);
			long callTime = System.currentTimeMillis();
			List<Ticker> tickers = krakenCaller.getTickers(assetPairNames);
			dataModel.insertTickers(tickers, callTime);
			logger.info("%d tickers downloaded", tickers.size());

		} catch (Exception ex) {
			logger.error(ex, "Exception caught while downloading tickers");
		}
	}

}
