package com.fede.ct.v2.dao;

import com.fede.ct.v2.common.context.UserCtx;

/**
 * Created by f.barbano on 06/11/2017.
 */
public interface IUsersDao {

	UserCtx getByUserId(int userId);

	int createNewUserId(String userName, String apiKey, String apiSecret);
}
