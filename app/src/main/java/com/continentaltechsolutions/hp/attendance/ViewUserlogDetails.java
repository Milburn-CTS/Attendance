package com.continentaltechsolutions.hp.attendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ViewUserlogDetails extends AppCompatActivity {
    int UserID;
    public static EditText editTextFromDate,editTextToDate;
    private String frmdatetime,todatetime,Username,url;
    String formattedFromDate, formattedToDate;
    // String url="http://192.168.1.39:8089/";
    RestLocationLogService restLocationLogService;
    private DatePickerDialogFragment mDatePickerDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_userlog_details);
        Intent intdet = getIntent();
        UserID = intdet.getIntExtra("usrid", 0);
        Username=intdet.getStringExtra("usrname");
        url=intdet.getStringExtra("ip");
        editTextFromDate = (EditText) findViewById(R.id.edTxtFromDate);
        editTextToDate = (EditText) findViewById(R.id.edTxtToDate);
        Button searchbtn = (Button) findViewById(R.id.btnSearch);
        restLocationLogService = new RestLocationLogService(url);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        formattedToDate = df.format(c.getTime());
        editTextToDate.setText(formattedToDate);

        //To set From date
        c.add(Calendar.DATE, -70);
        formattedFromDate = df.format(c.getTime());
        editTextFromDate.setText(formattedFromDate);
        if (!isInternetOn()) {
            Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            mDatePickerDialogFragment = new DatePickerDialogFragment();
            editTextFromDate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mDatePickerDialogFragment.setFlag(ViewUserlogDetails.DatePickerDialogFragment.FLAG_START_DATE);
                    mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });

            editTextToDate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mDatePickerDialogFragment.setFlag(ViewUserlogDetails.DatePickerDialogFragment.FLAG_END_DATE);
                    mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });
            frmdatetime = editTextFromDate.getText().toString() + " 00:00:00";
            todatetime = editTextToDate.getText().toString() + " 00:00:00";
            searchbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                    ///   params.put("puserid", String.valueOf(UserID));
                    //   params.put("pFromDate", frmdatetime);//pFromDate "2016-07-01"
                    //  params.put("pToDate", todatetime);//pToDate "2016-08-07"
                    if (!isInternetOn()) {
                        Toast.makeText(getApplicationContext(), "No Internet connection detected, Please try again later", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Attendancemin attendancemin = new Attendancemin();
                        attendancemin.UserID = UserID;
                        attendancemin.FromDate = frmdatetime;
                        attendancemin.ToDate = todatetime;
                        restLocationLogService.getService().getuserlogbyid(attendancemin, new Callback<List<LocLogListView>>() {
                            @Override
                            public void success(List<LocLogListView> locLogListViews, Response response) {
                                ListView lv = (ListView) findViewById(R.id.listView);

                                AttendanceAdapter customAdapter = new AttendanceAdapter(ViewUserlogDetails.this, R.layout.attendancelistview, locLogListViews);
                                lv.setLongClickable(true);
                                lv.setAdapter(customAdapter);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                        //  lv.setAdapter(customAdapter);
                    }
                }
            });

        }}
    public static class DatePickerDialogFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        public static final int FLAG_START_DATE = 0;
        public static final int FLAG_END_DATE = 1;

        private int flag = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setFlag(int i) {
            flag = i;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (flag == FLAG_START_DATE) {
                editTextFromDate.setText(format.format(calendar.getTime()));
            } else if (flag == FLAG_END_DATE) {
                editTextToDate.setText(format.format(calendar.getTime()));
            }
        }
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
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //  startActivity(new Intent(ViewUserlogDetails.this, Check_in.class));
        Intent i=new Intent(getApplicationContext(),check_in.class);
        i.putExtra("usrid",UserID);
        i.putExtra("usrname",Username);
        i.putExtra("ip",url);
        startActivity(i);
        finish();

    }
    }

