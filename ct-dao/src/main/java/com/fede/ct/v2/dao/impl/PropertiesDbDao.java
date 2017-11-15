package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.dao.IPropertiesDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class PropertiesDbDao extends AbstractDbDao2 implements IPropertiesDao {

	private static final String SELECT_PROPERTY = "SELECT PROP_VALUE FROM CT_PROPERTIES WHERE USER_ID = ? AND PROP_NAME = ?";
	private static final String SET_PROPERTY = "REPLACE INTO CT_PROPERTIES (USER_ID, PROP_NAME, PROP_VALUE) VALUES (?,?,?)";

	private static final String PROP_DOWNLOAD_ORDERS_ENABLED = "DOWNLOAD_ORDERS_ENABLED";


	public PropertiesDbDao(CryptoContext ctx) {
		super(ctx);
	}


	@Override
	public boolean isDownloadOrdersEnabled() {
		Query query = new Query(SELECT_PROPERTY, getUserCtx().getUserId(), PROP_DOWNLOAD_ORDERS_ENABLED);
		List<InquiryResult> results = super.performInquiry(query);
		boolean enabled = false;
		if(!results.isEmpty()) {
			String propValue = results.get(0).getString("PROP_VALUE");
			enabled = Boolean.valueOf(propValue);
		}
		return enabled;
	}

	@Override
	public void setDownloadOrdersEnabled(boolean enabled) {
		Query setQuery = new Query(SET_PROPERTY, getUserCtx().getUserId(), PROP_DOWNLOAD_ORDERS_ENABLED, String.valueOf(enabled));
		super.performUpdate(setQuery);
	}
}
