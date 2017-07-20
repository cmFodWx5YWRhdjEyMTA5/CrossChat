package com.example.kamaloli.crosschat;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

public class FragmentContactUs extends Fragment implements View.OnClickListener {


    JSONObject crossChatResponse, openfireResponse;

    EditText full_name;
    EditText email_address;
    EditText message;
    Button submit;
    String name,email,msg;
    Bundle savedInstanceState;
    View view;
    private ProgressDialog progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        full_name = (EditText) view.findViewById(R.id.name);
        email_address=(EditText)view.findViewById(R.id.email);
        message=(EditText)view.findViewById(R.id.message_send);
        submit = (Button) view.findViewById(R.id.send);
        progress=new ProgressDialog(getActivity());



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
            case R.id.send:
                Toast.makeText(getActivity(),"hi",Toast.LENGTH_SHORT).show();
                if (isUserConnectedToInternet()) {
                    name = full_name.getText().toString().trim();
                    email = email_address.getText().toString().trim();
                    msg = message.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || msg.isEmpty()) {
                        errorDialogDisplay("Oops!! Some fields are empty", getResources().getString(R.string.form_field_left_blank_message));
                    } else {
                        Log.e("mugi raju chuitiya ho",email+" "+name+" "+" "+msg);
                        ThrowingData t=new ThrowingData();
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setMessage("Submitting Data. Please wait...");
                        progress.setCancelable(false);
                        progress.show();
                        t.execute(email,name,msg);


                    }
                } else {
                    errorDialogDisplay("Network Error!!", getResources().getString(R.string.network_connection_error_message));
                }
                break;


        }
    }

//
//    public boolean isUserCreatedSuccessfully(int confirmationCode) {
//
//        if(confirmationCode==201){
//            Log.e("User successfully"," registered with crosschat");
//            return true;
//        }
//        else if(confirmationCode==403){
//            Log.e("Mobile number is ","already registered");
//            return false;
//        }
//        else if(confirmationCode==401){
//            Log.e("Email is already"," registered.");
//            return false;
//        }
//        else if(confirmationCode==402){
//            Log.e("User name is","already registered");
//            return false;
//        }
//        else{
//            Log.e("Something went","Wrong");
//            return false;
//        }
//    }

    class ThrowingData extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
           String email=params[0];
            String name=params[1];
            String msg=params[2];
            String stringUrl="http://192.168.43.35:8000/crosschat/api/v1/contactus";
            StringBuilder builder;
            String returnedValue;
            HttpURLConnection httpURLConnection = null;
            JSONObject jsonObject = null;
            URL url;
            try {

                url = new URL(stringUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
//                httpURLConnection.setRequestProperty("X-Requested-With","XMLHttpRequest");
//                httpURLConnection.setRequestProperty("charset","UTF-8");
                JSONObject object = new JSONObject();
                object.put("full_name", name);
                object.put("email_address", email);
                object.put("message", msg);
                BufferedWriter sendOut = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                Log.e("Json data", object.toString());
                sendOut.write(object.toString());
                sendOut.close();
                builder = new StringBuilder();
                BufferedReader readInput = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((returnedValue = readInput.readLine()) != null) {
                    builder.append(returnedValue + "\n");

                    Log.e("buidler", builder.toString());
                }

                readInput.close();
                jsonObject = new JSONObject(builder.toString());
                return jsonObject;

            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", e + "");
            } catch (IOException e) {
                Log.e("IOException", e + "");
            } catch (JSONException e) {
                Log.e("JSONException", e + "");
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject==null)
            {
                progress.hide();
                Log.e("Message","Error cant ");
            }
            else
            {
                progress.hide();
                Log.e("message",""+jsonObject);
            }
        }
    }



}
