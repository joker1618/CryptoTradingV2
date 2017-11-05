package com.fede.ct.v2.kraken.impl;

import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.AssetPair.FeeSchedule;
import com.fede.ct.v2.common.model._public.Ticker;
import com.fede.ct.v2.common.model._public.Ticker.TickerPrice;
import com.fede.ct.v2.common.model._public.Ticker.TickerVolume;
import com.fede.ct.v2.common.model._public.Ticker.TickerWholePrice;
import com.fede.ct.v2.common.util.Converter;
import com.fede.ct.v2.common.util.StreamUtil;

import javax.json.*;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by f.barbano on 05/11/2017.
 */
class JsonToModel {

	private List<String> errors;
	private JsonObject result;

	JsonToModel(String jsonString) {
		JsonReader reader = Json.createReader(new StringReader(jsonString));
		JsonObject jsonObject = reader.readObject();
		reader.close();

		this.errors = getArrayString(jsonObject, "error");
		if(errors.isEmpty()) {
			this.result = jsonObject.getJsonObject("result");
		}
	}

	public boolean containsErrors() {
		return !errors.isEmpty();
	}

	public List<String> getErrors() {
		return errors;
	}


	public List<Asset> parseAssets() {
		List<Asset> assetList = new ArrayList<>();
		for(Map.Entry<String, JsonValue> entry : result.entrySet()) {
			JsonObject jsonAsset = entry.getValue().asJsonObject();
			Asset asset = new Asset();
			asset.setAssetName(entry.getKey());
			asset.setAClass(getString(jsonAsset, "aclass"));
			asset.setAltName(getString(jsonAsset, "altname"));
			asset.setDecimals(getInt(jsonAsset, "decimals"));
			asset.setDisplayDecimals(getInt(jsonAsset, "display_decimals"));
			assetList.add(asset);
		}
		assetList.sort(Comparator.comparing(Asset::getAssetName));
		return assetList;
	}

	public List<AssetPair> parseAssetPairs() {
		List<AssetPair> assetPairs = new ArrayList<>();
		for(Map.Entry<String, JsonValue> entry : result.entrySet()) {
			AssetPair pair = new AssetPair();
			JsonObject jsonPair = entry.getValue().asJsonObject();
			pair.setPairName(entry.getKey());
			pair.setAltName(getString(jsonPair, "altname"));
			pair.setAClassBase(getString(jsonPair, "aclass_base"));
			pair.setBase(getString(jsonPair, "base"));
			pair.setAClassQuote(getString(jsonPair, "aclass_quote"));
			pair.setQuote(getString(jsonPair, "quote"));
			pair.setLot(getString(jsonPair, "lot"));
			pair.setPairDecimals(getInt(jsonPair, "pair_decimals"));
			pair.setLotDecimals(getInt(jsonPair, "lot_decimals"));
			pair.setLotMultiplier(getInt(jsonPair, "lot_multiplier"));
			pair.setLeverageBuy(getArrayInt(jsonPair, "leverage_buy"));
			pair.setLeverageSell(getArrayInt(jsonPair, "leverage_sell"));
			pair.setFees(parseJsonFeeScheduleArray(jsonPair, "fees"));
			pair.setFeesMaker(parseJsonFeeScheduleArray(jsonPair, "fees_maker"));
			pair.setFeeVolumeCurrency(getString(jsonPair, "fee_volume_currency"));
			pair.setMarginCall(getInt(jsonPair, "margin_call"));
			pair.setMarginStop(getInt(jsonPair, "margin_stop"));
			assetPairs.add(pair);
		}
		Collections.sort(assetPairs, Comparator.comparing(AssetPair::getPairName));
		return assetPairs;
	}

	public List<Ticker> parseTickers(long callTime) {
		List<Ticker> toRet = new ArrayList<>();
		for(Map.Entry<String, JsonValue> entry : result.entrySet()) {
			JsonObject jt = entry.getValue().asJsonObject();
			Ticker ticker = new Ticker();
			ticker.setCallTime(callTime);
			ticker.setPairName(entry.getKey());
			ticker.setAsk(parseTickerWholePrice(jt, "a"));
			ticker.setBid(parseTickerWholePrice(jt, "b"));
			ticker.setLastTradeClosed(parseTickerPrice(jt, "c"));
			ticker.setVolume(parseTickerVolume(jt, "v"));
			ticker.setWeightedAverageVolume(parseTickerVolume(jt, "p"));
			ticker.setTradesNumber(parseTickerVolume(jt, "t"));
			ticker.setLow(parseTickerVolume(jt, "l"));
			ticker.setHigh(parseTickerVolume(jt, "h"));
			ticker.setOpeningPrice(getBigDecimal(jt, "o"));
			toRet.add(ticker);
		}
		toRet.sort(Comparator.comparing(Ticker::getPairName));
		return toRet;
	}


	private List<FeeSchedule> parseJsonFeeScheduleArray(JsonObject jsonObj, String key) {
		List<FeeSchedule> toRet = new ArrayList<>();
		JsonArray jsonArray = jsonObj.getJsonArray(key);
		if(jsonArray != null) {
			jsonArray.forEach(jv -> {
				int vol = jv.asJsonArray().getInt(0);
				BigDecimal perc = jv.asJsonArray().getJsonNumber(1).bigDecimalValue();
				toRet.add(new FeeSchedule(vol, perc));
			});
		}
		Collections.sort(toRet, Comparator.comparingInt(FeeSchedule::getVolume));
		return toRet;
	}
	
	private TickerPrice parseTickerPrice(JsonObject jsonObj, String key) {
		List<String> values = getArrayString(jsonObj, key);
		TickerPrice tp = new TickerPrice();
		tp.setPrice(Converter.stringToBigDecimal(values.get(0)));
		tp.setLotVolume(Converter.stringToBigDecimal(values.get(1)));
		return tp;
	}
	private TickerWholePrice parseTickerWholePrice(JsonObject jsonObj, String key) {
		List<String> values = getArrayString(jsonObj, key);
		TickerWholePrice twp = new TickerWholePrice();
		twp.setPrice(Converter.stringToBigDecimal(values.get(0)));
		twp.setWholeLotVolume(Integer.parseInt(values.get(1)));
		twp.setLotVolume(Converter.stringToBigDecimal(values.get(2)));
		return twp;
	}
	private TickerVolume parseTickerVolume(JsonObject jsonObj, String key) {
		List<String> values = getArrayString(jsonObj, key);
		TickerVolume tv = new TickerVolume();
		tv.setToday(Converter.stringToBigDecimal(values.get(0)));
		tv.setLast24Hours(Converter.stringToBigDecimal(values.get(1)));
		return tv;
	}


	private String jsonValueToString(JsonValue jv) {
		if(jv.getValueType() == JsonValue.ValueType.STRING) {
			return ((JsonString)jv).getString();
		} else {
			return jv.toString();
		}
	}
	private String getStringValue(JsonObject jsonObject, String key) {
		JsonValue jv = jsonObject.get(key);
		String value = null;
		if(jv != null && !jsonObject.isNull(key)) {
			value = jsonValueToString(jv);
		}
		return value;
	}
	private String getString(JsonObject jsonObject, String key) {
		JsonValue jv = jsonObject.get(key);
		if(jv != null && !jsonObject.isNull(key)) {
			if(jv.getValueType() == JsonValue.ValueType.STRING) {
				return ((JsonString)jv).getString();
			}
		}
		return null;
	}
	private BigDecimal getBigDecimal(JsonObject jsonObject, String key) {
		String value = getStringValue(jsonObject, key);
		if(value == null) 	return null;
		return Converter.stringToBigDecimal(value);
	}
	private Double getDouble(JsonObject jsonObject, String key) {
		String value = getStringValue(jsonObject, key);
		if(value == null) 	return null;
		return Double.parseDouble(value);
	}
	private Integer getInt(JsonObject jsonObject, String key) {
		String value = getStringValue(jsonObject, key);
		if(value == null) 	return null;
		return Integer.parseInt(value);
	}
	private List<String> getArrayString(JsonObject jObj, String key) {
		List<String> toRet = new ArrayList<>();
		JsonArray jsonArr = jObj.getJsonArray(key);
		if(jsonArr != null) {
			jsonArr.forEach(jv -> toRet.add(jsonValueToString(jv)));
		}
		return toRet;
	}
	private List<Integer> getArrayInt(JsonObject jObj, String key) {
		List<Integer> intList = StreamUtil.map(getArrayString(jObj, key), Integer::parseInt);
		Collections.sort(intList);
		return intList;
	}
}
