package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.model._public.Asset;
import com.fede.ct.v2.common.model._public.AssetPair.FeeSchedule;
import com.fede.ct.v2.common.model._public.AssetPair;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IAssetPairsDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;



/**
 * Created by f.barbano on 05/11/2017.
 */
public class AssetPairsDbDao extends AbstractDbDao implements IAssetPairsDao {

	private static final String FEE_TYPE_FEES = "fees";
	private static final String FEE_TYPE_FEES_MAKER = "fees_maker";
	private static final String LEVERAGE_TYPE_BUY = "buy";
	private static final String LEVERAGE_TYPE_SELL = "sell";

	private static final String SELECT_VALIDS = "SELECT PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0";
	private static final String SELECT_VALIDS_NO_DOT_D = "SELECT PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP FROM ASSET_PAIRS WHERE EXPIRE_TIME = 0 AND PAIR_NAME NOT LIKE '%.d'";
	private static final String SELECT_VALIDS_FEE = "SELECT PAIR_ID, FEE_TYPE, VOLUME, PERCENT_FEE FROM ASSET_PAIRS_FEE WHERE PAIR_ID IN (%s)";
	private static final String SELECT_VALIDS_LEVERAGE = "SELECT PAIR_ID, LEVERAGE_TYPE, LEVERAGE_VALUE FROM ASSET_PAIRS_LEVERAGE WHERE PAIR_ID IN (%s)";

	private static final String UPDATE_EXPIRE_TIME = "UPDATE ASSET_PAIRS SET EXPIRE_TIME = %d WHERE EXPIRE_TIME = 0";
	private static final String SELECT_NEXT_ID = "SELECT MAX(PAIR_ID) AS MAX_ID FROM ASSET_PAIRS";
	private static final String INSERT_NEW_PREFIX = "INSERT INTO ASSET_PAIRS (PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP, START_TIME, EXPIRE_TIME) VALUES ";
	private static final String INSERT_NEW_FEE_PREFIX = "INSERT INTO ASSET_PAIRS_FEE (PAIR_ID, FEE_TYPE, VOLUME, PERCENT_FEE) VALUES ";
	private static final String INSERT_NEW_LEVERAGE_PREFIX = "INSERT INTO ASSET_PAIRS_LEVERAGE (PAIR_ID, LEVERAGE_TYPE, LEVERAGE_VALUE) VALUES ";


	public AssetPairsDbDao(Connection connection) {
		super(connection);
	}

	@Override
	public List<AssetPair> selectAssetPairs(boolean discardDotD) {
		Map<Long, AssetPair> map = inquiryTableAssetPairs(discardDotD);
		if(!map.isEmpty()) {
			inquiryTableAssetPairsFee(map);
			inquiryTableAssetPairsLeverage(map);
		}
		List<AssetPair> toRet = new ArrayList<>(map.values());
		toRet.sort(Comparator.comparing(AssetPair::getPairName));
		return toRet;
	}

	@Override
	public void insertNewAssetPairs(Collection<AssetPair> assetPairs, long callTime) {
		String qUpdate = String.format(UPDATE_EXPIRE_TIME, callTime);

		AtomicLong nextId = new AtomicLong(getNextId());
		List<String> apValues = new ArrayList<>();
		List<String> feeValues = new ArrayList<>();
		List<String> levValues = new ArrayList<>();
		for(AssetPair ap : assetPairs) {
			long id = nextId.getAndIncrement();
			apValues.add(assetPairToValues(id, callTime, ap));
			feeValues.add(feesToValues(id, ap));
			levValues.add(leverageToValues(id, ap));
		}
		String qInsAssetPairs = INSERT_NEW_PREFIX + StreamUtil.join(apValues, ",");
		String qInsFee = INSERT_NEW_FEE_PREFIX + StreamUtil.join(feeValues, ",");
		String qInsLev = INSERT_NEW_LEVERAGE_PREFIX + StreamUtil.join(levValues, ",");

		super.performUpdateBatch(qUpdate, qInsAssetPairs, qInsFee, qInsLev);
	}

	private long getNextId() {
		try(PreparedStatement ps = createPreparedStatement(SELECT_NEXT_ID);
			ResultSet rs = ps.executeQuery()){

			if(rs != null && rs.next()) {
				long maxId = rs.getLong("MAX_ID");
				return maxId+1;
			} else {
				return 0L;
			}

		} catch(SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", SELECT_NEXT_ID);
		}
	}

	private Map<Long, AssetPair> inquiryTableAssetPairs(boolean discardDotD) {
		String query = discardDotD ? SELECT_VALIDS_NO_DOT_D : SELECT_VALIDS;
		try (PreparedStatement ps = createPreparedStatement(query);
			 ResultSet rs = ps.executeQuery()){

			Map<Long, AssetPair> assetPairs = new HashMap<>();

			if(rs != null) {
				while(rs.next()) {
					Long pairID = rs.getLong("PAIR_ID");
					AssetPair ap = new AssetPair();
					ap.setPairName(rs.getString("PAIR_NAME"));
					ap.setAltName(rs.getString("ALT_NAME"));
					ap.setAClassBase(rs.getString("A_CLASS_BASE"));
					ap.setBase(rs.getString("BASE"));
					ap.setAClassQuote(rs.getString("A_CLASS_QUOTE"));
					ap.setQuote(rs.getString("QUOTE"));
					ap.setLot(rs.getString("LOT"));
					ap.setPairDecimals(rs.getInt("PAIR_DECIMALS"));
					ap.setLotDecimals(rs.getInt("LOT_DECIMALS"));
					ap.setLotMultiplier(rs.getInt("LOT_MULTIPLIER"));
					ap.setFeeVolumeCurrency(rs.getString("FEE_VOLUME_CURRENCY"));
					ap.setMarginCall(rs.getInt("MARGIN_CALL"));
					ap.setMarginStop(rs.getInt("MARGIN_STOP"));
					assetPairs.put(pairID, ap);
				}
			}
			return assetPairs;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", query);
		}
	}
	private void inquiryTableAssetPairsFee(Map<Long, AssetPair> assetPairs) {
		String pairIDsString = StreamUtil.join(assetPairs.keySet(), ",", String::valueOf);
		String query = String.format(SELECT_VALIDS_FEE, pairIDsString);

		try (PreparedStatement ps = createPreparedStatement(query);
			 ResultSet rs = ps.executeQuery()){

			if(rs != null) {
				while(rs.next()) {
					long pairID = rs.getLong("PAIR_ID");
					int volume = rs.getInt("VOLUME");
					BigDecimal percentFee = rs.getBigDecimal("PERCENT_FEE");
					FeeSchedule fs = new FeeSchedule(volume, percentFee);

					String feeType = rs.getString("FEE_TYPE");
					if(FEE_TYPE_FEES.equals(feeType)) {
						assetPairs.get(pairID).getFees().add(fs);
					} else {
						assetPairs.get(pairID).getFeesMaker().add(fs);
					}
				}
			}

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", query);
		}
	}
	private void inquiryTableAssetPairsLeverage(Map<Long, AssetPair> assetPairs) {
		String pairIDsString = StreamUtil.join(assetPairs.keySet(), ",", String::valueOf);
		String query = String.format(SELECT_VALIDS_LEVERAGE, pairIDsString);

		try (PreparedStatement ps = createPreparedStatement(query);
			 ResultSet rs = ps.executeQuery()){

			if(rs != null) {
				while(rs.next()) {
					long pairID = rs.getLong("PAIR_ID");
					int volume = rs.getInt("LEVERAGE_VALUE");
					String leverageType = rs.getString("LEVERAGE_TYPE");
					if(LEVERAGE_TYPE_BUY.equals(leverageType)) {
						assetPairs.get(pairID).getLeverageBuy().add(volume);
					} else {
						assetPairs.get(pairID).getLeverageSell().add(volume);
					}
				}
			}

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s]", query);
		}
	}


	private String assetPairToValues(long id, Long callTime, AssetPair assetPair) {
		//PAIR_ID, PAIR_NAME, ALT_NAME, A_CLASS_BASE, BASE, A_CLASS_QUOTE, QUOTE, LOT, PAIR_DECIMALS, LOT_DECIMALS, LOT_MULTIPLIER, FEE_VOLUME_CURRENCY, MARGIN_CALL, MARGIN_STOP
		return String.format("(%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, %d, %d, '%s', %d, %d, %d, 0)",
			id,
			assetPair.getPairName(),
			assetPair.getAltName(),
			assetPair.getAClassBase(),
			assetPair.getBase(),
			assetPair.getAClassQuote(),
			assetPair.getQuote(),
			assetPair.getLot(),
			assetPair.getPairDecimals(),
			assetPair.getLotDecimals(),
			assetPair.getLotMultiplier(),
			assetPair.getFeeVolumeCurrency(),
			assetPair.getMarginCall(),
			assetPair.getMarginStop(),
			callTime
		);
	}
	private String feesToValues(Long pairID, AssetPair assetPair) {
		StringBuilder sb = new StringBuilder();
		for(FeeSchedule fee : assetPair.getFees()) {
			if(sb.length() > 0)		sb.append(",");
			String val = String.format("(%d, '%s', %d, %s)", pairID, FEE_TYPE_FEES, fee.getVolume(), toJdbcString(fee.getPercentFee()));
			sb.append(val);
		}
		for(FeeSchedule fee : assetPair.getFeesMaker()) {
			if(sb.length() > 0)		sb.append(",");
			String val = String.format("(%d, '%s', %d, %s)", pairID, FEE_TYPE_FEES_MAKER, fee.getVolume(), toJdbcString(fee.getPercentFee()));
			sb.append(val);
		}
		return sb.toString();
	}
	private String leverageToValues(Long pairID, AssetPair assetPair) {
		StringBuilder sb = new StringBuilder();
		for(Integer lev : assetPair.getLeverageBuy()) {
			if(sb.length() > 0)		sb.append(",");
			String val = String.format("(%d, '%s', %d)", pairID, LEVERAGE_TYPE_BUY, lev);
			sb.append(val);
		}
		for(Integer lev : assetPair.getLeverageSell()) {
			if(sb.length() > 0)		sb.append(",");
			String val = String.format("(%d, '%s', %d)", pairID, LEVERAGE_TYPE_SELL, lev);
			sb.append(val);
		}
		return sb.toString();
	}
}
