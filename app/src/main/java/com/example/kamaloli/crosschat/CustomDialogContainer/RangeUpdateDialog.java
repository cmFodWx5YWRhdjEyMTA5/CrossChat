package com.example.kamaloli.crosschat.CustomDialogContainer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.kamaloli.crosschat.Authentication.SingletonDesignPatternForAbstractXmpp;
import com.example.kamaloli.crosschat.R;

/**
 * Created by KAMAL OLI on 19/03/2017.
 */

public class RangeUpdateDialog extends DialogFragment{
    EditText range;
    LayoutInflater inflater;
    SingletonDesignPatternForAbstractXmpp singleton;
    View view;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater=getActivity().getLayoutInflater();
        view=inflater.inflate(R.layout.dialog_layout,null);
        singleton=SingletonDesignPatternForAbstractXmpp.getInstance();
        range=(EditText)view.findViewById(R.id.range);
        AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        dialog.setView(view);
        dialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!(range.getText().toString().trim().isEmpty())){
                    singleton.rangeForMap=Integer.parseInt(range.getText().toString().trim());
//                    final int r=Integer.parseInt(range.getText().toString().trim());
//                    SharedPreferences preferences=getActivity().getSharedPreferences("userCredentials",getActivity().MODE_PRIVATE);
//                    SharedPreferences.Editor editor=preferences.edit();
//                    editor.putInt("rangeForMap",r);
//                    editor.commit();
//                    Log.e("Value",r+"");
                }
                else{
                    singleton.setRangeForMapDisplay(getActivity(),0);
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog.create();
    }
}
