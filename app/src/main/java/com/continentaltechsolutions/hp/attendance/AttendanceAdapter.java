package com.continentaltechsolutions.hp.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by CTS on 31-05-2017.
 */

public class AttendanceAdapter extends ArrayAdapter<LocLogListView> {
    public AttendanceAdapter(Context context, int resource, List<LocLogListView> locloglistview) {
        super(context, resource, locloglistview);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.attendancelistview, parent, false);
        }

        LocLogListView locLogListView = getItem(position);

        if (locLogListView != null) {
            try {
                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                TextView tvDate = (TextView) v.findViewById(R.id.textViewDate);
                TextView tvLocation = (TextView) v.findViewById(R.id.textViewLocation);
                TextView tvDist = (TextView) v.findViewById(R.id.textViewDistance);
                TextView tvLogid = (TextView) v.findViewById(R.id.textViewLogid);
                tvLogid.setText(Integer.toString(locLogListView.LogID));
                //Date newDate = formatDate.parse(servicereportmin.date);
                //formatDate = new SimpleDateFormat("yyyy-MM-dd");
                tvDate.setText(locLogListView.Logdate);//tvDate.setText(formatDate.format(newDate));
                tvLocation.setText(locLogListView.Location_Address);
                tvDist.setText(String.valueOf(locLogListView.Kilometers));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return v;
    }

}
