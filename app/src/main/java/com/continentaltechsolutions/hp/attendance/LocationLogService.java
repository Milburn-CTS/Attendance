package com.continentaltechsolutions.hp.attendance;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by CTS on 31-05-2017.
 */

public interface LocationLogService {
    @POST("/api/LocationLog")
    public void addLocationLog(@Body LocationLog locationlog, Callback<LocationLog> callback);

    @GET("/api/LocationLog/{id}")
    public void getCordinates(@Path("id") Integer id, Callback<List<LocationLog>> callback);


    @POST("/api/LocationLog/PostUserLogDetailsByID/")
    public void getuserlogbyid(@Body Attendancemin attendancemin,Callback<List<LocLogListView>>callback);

    @GET("/api/LocationLog")
    public void viewmap(Callback<List<LocationLogMin>> callback);
}
