package com.fede.ct.v2.service;

import com.fede.ct.v2.common.config.Config;
import com.fede.ct.v2.common.config.IConfig;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.util.Converter;
import com.fede.ct.v2.datalayer.ICryptoModel;
import com.fede.ct.v2.datalayer.impl.CryptoModelFactory;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;
import com.fede.ct.v2.kraken.impl.KrakenFactory;

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
public class PublicService implements IPublicService {

	private static final SimpleLog logger = LogService.getLogger(PublicService.class);
	private static final int THREAD_POOL_SIZE = 3; // assets, asset pairs, tickers

	private static final IPublicService instance = new PublicService();
	private static final IConfig config = Config.getUniqueInstance();

	private final IKrakenPublic krakenCaller;
	private final ICryptoModel model;

	private PublicService() {
		krakenCaller = KrakenFactory.getPublicCaller();
		model = CryptoModelFactory.getModel();
	}

	public static IPublicService getService() {
		return instance;
	}

	@Override
	public synchronized void startPublicEngine() {
		logger.debug("Start Kraken public engine");

		// Download first assets and assets pairs
		downloadAssets();
		downloadAssetPairs();

		// Schedule jobs
		long nextMidnightDelay = getNextMidnightDelay();
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
		executorService.scheduleAtFixedRate(this::downloadAssets, nextMidnightDelay, config.getCallAssetsSecondsFrequency(), TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::downloadAssetPairs, nextMidnightDelay, config.getCallAssetPairsSecondsFrequency(), TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::downloadTickers, 2, config.getCallTickersSecondsFrequency(), TimeUnit.SECONDS);

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

	private long getNextMidnightDelay() {
		LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
		long delayMilli = Converter.localDateTimeToMillis(tomorrow) - System.currentTimeMillis();
		return (delayMilli / 1000L);
	}

	private void downloadAssets() {
		try {
			long callTime = System.currentTimeMillis();
			List<Asset> assets = krakenCaller.getAssets();
			boolean changed = model.setNewAssets(assets, callTime);
			logger.info("%d assets downloaded --> %s", assets.size(), (changed ? "data changed (new valid startTime=" + callTime + ")" : "no changes"));
			
		} catch (KrakenCallError | KrakenException ex) {
			logger.error(ex.getMessage());
		}
	}

	private void downloadAssetPairs() {
		try {
			long callTime = System.currentTimeMillis();
			List<AssetPair> assetPairs = krakenCaller.getAssetPairs();
			boolean changed = model.setNewAssetPairs(assetPairs, callTime);
			logger.info("%d asset pairs downloaded --> %s", assetPairs.size(), (changed ? "data changed (new valid startTime is " + callTime + ")" : "no changes"));

		} catch (KrakenCallError | KrakenException ex) {
			logger.error(ex.getMessage());
		}
	}

	private void downloadTickers() {
		try {
			List<String> assetPairNames = model.getAssetPairNames(true);
			long callTime = System.currentTimeMillis();
			List<Ticker> tickers = krakenCaller.getTickers(assetPairNames);
			model.insertTickers(tickers, callTime);
			logger.info("%d tickers downloaded", tickers.size());

		} catch (KrakenCallError | KrakenException ex) {
			logger.error(ex.getMessage());
		}
	}




}
