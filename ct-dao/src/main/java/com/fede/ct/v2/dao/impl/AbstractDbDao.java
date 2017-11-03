package com.fede.ct.v2.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by f.barbano on 02/11/2017.
 */
abstract class AbstractDbDao {

	private final Connection connection;

	public AbstractDbDao(Connection connection) {
		this.connection = connection;
	}

	protected int performUpdate(String query, Object... params) throws SQLException {

		try (PreparedStatement ps = createPreparedStatement(query)){
			if(params != null) {
				int idx = 1;
				for (Object param : params) {
					if (param instanceof Long) ps.setLong(idx, (Long) param);
					else if (param instanceof Integer) 	ps.setInt(idx, (Integer) param);
					else if (param instanceof Double) 	ps.setDouble(idx, (Double) param);
					else if (param instanceof BigDecimal) ps.setBigDecimal(idx, (BigDecimal) param);
					else ps.setString(idx, (String) param);
					idx++;
				}
			}

			int num = ps.executeUpdate();
			// review log
			return num;
		}
	}

	protected synchronized PreparedStatement createPreparedStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}
}