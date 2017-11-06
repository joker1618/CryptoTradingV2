package com.fede.ct.v2.dao;

/**
 * Created by f.barbano on 03/11/2017.
 */
public interface IPropertiesDao {

	boolean isDownloadOrdersEnabled(int userId);
	void setDownloadOrdersEnabled(int userId, boolean enabled);


}
