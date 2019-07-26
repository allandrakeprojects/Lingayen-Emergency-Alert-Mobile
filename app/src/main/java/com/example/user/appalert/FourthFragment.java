package com.example.user.appalert;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FourthFragment extends Fragment {

    EditText edtName, edtAge, edtAddress;
    Button btnSave;
    DataBaseKo mydb;
    Spinner spinner1;

    public FourthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        init(view);
        return view;
    }

    private void init(View view) {

        mydb = new DataBaseKo(getActivity());

        spinner1 = (Spinner)view.findViewById(R.id.spinner1);
        edtName = (EditText)view.findViewById(R.id.edtname);
        edtAge = (EditText)view.findViewById(R.id.edtage);
        //edtAddress = (EditText)findViewById(R.id.edtaddress);

        btnSave = (Button)view.findViewById(R.id.save);

        Cursor reader = mydb.getUser();
        if(reader.getCount() > 0){
            reader.moveToNext();
            edtName.setText(reader.getString(1));
            edtAge.setText(reader.getString(2));
            if(reader.getString(3).equals("Male")){
                spinner1.setSelection(0);
            } else{
                spinner1.setSelection(1);
            }
            //edtAddress.setText(reader.getString(3));
        }

        Cursor cReader = mydb.getContact();
        if(cReader.getCount() < 1){
            mydb.insertContact("","","first");
            mydb.insertContact("","","second");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString(), age = edtAge.getText().toString(), address = spinner1.getSelectedItem().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(address)){
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor update = mydb.getUser();
                if(update.getCount() > 0){
                    mydb.updateUser("1",name,age,address);
                    Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    return;
                }

                mydb.insertUser(name,age,address);
                Toast.makeText(getActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
