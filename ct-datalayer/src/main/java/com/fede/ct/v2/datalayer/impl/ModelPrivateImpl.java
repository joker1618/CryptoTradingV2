package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.dao.IOrdersDao;
import com.fede.ct.v2.dao.IPropertiesDao;
import com.fede.ct.v2.datalayer.IModelPrivate;

import java.util.List;

/**
 * Created by f.barbano on 14/11/2017.
 */
public class ModelPrivateImpl implements IModelPrivate {

	private IPropertiesDao propertiesDao;
	private IOrdersDao ordersDao;

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



	
	void setPropertiesDao(IPropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}
   	void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
}
