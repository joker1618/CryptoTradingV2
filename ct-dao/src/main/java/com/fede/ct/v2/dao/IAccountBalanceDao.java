package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._private.AccountBalance;

import java.util.List;

/**
 * Created by f.barbano on 15/11/2017.
 */
public interface IAccountBalanceDao {


	void addAccountBalance(List<AccountBalance> accountBalanceList);

	List<AccountBalance> getAccountBalance();



}
