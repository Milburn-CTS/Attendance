package com.continentaltechsolutions.hp.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class details_ofvisit extends AppCompatActivity {
    private  int Usrid,ltagid;
    private String Usrname,address,UsrdetDate,url;
    private TrackGPS gps;
    double longitude,latitude,dblongitude,dblatitude;
    private String Deviceid = null;
    private double distance,distanceinkm;
    private LocationLog locationlog;
    private boolean isMockON,mockpermissionapp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_ofvisit);
        isMockON = isMockSettingsON(getApplicationContext());
        mockpermissionapp=areThereMockPermissionApps(getApplicationContext());
        if (!isMockON) {
            if (!mockpermissionapp) {
                //  SharedPreferences prefs=getSharedPreferences("LogDeatils",context.MODE_PRIVATE);
                //   dblatitude= prefs.getLong("dblat",0);
                // dblongitude=prefs.getLong("dblong",0);
                //  ltagid=prefs.getInt("LocTag",0);
                final EditText UsrLocDetails=(EditText)findViewById(R.id.LocationDet);
                final EditText UsrPurpose=(EditText)findViewById(R.id.purpose);
                Deviceid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                UsrdetDate = df.format(c.getTime());
                Intent I=getIntent();
                Usrid=I.getIntExtra("usrid",0);
                Usrname=I.getStringExtra("usrname");
                url=I.getStringExtra("ip");
                dblatitude=I.getDoubleExtra("dblat",0);
                Log.e("latfrom db",String.valueOf(dblatitude));
                dblongitude=I.getDoubleExtra("dblong",0);
                Log.e("long from db ",String.valueOf(longitude));
                ltagid=I.getIntExtra("ltagid",0);
                Button Submit=(Button) findViewById(R.id.submitbtn);
                Submit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        gps = new TrackGPS(details_ofvisit.this);
                        if (gps.canGetLocation()) {
                            RestLocationLogService restLocationLogService= new RestLocationLogService(url);
                            longitude = fourdecimalplaces(gps.getLongitude());
                            Log.e("long",String.valueOf(longitude));
                            latitude = fourdecimalplaces(gps.getLatitude());
                            Log.e("lat",String.valueOf(latitude));
                            address = getCompleteAddressString(latitude, longitude);
                            if (ltagid!=2) {
                                LatLng from = new LatLng(dblatitude,dblongitude);
                                LatLng to = new LatLng(latitude,longitude);

                                //Calculating the distance in meters
                                distance = SphericalUtil.computeDistanceBetween(from, to);
                                Log.e("distance in loc",String.valueOf(distance));
                                distanceinkm = distance/1000;
                                Log.e("distance in km in loc",String.valueOf(distance));
                            }
                            else
                            {
                                distanceinkm=0;
                            }
                            locationlog = AddtoLocationclass.Add(Usrid, Usrname, Deviceid, UsrdetDate, latitude, longitude, address, 3, UsrLocDetails.getText().toString(), UsrPurpose.getText().toString(), distanceinkm);
                            restLocationLogService.getService().addLocationLog(locationlog, new Callback<LocationLog>() {

                                public void success(LocationLog LocationLg, Response response) {
                                    Toast.makeText(getApplicationContext(), "Data Added Successfully to Database", Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(getApplicationContext(),check_in.class);
                                    i.putExtra("usrid",Usrid);
                                    i.putExtra("usrname",Usrname);
                                    i.putExtra("ip",url);
                                    startActivity(i);
                                    finish();
                                }

                                public void failure(RetrofitError error) {
                                    try {
                                        if (((TypedByteArray) error.getResponse().getBody()).getBytes() != null) {
                                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes()); //Sometimes returns null. Chk reqd
                                            Toast.makeText(details_ofvisit.this, json, Toast.LENGTH_LONG).show();


                                        } else if (error.getMessage().toString() != null) {
                                            Toast.makeText(details_ofvisit.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(details_ofvisit.this, "Error. Please try again...", Toast.LENGTH_LONG).show();

                                        }
                                    } catch (Exception e) {
                                        Log.e("Data added ,Fail Catch:", e.toString());
                                        Toast.makeText(details_ofvisit.this, "Error. Please try again...", Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                        //network Exception is throw here
                                    }
                                }

                            });
                        }
                        else {

                            gps.showSettingsAlert();
                        }
                    }
                });
            }
            else
            {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(details_ofvisit.this);
                //alertDialog.setTitle("Mock permission app enabled");
                alertDialog.setMessage("Mock permission app enabled.Kindly disable!!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        details_ofvisit.this.finish();
                    }
                });
                android.app.AlertDialog alert = alertDialog.create();
                alert.show();
            }
        }
        else
        {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(details_ofvisit.this);
            //alertDialog.setTitle("Mock permission app enabled");
            alertDialog.setMessage("Mock settings enabled.Kindly disable!!");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    details_ofvisit.this.finish();
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
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent i=new Intent(getApplicationContext(),check_in.class);
        i.putExtra("usrid",Usrid);
        i.putExtra("usrname",Usrname);
        i.putExtra("ip",url);
        startActivity(i);
        finish();

    }
    public final double fourdecimalplaces(double value)
    {
        DecimalFormat newFormat= new DecimalFormat("#.####");
        double twoDecimal =  Double.valueOf(newFormat.format(value));
        return twoDecimal;
    }
    }

