package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.model._public.Asset;

import java.util.Collection;
import java.util.List;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IAssetsDao {

	List<Asset> selectAssets();

	void insertNewAssets(Collection<Asset> assets, long callTime);

}
