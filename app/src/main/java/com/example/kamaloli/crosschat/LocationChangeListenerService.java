package com.example.kamaloli.crosschat;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

public class LocationChangeListenerService extends Service {

    public LocationChangeListenerService() {
    }

    private final IBinder locationServiceBinder = new LocationChangeListenerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return locationServiceBinder;
    }

    public class LocationChangeListenerBinder extends Binder {
        LocationChangeListenerService getService() {
            return LocationChangeListenerService.this;
        }
    }

}
