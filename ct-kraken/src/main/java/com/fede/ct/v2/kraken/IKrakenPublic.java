package com.fede.ct.v2.kraken;

import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;

import java.util.Collection;
import java.util.List;

/**
 * Created by f.barbano on 03/11/2017.
 */
public interface IKrakenPublic {

	List<Asset> getAssets() throws KrakenCallError, KrakenException;

	List<AssetPair> getAssetPairs() throws KrakenCallError, KrakenException;

	List<Ticker> getTickers(Collection<String> pairNames) throws KrakenCallError, KrakenException;

}
