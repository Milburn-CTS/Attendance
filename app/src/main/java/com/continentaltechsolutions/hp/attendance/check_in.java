package com.continentaltechsolutions.hp.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.continentaltechsolutions.hp.attendance.MainActivity.areThereMockPermissionApps;
import static com.continentaltechsolutions.hp.attendance.MainActivity.isMockSettingsON;

public class check_in extends AppCompatActivity {
    private boolean isMockON,mockpermissionapp;
    private TrackGPS gps;
    double Chk_in_longitude,dblongitude;
    double Chk_in_latitude,dblatitude;
    Context context;
    private double distance,distanceinkm;
    private  int Userid,ltagid;
    private String Deviceid = null;
    private LocationLog locationlog;
    private String usrnam,url;
    private Button btnchkin,btnchkout,btnLocation,btnviewlog,btnViewmap;
    private String Chk_in_address,Chk_out_address,Username,CheckinDate;
    private int tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        isMockON = false;//isMockSettingsON(getApplicationContext());
        mockpermissionapp=false;//areThereMockPermissionApps(getApplicationContext());
        if (!isMockON) {
            if (!mockpermissionapp) {
                final TextView usrtxt = (TextView) findViewById(R.id.welcomeusr);

                final SharedPreferences.Editor editor = getSharedPreferences("LogDeatils", context.MODE_PRIVATE).edit();
                btnchkin = (Button) findViewById(R.id.chkinbtn);
                btnchkout = (Button) findViewById(R.id.chkoutbtn);
                btnLocation = (Button) findViewById(R.id.locationbtn);
                btnviewlog = (Button) findViewById(R.id.viewlogbtn);
                btnViewmap = (Button) findViewById(R.id.viewmapbtn);
                Deviceid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                //      final SharedPreferences prefstest = getSharedPreferences("LogDeatils", context.MODE_PRIVATE);
                //  Log.e("last tag id",String.valueOf(prefstest.getInt("LocTag", 0)));
                Intent i = getIntent();

             /*   Bundle extras = i.getExtras();
                if (extras.containsKey("login")) {
                    login = i.getStringExtra("login").toString();
                }
                Log.e("login",login);*/
                Username = i.getStringExtra("usrname").toString();

                Userid = i.getIntExtra("usrid", 0);
                url=i.getStringExtra("ip");
                Log.e("url",url);
                RestLocationLogService restLocLogService = new RestLocationLogService(url);
                usrtxt.setText("Welcome " + Username);

                if (!isInternetOn()) {
                    Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                    finish();

                }
                else {

                    restLocLogService.getService().getCordinates(Userid, new Callback<List<LocationLog>>() {
                        @Override
                        public void success(List<LocationLog> locationLogs, Response response) {
                     /*       editor.putLong("dblong", Double.doubleToLongBits(locationLogs.get(0).Location_Long));
                            editor.putLong("dblat", Double.doubleToLongBits(locationLogs.get(0).Location_Lat));
                            editor.putInt("LocTag", locationLogs.get(0).LTagID);
                            editor.apply();*/

                            dblatitude = locationLogs.get(0).Location_Lat;
                            dblongitude =locationLogs.get(0).Location_Long;
                            Log.e("dblat",String.valueOf(dblatitude));
                            Log.e("dblong",String.valueOf(dblongitude)); //DecimalFormat newFormat = new DecimalFormat("#.##");}
                            ltagid = locationLogs.get(0).LTagID;
                            tag=ltagid;
                            Log.e("inside sucess",String.valueOf(tag));
                            if ((ltagid == 1 )) {
                                btnchkout.setEnabled(true);
                                btnLocation.setEnabled(true);
                                btnchkin.setEnabled(false);

                            } else if(ltagid == 2)
                            {
                                btnchkin.setEnabled(true);
                                btnchkout.setEnabled(false);
                                btnLocation.setEnabled(false);
                            }
                            else if(ltagid == 3)
                            {
                                btnchkout.setEnabled(true);
                                btnLocation.setEnabled(true);
                                btnchkin.setEnabled(false);
                            }
                            else
                            {
                                btnchkin.setEnabled(true);
                                btnchkout.setEnabled(false);
                                btnLocation.setEnabled(false);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(check_in.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    //    final SharedPreferences prefs = getSharedPreferences("LogDeatils", context.MODE_PRIVATE);

                    Log.e("tag outisde sucess",String.valueOf(tag));
                    Log.e("tag",String.valueOf(ltagid));

                    Log.e("before date", "before date");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar c = Calendar.getInstance();
                    CheckinDate = df.format(c.getTime());
                    Log.e("after date", "after date");
                }
                btnchkin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isInternetOn()) {
                            Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            gps = new TrackGPS(check_in.this);


                            if (gps.canGetLocation()) {
                                LocationLog LocLog = new LocationLog();
                                RestLocationLogService restLocationLogService = new RestLocationLogService(url);
                                //Displaying the distance
                                Chk_in_longitude = fourdecimalplaces(gps.getLongitude());
                                Log.e("long",String.valueOf(Chk_in_longitude));
                                Chk_in_latitude = fourdecimalplaces(gps.getLatitude());
                                Log.e("lat",String.valueOf(Chk_in_latitude));
                                Chk_in_address = getCompleteAddressString(Chk_in_latitude, Chk_in_longitude);
                                locationlog = AddtoLocationclass.Add(Userid, Username, Deviceid, CheckinDate, Chk_in_latitude, Chk_in_longitude, Chk_in_address, 1, null, null, 0);
                                restLocationLogService.getService().addLocationLog(locationlog, new Callback<LocationLog>() {

                                    public void success(LocationLog LocationLg, Response response) {
                                        Toast.makeText(getApplicationContext(), "Data Added Successfully to Database", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(check_in.this,
                                                check_in.class);
                                        intent.putExtra("usrid", Userid);
                                        intent.putExtra("usrname", Username);
                                        intent.putExtra("ip",url);
                                        startActivity(intent);
                                    }

                                    public void failure(RetrofitError error) {
                                        try {
                                            if (((TypedByteArray) error.getResponse().getBody()).getBytes() != null) {
                                                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes()); //Sometimes returns null. Chk reqd
                                                Toast.makeText(check_in.this, json, Toast.LENGTH_LONG).show();


                                            } else if (error.getMessage().toString() != null) {
                                                Toast.makeText(check_in.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(check_in.this, "Error. Please try again...", Toast.LENGTH_LONG).show();

                                            }
                                        } catch (Exception e) {
                                            Log.e("Login Fail Catch:", e.toString());
                                            Toast.makeText(check_in.this, "Error. Please try again...", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                            //network Exception is throw here
                                        }
                                    }

                                });
                            } else {

                                gps.showSettingsAlert();


                            }
                        }
                    }
                });

                btnchkout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isInternetOn()) {
                            Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            gps = new TrackGPS(check_in.this);
                            if (gps.canGetLocation()) {
                                RestLocationLogService restLocationLogService = new RestLocationLogService(url);
                                Chk_in_longitude = fourdecimalplaces(gps.getLongitude());
                                Chk_in_latitude = fourdecimalplaces(gps.getLatitude());
                                Chk_in_address = getCompleteAddressString(Chk_in_latitude, Chk_in_longitude);
                                if (ltagid!=2) {
                                    LatLng from = new LatLng(dblatitude, dblongitude);
                                    LatLng to = new LatLng(Chk_in_latitude, Chk_in_longitude);
                                    //Calculating the distance in meters
                                    distance = SphericalUtil.computeDistanceBetween(from, to);
                                    Log.e("distance",String.valueOf(distance));
                                    distanceinkm = distance/1000;
                                    Log.e("distance in km",String.valueOf(distanceinkm));
                                }
                                else
                                    distanceinkm=0;
                                locationlog = AddtoLocationclass.Add(Userid, Username, Deviceid, CheckinDate, Chk_in_latitude, Chk_in_longitude, Chk_in_address, 2, null, null, distance);
                                restLocationLogService.getService().addLocationLog(locationlog, new Callback<LocationLog>() {

                                    public void success(LocationLog LocationLg, Response response) {
                                        // editor.clear();
                                        //  editor.commit();
                                        Intent intent = new Intent(check_in.this,
                                                MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Data Added Successfully to Database", Toast.LENGTH_SHORT).show();
                                    }

                                    public void failure(RetrofitError error) {
                                        try {
                                            if (((TypedByteArray) error.getResponse().getBody()).getBytes() != null) {
                                                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes()); //Sometimes returns null. Chk reqd
                                                Toast.makeText(check_in.this, json, Toast.LENGTH_LONG).show();


                                            } else if (error.getMessage().toString() != null) {
                                                Toast.makeText(check_in.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(check_in.this, "Error. Please try again...", Toast.LENGTH_LONG).show();

                                            }
                                        } catch (Exception e) {
                                            Log.e("Login Fail Catch:", e.toString());
                                            Toast.makeText(check_in.this, "Error. Please try again...", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                            //network Exception is throw here
                                        }
                                    }

                                });
                            } else {
                                gps.showSettingsAlert();

                            }
                        }
                    }
                });

                btnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isInternetOn()) {
                            Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Intent visitdetialsint = new Intent(getApplicationContext(), details_ofvisit.class);
                            visitdetialsint.putExtra("usrid", Userid);
                            visitdetialsint.putExtra("usrname", Username);
                            visitdetialsint.putExtra("ip",url);
                            visitdetialsint.putExtra("dblong",dblongitude);
                            visitdetialsint.putExtra("dblat",dblatitude);
                            visitdetialsint.putExtra("ltagid",ltagid);
                            startActivity(visitdetialsint);
                            finish();
                            //  LayoutInflater li = LayoutInflater.from(context.LAYOUT_INFLATER_SERVICE);
                        }

                    }
                });

                btnviewlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isInternetOn()) {
                            Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Intent viewlogint = new Intent(getApplicationContext(), ViewUserlogDetails.class);
                            viewlogint.putExtra("usrid", Userid);
                            viewlogint.putExtra("usrname", Username);
                            viewlogint.putExtra("ip",url);
                            startActivity(viewlogint);
                            finish();
                        }
                    }
                });

                btnViewmap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isInternetOn()) {
                            Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Intent viewmapint = new Intent(getApplicationContext(), MapsActivity.class);
                            viewmapint.putExtra("usrid", Userid);
                            viewmapint.putExtra("usrname", Username);
                            viewmapint.putExtra("ip",url);
                            startActivity(viewmapint);
                            finish();
                        }
                    }
                });
            }
            else
            {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(check_in.this);
                //alertDialog.setTitle("Mock permission app enabled");
                alertDialog.setMessage("Mock permission app enabled.Kindly disable!!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        check_in.this.finish();
                    }
                });
                android.app.AlertDialog alert = alertDialog.create();
                alert.show();
            }
        }
        else
        {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(check_in.this);
            //alertDialog.setTitle("Mock permission app enabled");
            alertDialog.setMessage("Mock settings enabled.Kindly disable!!");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    check_in.this.finish();
                }
            });
            android.app.AlertDialog alert = alertDialog.create();
            alert.show();
        }

    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("loction address", "" + strReturnedAddress.toString());
            } else {
                Log.e("loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("loction address", "Canont get Address!");
        }
        return strAdd;
    }
    public final boolean isInternetOn() {

        //  boolean conn=false;
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            //Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    public final double fourdecimalplaces(double value)
    {
        DecimalFormat newFormat= new DecimalFormat("#.####");
        double twoDecimal =  Double.valueOf(newFormat.format(value));
        return twoDecimal;
    }

    }

