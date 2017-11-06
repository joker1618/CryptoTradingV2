package com.fede.ct.v2.datalayer.impl;

import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.dao.IPropertiesDao;
import com.fede.ct.v2.dao.IUserIdDao;
import com.fede.ct.v2.datalayer.IContextModel;

/**
 * Created by f.barbano on 06/11/2017.
 */
class ContextModelImpl implements IContextModel {

	private static final SimpleLog logger = LogService.getLogger(ContextModelImpl.class);

	private IPropertiesDao propertiesDao;
	private IUserIdDao userIdDao;

	private Integer userId;

	@Override
	public int getUserId() {
		return userId;
	}

	@Override
	public boolean isDownloadOrdersEnabled() {
		return propertiesDao.isDownloadOrdersEnabled(userId);
	}

	@Override
	public void setDownloadOrdersEnabled(boolean enabled) {
		propertiesDao.setDownloadOrdersEnabled(userId, enabled);
	}


	void initialize(String apiKey, String apiSecret) {
		userId = userIdDao.getUserId(apiKey, apiSecret);
		logger.info("Context initialized --> user ID = %d", userId);
	}

	void setUserIdDao(IUserIdDao userIdDao) {
		this.userIdDao = userIdDao;
	}
	void setPropertiesDao(IPropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}

}
