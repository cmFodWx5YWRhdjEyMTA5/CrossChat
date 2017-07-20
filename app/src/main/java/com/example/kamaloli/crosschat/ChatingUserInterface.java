package com.example.kamaloli.crosschat;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;
import com.example.kamaloli.crosschat.communication.MessageHandlerForDisplay;
import com.example.kamaloli.crosschat.communication.MessageStructure;
import com.example.kamaloli.crosschat.communication.UserInformationController;
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
import android.os.Handler;
public class ChatingUserInterface extends AppCompatActivity implements ChatMessageListener,View.OnClickListener{
    final String hostName="54.209.36.83";
    final String serviceName="ip-172-31-21-141.ec2.internal";
    public UserInformationController user;
    final String resourceName="Smack";
    final int port=5222;
    Chat chatting=null;
    ImageButton sendMessageButton;
    SharedPreferences preferences;
    EditText inputMessage;
    AbstractXMPPConnection userConnection=null;
    XMPPTCPConnectionConfiguration.Builder connection;
    ListView messageListView;
    ArrayList<MessageStructure> messageList;
    MessageHandlerForDisplay messageHandlerForDisplay;
    SingletonDesignPatternForAbstractXmpp singletonObject;
    ProgressDialog progressDialog;
    String userName,password,rUsername,rPassword;
    Bundle bundle;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        variableInitialization();
    }
    private void variableInitialization() {
        singletonObject=SingletonDesignPatternForAbstractXmpp.getInstance();
        userName=singletonObject.username;
        password=singletonObject.password;
        progressDialog=new ProgressDialog(ChatingUserInterface.this);
        android.support.v7.app.ActionBar bar=getSupportActionBar();
       // bar.setSubtitle(singletonObject.fullName);
        bar.setTitle(singletonObject.rUsername);
        user=new UserInformationController();
        user.setRemotelyChatingUserInfo(singletonObject.rUsername+"@ip-172-31-21-141.ec2.internal",singletonObject.rUsersName
                ,singletonObject.rUserEmail);
        messageListView=(ListView)findViewById(R.id.message_list_view);
        messageList=new ArrayList<MessageStructure>();
        messageHandlerForDisplay=new MessageHandlerForDisplay(getApplicationContext(),messageList);
        messageListView.setAdapter(messageHandlerForDisplay);
        sendMessageButton=(ImageButton)findViewById(R.id.sendMessageButton);
        inputMessage =(EditText)findViewById(R.id.messageEditText);
        sendMessageButton.setOnClickListener(this);
        handler=new Handler();
        //Initialization of server and user connection for the first time
        BackGroundServerConnection backgroundServerConnection=new BackGroundServerConnection();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait..making things ready for you");
        progressDialog.show();
        backgroundServerConnection.execute(userConnection);
        logMessageHandler("Hello","hi");
        defaultToastMessageHandler(getApplicationContext(),"Hello this is mainActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void bridgeBetweenUserChating(String username,String usersName,String email){
        //This method stores the information about the remote user to whom the current user is chatting.
        user.setRemotelyChatingUserInfo(username,usersName,email);
    }
    @Override
    public void processMessage(Chat chat, Message message) {
        chatting=chat;
        final String messageD=message.getBody();
        if(messageD!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    receiveMessage(messageD,false);
                }
            });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendMessageButton:
                //Toast.makeText(getApplicationContext(),"Message Button is working fine"+chating,Toast.LENGTH_SHORT).show();
                if(chatting!=null){
                        sendingMessage(inputMessage.getText().toString(),true);
                }
        }
    }
    class BackGroundServerConnection extends AsyncTask<AbstractXMPPConnection,Void,AbstractXMPPConnection>{
        @Override
        protected AbstractXMPPConnection doInBackground(AbstractXMPPConnection... params) {
            AbstractXMPPConnection serverConnection=params[0];
            if(serverConnection==null){
                connection=XMPPTCPConnectionConfiguration.builder();
                connection.setHost(hostName);
                connection.setUsernameAndPassword(userName,password);
                connection.setPort(port);
                connection.setServiceName(serviceName);
                connection.setResource(resourceName);
                connection.setDebuggerEnabled(true);
                connection.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                serverConnection= new XMPPTCPConnection(connection.build());
                try {
                    serverConnection.connect();
                    serverConnection.login();
                    ChatManager manager=ChatManager.getInstanceFor(serverConnection);
                    manager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            chat.addMessageListener(new ChatMessageListenerForIncommingMessages());
                        }
                    });
                } catch (SmackException e) {
                    logMessageHandler("SmackException",e+"");
                } catch (IOException e) {
                    logMessageHandler("IOException",e+"");
                } catch (XMPPException e) {
                    logMessageHandler("XMPPException",e+"");
                }
            }


            return serverConnection;
        }
        @Override
        protected void onPostExecute(AbstractXMPPConnection abstractXMPPConnection) {
            if(abstractXMPPConnection.isAuthenticated()){
                logMessageHandler("The user ",abstractXMPPConnection.getUser()+" is authenticated");
                userConnection=abstractXMPPConnection;
                user.setServerConnection(userConnection);
                defaultToastMessageHandler(ChatingUserInterface.this,"Connection Established successfully"+abstractXMPPConnection);
                BackgroundServerConnectionForChatting c=new BackgroundServerConnectionForChatting();
                c.execute(userConnection);
                progressDialog.hide();
                defaultToastMessageHandler(getApplication(),"MessageListener is initialized");
            }
            else{
                defaultToastMessageHandler(getApplication(),"The user is not authenticated");
            }

        }
    }

    public void logMessageHandler(String title,String message){
        Log.e(title,message);
    }

    public void defaultToastMessageHandler(Context c,String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }

    class BackgroundServerConnectionForChatting extends AsyncTask<AbstractXMPPConnection,Void,Chat> {
        @Override
        protected Chat doInBackground(AbstractXMPPConnection... params) {
            Chat chattingObject=null;
            if(chatting==null){
                AbstractXMPPConnection connection = params[0];
                ChatManager manager = ChatManager.getInstanceFor(connection);
                chattingObject = manager.createChat(user.getRemotelyChatingUserName());
                //manager.addChatListener();
            }

            return chattingObject;
        }

        @Override
        protected void onPostExecute(Chat chat) {
            chatting=chat;

        }
    }

    public void sendingMessage(String message,boolean flag){
        MessageStructure object=new MessageStructure(message,flag);
        try {
            chatting.sendMessage(message);
            messageList.add(object);
            messageHandlerForDisplay.notifyDataSetChanged();
            messageListView.setStackFromBottom(true);
            inputMessage.setText("");
        } catch (SmackException.NotConnectedException e) {
           logMessageHandler("SmackException",e.getMessage());
        }
    }
    public void receiveMessage(String message,boolean flag){
        MessageStructure object=new MessageStructure(message,flag);
        messageList.add(object);
        messageHandlerForDisplay.notifyDataSetChanged();
        messageListView.setStackFromBottom(true);
        inputMessage.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        singletonObject.rUsersName=null;
        singletonObject.rUsername=null;
        singletonObject.rUserEmail=null;
    }
    public class ChatMessageListenerForIncommingMessages implements ChatMessageListener{
        @Override
        public void processMessage(Chat chat, Message message) {
            chatting=chat;
            final String messageD=message.getBody();
            if(messageD!=null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        receiveMessage(messageD,false);
                    }
                });

            }
        }
    }
}