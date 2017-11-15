package com.fede.ct.v2.dao.impl;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.context.UserCtx;
import com.fede.ct.v2.common.exception.TechnicalException;
import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.logger.SimpleLog;
import com.fede.ct.v2.common.util.OutFormat;
import com.fede.ct.v2.common.util.StreamUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * Created by f.barbano on 02/11/2017.
 */
abstract class AbstractDbDao2 {

	private static final SimpleLog logger = LogService.getLogger(AbstractDbDao2.class);

	private static final int MAX_MARKS_NUMBER = 200; // JDBC max is 232

	private final CryptoContext ctx;
	private final Object monitorWrite;

	protected AbstractDbDao2(CryptoContext ctx) {
		this.ctx = ctx;
		this.monitorWrite = new Object();
	}


	protected List<InquiryResult> performInquiry(Query query) {
		try(PreparedStatement ps = createPreparedStatement(query);
			ResultSet rs = ps.executeQuery()) {

			List<InquiryResult> toRet = new ArrayList<>();

			if(rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();
				List<String> labels = new ArrayList<>();
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					labels.add(metaData.getColumnLabel(i+1));
				}

				while(rs.next()) {
					InquiryResult ires = new InquiryResult();
					for (String label : labels) {
						ires.addField(label, rs.getObject(label));
					}
					toRet.add(ires);
				}
			}

			return toRet;

		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing inquiry %s", query);
		}
	}

	protected int performUpdate(Query query) {
		synchronized (monitorWrite) {
			return doUpdate(query);
		}
	}

	protected void performTransaction(Query... queries) {
	    performTransaction(Arrays.asList(queries));
	}
	protected void performTransaction(List<Query> queries) {
		try {
			synchronized (monitorWrite) {
				long startTime = System.currentTimeMillis();
				logger.debug("Start batch %s", queries);
				ctx.getDbConn().setAutoCommit(false);
				for (Query query : queries) doUpdate(query);
				ctx.getDbConn().commit();
				ctx.getDbConn().setAutoCommit(true);
				long endTime = System.currentTimeMillis();
				logger.debug("End batch, elapsed=%s\t%s", OutFormat.toStringElapsed(startTime, endTime, true), queries);
			}
		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing transaction %s", queries);
		}
	}

	protected UserCtx getUserCtx() {
		return ctx == null ? null : ctx.getUserCtx();
	}

	/**
	 * JDBC allow a maximum of 232 marks ('?') in each query.
	 * This method uses 200 as a limit of marks.
	 */
	protected <T> List<Query> createJdbcQueries(String prefix, int totElem, int numElemMarks, List<T> values, List<Function<T,Object>> functions) {

		int totMarks = totElem * numElemMarks;
		if(totMarks == 0) {
			return new ArrayList<>();
		}
		
		StringBuilder sbFields = new StringBuilder();
		sbFields.append("(?");
		if(numElemMarks > 1)	sbFields.append(StringUtils.repeat(",?", numElemMarks-1));
		sbFields.append(")");
		String marksValueString = sbFields.toString();

		int subElemSize = MAX_MARKS_NUMBER / numElemMarks;

		int valuesIndex = 0;
		List<Query> queries = new ArrayList<>();
		for(int i = 0; i < totElem; i += subElemSize) {
			int effElems = Math.min(subElemSize, totElem - i);
			int effMarks = effElems * numElemMarks;
			String strValues = marksValueString + StringUtils.repeat("," + marksValueString, effElems - 1);
			Query query = new Query(String.format("%s %s", prefix, strValues));
			for(int j = 0; j < effMarks; j += functions.size(), valuesIndex++) {
				T value = values.get(valuesIndex);
				functions.forEach(f -> query.addParams(f.apply(value)));
			}
			queries.add(query);
		}

		return queries;
	}
	protected <T> List<Query> createJdbcQueries(String prefix, int totElems, int numElemFields, List<T> values, Function<T,Object>... functions) {
		return createJdbcQueries(prefix, totElems, numElemFields, values, Arrays.asList(functions));
	}


	private int doUpdate(Query query) {
		long startTime = System.currentTimeMillis();
		try(PreparedStatement ps = createPreparedStatement(query)) {
			int num = ps.executeUpdate();
			logger.config("Executed update in %s, %d rows altered %s", OutFormat.toStringElapsed(startTime, System.currentTimeMillis(), true), num, query);
			return num;
		} catch (SQLException e) {
			throw new TechnicalException(e, "Error performing update %s", query);
		}
	}

	private PreparedStatement createPreparedStatement(Query query) throws SQLException {
		PreparedStatement ps = ctx.getDbConn().prepareStatement(query.query);
		int idx = 1;
		for (Object param : query.getParamArray()) {
			if (param instanceof Long) ps.setLong(idx, (Long) param);
			else if (param instanceof Integer) 	ps.setInt(idx, (Integer) param);
			else if (param instanceof Double) 	ps.setDouble(idx, (Double) param);
			else if (param instanceof BigDecimal) ps.setBigDecimal(idx, (BigDecimal) param);
			else ps.setString(idx, (String) param);
			idx++;
		}
		return ps;
	}





	protected static class InquiryResult {
		private Map<String, Object> result = new HashMap<>();

		private void addField(String colName, Object value) {
			result.put(colName, value);
		}

		protected String getString(String colName) {
			return getValue(colName, StringUtils.EMPTY);
		}
		protected Integer getInteger(String colName) {
			return getValue(colName, NumberUtils.INTEGER_ZERO);
		}
		protected Long getLong(String colName) {
			return getValue(colName, NumberUtils.LONG_ZERO);
		}
		protected Double getDouble(String colName) {
			return getValue(colName, NumberUtils.DOUBLE_ZERO);
		}
		protected BigInteger getBigInteger(String colName) {
			return getValue(colName, BigInteger.ZERO);
		}
		protected BigDecimal getBigDecimal(String colName) {
			return getValue(colName, BigDecimal.ZERO);
		}

		private <T> T getValue(String colName, T instance) {
			Object value = result.get(colName);
			if(value == null)	return null;

			if(value.getClass() != instance.getClass()) {
				String mex = String.format("Unable to get %s: column=%s, type=%s, value=%s",
					instance.getClass().getSimpleName(), colName, value.getClass().getSimpleName(), String.valueOf(value));
				throw new IllegalArgumentException(mex);
			}

			return (T) value;
		}

		@Override
		public String toString() {
			return result.toString();
		}
	}

	protected static class Query {
		private String query;
		private List<Object> params;

		protected Query() {
			this(null);
		}
		protected Query(String query, Object... params) {
			this.query = query;
			this.params = new ArrayList<>();
			if(params.length > 0) {
				this.params.addAll(Arrays.asList(params));
			}
		}

		protected void addParams(Object... params) {
			this.params.addAll(Arrays.asList(params));
		}

		private Object[] getParamArray() {
			return params.toArray(new Object[0]);
		}

		@Override
		public String toString() {
			String strParams = params.isEmpty() ? "" : String.format(", params=%s", StreamUtil.join(params, ","));
			return String.format("[query=%s%s]", query, strParams);
		}

		protected String getQuery() {
			return query;
		}

		protected void setQuery(String query) {
			this.query = query;
		}
	}
}