package com.fede.ct.v2.dao.impl;

/**
 * Created by f.barbano on 02/11/2017.
 */
// review to delete class
abstract class AbstractDbDao {
/*

	private static final SimpleLog logger = LogService.getLogger(AbstractDbDao.class);

	private final Connection connection;

	protected AbstractDbDao(Connection connection) {
		this.connection = connection;
	}

	protected synchronized int performUpdate(String query, Object... params) {
		try (PreparedStatement ps = createPreparedStatement(query, params)){
			int num = ps.executeUpdate();
			logger.fine("Executed update for [%s]: %d rows altered", query, num);
			return num;
		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing update [query=%s, params=%s]", query, Arrays.toString(params));
		}
	}

	protected synchronized int[] performUpdateBatch(String... queries) {
		try (Statement st = connection.createStatement()) {
			for(String query : queries) {
				st.addBatch(query);
			}
			int[] res = st.executeBatch();
			logger.fine("Executed update batch for [%s]", Arrays.toString(queries));
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

	protected String toJdbcString(String str) {
		if(str == null)	return null;
		return String.format("\'%s\'", str);
	}
	protected String toJdbcString(Integer num) {
		if(num == null)	return null;
		return String.format("%d", num);
	}
	protected String toJdbcString(Long num) {
		if(num == null)	return null;
		return String.format("%d", num);
	}
	protected String toJdbcString(BigDecimal bigDecimal) {
		if(bigDecimal == null)	return null;
		return OutFormat.getEnglishFormat().format(bigDecimal);
	}

*/
}