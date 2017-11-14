package com.fede.ct.v2.common.config._public;

import com.fede.ct.v2.common.config.IConfigCommon;

import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Created by f.barbano on 02/11/2017.
 */
public interface IConfigPublic extends IConfigCommon {

	// Call rate in seconds
	int getCallRateAssets();
	int getCallRateAssetPairs();
	int getCallRateTickers();

}
