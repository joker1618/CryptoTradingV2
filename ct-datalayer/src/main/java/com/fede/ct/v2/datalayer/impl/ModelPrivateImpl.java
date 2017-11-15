package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.model._private.AccountBalance;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.dao.IAccountBalanceDao;
import com.fede.ct.v2.dao.IOrdersDao;
import com.fede.ct.v2.dao.IPropertiesDao;
import com.fede.ct.v2.datalayer.IModelPrivate;

import java.util.List;

/**
 * Created by f.barbano on 14/11/2017.
 */
class ModelPrivateImpl implements IModelPrivate {

	private IPropertiesDao propertiesDao;
	private IOrdersDao ordersDao;
	private IAccountBalanceDao accountBalanceDao;

	@Override
	public boolean isDownloadOrdersEnabled() {
		return propertiesDao.isDownloadOrdersEnabled();
	}

	@Override
	public void setDownloadOrdersEnabled(boolean enabled) {
		propertiesDao.setDownloadOrdersEnabled(enabled);
	}

	@Override
	public void updateOrders(List<OrderInfo> orders) {
		ordersDao.updateOrders(orders);
	}

	@Override
	public void addAccountBalance(List<AccountBalance> accountBalanceList) {
		accountBalanceDao.addAccountBalance(accountBalanceList);
	}


	void setPropertiesDao(IPropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}
   	void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
	void setAccountBalanceDao(IAccountBalanceDao accountBalanceDao) {
		this.accountBalanceDao = accountBalanceDao;
	}
}
