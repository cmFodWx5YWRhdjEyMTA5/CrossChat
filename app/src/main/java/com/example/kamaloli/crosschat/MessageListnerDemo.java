package com.example.kamaloli.crosschat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageListnerDemo extends AppCompatActivity {
    MessageListenerService backgroundMessageListener;
    boolean isServiceBounded=false;
    Button bind,unbind;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_listner_demo);
        bind=(Button)findViewById(R.id.bind);
        message=(TextView)findViewById(R.id.service_message);
        unbind=(Button)findViewById(R.id.unbind);
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceBounded){

                }
            }
        });
        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(serviceConnection);
                isServiceBounded=false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service=new Intent(this,MessageListenerService.class);
        bindService(service,serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageListenerService.MessageListenerBinder binder=(MessageListenerService.MessageListenerBinder)service;
            backgroundMessageListener=binder.getService();
            isServiceBounded=true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBounded=false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        isServiceBounded=false;
    }
}
