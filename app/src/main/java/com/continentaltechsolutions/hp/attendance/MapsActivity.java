package com.continentaltechsolutions.hp.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.continentaltechsolutions.hp.attendance.MainActivity.areThereMockPermissionApps;
import static com.continentaltechsolutions.hp.attendance.MainActivity.isMockSettingsON;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double maplatitude,maplongitude;
    private boolean isMockON,mockpermissionapp;
    private String mapUsername,Usrname,url;
    private int Usrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent I=getIntent();
        Usrid=I.getIntExtra("usrid",0);
        Usrname=I.getStringExtra("usrname");
        url=I.getStringExtra("ip");
        isMockON = isMockSettingsON(getApplicationContext());
        mockpermissionapp=areThereMockPermissionApps(getApplicationContext());
        if (!isMockON) {
            if (!mockpermissionapp) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
            else
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
                //alertDialog.setTitle("Mock permission app enabled");
                alertDialog.setMessage("Mock permission app enabled.Kindly disable!!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MapsActivity.this.finish();
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
            //alertDialog.setTitle("Mock permission app enabled");
            alertDialog.setMessage("Mock settings enabled.Kindly disable!!");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    MapsActivity.this.finish();
                }
            });
            AlertDialog alert = alertDialog.create();
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RestLocationLogService restLocationLogService=new RestLocationLogService(url);
        restLocationLogService.getService().viewmap(new Callback<List<LocationLogMin>>() {
            @Override
            public void success(List<LocationLogMin> locationLogMins, Response response) {
                for (int i=0;i<locationLogMins.size();i++)
                {
                    maplatitude=locationLogMins.get(i).Location_Lat;
                    maplongitude=locationLogMins.get(i).Location_Long;
                    mapUsername=locationLogMins.get(i).UName.toString();
                    LatLng maplocation = new LatLng(maplatitude, maplongitude);
                    mMap.addMarker(new MarkerOptions().position(maplocation).title(mapUsername));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(maplocation));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    if (((TypedByteArray) error.getResponse().getBody()).getBytes() != null) {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes()); //Sometimes returns null. Chk reqd
                        Toast.makeText(MapsActivity.this, json, Toast.LENGTH_LONG).show();


                    } else if (error.getMessage().toString() != null) {
                        Toast.makeText(MapsActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MapsActivity.this, "Error. Please try again...", Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    Log.e("Maps ,Fail Catch:", e.toString());
                    Toast.makeText(MapsActivity.this, "Error. Please try again...", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    //network Exception is throw here
                }
            }
        });
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        //  startActivity(new Intent(ViewUserlogDetails.this, Check_in.class));
        Intent i=new Intent(getApplicationContext(),check_in.class);
        i.putExtra("usrid",Usrid);
        i.putExtra("usrname",Usrname);
        i.putExtra("ip",url);
        startActivity(i);
        finish();

    }
}
