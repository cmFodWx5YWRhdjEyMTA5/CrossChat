package com.example.kamaloli.crosschat.CustomDialogContainer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;
import com.example.kamaloli.crosschat.ChatingUserInterface;
import com.example.kamaloli.crosschat.R;

public class UserDetailDisplay extends DialogFragment{
    Button closeUserDetail,chatWithUser;
    TextView fullName,emailAddress,username,permanentAddress,highestQualification,interestedField,areaOfSpecialization;
    LayoutInflater inflater;
    CommunicationChannel communication;
    SingletonDesignPatternForAbstractXmpp singletonDesign;
    AlertDialog.Builder dialog;
    String rUsername,rEmail,rUsersName;
    View view;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        savedInstanceState=getArguments();
         dialog=new AlertDialog.Builder(getActivity());
        inflater=getActivity().getLayoutInflater();
        singletonDesign=SingletonDesignPatternForAbstractXmpp.getInstance();
        view=inflater.inflate(R.layout.fragment_user_detail_display,null);
//        closeUserDetail=(Button)view.findViewById(R.id.cancel_user_detail);
//        chatWithUser=(Button)view.findViewById(R.id.chat);
        fullName=(TextView)view.findViewById(R.id.full_name);
        emailAddress=(TextView)view.findViewById(R.id.email_address);
        username=(TextView)view.findViewById(R.id.username);
        permanentAddress=(TextView)view.findViewById(R.id.permanent_address);
        highestQualification=(TextView)view.findViewById(R.id.highest_qualification);
        interestedField=(TextView)view.findViewById(R.id.interested_field);
        areaOfSpecialization=(TextView)view.findViewById(R.id.area_of_specialization);
        rUsersName=savedInstanceState.getString("fullName");
        rUsername=savedInstanceState.getString("username");
        rEmail=savedInstanceState.getString("emailAddress");
        fullName.setText(rUsersName);
        emailAddress.setText(rEmail);
        username.setText(rUsername);
        permanentAddress.setText(savedInstanceState.getString("permanentAddress"));
        highestQualification.setText(savedInstanceState.getString("highestQualification"));
        interestedField.setText(savedInstanceState.getString("fieldOfInterest"));
        areaOfSpecialization.setText(savedInstanceState.getString("areaOfSpecialization"));
        dialog.setView(view);
        dialog.setPositiveButton("Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent chattingInterface=new Intent(getActivity(),ChatingUserInterface.class);
                singletonDesign.rUsername=rUsername;
                singletonDesign.rUsersName=rUsername;
                singletonDesign.rUserEmail=rEmail;
                startActivity(chattingInterface);
                getActivity().finish();
            }
        });
        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog.create();
    }

    public interface CommunicationChannel {
        void onExitDialog(boolean flag);
        void onChat(boolean flag);
    }
}
