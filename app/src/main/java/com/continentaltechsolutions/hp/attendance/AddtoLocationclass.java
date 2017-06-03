package com.continentaltechsolutions.hp.attendance;

/**
 * Created by CTS on 31-05-2017.
 */

public class AddtoLocationclass {
    public static LocationLog Add(int usrid,String Usrname,String deviceid,String logDate,double latitde,double longitde,String Adress, int tagid,String Locbyusr,String Visitpurpose,double kms)
    {
        LocationLog LocLog=new LocationLog();
        LocLog.UID=usrid;
        LocLog.UName=Usrname;
        LocLog.UDeviceID=deviceid;
        LocLog.Logdate=logDate;
        LocLog.Location_Lat=latitde;
        LocLog.Location_Long=longitde;
        LocLog.Location_Address=Adress;
        LocLog.LTagID=tagid;
        LocLog.LocationByUser=Locbyusr;
        LocLog.PurposeOfVisit=Visitpurpose;
        LocLog.Kilometers=kms;
        return LocLog;
    }
}
