package com.example.kamaloli.crosschat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;
import com.example.kamaloli.crosschat.communication.MessageStructure;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageListenerService extends Service implements ChatMessageListener{
    SingletonDesignPatternForAbstractXmpp singleton;
    public HashMap<Integer,String> notificationManager;
    HashMap<String,ArrayList<MessageStructure>> messageSenderInfo;
    @Override
    public void onCreate() {
        messageSenderInfo=new HashMap<>();
        super.onCreate();
    }
    private final IBinder serviceBinder=new MessageListenerBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }
    @Override
    public void processMessage(Chat chat, Message message) {
        if(message.getBody()!=null)
        {
            singleton.chatObject=chat;
            String []userName=message.getFrom().split("@");
            singleton.rUsername=userName[0];
            notificationForMessages(message.getFrom(),message.getBody());
            Log.e("Message",message.getBody()+"");
        }

    }

    public class MessageListenerBinder extends Binder {
        MessageListenerService getService(){
            return MessageListenerService.this;
        }
    }
    public String hello(){
        return "Hello Kamal you have successfully implemented Bound service with IBinder";
    }
    public String initializeMessageListener(){
        String listener="Successfull";
        BackgroundServerConnectionForMessageListener con=new BackgroundServerConnectionForMessageListener();
        con.execute();
        return listener;
    }
    class BackgroundServerConnectionForMessageListener extends AsyncTask<Void,Void,AbstractXMPPConnection>{

        @Override
        protected AbstractXMPPConnection doInBackground(Void... params) {
            singleton=SingletonDesignPatternForAbstractXmpp.getInstance();
            XMPPTCPConnectionConfiguration.Builder connection=XMPPTCPConnectionConfiguration.builder();
            connection.setHost("54.209.36.83");
            connection.setUsernameAndPassword(singleton.username,singleton.password);
            connection.setPort(5222);
            connection.setServiceName("ip-172-31-21-141.ec2.internal");
            connection.setResource("Smack");
            connection.setDebuggerEnabled(true);
            connection.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            AbstractXMPPConnection serverConnection= new XMPPTCPConnection(connection.build());
            try {
                serverConnection.connect();
                serverConnection.login();
                if(serverConnection.isAuthenticated()){
                    ChatManager manager=ChatManager.getInstanceFor(serverConnection);
                    manager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            chat.addMessageListener(MessageListenerService.this);
                        }
                    });
                }
            } catch (SmackException e) {
                Log.e("SmackException",e+"");
            } catch (IOException e) {
                Log.e("IOException",e+"");
            } catch (XMPPException e) {
                Log.e("XMPPException",e+"");
            }
            return serverConnection;
        }

        @Override
        protected void onPostExecute(AbstractXMPPConnection abstractXMPPConnection) {
            if(abstractXMPPConnection.isAuthenticated()){
                Log.e(abstractXMPPConnection.getUser(),"is authenticated");
            }
            else{
                Log.e(abstractXMPPConnection.getUser(),"is not authenticated");
            }
        }
    }
    public void notificationForMessages(String title,String message){

        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.notication1);
        notificationBuilder.setContentTitle("New message from "+singleton.rUsername);
        notificationBuilder.setContentText(message);
        Intent intent=new Intent(this,ChatingUserInterface.class);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatingUserInterface.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notificationBuilder.build());
    }
    class LocationChangeListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
