package com.example.kamaloli.crosschat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kamaloli.crosschat.communication.BackgroundServerConnection;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.HashMap;

public class AccountManagerDemo extends AppCompatActivity {
    EditText username,password;
    Button submit;
    String user,pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager_demo);
        username= (EditText) findViewById(R.id.eUsername);
        password= (EditText) findViewById(R.id.ePassword);
        submit= (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        pwd=password.getText().toString().trim();
                        user=username.getText().toString().trim();
                        BackgroundConnection conn=new BackgroundConnection();
                        conn.execute();

            }
        });
    }
    class BackgroundConnection extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            XMPPTCPConnectionConfiguration.Builder connection=XMPPTCPConnectionConfiguration.builder();
            connection.setHost("54.92.252.141");
            connection.setServiceName("ip-172-31-21-141.ec2.internal");
            connection.setPort(5222);
            connection.setResource("android-phone");
            connection.setDebuggerEnabled(true);
            connection.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            XMPPTCPConnection conn= new XMPPTCPConnection(connection.build());
            HashMap<String,String> userInfo=new HashMap<>();
            userInfo.put("email","krishnaoli@gmail.com");
            userInfo.put("name","Krishna OLI");
            try {
                conn.connect();
                AccountManager manager=AccountManager.getInstance(conn);
                if(manager.supportsAccountCreation()){
                    manager.sensitiveOperationOverInsecureConnection(true);
                    manager.createAccount(user,pwd,userInfo);

                    Log.e("Successful","created user");
                }
            } catch (SmackException e) {
                Log.e("SmackException",e.getMessage());
            } catch (IOException e) {
                Log.e("IOException",e+"");
            } catch (XMPPException e) {
                Log.e("XMPPException",e.getMessage());
            }
            return null;
        }
    }
}
