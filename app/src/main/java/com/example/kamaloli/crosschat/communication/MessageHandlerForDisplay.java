package com.example.kamaloli.crosschat.communication;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kamaloli.crosschat.R;

import java.util.ArrayList;

/**
 * Created by KAMAL OLI on 12/02/2017.
 */

public class MessageHandlerForDisplay extends BaseAdapter {
    ArrayList<MessageStructure> messageList;
    LayoutInflater inflater;
    Context mainActivityContext;

    public MessageHandlerForDisplay(Context context, ArrayList<MessageStructure> messages){
        mainActivityContext=context;
        messageList=messages;
        inflater= (LayoutInflater) mainActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageStructure indivisualMessage=(MessageStructure)messageList.get(position);
        View view=convertView;
        if(convertView==null)
            view=inflater.inflate(R.layout.bubble_chat_indivisual_message_layout,null);
        TextView messageHolder=(TextView)view.findViewById(R.id.message_text);
        messageHolder.setText(indivisualMessage.message);
        LinearLayout layoutParent=(LinearLayout)view.findViewById(R.id.chating_bubble_layout_parent);
        LinearLayout layoutBubbleChild=(LinearLayout)view.findViewById(R.id.chating_bubble_layout);

        if(indivisualMessage.didIComposeIt){
            layoutBubbleChild.setBackgroundResource(R.drawable.bubble2);
            layoutParent.setGravity(Gravity.RIGHT);
            messageHolder.setText(indivisualMessage.message);
        }
        else{
            layoutBubbleChild.setBackgroundResource(R.drawable.bubble1);
            layoutParent.setGravity(Gravity.LEFT);
        }
        messageHolder.setTextColor(Color.BLACK);
        return view;
    }
}
