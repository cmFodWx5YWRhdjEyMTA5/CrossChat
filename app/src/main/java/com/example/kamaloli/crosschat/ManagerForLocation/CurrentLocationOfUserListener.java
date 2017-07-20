package com.example.kamaloli.crosschat.ManagerForLocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.example.kamaloli.crosschat.HomeActivity;

/**
 * Created by KAMAL OLI on 10/03/2017.
 */

public class CurrentLocationOfUserListener implements LocationListener {
    public CurrentLocationOfUserListener(){}
    @Override
    public void onLocationChanged(Location location) {
       Log.e("location",location.getLatitude()+" "+location.getLongitude());
        //((HomeActivity)c).notifyLocationChanged("Home Lat="+location.getLatitude(),"Home Lon="+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("onStatusChanged","provider changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("Provider enabled or not",provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("Provider disabled not",provider);
    }

}
