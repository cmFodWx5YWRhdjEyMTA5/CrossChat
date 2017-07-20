package com.example.kamaloli.crosschat.SignUpComponets;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kamaloli.crosschat.R;
import com.example.kamaloli.crosschat.SignIn;

/**
 * Created by KAMAL OLI on 08/03/2017.
 */

public class UserInfoOfSignUpFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    EditText usersName,username,emailAddress,password,rePassword,mobileNumber;
    InterFragmentCommunicationSignUp communication;
    TextView alreadyHaveAccount;
    Button nextButton;
    String question;
    String[] QUESTIONS =
            {"Select Your Security Question","What is your pet’s name?", "Where did you meet your spouse?", "What Town was your Mother born in?", "What  is the name of your favourite Uncle?","What is your ID card Number?","In which year your were born?","What is your favourite food?"};
    ArrayAdapter<String> aa;
    EditText ans;
    Spinner sp;
    TextView refToSignin;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.user_info_of_sign_up,container,false);
        usersName=(EditText)view.findViewById(R.id.users_name);
        username=(EditText)view.findViewById(R.id.username);
        emailAddress=(EditText)view.findViewById(R.id.email_address);
        password=(EditText)view.findViewById(R.id.password);
        rePassword=(EditText)view.findViewById(R.id.re_password);
        alreadyHaveAccount=(TextView)view.findViewById(R.id.already_have_account);
        nextButton=(Button)view.findViewById(R.id.next_button);
        sp= (Spinner) view.findViewById(R.id.security_question);
        ans= (EditText)view.findViewById(R.id.security_answer);
        mobileNumber=(EditText)view.findViewById(R.id.mobile_number);
        refToSignin=(TextView)view.findViewById(R.id.already_have_account);
        refToSignin.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        sp.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,QUESTIONS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        sp.setAdapter(aa);

        return view;
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        question=parent.getItemAtPosition(pos).toString();

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communication =(InterFragmentCommunicationSignUp)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"Must implement receiveUserData");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_button:
                String usn=usersName.getText().toString().trim();
                String un=username.getText().toString().trim();
                String ea=emailAddress.getText().toString().trim();
                String pw=password.getText().toString().trim();
                String rpw=rePassword.getText().toString().trim();
                String mn=mobileNumber.getText().toString().trim();
                String answer=ans.getText().toString().trim();
                if(question.equals("Select Your Security Question"))
                {
                    showDialog("Please Select proper question");
                }
                else{
                    if(usn.isEmpty()||un.isEmpty()
                            ||ea.isEmpty()||pw.isEmpty()
                            ||rpw.isEmpty()||mn.isEmpty() || answer.isEmpty()){
                        showDialog(getResources().getString(R.string.form_field_left_blank_message));
                    }
                    else{
                        if(!pw.equals(rpw)){
                            showDialog("Password you entered does not match.Please try again.");

                        }
                        else {
                            Bundle bundle=new Bundle();
                            bundle.putString("usersName",usn);
                            bundle.putString("username",un);
                            bundle.putString("emailAddress",ea);
                            bundle.putString("password",pw);
                            bundle.putString("mobileNumber",mn);
                            bundle.putString("question",question);
                            bundle.putString("answer",answer);
                            communication.receiveUserData(bundle);
                        }
                    }}


                break;
            case R.id.already_have_account:
                Intent ref=new Intent(getActivity(),SignIn.class);
                ref.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ref);

                getActivity().overridePendingTransition(0,0);
            break;
        }
    }
    public interface InterFragmentCommunicationSignUp{
        void receiveUserData(Bundle data);
    }
    void showDialog(String message){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        //LayoutInflater dialogLayout=getActivity().getLayoutInflater();
        dialog.setTitle("Oops!! something went wrong");
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.create();
        dialog.show();

    }

}
