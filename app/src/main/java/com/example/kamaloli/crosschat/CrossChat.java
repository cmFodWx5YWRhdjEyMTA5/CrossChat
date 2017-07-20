package com.example.kamaloli.crosschat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;

public class CrossChat extends AppCompatActivity {
    FragmentManager manager;
    MessageListenerService messageListenerService;
    boolean isServiceBounded=false;
    SingletonDesignPatternForAbstractXmpp userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_chat);
        userInfo=SingletonDesignPatternForAbstractXmpp.getInstance();
        if(userInfo.isUserCredentialPresent(getApplicationContext())){
            manager=getFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.add(R.id.activity_cross_chat,new MapPresentation()).commit();
        }
        else{
            Intent intent=new Intent(CrossChat.this,SignIn.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    protected void onStart() {

        super.onStart();
        Intent messageListenerService=new Intent(this,MessageListenerService.class);
        bindService(messageListenerService,serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageListenerService.MessageListenerBinder binder=(MessageListenerService.MessageListenerBinder)service;
            messageListenerService=binder.getService();
            isServiceBounded=true;
            messageListenerService.initializeMessageListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBounded=false;
        }
    };
    @Override
    protected void onResume() {
        super.onResume();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userInfo.isUserCredentialPresent(getApplicationContext())){
            unbindService(serviceConnection);
            isServiceBounded=false;
        }
    }
}
