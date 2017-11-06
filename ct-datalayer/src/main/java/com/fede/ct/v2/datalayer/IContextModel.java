package com.fede.ct.v2.datalayer;

/**
 * Created by f.barbano on 06/11/2017.
 */
public interface IContextModel {

	int getUserId();

	boolean isDownloadOrdersEnabled();
	void setDownloadOrdersEnabled(boolean enabled);
}
