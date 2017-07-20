package com.example.kamaloli.crosschat;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by KAMAL OLI on 22/03/2017.
 */

public class FragmentMyProfile extends Fragment{
    View userView;
    TextView fullName,emailAddress,address,username,highestQualification,areaOfSpecialization,interestedField,mobileNumber,city,country;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userView=inflater.inflate(R.layout.fragment_myprofile,null);
        SharedPreferences preferences=getActivity().getSharedPreferences("userCredentials",getActivity().MODE_PRIVATE);
        setHasOptionsMenu(true);
        SharedPreferences.Editor edit=preferences.edit();
        edit.putInt("rangeForMap",50);
        edit.commit();
        fullName=(TextView)userView.findViewById(R.id.full_name);
        emailAddress=(TextView)userView.findViewById(R.id.email_address);
        username=(TextView)userView.findViewById(R.id.username);
        address=(TextView)userView.findViewById(R.id.permanent_address);
        highestQualification=(TextView)userView.findViewById(R.id.highest_qualification);
        areaOfSpecialization=(TextView)userView.findViewById(R.id.area_of_specialization);
        interestedField=(TextView)userView.findViewById(R.id.interested_field);
        mobileNumber=(TextView)userView.findViewById(R.id.mobile_number);
        city=(TextView)userView.findViewById(R.id.city);
        country=(TextView)userView.findViewById(R.id.country);
        fullName.setText(preferences.getString("fullName",null));
        emailAddress.setText(preferences.getString("emailAddress",null));
        username.setText(preferences.getString("username",null));
        highestQualification.setText(preferences.getString("educationLevel",null));
        areaOfSpecialization.setText(preferences.getString("areaOfSpecialization",null));
        interestedField.setText(preferences.getString("interestedField",null));
        mobileNumber.setText(preferences.getString("mobileNumber",null));
        city.setText(preferences.getString("city",null));
        country.setText(preferences.getString("country",null));
        address.setText(preferences.getString("permanentAddress",null));

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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
