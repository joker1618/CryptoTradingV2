package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.util.StreamUtil;
import com.fede.ct.v2.dao.IAccountBalanceDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by f.barbano on 15/11/2017.
 */
public class AccountBalanceDbDao extends AbstractDbDao implements IAccountBalanceDao {

	private static final String INVALITE_UPDATE = "UPDATE ACCOUNT_BALANCE SET VALID = 0 WHERE VALID = 1";
	private static final String INSERT_NEW = "INSERT INTO ACCOUNT_BALANCE (USER_ID, CALL_TIME, ASSET_NAME, BALANCE, VALID) VALUES (?,?,?,?,?)";
	private static final String SELECT_VALIDS = "SELECT CALL_TIME, ASSET_NAME, BALANCE FROM ACCOUNT_BALANCE WHERE USER_ID = ? AND VALID = 1";

	public AccountBalanceDbDao(CryptoContext ctx) {
		super(ctx);
	}

	@Override
	public void addAccountBalance(List<AccountBalance> accountBalanceList) {
		List<Query> queries = new ArrayList<>();
		queries.add(new Query(INVALITE_UPDATE));
		queries.addAll(StreamUtil.map(accountBalanceList, this::createaQueryIns));
		super.performTransaction(queries);
	}

	@Override
	public List<AccountBalance> getAccountBalance() {
		Query query = new Query(SELECT_VALIDS, getUserCtx().getUserId());
		List<InquiryResult> results = super.performInquiry(query);
		return StreamUtil.map(results, this::parseAccounBalance);
	}

	private AccountBalance parseAccounBalance(InquiryResult ir) {
		AccountBalance ab = new AccountBalance();
		ab.setCallTime(ir.getLong("CALL_TIME"));
		ab.setAssetName(ir.getString("ASSET_NAME"));
		ab.setBalance(ir.getBigDecimal("BALANCE"));
		return ab;
	}

	private Query createaQueryIns(AccountBalance ab) {
		Query query = new Query(INSERT_NEW);
		query.addParams(getUserCtx().getUserId());
		query.addParams(ab.getCallTime());
		query.addParams(ab.getAssetName());
		query.addParams(ab.getBalance());
		query.addParams(1);
		return query;
	}
}
