package com.continentaltechsolutions.hp.attendance;

/**
 * Created by CTS on 31-05-2017.
 */

public class RestLoginInfoService {
    private retrofit.RestAdapter restAdapter;
    private LoginInfoService apiService;
    public RestLoginInfoService(String URL)
    {
        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        apiService = restAdapter.create(LoginInfoService.class);
    }

    public LoginInfoService getService()
    {
        return apiService;
    }
}
