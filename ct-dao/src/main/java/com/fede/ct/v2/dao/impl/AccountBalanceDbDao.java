package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.config.AbstractConfig;
import com.fede.ct.v2.dao.IAccountBalanceDao;

/**
 * Created by f.barbano on 09/11/2017.
 */
public class AccountBalanceDbDao extends AbstractConfig implements IAccountBalanceDao {


	public AccountBalanceDbDao(String configPath) {
		super(configPath);
	}
}
