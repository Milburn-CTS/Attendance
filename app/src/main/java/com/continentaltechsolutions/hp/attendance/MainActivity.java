package com.continentaltechsolutions.hp.attendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MainActivity extends AppCompatActivity {
    RestLoginInfoService restLoginService;
    private boolean isMockON,mockpermissionapp;
    private String ip,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isMockON = false;//isMockSettingsON(getApplicationContext());
        mockpermissionapp = false;//areThereMockPermissionApps(getApplicationContext());
        if (!isMockON) {
            if (!mockpermissionapp) {
                final EditText Usrname = (EditText) findViewById(R.id.Username);
                final EditText passwrd = (EditText) findViewById(R.id.password);
                final EditText ipadress=(EditText)findViewById(R.id.ip);
                Button Loginbtn = (Button) findViewById(R.id.Loginbtn);
                if (!isInternetOn()) {
                    Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                    Usrname.setText("");
                    passwrd.setText("");

                }
                else
                {
                    Loginbtn.setOnClickListener(new View.OnClickListener() {
                        @Override

                        public void onClick(View v) {
                            LoginInfo logininfo = new LoginInfo();
                            logininfo.LogonName = Usrname.getText().toString();
                            logininfo.Password = passwrd.getText().toString();
                            ip=ipadress.getText().toString();
                            url = "http://"+ip+":8089/";
                            Log.e("url",url);
                            restLoginService = new RestLoginInfoService(url);
                            try {

                                restLoginService.getService().getUser(logininfo, new Callback<UserInfo>() {

                                    public void success(UserInfo usr, Response response) {
                                        Intent int1 = new Intent(getApplicationContext(), check_in.class);
                                        Log.e("usrid in response", String.valueOf(usr.UserID));
                                        //  Toast.makeText(getApplicationContext(), "usrid " + String.valueOf(usr.UserID), Toast.LENGTH_SHORT).show();
                                        int1.putExtra("usrid", usr.UserID);
                                        int1.putExtra("login", "login");
                                        int1.putExtra("usrname", usr.DisplayName);
                                        int1.putExtra("ip",url);
                                        startActivity(int1);
                                        finish();
                                    }

                                    public void failure(RetrofitError error) {
                                        try {
                                            if (((TypedByteArray) error.getResponse().getBody()).getBytes() != null) {
                                                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes()); //Sometimes returns null. Chk reqd
                                                Toast.makeText(MainActivity.this, json, Toast.LENGTH_LONG).show();
                                                Usrname.setText("");
                                                passwrd.setText("");

                                            } else if (error.getMessage().toString() != null) {
                                                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                                                Usrname.setText("");
                                                passwrd.setText("");

                                            } else {
                                                Toast.makeText(MainActivity.this, "Error. Please try again...", Toast.LENGTH_LONG).show();

                                            }
                                        } catch (Exception e) {
                                            Log.e("Login Fail Catch:", e.toString());
                                            Toast.makeText(MainActivity.this, "Error. Please try again...", Toast.LENGTH_LONG).show();
                                            Usrname.setText("");
                                            passwrd.setText("");
                                            //hideDialog();
                                            e.printStackTrace();
                                            //network Exception is throw here
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                //network Exception is throw here
                            }
                        }
                    });

                }
            }
            else
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                //alertDialog.setTitle("Mock permission app enabled");
                alertDialog.setMessage("Mock permission app enabled.Kindly disable!!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            //alertDialog.setTitle("Mock permission app enabled");
            alertDialog.setMessage("Mock settings enabled.Kindly disable!!");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    MainActivity.this.finish();
                }
            });
            AlertDialog alert = alertDialog.create();
        }
    }
    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }
    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ","" + e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
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
    public static Dialog getDialog(Context context, String title, String message, DialogType typeButtons ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        if (typeButtons == DialogType.SINGLE_BUTTON) {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                }
            });
        }

        AlertDialog alert = builder.create();

        return alert;
    }

    public enum DialogType {
        SINGLE_BUTTON

    }
    }

