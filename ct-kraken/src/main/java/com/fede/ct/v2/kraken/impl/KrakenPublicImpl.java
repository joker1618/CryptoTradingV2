package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.kraken.IKrakenPublic;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;
import com.fede.ct.v2.kraken.impl.api.KrakenApi;
import com.fede.ct.v2.kraken.impl.api.KrakenMethod;

import java.util.Collection;
import java.util.List;

/**
 * Created by f.barbano on 04/11/2017.
 */
class KrakenPublicImpl extends AbstractKrakenCaller implements IKrakenPublic {

	KrakenPublicImpl() {
		super(new KrakenApi());
	}


	@Override
	public List<Asset> getAssets() throws KrakenCallError, KrakenException {
		JsonToModel jm = super.performKrakenCall(KrakenMethod.ASSETS);
		return jm.parseAssets();
	}

	@Override
	public List<AssetPair> getAssetPairs() throws KrakenCallError, KrakenException {
		JsonToModel jm = super.performKrakenCall(KrakenMethod.ASSET_PAIRS);
		return jm.parseAssetPairs();
	}

	@Override
	public List<Ticker> getTickers(Collection<String> pairNames) throws KrakenCallError, KrakenException {
		ApiParam pairParam = new ApiParam("pair", StreamUtil.join(pairNames, ","));
		long callTime = System.currentTimeMillis();
		JsonToModel jm = performKrakenCall(KrakenMethod.TICKER, pairParam);
		return jm.parseTickers(callTime);
	}

}
