package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.AssetPair.FeeSchedule;
import com.fede.ct.v2.common.model._public.AssetPair.Leverage;
import com.fede.ct.v2.common.model.types.FeeType;
import com.fede.ct.v2.common.model.types.LeverageType;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IAssetPairsDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by f.barbano on 05/11/2017.
 */
public class AssetPairsDbDao2 extends AbstractDbDao2 implements IAssetPairsDao {

	private static final String SELECT_VALID_ASSET_PAIRS = "SELECT PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 ORDER BY PAIR_NAME";
	private static final String SELECT_TRADABLE_ASSET_PAIRS = "SELECT PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 AND PAIR_NAME NOT LIKE '%.d' ORDER BY PAIR_NAME";
	private static final String SELECT_VALID_FEES_PREFIX = "SELECT PAIR_ID, FEE_TYPE, VOLUME, PERCENT_FEE FROM ASSET_PAIRS_FEE WHERE PAIR_ID IN ";
	private static final String SELECT_VALID_LEVERAGES_PREFIX = "SELECT PAIR_ID, LEVERAGE_TYPE, LEVERAGE_VALUE FROM ASSET_PAIRS_LEVERAGE WHERE PAIR_ID IN ";

	private static final String UPDATE_EXPIRE_TIME = "UPDATE ASSET_PAIRS SET EXPIRE_TIME = ? WHERE EXPIRE_TIME = 0";
	private static final String SELECT_MAX_ID = "SELECT MAX(PAIR_ID) AS MAX_ID FROM ASSET_PAIRS";
	private static final String INSERT_NEW_PREFIX = "INSERT INTO ASSET_PAIRS (PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP, START_TIME, EXPIRE_TIME) VALUES ";
	private static final String INSERT_NEW_FEE_PREFIX = "INSERT INTO ASSET_PAIRS_FEE (PAIR_ID, FEE_TYPE, VOLUME, PERCENT_FEE) VALUES ";
	private static final String INSERT_NEW_LEVERAGE_PREFIX = "INSERT INTO ASSET_PAIRS_LEVERAGE (PAIR_ID, LEVERAGE_TYPE, LEVERAGE_VALUE) VALUES ";

	private static final String SELECT_VALID_NAMES = "SELECT PAIR_NAME FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 ORDER BY PAIR_NAME";
	private static final String SELECT_TRADABLE_NAMES = "SELECT PAIR_NAME FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 AND PAIR_NAME NOT LIKE '%.d' ORDER BY PAIR_NAME";


	public AssetPairsDbDao2(Connection connection) {
		super(connection);
	}

	@Override
	public List<AssetPair> selectAssetPairs(boolean onlyTradables) {

		List<AssetPair> toRet = new ArrayList<>();

		// Table ASSET_PAIRS
		String strQuery = onlyTradables ? SELECT_TRADABLE_ASSET_PAIRS : SELECT_VALID_ASSET_PAIRS;
		List<InquiryResult> res = super.performInquiry(new Query(strQuery));

		if(!res.isEmpty()) {
			// Parse result of ASSET_PAIRS inquiry
			Map<Long, AssetPair> map = new HashMap<>();
			res.forEach(ir -> parseAssetPair(ir, map));

			// Table ASSET_PAIRS_FEE
			List<Long> pairIds = new ArrayList<>(map.keySet());
			List<Query> feesQueryList = createJdbcQueries(SELECT_VALID_FEES_PREFIX, 1, pairIds.size(), pairIds, l -> l);
			for(Query query : feesQueryList) {
				List<InquiryResult> feesRes = super.performInquiry(query);
				feesRes.forEach(ir -> parseAssetPairFee(ir, map));
			}

			// Table ASSET_PAIRS_LEVERAGE
			List<Query> levQueryList = createJdbcQueries(SELECT_VALID_LEVERAGES_PREFIX, 1, pairIds.size(), pairIds, l -> l);
			for(Query query : levQueryList) {
				List<InquiryResult> levRes = super.performInquiry(query);
				levRes.forEach(ir -> parseAssetPairLeverage(ir, map));
			}

			toRet.addAll(map.values());
			toRet.sort(Comparator.comparing(AssetPair::getPairName));
		}

		return toRet;
	}

	@Override
	public List<String> selectAssetPairNames(boolean onlyTradables) {
		String strQuery = onlyTradables ? SELECT_TRADABLE_NAMES : SELECT_VALID_NAMES;
		List<InquiryResult> results = super.performInquiry(new Query(strQuery));
		return StreamUtil.map(results, ir -> parseAssetPair(ir).getPairName());
	}

	@Override
	public void insertNewAssetPairs(List<AssetPair> assetPairs, long callTime) {
		// 1. get next id
		List<InquiryResult> results = super.performInquiry(new Query(SELECT_MAX_ID));
		long nextId = 0L;
		if(!results.isEmpty()) {
			Long maxId = results.get(0).getLong("MAX_ID");
			if(maxId != null)	nextId = maxId+1;
		}

		// Set next id to asset pair and sub struct Leverage and FeeShedule
		for(AssetPair ap : assetPairs) {
			ap.setPairId(nextId);
			for(FeeSchedule fs : ap.getFees())		fs.setPairId(nextId);
			for(FeeSchedule fs : ap.getFeesMaker())	fs.setPairId(nextId);
			for(Leverage lev : ap.getLeverageBuy())		lev.setPairId(nextId);
			for(Leverage lev : ap.getLeverageSell())	lev.setPairId(nextId);
			nextId++;
		}

		List<FeeSchedule> feeList = assetPairs.stream().flatMap(ap -> {
			List<FeeSchedule> list = new ArrayList<>(ap.getFees());
			list.addAll(ap.getFeesMaker());
			return list.stream();
		}).collect(Collectors.toList());

		List<Leverage> levList = assetPairs.stream().flatMap(ap -> {
			List<Leverage> list = new ArrayList<>(ap.getLeverageBuy());
			list.addAll(ap.getLeverageSell());
			return list.stream();
		}).collect(Collectors.toList());

		// 2. query update valids
		List<Query> transactionQueries = new ArrayList<>();
		transactionQueries.add(new Query(UPDATE_EXPIRE_TIME, callTime));

		// 3. query insert asset pairs
		List<Function<AssetPair, Object>> apFuncs = getAssetPairInsFunctions(callTime);
		transactionQueries.addAll(createJdbcQueries(INSERT_NEW_PREFIX, assetPairs.size(), apFuncs.size(), assetPairs, apFuncs));

		List<Function<FeeSchedule, Object>> feeFuncs = getFeeScheduleInsFunctions();
		transactionQueries.addAll(createJdbcQueries(INSERT_NEW_FEE_PREFIX, feeList.size(), feeFuncs.size(), feeList, feeFuncs));

		List<Function<Leverage, Object>> levFuncs = getLeverageInsFunctions();
		transactionQueries.addAll(createJdbcQueries(INSERT_NEW_LEVERAGE_PREFIX, levList.size(), levFuncs.size(), levList, levFuncs));

		// 4. execute transaction
		super.performTransaction(transactionQueries);
	}

	private List<Function<AssetPair, Object>> getAssetPairInsFunctions(Long callTime) {
		List<Function<AssetPair, Object>> functions = new ArrayList<>();
		functions.add(AssetPair::getPairId);
		functions.add(AssetPair::getPairName);
		functions.add(AssetPair::getAltName);
		functions.add(AssetPair::getAClassBase);
		functions.add(AssetPair::getBase);
		functions.add(AssetPair::getAClassQuote);
		functions.add(AssetPair::getQuote);
		functions.add(AssetPair::getLot);
		functions.add(AssetPair::getPairDecimals);
		functions.add(AssetPair::getLotDecimals);
		functions.add(AssetPair::getLotMultiplier);
		functions.add(AssetPair::getFeeVolumeCurrency);
		functions.add(AssetPair::getMarginCall);
		functions.add(AssetPair::getMarginStop);
		functions.add(ap -> callTime);
		functions.add(ap -> 0L);
		return functions;
	}
	private List<Function<FeeSchedule, Object>> getFeeScheduleInsFunctions() {
		List<Function<FeeSchedule, Object>> functions = new ArrayList<>();
		functions.add(FeeSchedule::getPairId);
		functions.add(fs -> fs.getFeeType().label());
		functions.add(FeeSchedule::getVolume);
		functions.add(FeeSchedule::getPercentFee);
		return functions;
	}
	private List<Function<Leverage, Object>> getLeverageInsFunctions() {
		List<Function<Leverage, Object>> functions = new ArrayList<>();
		functions.add(Leverage::getPairId);
		functions.add(lev -> lev.getType().label());
		functions.add(Leverage::getValue);
		return functions;
	}

	private AssetPair parseAssetPair(InquiryResult res) {
		AssetPair ap = new AssetPair();
		ap.setPairId(res.getLong("PAIR_ID"));
		ap.setPairName(res.getString("PAIR_NAME"));
		ap.setAltName(res.getString("ALT_NAME"));
		ap.setAClassBase(res.getString("A_CLASS_BASE"));
		ap.setBase(res.getString("BASE"));
		ap.setAClassQuote(res.getString("A_CLASS_QUOTE"));
		ap.setQuote(res.getString("QUOTE"));
		ap.setLot(res.getString("LOT"));
		ap.setPairDecimals(res.getInteger("PAIR_DECIMALS"));
		ap.setLotDecimals(res.getInteger("LOT_DECIMALS"));
		ap.setLotMultiplier(res.getInteger("LOT_MULTIPLIER"));
		ap.setFeeVolumeCurrency(res.getString("FEE_VOLUME_CURRENCY"));
		ap.setMarginCall(res.getInteger("MARGIN_CALL"));
		ap.setMarginStop(res.getInteger("MARGIN_STOP"));
		return ap;
	}
	private void parseAssetPair(InquiryResult res, Map<Long, AssetPair> map) {
		AssetPair ap = parseAssetPair(res);
		map.put(ap.getPairId(), ap);
	}
	private void parseAssetPairFee(InquiryResult res, Map<Long, AssetPair> map) {
		Long pairId = res.getLong("PAIR_ID");
		FeeType feeType = FeeType.getByLabel(res.getString("FEE_TYPE"));
		Integer volume = res.getInteger("VOLUME");
		BigDecimal percentFee = res.getBigDecimal("PERCENT_FEE");

		AssetPair ap = map.get(pairId);
		List<FeeSchedule> list = feeType == FeeType.FEES ? ap.getFees() : ap.getFeesMaker();
		list.add(new FeeSchedule(pairId, feeType, volume, percentFee));
	}
	private void parseAssetPairLeverage(InquiryResult res, Map<Long, AssetPair> map) {
		Long pairId = res.getLong("PAIR_ID");
		LeverageType levType = LeverageType.getByLabel(res.getString("LEVERAGE_TYPE"));
		Integer levValue = res.getInteger("LEVERAGE_VALUE");

		AssetPair ap = map.get(pairId);
		List<Leverage> list = levType == LeverageType.BUY ? ap.getLeverageBuy() : ap.getLeverageSell();
		list.add(new Leverage(pairId, levType, levValue));
	}

}
