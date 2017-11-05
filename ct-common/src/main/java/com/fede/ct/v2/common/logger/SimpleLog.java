package com.fede.ct.v2.common.logger;

/**
 * Created by f.barbano on 01/10/2017.
 */
public interface SimpleLog {

	void error(Throwable t);
	void error(String mex, Object... params);
	void error(Throwable t, String mex, Object... params);
	void warning(Throwable t);
	void warning(String mex, Object... params);
	void warning(Throwable t, String mex, Object... params);
	void info(String mex, Object... params);
	void debug(String mex, Object... params);
	void config(String mex, Object... params);
	void fine(String mex, Object... params);
	void finer(String mex, Object... params);
	void finest(String mex, Object... params);

}
