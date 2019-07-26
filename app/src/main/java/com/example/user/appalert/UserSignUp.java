package com.example.user.appalert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserSignUp extends AppCompatActivity {

    private static final String TAG = "TAG";
    //Firebase
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase, databaseReference;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseApp secondary;

    //Widgets
    EditText mEmail, mPassword;
    Spinner spinner;
    TextView mLogin;
    Button mSignUp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        secondary = FirebaseApp.getInstance("secondary");
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance(secondary);
        databaseReference = mFirebaseDatabase.getReference().child("info");

        //hide soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        init();
        database();
    }

    private void database() {
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();

                for (com.google.firebase.database.DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("bname").getValue(String.class);
                    areas.add(areaName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(UserSignUp.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init(){
        progressDialog = new ProgressDialog(this);

        spinner = (Spinner)findViewById(R.id.spinner);
        mEmail = (EditText)findViewById(R.id.username);
        mPassword = (EditText)findViewById(R.id.password);

        mLogin = (TextView)findViewById(R.id.txtview);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSignUp.this, UserLogin.class);
                startActivity(intent);
                finish();
            }
        });

        mSignUp = (Button)findViewById(R.id.btn);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();
                String name;
                try {
                    name = spinner.getSelectedItem().toString();
                } catch(Exception n){
                    name = "";
                }

                if(TextUtils.isEmpty(user)){
                    Toast.makeText(UserSignUp.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(UserSignUp.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(UserSignUp.this, "Please select your barangay", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Registering User...");
                progressDialog.show();
                final String finalName = name;
                firebaseAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(UserSignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            Firebase.setAndroidContext(UserSignUp.this);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            writeNewUser(uid, finalName);
                            //NEW
//                            sendEmailVerification();
                        }
                        else{
                            Toast.makeText(UserSignUp.this, "Could not Register. "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(UserSignUp.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    private void writeNewUser(String uid, String name) {
        const_Profile prof = new const_Profile();
        prof.setStatus("X");
        prof.setEmail(mEmail.getText().toString().trim());
        prof.setBname(name);
        prof.setUid(uid);
        mDatabase.child("user").child(uid).setValue(prof);
    }
}
