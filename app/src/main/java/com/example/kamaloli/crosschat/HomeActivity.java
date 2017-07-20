package com.example.kamaloli.crosschat;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;
import com.example.kamaloli.crosschat.CustomDialogContainer.RangeUpdateDialog;

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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MapPresentation.MapAndHomeActivityCommunication{
    TextView tv;
    DrawerLayout drawer;
    LocationChangeListenerService locationListenerService;
    android.app.FragmentManager manager;
    MessageListenerService messageListenerService;
    Activity mainActivity;
    boolean isServiceBounded = false, isLocationServiceBounded = false;
    SingletonDesignPatternForAbstractXmpp userInfo;
    boolean isMapLoadingData=false;
    double latitude,longitude;
    MapPresentation mapPresentation;
    MessageListenerService.LocationChangeListener listener;
   // CurrentLocationOfUserListener context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CrossChat");
        setSupportActionBar(toolbar);
        tv = (TextView) findViewById(R.id.tv);
        toolbar.setNavigationIcon(R.mipmap.left_drawer);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        userInfo = SingletonDesignPatternForAbstractXmpp.getInstance();
        //context=new CurrentLocationOfUserListener(HomeActivity.this);
        mapPresentation = new MapPresentation();
        if (userInfo.isUserCredentialPresent(getApplicationContext())) {
            manager = getFragmentManager();
            android.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.home_activity, mapPresentation, "mapfragment").commit();
        } else {
            Intent intent = new Intent(HomeActivity.this, SignIn.class);
            startActivity(intent);
            finish();
        }
        navigationView.setNavigationItemSelectedListener(this);
        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            SharedPreferences preferences=getSharedPreferences("userCredentials",MODE_PRIVATE);
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(!query.trim().isEmpty()){
                mapPresentation.doSearch(query,preferences.getString("token",null),
                        preferences.getFloat("latitude",0)+"",preferences
                .getFloat("longitude",0)+"");
                isMapLoadingData=true;
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent messageListenerService = new Intent(this, MessageListenerService.class);
        bindService(messageListenerService, serviceConnection, Context.BIND_AUTO_CREATE);

    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageListenerService.MessageListenerBinder binder = (MessageListenerService.MessageListenerBinder) service;
            messageListenerService = binder.getService();
            initializeListener();
            isServiceBounded = true;
            messageListenerService.initializeMessageListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBounded = false;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_item, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                isMapLoadingData=false;
                android.app.FragmentManager mgrr=getFragmentManager();
                FragmentTransaction trn=mgrr.beginTransaction();
                trn.replace(R.id.home_activity,new MapPresentation());
                getSupportActionBar().setTitle("Cross Chat");

                trn.commit();
                drawer.closeDrawers();

                break;
            case R.id.mprofile:
                isMapLoadingData=true;
                android.app.FragmentManager manager=getFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.home_activity,new FragmentMyProfile());
                getSupportActionBar().setTitle("My Profile");
               //transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawers();
                Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.change_range:
                isMapLoadingData=true;
                drawer.closeDrawer(GravityCompat.START);
                RangeUpdateDialog dialog = new RangeUpdateDialog();
                dialog.show(getSupportFragmentManager(), "kamal");
//                mapPresentation.updateMapForRange();
                Toast.makeText(getApplicationContext(), "Range changed to " + userInfo.rangeForMap, Toast.LENGTH_SHORT).show();
                break;
            case R.id.update:
                isMapLoadingData=false;
                mapPresentation.updateMapForRange();
            drawer.closeDrawers();
            Toast.makeText(getApplicationContext(),"Updating Map", Toast.LENGTH_SHORT).show();
            break;
            case R.id.contact_us:
                isMapLoadingData=true;
                Toast.makeText(getApplicationContext(), "Contact Us", Toast.LENGTH_SHORT).show();
                FragmentContactUs fcu=new FragmentContactUs();
                android.app.FragmentManager mg=getFragmentManager();
                android.app.FragmentTransaction ft=mg.beginTransaction()
                        .replace(R.id.home_activity,fcu);
                getSupportActionBar().setTitle("Contact us");
                ft.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.about_us:
                isMapLoadingData=true;
                Toast.makeText(getApplicationContext(), "About Us", Toast.LENGTH_SHORT).show();
                FragmentAboutUs fau = new FragmentAboutUs();
                android.app.FragmentManager manager1 = getFragmentManager();
                android.app.FragmentTransaction transaction1 = manager1.beginTransaction()
                        .replace(R.id.home_activity,fau);
                transaction1.commit();
                getSupportActionBar().setTitle("About us");
                drawer.closeDrawers();
                break;

            case R.id.logout:
                Intent logOut = new Intent(HomeActivity.this, SignIn.class);
                SharedPreferences pref=getSharedPreferences("userCredentials",MODE_PRIVATE);
                SharedPreferences.Editor edt=pref.edit();
                edt.clear().commit();
                startActivity(logOut);
                finish();
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_exit:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMapLoadingData=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isMapLoadingData=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isMapLoadingData=true;
        if (userInfo.isUserCredentialPresent(getApplicationContext())) {
            unbindService(serviceConnection);
            isServiceBounded = false;
        }
    }

    public void rangeUpdateDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        final EditText r = (EditText) view.findViewById(R.id.range);
        dialog.setView(view);
        dialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!(r.getText().toString().isEmpty())) {
                    SharedPreferences userData = getSharedPreferences("UserDetail", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userData.edit();
                    editor.putInt("distance", Integer.parseInt(r.getText().toString()));
                    dialog.dismiss();

                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    public void initializeListener() {
           //  listener = messageListenerService.new LocationChangeListener();
//        LocationListener cListener=new CurrentLocationOfUserListener();
//        LocationListener currentLocationOfUserListener=new CurrentLocationOfUserListener();
//        LocationListener cListener= messageListenerService.new LocationChangeListener();
//        LocationListener currentLocationOfUserListener=messageListenerService.new LocationChangeListener();
        LocationListener currentLocationOfUserListener=new LocationChangeListener();
        LocationListener cListener=new LocationChangeListener();
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,new String [] { Manifest.permission.ACCESS_FINE_LOCATION ,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            ActivityCompat.requestPermissions(HomeActivity.this,new String []{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
        else{
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,30000,50,cListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,50,currentLocationOfUserListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                //LocationListener listener = messageListenerService.new LocationChangeListener();
//                LocationListener cListener= messageListenerService.new LocationChangeListener();
//                LocationListener currentLocationOfUserListener=messageListenerService.new LocationChangeListener();
                LocationListener currentLocationOfUserListener=new LocationChangeListener();
                LocationListener cListener=new LocationChangeListener();
//
            if(grantResults.length>0&&grantResults[0]==PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PERMISSION_GRANTED&&
                        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED){
                    LocationManager manager=(LocationManager)getSystemService(LOCATION_SERVICE);
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,50,cListener);
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,30000,50,currentLocationOfUserListener);
                }
            }
                break;
        }
    }

    @Override
    public void receiveSearchCompleteMessage(boolean isCompleted) {
        isMapLoadingData=false;
    }

    public class LocationChangeListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(!isMapLoadingData){
                latitude=Math.toRadians(location.getLatitude());
                longitude=Math.toRadians(location.getLongitude());
                LocationUpdateToServer update=new LocationUpdateToServer();
                isMapLoadingData =true;
                update.execute(latitude,longitude);
                Log.e("Home activity location",location.getLatitude()+" "+location.getLongitude());
            }
            Log.e("Home activity location",location.getLatitude()+" "+location.getLongitude());
            //((HomeActivity)c).notifyLocationChanged("Home Lat="+location.getLatitude(),"Home Lon="+location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(" Home onStatusChanged","provider changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("Home Providerr enabled",provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("HomeProviderdisablenot",provider);
        }
    }

    class LocationUpdateToServer extends AsyncTask<Double,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(Double... params) {
            double latitude=params[0];
            double longitude=params[1];
            URL url;
            JSONObject response=null;
            HttpURLConnection connection=null;
            SharedPreferences preferences=getSharedPreferences("userCredentials",MODE_PRIVATE);
            try {
                url=new URL("http://192.168.43.35:8000/crosschat/api/v1/update_current_location?token="+preferences.getString("token",null));
                Log.e("Backtoken",preferences.getString("token",null));
                connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type","application/json");
                JSONObject object=new JSONObject();
                object.put("latitude",latitude);
                object.put("longitude",longitude);
                Log.e("OnBg latitude",latitude+"");
                Log.e("OnBg longitude",longitude+"");
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(object.toString());
                writer.close();
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder=new StringBuilder();
                String responseSingle;
                while((responseSingle=reader.readLine())!=null){
                    builder.append(responseSingle+"\n");
                }
                response=new JSONObject(builder.toString());
                return response;
            } catch (MalformedURLException e) {
                Log.e("BgMalformedURLException",e+"");
            } catch (IOException e) {
                Log.e("BgIOException",e+"");
            } catch (JSONException e) {
                Log.e("BgJSONException",e+"");
            }
            finally{
                if(connection!=null){
                    connection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject==null){
                Log.e("Message","reply is null");

            }
            else{
               boolean isSuccess= parseJsonReply(jsonObject);
                if(isSuccess){
                    SharedPreferences preferences=getSharedPreferences("userCredentials",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putFloat("latitude",(float)latitude);
                    editor.putFloat("longitude",(float)longitude);
                    editor.commit();
                    userInfo.latitude=latitude;
                    userInfo.longitude=longitude;
                    if(isMapLoadingData!=true)
                       mapPresentation.updateMapForRange();
                }
            }
        }

        private boolean parseJsonReply(JSONObject jsonObject) {
            try {
                if (jsonObject.getString("message").equals("success")) {
                    Log.e("successfully", "updated location");
                    return true;
                }
                if (jsonObject.getString("message").equals("failed")){
                    Log.e("Transaction", "Failed");
                    return false;
                }
                if(jsonObject.getInt("code")==500) {
                    Log.e("PDOException", jsonObject.getString("message"));
                    return false;
                }
                if(jsonObject.getString("message").equals("TokenExpiredException")) {
                    Log.e("JWTException", "TokenExpired");
                    return false;
                }
                return false;
            } catch (JSONException e) {
                Log.e("JSONException ","In parseJsonReplyMethod");
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
