package com.example.kamaloli.crosschat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{
    final String forgotPasswordUrl="";
    String email="http://192.168.43.35:8000/crosschat/api/v1/forgot_password";
    Button submit,submitEmail;
    EditText emailAddress,answer;
    TextView question;
    ProgressDialog progress;
    String securityQuestion="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        submit=(Button)findViewById(R.id.submit);
        emailAddress=(EditText)findViewById(R.id.forgot_password_email);
        answer=(EditText)findViewById(R.id.security_answer);
        progress=new ProgressDialog(ForgotPassword.this);
        question=(TextView)findViewById(R.id.security_question);
        question.setVisibility(View.GONE);
        answer.setVisibility(View.GONE);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit:
                String eEmail=emailAddress.getText().toString().trim();
                if(!eEmail.isEmpty()){
                    this.email=eEmail;
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setMessage("Verifying your email...");
                    progress.setCancelable(false);
                    VerifyEmail verifyEmail=new VerifyEmail();
                    Log.e("email",eEmail);
                    progress.show();
                    verifyEmail.execute(email);
                }
                String ans=answer.getText().toString().trim();
                Log.e("answerSubmit",ans);
                if(!email.isEmpty()&&!securityQuestion.isEmpty()&&!ans.isEmpty()){
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setMessage("Verifying your answer...");
                    VerifySecurityAnswer verifyAns=new VerifySecurityAnswer();
                    progress.setCancelable(false);
                    progress.show();
                    verifyAns.execute(securityQuestion,ans);
                }
                break;
        }
    }

    class VerifyEmail extends AsyncTask<String,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            String emailAddres=params[0];
            String u="http://192.168.43.35:8000/crosschat/api/v1/forgot_password";
            URL url;
            HttpURLConnection connection=null;
            JSONObject replyJson=null;
            try {
                url=new URL(u);
                connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type","application/json");
                JSONObject object=new JSONObject();
                object.put("email_address",emailAddres);
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(object.toString());
                writer.close();
                StringBuilder builder =new StringBuilder();
                String single;
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((single=reader.readLine())!=null)
                    builder.append(single+"\n");
                replyJson=new JSONObject(builder.toString());
                reader.close();
                return replyJson;
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException",e+"");
            } catch (IOException e) {
                Log.e("IOException",e+"");
            } catch (JSONException e) {
                Log.e("JSONException",e+"");
            }
            finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return replyJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject==null){
                progress.hide();
                errorDialogDisplay("Opps!!","Something happened please check your network connection and try again.");
                Log.e("Operation","Unsuccessful");
            }else{
                if(emailVerification(jsonObject))
                {
                    progress.hide();
                    emailAddress.setVisibility(View.GONE);
                    question.setVisibility(View.VISIBLE);
                    answer.setVisibility(View.VISIBLE);
                    try {
                        question.setText(jsonObject.getString("security_question"));
                        securityQuestion=jsonObject.getString("security_question");

                    } catch (JSONException e) {
                        Log.e("JSONExceptionOnpos",e+"");
                    }
                }
            }
        }
    }

    private boolean emailVerification(JSONObject jsonObject) {
        try {
            String message=jsonObject.getString("message");
            int code=jsonObject.getInt("code");
            if(message.equals("invalidEmail")){
                progress.hide();
                Log.e("invalidEmail","Address");
                errorDialogDisplay("Invalid Email!","The email address you provided is not present in our server.");
                email="";
                emailAddress.setText("");
                securityQuestion="";
                return false;
            }
            if(code==222){
                emailAddress.setText("");
                return true;
            }
        } catch (JSONException e) {
            progress.hide();
            emailAddress.setText("");
           Log.e("JSONException",e+"");
        }
        return false;
    }
    class VerifySecurityAnswer extends AsyncTask<String,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            String sQuestion=params[0];
            String answer=params[1];
            String u="http://192.168.43.35:8000/crosschat/api/v1/forgot_password";
            URL url;
            HttpURLConnection connection=null;
            JSONObject replyJson=null;
            try {
                url=new URL(u);
                connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type","application/json");
                JSONObject object=new JSONObject();
                object.put("security_question",sQuestion);
                object.put("answer",answer);
                object.put("email_address",email);
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(object.toString());
                writer.close();
                StringBuilder builder =new StringBuilder();
                String single;
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((single=reader.readLine())!=null)
                    builder.append(single+"\n");
                replyJson=new JSONObject(builder.toString());
                reader.close();
                return replyJson;
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException",e+"");
            } catch (IOException e) {
                Log.e("IOException",e+"");
            } catch (JSONException e) {
                Log.e("JSONException",e+"");
            }
            finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return replyJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject==null){
                progress.hide();
                errorDialogDisplay("Opps!!","Something happened please check your network connection and try again.");
                Log.e("Operation failed","couldn't getPassword");
            }
            else{
                jsonParseForSecurityAnswer(jsonObject);
            }
        }
    }

    private void jsonParseForSecurityAnswer(JSONObject jsonObject) {
        try {
            int responseCode=jsonObject.getInt("code");
            if(responseCode==222){
                progress.hide();
                answer.setText("");
                redirectToLoginDialog("Congratulations!!","You successfully verified yourself. and your password is : " +
                        ""+jsonObject.get("message"));
            }
            if(responseCode==444){
                progress.hide();
                answer.setText("");
                errorDialogDisplay("Invalid answer!","The answer you provided is not valid.");
                Log.e("Invalid","Answer");
            }
            if(responseCode==501){
                answer.setText("");
                progress.hide();
                errorDialogDisplay("Opps","There is a error with our database server.Please try again.");
                Log.e("Server error","Error in our server");
            }
            progress.hide();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    void errorDialogDisplay(String tittle, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ForgotPassword.this);
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
   public void redirectToLoginDialog(String tittle,String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ForgotPassword.this);
        dialog.setTitle(tittle);
        dialog.setMessage(message);
        dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(ForgotPassword.this,SignIn.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
    protected void onDestroy() {
        super.onDestroy();
        progress=null;
        email=null;
        securityQuestion=null;
    }
}
