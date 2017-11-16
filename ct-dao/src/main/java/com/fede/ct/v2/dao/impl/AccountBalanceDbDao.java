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
	private static final String INSERT_NEW_VALUES = "INSERT INTO ACCOUNT_BALANCE (USER_ID, CALL_TIME, ASSET_NAME, BALANCE, VALID) VALUES ";
	private static final String SELECT_VALIDS = "SELECT CALL_TIME, ASSET_NAME, BALANCE FROM ACCOUNT_BALANCE WHERE USER_ID = ? AND VALID = 1";

	public AccountBalanceDbDao(CryptoContext ctx) {
		super(ctx);
	}

	@Override
	public void addAccountBalance(List<AccountBalance> accountBalanceList) {
		Query updateQuery = new Query(INVALITE_UPDATE);
		List<Query> queries = super.createJdbcQueries(INSERT_NEW_VALUES, accountBalanceList.size(), 5, accountBalanceList, getInsertFunctions());
		queries.add(0, updateQuery);
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

	private List<Function<AccountBalance, Object>> getInsertFunctions() {
		List<Function<AccountBalance, Object>> functions = new ArrayList<>();
		functions.add(ab -> getUserCtx().getUserId());
		functions.add(AccountBalance::getCallTime);
		functions.add(AccountBalance::getAssetName);
		functions.add(AccountBalance::getBalance);
		functions.add(ab -> 1);
		return functions;
	}
}
