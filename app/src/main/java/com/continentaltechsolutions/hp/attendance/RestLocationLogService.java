package com.continentaltechsolutions.hp.attendance;

import android.util.Log;

/**
 * Created by CTS on 31-05-2017.
 */

public class RestLocationLogService {
    private retrofit.RestAdapter restAdapter;
    private LocationLogService apiService;
    private static final String TAG = RestLocationLogService.class.getSimpleName();

    public RestLocationLogService(String URL) {
        try{
            restAdapter = new retrofit.RestAdapter.Builder()
                    .setEndpoint(URL)
                    .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                    .build();

            apiService = restAdapter.create(LocationLogService.class);
        }
        catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public LocationLogService getService() {
        return apiService;
    }

}
