package com.example.kamaloli.crosschat;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.kamaloli.crosschat.ManagerForLocation.CurrentLocationOfUserListener;
import com.example.kamaloli.crosschat.SignUpComponets.LocationRegistrationFragment;
import com.example.kamaloli.crosschat.SignUpComponets.UserInfoOfSignUpFragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SignUp extends AppCompatActivity implements View.OnClickListener,
        UserInfoOfSignUpFragment.InterFragmentCommunicationSignUp {
    android.app.FragmentManager manager;
    FragmentTransaction fragmentTransaction;
    Location optimalLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fetchUserCurrentLocation();
        manager=getFragmentManager();
        fragmentTransaction=manager.beginTransaction();
        fragmentTransaction.add(R.id.activity_sign_up_id,new UserInfoOfSignUpFragment());
        fragmentTransaction.commit();

    }

    private void fetchUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SignUp.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignUp.this,new String [] { Manifest.permission.ACCESS_FINE_LOCATION ,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            ActivityCompat.requestPermissions(SignUp.this,new String []{Manifest.permission.ACCESS_COARSE_LOCATION},2);

        }else{
            Location gps, network;
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            LocationListener gpsListener = new CurrentLocationOfUserListener();
            LocationListener networkListener=new CurrentLocationOfUserListener();
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, gpsListener, null);
            gps=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,networkListener,null);
            network=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(gps==null){
                optimalLocation=network;
                Log.e("Current Location net",network.getLatitude()+" "+network.getLongitude()+"accuracy "+network.getAccuracy());
            }
            else{
                Log.e("Current Location gps",gps.getLatitude()+" "+gps.getLongitude()+"accuracy "+gps.getAccuracy());
                Log.e("Current Location net",network.getLatitude()+" "+network.getLongitude()+"accuracy "+network.getAccuracy());
                if(gps.getAccuracy()<network.getAccuracy())
                    optimalLocation=gps;
                else{
                    optimalLocation=network;
                }

            }


        }
    }

    @Override
    public void onClick(View v) {

    }
    void errorDialogDisplay(String tittle, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
        dialog.setTitle(tittle);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.create();
        dialog.show();


    }

    @Override
    public void receiveUserData(Bundle data) {
        data.putDouble("latitude",Math.toRadians(optimalLocation.getLatitude()));
        data.putDouble("longitude",Math.toRadians(optimalLocation.getLongitude()));
        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        LocationRegistrationFragment location=new LocationRegistrationFragment();
        location.setArguments(data);
        transaction.replace(R.id.activity_sign_up_id,location);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PERMISSION_GRANTED&&
                            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED){
                        Location gps, network;
                        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
                        LocationListener gpsListener = new CurrentLocationOfUserListener();
                        LocationListener networkListener=new CurrentLocationOfUserListener();
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,gpsListener,null);
                        gps=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,networkListener,null);
                        network=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(gps==null){
                            optimalLocation=network;
                            Log.e("Current Location net",network.getLatitude()+" "+network.getLongitude()+"accuracy "+network.getAccuracy());
                        }
                        else{
                            Log.e("Current Location gps",gps.getLatitude()+" "+gps.getLongitude()+"accuracy "+gps.getAccuracy());
                            Log.e("Current Location net",network.getLatitude()+" "+network.getLongitude()+"accuracy "+network.getAccuracy());
                            if(gps.getAccuracy()<network.getAccuracy())
                                optimalLocation=gps;
                            else{
                                optimalLocation=network;
                            }

                        }
                    }
                }
                break;
        }
    }

}
