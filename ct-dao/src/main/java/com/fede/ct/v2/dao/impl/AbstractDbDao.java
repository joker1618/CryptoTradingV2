package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.util.OutFormat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created by f.barbano on 02/11/2017.
 */
abstract class AbstractDbDao {

	private static final SimpleLog logger = LogService.getLogger(AbstractDbDao.class);

	private final Connection connection;

	protected AbstractDbDao(Connection connection) {
		this.connection = connection;
	}

	protected synchronized int performUpdate(String query, Object... params) {
		String finalQuery = String.format(query, params);
		try (PreparedStatement ps = connection.prepareStatement(finalQuery)){
			int num = ps.executeUpdate();
			logger.config("Executed update for [%s]: %d rows altered", finalQuery, num);
			return num;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing update [%s]", finalQuery);
		}
	}

	protected synchronized int[] performUpdateBatch(String... queries) {
		try (Statement st = connection.createStatement()){
			for(String query : queries) {
				st.addBatch(query);
			}
			int[] res = st.executeBatch();
			logger.config("Executed update batch for [%s]", Arrays.toString(queries));
			return res;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing update batch [%s]", Arrays.toString(queries));
		}
	}

	protected synchronized PreparedStatement createPreparedStatement(String query, Object... params) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(query);
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
		return ps;
	}

	protected String toJdbcString(BigDecimal bigDecimal) {
		if(bigDecimal == null)	return "-";
		return OutFormat.getEnglishFormat().format(bigDecimal);
	}
}