package com.example.kamaloli.crosschat;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by KAMAL OLI on 22/03/2017.
 */

public class UserProfile extends Fragment{
    View userView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userView=inflater.inflate(R.layout.fragment_user_detail_display,null);
        SharedPreferences preferences=getActivity().getSharedPreferences("userCredentials",getActivity().MODE_PRIVATE);
        SharedPreferences.Editor edit=preferences.edit();
        edit.putInt("rangeForMap",50);
        edit.commit();
       Log.e("user info ",preferences.getString("username",null)+
        preferences.getString("password",null)+
        preferences.getString("emailAddress",null)+
        preferences.getString("token",null)+
        preferences.getString("mobileNumber",null)+
        preferences.getString("interestedField",null)+
        preferences.getString("areaOfSpecialization",null)+
        preferences.getString("city",null)+
        preferences.getString("country",null)+
        preferences.getFloat("latitude",0)+
        preferences.getFloat("longitude",0)+
        preferences.getString("fullName",null)+
        preferences.getString("educationLevel",null)+
        preferences.getString("permanentAddress",null)+
        preferences.getInt("rangeForMap",100));

        return userView;
    }
}
