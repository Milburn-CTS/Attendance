package com.continentaltechsolutions.hp.attendance;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by CTS on 31-05-2017.
 */

public interface UserInfoService {
    @GET("/api/User")
    public void getUser(Callback<List<UserInfo>> callback);
}
