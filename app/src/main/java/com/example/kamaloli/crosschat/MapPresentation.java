package com.example.kamaloli.crosschat;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;
import com.example.kamaloli.crosschat.CustomDialogContainer.UserDetailDisplay;
import com.example.kamaloli.crosschat.visulizationOfPeopleToEachOther.ParameterAnalysis;
import com.example.kamaloli.crosschat.visulizationOfPeopleToEachOther.UserPropertiesToBeDisplayedOnMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MapPresentation extends Fragment implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{
    JSONObject jsonObjectReceivedFromServer;
    final String NEAR_BY_PLACES_URL="http://54.69.105.54/crosschat/api/v1/nearby_places";
    final String SEARCH_INTEREST_URL="http://54.69.105.54/crosschat/api/v1/interests?token=";
    MapView mapView;
    SingletonDesignPatternForAbstractXmpp singleton;
    UserDetailDisplay detailDisplay;
    private GoogleMap mMap;
    ProgressBar progressBar;
    public MapAndHomeActivityCommunication communication;
    public HashMap<String,UserPropertiesToBeDisplayedOnMap> listOfUsers,searchedListOfUsers;
    int range;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewHoldinMapView=inflater.inflate(R.layout.activity_map_presentation,container,false);
        mapView=(MapView) viewHoldinMapView.findViewById(R.id.map_presentation);
        progressBar=(ProgressBar)viewHoldinMapView.findViewById(R.id.loading_map);
        detailDisplay=new UserDetailDisplay();
//        SharedPreferences preferences=getActivity().getSharedPreferences("userCredential",getActivity().MODE_PRIVATE);
        listOfUsers=new HashMap<>();
        mapView.onCreate(savedInstanceState);
        singleton=SingletonDesignPatternForAbstractXmpp.getInstance();
        range=singleton.rangeForMap;
        mapView.onResume();
        mapView.getMapAsync(this);
        return viewHoldinMapView;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ParameterAnalysis param=new ParameterAnalysis();
        param.url=NEAR_BY_PLACES_URL;
        SharedPreferences preferences=getActivity().getSharedPreferences("userCredentials",getActivity().MODE_PRIVATE);
        param.latitude=(float)singleton.latitude;
        param.longitude=(float)singleton.longitude;
        param.authenticationToken=preferences.getString("token",null);
        param.radius=singleton.rangeForMap;
        Log.e("Latituded onMap",preferences.getFloat("latitude",0)+"");
        Log.e("onMapready",singleton.latitude+singleton.longitude+param.authenticationToken+param.radius);
        BackgroundServerConnectionForMap connectionForMap=new BackgroundServerConnectionForMap();
        mMap = googleMap;
        progressBar.setVisibility(View.VISIBLE);
        connectionForMap.execute(param);

        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void populateMapWithUser(GoogleMap googleMap, HashMap<String,UserPropertiesToBeDisplayedOnMap> listOfUsers) {
        int height=200;
        int width=200;
        this.listOfUsers=listOfUsers;
        BitmapDrawable drawable= (BitmapDrawable) getResources().getDrawable(R.drawable.customemarker);
        Bitmap bitmapDrawable=drawable.getBitmap();
        Bitmap bitmap=Bitmap.createScaledBitmap(bitmapDrawable,height,width,false);

        for(String key:listOfUsers.keySet()){
            LatLng peddapuram=new LatLng(Math.toDegrees(Double.parseDouble(listOfUsers.get(key).latitude)),
                    Math.toDegrees(Double.parseDouble(listOfUsers.get(key).longitude)));
            mMap.addMarker(new MarkerOptions().position(peddapuram).title(listOfUsers.get(key).fullName)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).snippet(listOfUsers.get(key).userName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(peddapuram));
            mMap.setMaxZoomPreference(20);
            mMap.setMinZoomPreference(5);
        }
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String username=marker.getSnippet();
        UserPropertiesToBeDisplayedOnMap properties=listOfUsers.get(username);
        Log.e(marker.getTitle(),marker.getPosition()+"userName:"+marker.getSnippet());
        Log.e("User info",listOfUsers.get(marker.getSnippet()).fullName+"  "+listOfUsers.get(marker.getSnippet()).emailAddress);
        UserDetailDisplay detailDisplay=new UserDetailDisplay();
        Bundle bundle=new Bundle();
        bundle.putString("fullName",properties.fullName);
        bundle.putString("emailAddress",properties.emailAddress);
        bundle.putString("permanentAddress",properties.permanentAddress);
        bundle.putString("highestQualification",properties.highestQualification);
        bundle.putString("fieldOfInterest",properties.fieldOfInterest);
        bundle.putString("username",username);
        bundle.putString("areaOfSpecialization",properties.specialization);
        detailDisplay.setArguments(bundle);
        String kamal="oli";
        detailDisplay.show(getFragmentManager(),kamal);
        return false;
    }

    class BackgroundServerConnectionForMap extends AsyncTask<ParameterAnalysis,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(ParameterAnalysis... params) {
            URL url;
            HttpURLConnection httpURLConnection=null;
            StringBuilder builder;
            JSONObject jsonObject=null;
            ParameterAnalysis object=params[0];
            String indivisualLine;
            try {
                url=new URL(object.url);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                httpURLConnection.setUseCaches(false);
                JSONObject parameterToBeSent=new JSONObject();
                parameterToBeSent.put("token",object.authenticationToken);
                parameterToBeSent.put("latitude",object.latitude);
                parameterToBeSent.put("longitude",object.longitude);
                parameterToBeSent.put("radius",object.radius);
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                writer.write(parameterToBeSent.toString());
                writer.close();
                BufferedReader reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                builder=new StringBuilder();
                while((indivisualLine=reader.readLine())!=null){
                    builder.append(indivisualLine+"\n");
                }
                jsonObject=new JSONObject(builder.toString());
                reader.close();
                return  jsonObject;
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException",e+"");
            } catch (IOException e) {
               Log.e("IOEXCEPTION",e+"");
            } catch (JSONException e) {
                Log.e("JSONEXCEption",e+"");
            }
            finally {
                if(httpURLConnection!=null)
                httpURLConnection.disconnect();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);
            if(object!=null){
                progressBar.setVisibility(View.INVISIBLE);
               Log.e("users OnPostExecute",object.toString());
                HashMap<String,UserPropertiesToBeDisplayedOnMap> ls=jsonObjectParser(object);
                populateMapWithUser(mMap,ls);
            }
            else{
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("cant fetch data","from server");
            }
        }
    }
    public HashMap<String,UserPropertiesToBeDisplayedOnMap> jsonObjectParser(JSONObject object){
        JSONArray array;
        HashMap<String,UserPropertiesToBeDisplayedOnMap> users=new HashMap<>();
        try {
                if(object.getInt("response_code")==201&&object.getString("response_status").equals("success")){
                    array=object.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        JSONObject obj=array.getJSONObject(i);
                        UserPropertiesToBeDisplayedOnMap user=new UserPropertiesToBeDisplayedOnMap(obj.getString("full_name")
                                ,obj.getString("education_level"),obj.getString("email_address"),obj.getString("username"),obj.getString("current_address"),
                                obj.getString("permanent_address"),obj.getString("field_of_interest"),
                                obj.getString("specialization"),obj.getString("latitude"),obj.getString("longitude"));
                        users.put(user.userName,user);
                    }
                    Log.e("Number of users ",users.size()+""+" users:"+users);
                }
                else{
                    Log.e("Message ","Error occured");
                }

        } catch (JSONException e) {
            Log.e("jsonObjectParser","JSONException"+e);
        }
       // Log.e("users",object.toString());
        return users;
    }
    public void removeMarkers(){
        mMap.clear();
    }
    public void updateMapForRange(){
        removeMarkers();
        ParameterAnalysis param=new ParameterAnalysis();
        param.url=NEAR_BY_PLACES_URL;
        SharedPreferences preferences=getActivity().getSharedPreferences("userCredentials",getActivity().MODE_PRIVATE);
        param.latitude=preferences.getFloat("latitude",0);
        param.longitude=preferences.getFloat("longitude",0);
        param.authenticationToken=preferences.getString("token",null);
        Log.e("updateForRange",param.authenticationToken);
        param.radius=singleton.rangeForMap;
        BackgroundServerConnectionForMap connectionForMap=new BackgroundServerConnectionForMap();
        progressBar.setVisibility(View.VISIBLE);
        connectionForMap.execute(param);
    }


    class SearchingInterestedField extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            int radius=Integer.parseInt(params[0]);
            double latitude=Double.parseDouble(params[1]);
            double longitude=Double.parseDouble(params[2]);
            String token=params[3];
            String queryString=params[4];
            URL url;
            JSONObject replyFromServer=null;
            HttpURLConnection httpURLConnection=null;
            try {
                url=new URL(SEARCH_INTEREST_URL+token);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                JSONObject object=new JSONObject();
                object.put("query_string",queryString);
                object.put("radius",radius);
                object.put("latitude",latitude);
                object.put("longitude",longitude);
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                writer.write(object.toString());
                writer.close();
                StringBuilder builder=new StringBuilder();
                String singleReply;
                BufferedReader reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while((singleReply=reader.readLine())!=null){
                    builder.append(singleReply+"\n");
                }
                replyFromServer=new JSONObject(builder.toString());
                return replyFromServer;

            } catch (MalformedURLException e) {
                Log.e("MalformedURLException",e+"");
            } catch (IOException e) {
                Log.e("IOException",e+"");
            } catch (JSONException e) {
                Log.e("JSONException",e+"");
            }
            finally {
                if(httpURLConnection!=null)
                    httpURLConnection.disconnect();
            }

            return replyFromServer;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject==null){
                Log.e("Couldnt ","Search for now");
            }else{
                progressBar.setVisibility(View.INVISIBLE);
                HashMap<String,UserPropertiesToBeDisplayedOnMap> searchedListOfUsers=jsonObjectParser(jsonObject);
                mMap.clear();
                populateMapWithUser(mMap,searchedListOfUsers);
                communication.receiveSearchCompleteMessage(true);
            }
        }
    }

   public interface MapAndHomeActivityCommunication{
       public void receiveSearchCompleteMessage(boolean isCompleted);
   }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communication =(MapAndHomeActivityCommunication)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"Must implement receiveUserData");
        }

    }
    public void doSearch(String searchString,String token,String latitude,String longitude){
        progressBar.setVisibility(View.VISIBLE);
        Log.e("string",searchString);
        String radius=singleton.rangeForMap+"";
        SearchingInterestedField search=new SearchingInterestedField();
        Log.e("Parameter passed",latitude+longitude+searchString+token+radius);
        search.execute(radius,latitude,longitude,token,searchString);
    }
    public void showNormalView(){
        BackgroundServerConnectionForMap connectionForMap=new BackgroundServerConnectionForMap();
        progressBar.setVisibility(View.VISIBLE);
    }
}

//try
//        {
//        jsonObjectReceivedFromServer =connectionForMap.execute(param).get();
//        listOfUsers=jsonObjectParser(jsonObjectReceivedFromServer);
//        } catch (InterruptedException e) {
//        e.printStackTrace();
//        } catch (ExecutionException e) {
//        e.printStackTrace();
//        }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map_presentation);
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//
//    }
// Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//populateMapWithUser(googleMap, sydney);
