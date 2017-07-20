package com.example.kamaloli.crosschat;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;

public class SharedPreferenceExample extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preference_example);
        Button button=(Button)findViewById(R.id.button4);
        Button btn=(Button)findViewById(R.id.btn);
        textView=(TextView)findViewById(R.id.textView2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences=getSharedPreferences("creadential",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("kamal","Oli");
                editor.commit();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingletonDesignPatternForAbstractXmpp patternForAbstractXmpp=SingletonDesignPatternForAbstractXmpp.getInstance();
                //textView.setText(preferences.getString("username",null)+preferences.getString("password",null));
                textView.setText(patternForAbstractXmpp.isUserCredentialPresent(getApplicationContext())+"");
            }
        });
    }
}
