package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.model._public.AssetPair.FeeSchedule;
import com.fede.ct.v2.common.model._public.AssetPair.Leverage;
import com.fede.ct.v2.common.model.types.FeeType;
import com.fede.ct.v2.common.model.types.LeverageType;
import com.fede.ct.v2.common.util.OutFmt;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IAssetPairsDao;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by f.barbano on 05/11/2017.
 */
public class AssetPairsDbDao extends AbstractDbDao implements IAssetPairsDao {

	private static final SimpleLog logger = LogService.getLogger(AssetPairsDbDao.class);

	private static final String SELECT_VALID_ASSET_PAIRS = "SELECT PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 ORDER BY PAIR_NAME";
	private static final String SELECT_TRADABLE_ASSET_PAIRS = "SELECT PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 AND PAIR_NAME NOT LIKE '%.d' ORDER BY PAIR_NAME";
	private static final String SELECT_VALID_FEES = "SELECT PAIR_ID, FEE_TYPE, VOLUME, PERCENT_FEE FROM ASSET_PAIRS_FEE WHERE PAIR_ID = ?";
	private static final String SELECT_VALID_LEVERAGES = "SELECT PAIR_ID, LEVERAGE_TYPE, LEVERAGE_VALUE FROM ASSET_PAIRS_LEVERAGE WHERE PAIR_ID = ?";

	private static final String UPDATE_EXPIRE_TIME = "UPDATE ASSET_PAIRS SET EXPIRE_TIME = ? WHERE EXPIRE_TIME = 0";
	private static final String SELECT_MAX_ID = "SELECT MAX(PAIR_ID) AS MAX_ID FROM ASSET_PAIRS";
	private static final String INSERT_NEW = "INSERT INTO ASSET_PAIRS (PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP, START_TIME, EXPIRE_TIME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_NEW_FEE = "INSERT INTO ASSET_PAIRS_FEE (PAIR_ID, FEE_TYPE, VOLUME, PERCENT_FEE) VALUES (?,?,?,?)";
	private static final String INSERT_NEW_LEVERAGE = "INSERT INTO ASSET_PAIRS_LEVERAGE (PAIR_ID, LEVERAGE_TYPE, LEVERAGE_VALUE) VALUES (?,?,?)";

	private static final String SELECT_VALID_NAMES = "SELECT PAIR_NAME FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 ORDER BY PAIR_NAME";
	private static final String SELECT_TRADABLE_NAMES = "SELECT PAIR_NAME FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 AND PAIR_NAME NOT LIKE '%.d' ORDER BY PAIR_NAME";


	public AssetPairsDbDao(CryptoContext ctx) {
		super(ctx);
	}

	@Override
	public List<AssetPair> selectAssetPairs(boolean onlyTradables) {
		long start = System.currentTimeMillis();
		List<AssetPair> toRet = new ArrayList<>();

		// Table ASSET_PAIRS
		String strQuery = onlyTradables ? SELECT_TRADABLE_ASSET_PAIRS : SELECT_VALID_ASSET_PAIRS;
		List<InquiryResult> res = super.performInquiry(new Query(strQuery));

		if(!res.isEmpty()) {
			// Parse result of ASSET_PAIRS inquiry
			List<AssetPair> assetPairs = StreamUtil.map(res, this::parseAssetPair);
			for(AssetPair ap : assetPairs) {
				// add fees
				List<InquiryResult> results = super.performInquiry(new Query(SELECT_VALID_FEES, ap.getPairId()));
				List<FeeSchedule> fees = StreamUtil.map(results, this::parseAssetPairFee);
				fees.forEach(fee -> { if(fee.getFeeType() == FeeType.FEES) { ap.getFees().add(fee); } else { ap.getFeesMaker().add(fee); }} );
				// add leverages
				results = super.performInquiry(new Query(SELECT_VALID_LEVERAGES, ap.getPairId()));
				List<Leverage> leverages = StreamUtil.map(results, this::parseAssetPairLeverage);
				leverages.forEach(lev -> { if(lev.getType() == LeverageType.BUY) { ap.getLeverageBuy().add(lev); } else { ap.getLeverageSell().add(lev); }} );
			}

			toRet.addAll(assetPairs);
			toRet.sort(Comparator.comparing(AssetPair::getPairName));
		}

		logger.debug("Elapsed select asset pairs: %s", OutFmt.printElapsed(start, System.currentTimeMillis(), true));
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
		long start = System.currentTimeMillis();

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
		List<Query> insNew = StreamUtil.map(assetPairs, ap -> createQueryIns(ap, callTime));
		transactionQueries.addAll(insNew);

		List<Query> insFees = new ArrayList<>();
		assetPairs.forEach(ap -> insFees.addAll(createQueryInsFee(ap)));
		transactionQueries.addAll(insFees);

		List<Query> insLeveragess = new ArrayList<>();
		assetPairs.forEach(ap -> insLeveragess.addAll(createQueryInsLeverage(ap)));
		transactionQueries.addAll(insLeveragess);

		// 4. execute transaction
		super.performTransaction(transactionQueries);

		logger.debug("ins elapsed = %s", OutFmt.printElapsed(start, System.currentTimeMillis(), true));
	}

	private Query createQueryIns(AssetPair ap, Long callTime) {
		Query query = new Query(INSERT_NEW);
		query.addParams(ap.getPairId());
		query.addParams(ap.getPairName());
		query.addParams(ap.getAltName());
		query.addParams(ap.getAClassBase());
		query.addParams(ap.getBase());
		query.addParams(ap.getAClassQuote());
		query.addParams(ap.getQuote());
		query.addParams(ap.getLot());
		query.addParams(ap.getPairDecimals());
		query.addParams(ap.getLotDecimals());
		query.addParams(ap.getLotMultiplier());
		query.addParams(ap.getFeeVolumeCurrency());
		query.addParams(ap.getMarginCall());
		query.addParams(ap.getMarginStop());
		query.addParams(callTime);
		query.addParams(0L);
		return query;
	}
	private List<Query> createQueryInsFee(AssetPair ap) {
		List<Query> queries = new ArrayList<>();
		ap.getFees().forEach(fee -> queries.add(createQueryInsFee(fee)));
		ap.getFeesMaker().forEach(fee -> queries.add(createQueryInsFee(fee)));
		return queries;
	}
	private Query createQueryInsFee(FeeSchedule fee) {
		Query query = new Query(INSERT_NEW_FEE);
		query.addParams(fee.getPairId());
		query.addParams(fee.getFeeType().label());
		query.addParams(fee.getVolume());
		query.addParams(fee.getPercentFee());
		return query;
	}
	private List<Query> createQueryInsLeverage(AssetPair ap) {
		List<Query> queries = new ArrayList<>();
		ap.getLeverageBuy().forEach(lev -> queries.add(createQueryInsLeverage(lev)));
		ap.getLeverageSell().forEach(lev -> queries.add(createQueryInsLeverage(lev)));
		return queries;
	}
	private Query createQueryInsLeverage(Leverage lev) {
		Query query = new Query(INSERT_NEW_LEVERAGE);
		query.addParams(lev.getPairId());
		query.addParams(lev.getType().label());
		query.addParams(lev.getValue());
		return query;
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
	private FeeSchedule parseAssetPairFee(InquiryResult res) {
		Long pairId = res.getLong("PAIR_ID");
		FeeType feeType = FeeType.getByLabel(res.getString("FEE_TYPE"));
		Integer volume = res.getInteger("VOLUME");
		BigDecimal percentFee = res.getBigDecimal("PERCENT_FEE");
		return new FeeSchedule(pairId, feeType, volume, percentFee);
	}
	private Leverage parseAssetPairLeverage(InquiryResult res) {
		Long pairId = res.getLong("PAIR_ID");
		LeverageType levType = LeverageType.getByLabel(res.getString("LEVERAGE_TYPE"));
		Integer levValue = res.getInteger("LEVERAGE_VALUE");
		return new Leverage(pairId, levType, levValue);
	}

}
