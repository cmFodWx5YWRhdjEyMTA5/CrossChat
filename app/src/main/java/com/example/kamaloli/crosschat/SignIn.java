package com.example.kamaloli.crosschat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignIn extends AppCompatActivity implements View.OnClickListener{
    Button loginButton;
    EditText emailAddress;
    EditText loginPassword;
    String email,pass;
    TextView forgotPassword,signUpReference;
    AppCompatActivity activity;
    public ProgressBar signingProgress;
    final String loginUrl="http://54.69.105.54/crosschat/api/v1/login_user";
    SharedPreferences preferences;
    Editor editor;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        activity=this;
        loginButton=(Button)findViewById(R.id.login_button);
        emailAddress=(EditText)findViewById(R.id.email_address);
        loginPassword=(EditText)findViewById(R.id.login_password);
        //signingProgress=(ProgressBar)findViewById(R.id.signing);
        //signingProgress.setVisibility(View.INVISIBLE);
        progress=new ProgressDialog(activity);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Please wait....");
        progress.setCanceledOnTouchOutside(false);
        forgotPassword=(TextView)findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(this);
        signUpReference=(TextView)findViewById(R.id.sign_up_reference);
        signUpReference.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                email=emailAddress.getText().toString().trim();
                pass=loginPassword.getText().toString().trim();
                if(email.isEmpty()||pass.isEmpty())
                    errorDialogDisplay("Fields are empty!",getResources().getString(R.string.form_field_left_blank_message));
                else{
                    BackgroundServerConnectionForSignIn signIn=new BackgroundServerConnectionForSignIn();
                    progress.show();
                    signIn.execute(email,pass,loginUrl);
                }
                break;
            case R.id.sign_up_reference:
                Intent signUp=new Intent(SignIn.this,SignUp.class);
                startActivity(signUp);

                break;
            case R.id.forgot_password:
                Intent forgot_password=new Intent(SignIn.this,ForgotPassword.class);
                forgot_password.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(forgot_password);
                break;
        }
    }
    class BackgroundServerConnectionForSignIn extends AsyncTask<String,Void,JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            URL url;
            HttpURLConnection connection=null;
            String email=params[0];
            String pass=params[1];
            String lUrl=params[2];
            JSONObject resultJsonReply=null;
            try {
                url=new URL(lUrl);
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json");
                JSONObject object=new JSONObject();
                object.put("email_address",email);
                object.put("password",pass);
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(object.toString());
                writer.close();
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder=new StringBuilder();
                String result;
                while((result=reader.readLine())!=null){
                    builder.append(result+"\n");
                }
                reader.close();
                resultJsonReply=new JSONObject(builder.toString());
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException",""+e);
            } catch (IOException e) {
                Log.e("IOException",""+e);
            } catch (JSONException e) {
                Log.e("JSONException",""+e);
            }
            finally {
                if(connection!=null)
                    connection.disconnect();
            }

            return resultJsonReply;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject==null){
               //signingProgress.setVisibility(View.INVISIBLE);
                progress.hide();
                errorDialogDisplay("Server not responding","Could not SignIn for the moment please try again later");
            }
            else{
                parseJsonObjectRepliedFromServer(jsonObject);


            }
        }
    }
    private void parseJsonObjectRepliedFromServer(JSONObject jsonObject){
        String responseStatus;
        try {
            responseStatus=jsonObject.getString("response_status");
            if(responseStatus.equals("Failed"))
            {
                progress.hide();
                errorDialogDisplay("Invalid Credential","The username or password you entered is no valid");
            }
            if(responseStatus.equals("SError"))
            {
                progress.hide();
                errorDialogDisplay("Server error","Server error occurred.We apolosize for inconvinience.Please try again after some time.");
            }
            if(responseStatus.equals("Success")){
                SingletonDesignPatternForAbstractXmpp object=SingletonDesignPatternForAbstractXmpp.getInstance();
                object.storeUserCredential(jsonObject.getString("username"),pass,email,jsonObject.getString("token"),
                        jsonObject.getString("mobile_number"),
                        jsonObject.getString("field_of_interest"),jsonObject.getString("specialization"),
                        jsonObject.getString("city"),jsonObject.getString("country"),Double.parseDouble(jsonObject.getString("latitude")),
                        Double.parseDouble(jsonObject.getString("longitude")),jsonObject.getString("full_name"),
                        jsonObject.getString("education_level"),jsonObject.getString("permanent_address"));
                object.setUserCredentialToPreferences(getApplicationContext());
                //Log.e("values from singleton",b.emailAddress+b.username+b.token+b.password);
                progress.hide();
                //loginSuccessDialog("Success","You are Successfully signed in. pref present");
                Intent homePage=new Intent(getApplicationContext(),HomeActivity.class);
                //homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homePage);
                finish();
            }
        } catch (JSONException e) {
            Log.e("JSONException",e+"");
        }
    }

    void errorDialogDisplay(String tittle, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SignIn.this);
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
    void loginSuccessDialog(String tittle, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SignIn.this);
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
}
