package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.kraken.exception.KrakenCallError;
import com.fede.ct.v2.kraken.exception.KrakenException;
import com.fede.ct.v2.kraken.impl.api.KrakenApi;
import com.fede.ct.v2.kraken.impl.api.KrakenMethod;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by f.barbano on 05/11/2017.
 */
abstract class AbstractKrakenCaller {

	private static final SimpleLog logger = LogService.getLogger(AbstractKrakenCaller.class);

	private final KrakenApi krakenApi;

	public AbstractKrakenCaller(KrakenApi krakenApi) {
		this.krakenApi = krakenApi;
	}

	protected JsonToModel performKrakenCall(KrakenMethod method) throws KrakenCallError, KrakenException {
		return performKrakenCall(method, Collections.emptyList());
	}
	protected JsonToModel performKrakenCall(KrakenMethod method, ApiParam... apiParams) throws KrakenCallError, KrakenException {
		return performKrakenCall(method, Arrays.asList(apiParams));
	}
	protected JsonToModel performKrakenCall(KrakenMethod method, List<ApiParam> apiParamList) throws KrakenCallError, KrakenException {
		try {
			logger.debug("Performing kraken call, method=%s, apiParams=%s", method.getName(), apiParamList);

			// convert api param list to map
			Map<String, String> apiParamMap = null;
			if(apiParamList != null && !apiParamList.isEmpty()) {
				apiParamMap = new HashMap<>();
				for(ApiParam ap : apiParamList) {
					apiParamMap.put(ap.key, ap.value);
				}
			}

			// perform kraken call
			String json = method.isPublic() ? krakenApi.queryPublic(method, apiParamMap) : krakenApi.queryPrivate(method, apiParamMap);
			logger.fine("%s json received --> %s", method.getName(), json);

			JsonToModel jm = new JsonToModel(json);
			if(jm.containsErrors()) {
				throw new KrakenCallError(method, jm.getErrors());
			}

			return jm;

		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
			throw new KrakenException(method, e);
		}
	}

	protected static final class ApiParam {
		private String key;
		private String value;

		protected ApiParam(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return String.format("[key=%s, value=%s]", key, value);
		}
	}
}
