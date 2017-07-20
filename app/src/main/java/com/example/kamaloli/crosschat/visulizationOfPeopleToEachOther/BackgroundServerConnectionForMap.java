package com.example.kamaloli.crosschat.visulizationOfPeopleToEachOther;

import android.os.AsyncTask;
import android.util.Log;

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
import java.net.URLEncoder;

/**
 * Created by KAMAL OLI on 02/03/2017.
 */

public class BackgroundServerConnectionForMap extends AsyncTask<ParameterAnalysis,Void,JSONObject>{
    @Override
    protected JSONObject doInBackground(ParameterAnalysis... params) {
        try {
            URL url=new URL(params[0].url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type","application/json");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();
            if(true){
                BufferedWriter outputStream=new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()) );
                JSONObject dataToBeSent=new JSONObject();
                dataToBeSent.put("lattitude",0.215545533);
                dataToBeSent.put("longitude",1.402565556);
                dataToBeSent.put("user","kamal");
                outputStream.write(URLEncoder.encode(dataToBeSent.toString(),"UTF-8"));
                outputStream.close();
                BufferedReader reader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder builder=new StringBuilder();
                String line=null;
                while ((line=reader.readLine())!=null){
                    builder.append(line+"\n");
                }
                reader.close();
                Log.e("Json data",builder.toString());

            }
            else{
                Log.e("Response code",httpURLConnection.getResponseCode()+""+httpURLConnection.getResponseMessage());
                return new JSONObject();
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException",e+"");
        } catch (IOException e) {
            Log.e("IOException",e+"");
        } catch (JSONException e) {
            Log.e("JSONException",e+"");
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject object) {

    }

}
