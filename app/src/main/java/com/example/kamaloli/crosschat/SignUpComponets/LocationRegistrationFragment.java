package com.example.kamaloli.crosschat.SignUpComponets;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kamaloli.crosschat.ManagerForLocation.CurrentLocationOfUserListener;
import com.example.kamaloli.crosschat.R;
import com.example.kamaloli.crosschat.SignIn;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LocationRegistrationFragment extends Fragment implements View.OnClickListener {
    Location optimalLocation;
    final String SIGN_UP_URL = "http://54.69.105.54/crosschat/api/v1/register_user";
    final String HOST="54.69.105.54";
    final String SERVICE_NAME="ip-172-31-5-253.us-west-2.compute.internal";
    final int PORT=5222;
    JSONObject crossChatResponse, openfireResponse;
    double currentLocationLat, currentLocationLon;
    EditText ePermanentAddress, eEducationLevel,eCountry, eCity, eAreaOfSpecialization, eInterestedField;
    Button submit;
    String permanentAddress, educationLevel,country,city, areaOfSpecialization, interestedField;
    Bundle savedInstanceState;
    View view;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.location_registration, container, false);
        ePermanentAddress = (EditText) view.findViewById(R.id.permanent_address);
        eEducationLevel = (EditText) view.findViewById(R.id.education_level);
        eCountry=(EditText)view.findViewById(R.id.country);
        eCity = (EditText) view.findViewById(R.id.city);
        eAreaOfSpecialization = (EditText) view.findViewById(R.id.area_of_specialization);
        eInterestedField = (EditText) view.findViewById(R.id.interested_field);
        submit = (Button) view.findViewById(R.id.submit);
        progress=new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Please wait...Registering your to our server.");
        submit.setOnClickListener(this);
        return view;
    }

    boolean isUserConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (isConnected)
            return true;
        else
            return false;
    }

    void errorDialogDisplay(String tittle, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

    void succesDialog(String tittle, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(tittle);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), SignIn.class);
                startActivity(i);
                (getActivity()).overridePendingTransition(0,0);
            }
        });
        dialog.setCancelable(true);
        dialog.create();
        dialog.show();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (isUserConnectedToInternet()) {
                    permanentAddress = ePermanentAddress.getText().toString().trim();
                    educationLevel = eEducationLevel.getText().toString().trim();
                    city = eCity.getText().toString().trim();
                    areaOfSpecialization = eAreaOfSpecialization.getText().toString().trim();
                    interestedField = eInterestedField.getText().toString().trim();
                    country=eCountry.getText().toString().trim();
                    if (permanentAddress.isEmpty() || educationLevel.isEmpty() || city.isEmpty() ||
                            areaOfSpecialization.isEmpty() || interestedField.isEmpty()||country.isEmpty()) {
                        errorDialogDisplay("Oops!! Some fields are empty", getResources().getString(R.string.form_field_left_blank_message));
                    } else {
                        // getAccurateLocationOfUser();
                        //currentLocationLat=optimalLocation.getLatitude();
                        //currentLocationLon=optimalLocation.getLongitude();

                        savedInstanceState.putString("permanentAddress",permanentAddress);
                        savedInstanceState.putString("educationLevel",educationLevel);
                        savedInstanceState.putString("city",city);
                        savedInstanceState.putString("areaOfSpecialization",areaOfSpecialization);
                        savedInstanceState.putString("interestedField",interestedField);
                        savedInstanceState.putString("country",country);
                        savedInstanceState.putString("url",SIGN_UP_URL);
                        RegisterNewUserToCrossChat cRegister=new RegisterNewUserToCrossChat();
                        progress.show();
                        cRegister.execute(savedInstanceState);
                    }
                } else {
                    errorDialogDisplay("Network Error!!", getResources().getString(R.string.network_connection_error_message));
                }
                break;
        }
    }

    private void userOpenfireRegistration(int confirmationCode) throws InterruptedException, ExecutionException {
        if(isUserCreatedSuccessfully(confirmationCode)){
            RegisterNewUserToOpenfire oRegistration=new RegisterNewUserToOpenfire();
        }
    }

    public boolean isUserCreatedSuccessfully(int confirmationCode) {

        if(confirmationCode==201){
            Log.e("User successfully"," registered with crosschat");
            return true;
        }
        else if(confirmationCode==403){
            Log.e("Mobile number is ","already registered");
            return false;
        }
        else if(confirmationCode==401){
            Log.e("Email is already"," registered.");
            return false;
        }
        else if(confirmationCode==402){
            Log.e("User name is","already registered");
            return false;
        }
        else{
            Log.e("Something went","Wrong");
            return false;
        }
    }

    class RegisterNewUserToCrossChat extends AsyncTask<Bundle, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Bundle... params) {
            Bundle userInfo=params[0];
            // String stringUrl="http://192.168.0.101:8000/crosschat/api/v1/register_user";
            StringBuilder builder;
            String returnedValue;
            HttpURLConnection httpURLConnection=null;
            JSONObject jsonObject=null;
            URL url;
            try {

                url=new URL(userInfo.getString("url"));
                httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("content-type","application/json");
//                httpURLConnection.setRequestProperty("X-Requested-With","XMLHttpRequest");
//                httpURLConnection.setRequestProperty("charset","UTF-8");
                JSONObject object=new JSONObject();
                object.put("full_name",userInfo.getString("usersName"));
                object.put("username",userInfo.getString("username"));
                object.put("mobile_number",userInfo.getString("mobileNumber"));
                object.put("email_address",userInfo.getString("emailAddress"));
                object.put("password",userInfo.getString("password"));
                object.put("current_address",userInfo.getString("currentAddress"));
                object.put("permanent_address",userInfo.getString("permanentAddress"));
                object.put("field_of_interest",userInfo.getString("interestedField"));
                object.put("education_level",userInfo.getString("educationLevel"));
                object.put("country",userInfo.getString("country"));
                object.put("city",userInfo.getString("city"));
                object.put("latitude",userInfo.getDouble("latitude"));
                object.put("longitude",userInfo.getDouble("longitude"));
                object.put("specialization",userInfo.getString("areaOfSpecialization"));
                object.put("security_question",userInfo.getString("question"));
                object.put("security_answer",userInfo.getString("answer"));
                BufferedWriter sendOut=new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                Log.e("Json data",object.toString());
                sendOut.write(object.toString());
                sendOut.close();
                builder=new StringBuilder();
                BufferedReader readInput=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while((returnedValue=readInput.readLine())!=null){
                    builder.append(returnedValue+"\n");
                    Log.e("buidler",builder.toString());
                }

                readInput.close();
                jsonObject=new JSONObject(builder.toString());
                return  jsonObject;

            } catch (MalformedURLException e) {
                Log.e("MalformedURLException",e+"");
            } catch (IOException e) {
                Log.e("IOException",e+"");
            }
            catch (JSONException e) {
                Log.e("JSONException",e+"");
            }
            finally {
                if(httpURLConnection!=null)
                    httpURLConnection.disconnect();
            }
            progress.hide();
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            progress.setTitle("Registering to OpenFire Server. Please Wait...");
            progress.show();
            crossChatResponse=object;
            if(object!=null) {
                if (jsonParsingForDataReceivedFromCrossChat(object)) {
                    RegisterNewUserToOpenfire register=new RegisterNewUserToOpenfire();
                    register.execute(savedInstanceState);
                    //errorDialogDisplay("Successfull", "User created successfully and now we can proceed with openfire registration");
                }
            }
            else
            {
                Log.e("Returned value","Error getting value");
            }
        }
    }

    class RegisterNewUserToOpenfire extends AsyncTask<Bundle, Void, String> {
        @Override
        protected String doInBackground(Bundle... params) {
            String response;
            Bundle bundle=params[0];

            XMPPTCPConnectionConfiguration.Builder connection=XMPPTCPConnectionConfiguration.builder();
            connection.setHost(HOST);
            connection.setServiceName(SERVICE_NAME);
            connection.setPort(PORT);
            connection.setResource("android-phone");
            connection.setDebuggerEnabled(true);
            connection.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            XMPPTCPConnection conn= new XMPPTCPConnection(connection.build());

            HashMap<String,String> userInfo=new HashMap<>();
            userInfo.put("email",bundle.getString("emailAddress"));
            userInfo.put("name",bundle.getString("usersName"));
            try {
                conn.connect();
                AccountManager manager=AccountManager.getInstance(conn);
                if(manager.supportsAccountCreation()){
                    manager.sensitiveOperationOverInsecureConnection(true);
                    manager.createAccount(bundle.getString("username"),bundle.getString("password"),userInfo);
                    response="success";
                }
                else{
                    response="userCreationNotSupported";
                }
            } catch (SmackException e) {
                Log.e("SmackException",e+"");
                response="errorS";
            } catch (XMPPException e) {
                Log.e("XMPPException",e+"");
                response="errorsX";
            } catch (IOException e) {
                Log.e("IOException",e+"");
                response="notConnected";
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            progress.hide();
            if(response.equals("success")){
                succesDialog("Success",getResources().getString(R.string.user_created_successfully));

            }
            if(response.equals("errorS")){
                errorDialogDisplay("Oops",getResources().getString(R.string.xmpp_server_error));
            }
            if(response.equals("errorX")){
                errorDialogDisplay("Already registered",getResources().getString(R.string.username_already_registered));
            }
            if(response.equals("userCreationNotSupported")){
                errorDialogDisplay("Server error",getResources().getString(R.string.user_creation_is_not_possible));
            }
            if(response.equals("notConnected")){
                errorDialogDisplay("Failed to connect!","You are not connected with xampp server");
            }

        }
    }

    void getAccurateLocationOfUser() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String [] { Manifest.permission.ACCESS_FINE_LOCATION },1);
        }
        else{
            LocationManager manager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            LocationListener networkLocation, gpsLocation;
            Location gps,network;
            gpsLocation=new CurrentLocationOfUserListener();
            manager.requestSingleUpdate(LocationManager.GPS_PROVIDER,gpsLocation,null);
            gps=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            networkLocation=new CurrentLocationOfUserListener();
            manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,networkLocation,null);
            network=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(network.getAccuracy()<gps.getAccuracy()){
                optimalLocation=network;
            }
            else{
                optimalLocation=gps;
            }
        }
    }
    boolean jsonParsingForDataReceivedFromCrossChat(JSONObject jsonObject){
        try {
            boolean isSuccess= Boolean.parseBoolean(jsonObject.getString("Success"));
            if(isSuccess){
                savedInstanceState.putString("openfireUrl","http://54.209.36.83:9090/plugins/restapi/v1/users");
                return true;
            }
            else{
                int errorCode=jsonObject.getInt("code");
                if(errorCode==401){
                    errorDialogDisplay("Opps",getResources().getString(R.string.email_already_registered));
                }
                if(errorCode==402)
                    errorDialogDisplay("Opps",getResources().getString(R.string.username_already_registered));
                if(errorCode==403)
                    errorDialogDisplay("Opps",getResources().getString(R.string.mobile_number_already_registered));
                return false;
            }
        } catch (JSONException e) {
            errorDialogDisplay("Opss",getResources().getString(R.string.json_parsing_exception)+": "+e.getMessage());
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)==PERMISSION_GRANTED){

                        LocationManager manager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                        LocationListener networkLocation, gpsLocation;
                        Location gps,network;

                        gpsLocation=new CurrentLocationOfUserListener();
                        manager.requestSingleUpdate(LocationManager.GPS_PROVIDER,gpsLocation,null);
                        gps=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        networkLocation=new CurrentLocationOfUserListener();
                        manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,networkLocation,null);
                        network=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if(network.getAccuracy()<gps.getAccuracy()){
                            optimalLocation=network;
                        }
                        else{
                            optimalLocation=gps;
                        }
                    }
                }

                break;
        }

    }
}
