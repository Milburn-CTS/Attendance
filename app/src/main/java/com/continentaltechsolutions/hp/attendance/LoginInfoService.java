package com.continentaltechsolutions.hp.attendance;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by CTS on 31-05-2017.
 */

public interface LoginInfoService {
    @POST("/api/Login")
    public void getUser(@Body LoginInfo logininfo, Callback<UserInfo> callback);
}
