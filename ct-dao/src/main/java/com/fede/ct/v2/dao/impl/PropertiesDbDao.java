package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.dao.IPropertiesDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class PropertiesDbDao extends AbstractDbDao implements IPropertiesDao {

	private static final String SELECT_PROPERTY = "SELECT PROP_VALUE FROM CT_PROPERTIES WHERE PROP_NAME = ? AND USER_ID = ?";
	private static final String SET_PROPERTY = "REPLACE INTO CT_PROPERTIES (PROP_NAME, USER_ID, PROP_VALUE) VALUES (?,?,?)";

	private static final String PROP_DOWNLOAD_ORDERS_ENABLED = "DOWNLOAD_ORDERS_ENABLED";


	public PropertiesDbDao(Connection connection) {
		super(connection);
	}


	@Override
	public boolean isDownloadOrdersEnabled(int userId) {
		String value = selectPropertyValue(PROP_DOWNLOAD_ORDERS_ENABLED, userId);
		return value == null ? false : Boolean.valueOf(value);
	}


	@Override
	public void setDownloadOrdersEnabled(int userId, boolean enabled) {
		super.performUpdate(SET_PROPERTY, PROP_DOWNLOAD_ORDERS_ENABLED, userId, String.valueOf(enabled));
	}

	private String selectPropertyValue(String propertyName, int userId) {
		try (PreparedStatement ps = createPreparedStatement(SELECT_PROPERTY, propertyName, userId);
			 ResultSet rs = ps.executeQuery()){

			if(rs != null && rs.next()) {
				return rs.getString("PROP_VALUE");
			}

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing select [query=%s, propName=%s, userId=%d]", SELECT_PROPERTY, propertyName, userId);
		}

		return null;
	}

}
