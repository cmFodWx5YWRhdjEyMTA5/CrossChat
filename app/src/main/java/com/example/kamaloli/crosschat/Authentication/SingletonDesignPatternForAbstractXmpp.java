package com.example.kamaloli.crosschat.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Created by KAMAL OLI on 17/03/2017.
 */

public class SingletonDesignPatternForAbstractXmpp {
    public static SingletonDesignPatternForAbstractXmpp singletonInstance;
    public String username,password,emailAddress,token,mobileNumber,interestedField,areaOfSpecialization,city,country;
    public String fullName,educationLevel,permanentAddress;
    public double latitude,longitude;
    public String rUsername,rUsersName,rUserEmail;
    public Chat chatObject;
    public int rangeForMap;
    SharedPreferences preferences;
    private SingletonDesignPatternForAbstractXmpp(){
    }
    public static SingletonDesignPatternForAbstractXmpp getInstance(){
        if(singletonInstance==null){
            singletonInstance=new SingletonDesignPatternForAbstractXmpp();
        }
        return singletonInstance;
    }
    public void storeUserCredential(String username,String password,String emailAddress,String token,String mobileNumber,String
                                    interestedField,String areaOfSpecialization,String city,String country,double latitude,double longitude,
                                    String fullName,String educationLevel,String permanentAddress){
        this.username=username;
        this.password=password;
        this.emailAddress=emailAddress;
        this.token=token;
        this.mobileNumber=mobileNumber;
        this.interestedField=interestedField;
        this.areaOfSpecialization=areaOfSpecialization;
        this.city=city;
        this.country=country;
        this.latitude=latitude;
        this.longitude=longitude;
        this.fullName=fullName;
        this.educationLevel=educationLevel;
        this.permanentAddress=permanentAddress;
        this.rangeForMap=100;
    }
    public void setUserCredentialToPreferences(Context c){
        SharedPreferences preferences=c.getSharedPreferences("userCredentials",c.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.putString("emailAddress",emailAddress);
        editor.putString("token",token);
        editor.putString("mobileNumber",mobileNumber);
        editor.putString("interestedField",interestedField);
        editor.putString("areaOfSpecialization",areaOfSpecialization);
        editor.putString("city",city);
        editor.putString("country",country);
        editor.putFloat("latitude",(float) latitude);
        editor.putFloat("longitude",(float)longitude);
        editor.putString("fullName",fullName);
        editor.putString("educationLevel",educationLevel);
        editor.putString("permanentAddress",permanentAddress);
        editor.putInt("rangeForMap",100);
        editor.commit();
    }
    public boolean isUserCredentialPresent(Context c){
        SharedPreferences preferences=c.getSharedPreferences("userCredentials",c.MODE_PRIVATE);
        SharedPreferences preferences1=c.getSharedPreferences("rangeForMap",c.MODE_PRIVATE);
        if(preferences.getString("username",null)==null||preferences.getString("password",null)==null||
                preferences.getString("emailAddress",null)==null||preferences.getString("token",null)==null){
            return false;
        }
        else{
            username=preferences.getString("username",null);
            password=preferences.getString("password",null);
            emailAddress=preferences.getString("emailAddress",null);
            token=preferences.getString("token",null);
            latitude=preferences.getFloat("latitude",0);
            Log.e("latitude",latitude+"");
            longitude=preferences.getFloat("longitude",0);
            Log.e("longitude",longitude+"");
            rangeForMap=preferences.getInt("rangeForMap",100);
            return true;
        }
    }
    public void setRemotelyChatingUsername(String username){
        rUsername=username;
    }
    public void setRemotelyChatingUsersname(String username){
        rUsersName=username;
    }
    public void setRemotelyChatingUserEmail(Double lat,Double lon){
        rUserEmail=username;
    }
    public void setRangeForMapDisplay(Context c,int value){
        SharedPreferences preferences=c.getSharedPreferences("userCredentials",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        if(value==0){
            rangeForMap=0;
        }
        else
        {
            editor.putInt("rangeForMap",value);
            editor.commit();
            rangeForMap=value;
        }
    }
}
