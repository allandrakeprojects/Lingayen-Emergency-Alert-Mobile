package com.example.user.appalert;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class UserLogin extends AppCompatActivity {

    //Firebase
    FirebaseAuth firebaseAuth;

    //Widgets
    EditText mEmail, mPassword;
    TextView mSignUp;
    Button mLogin;
    ProgressDialog progressDialog;

    public static int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //Start
        handler.postDelayed(runnable, 5000);

        if(x==0) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyCktVjMB4PC-uv5HV0F_Ns6QNPK3VsmVOM")
                    .setStorageBucket("alertapp1-47d0a.appspot.com")
                    .setApplicationId("1:738948375197:android:37f8de51eeafc53d")
                    .setDatabaseUrl("https://alertapp1-47d0a.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(this, options, "secondary");
        }
        x++;

        //hide soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //check if an account is already logged in
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(this, Home.class));
            finish();
        }

        init();
    }

    // Init
    boolean isRunning = true;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isRunning){
                isRunning = false;
                if(firebaseAuth.getUid() != null){
                    Notification(new MyCallback() {
                        @Override
                        public void onCallback(int count_tran, int count) {
                            isRunning = true;
                        }
                    });
                } else{
                    isRunning = true;
                }

            }

            handler.postDelayed(runnable, 5000);
        }
    };





    public interface MyCallback {
        void onCallback(int count_tran, int count);
    }

    FirebaseApp secondary;
    FirebaseDatabase mFirebaseDatabase_info;
    DatabaseReference databaseReference_info;
    FirebaseDatabase mFirebaseDatabase_tran;
    DatabaseReference databaseReference_tran;
    int count_tran;
    int count = 0;
    int count_detect = 0;
    boolean count_tran_detect = false;
    private void Notification(final MyCallback myCallback){
        try{
            secondary = FirebaseApp.getInstance("secondary");
            mFirebaseDatabase_info = FirebaseDatabase.getInstance(secondary);
            databaseReference_info = mFirebaseDatabase_info.getReference().child("info");
            databaseReference_info.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {

                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    for(com.google.firebase.database.DataSnapshot preschool : dataSnapshot.getChildren())
                    {
                        mFirebaseDatabase_tran = FirebaseDatabase.getInstance(secondary);
                        databaseReference_tran = mFirebaseDatabase_tran.getReference().child("transaction").child(preschool.getKey().toString());
                        databaseReference_tran.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                for(com.google.firebase.database.DataSnapshot preschool : dataSnapshot.getChildren())
                                {
                                    if(preschool.child("u_id").getValue().toString().equals(firebaseAuth.getUid()) && preschool.child("read").getValue().toString().equals("false")){
                                        if(!count_tran_detect){
                                            count_tran++;
                                        }
                                        count++;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(UserLogin.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    if(count_tran > 0) {
                        count_tran_detect = true;
                    }
                    if(count > count_tran){
                        int asd = count - count_tran;
                        count_tran+=asd;
                    }
                    if(count_tran > count){
                        int asd = count_tran - count;
                        count_tran-=asd;

                        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());
                        b.setAutoCancel(true)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.emergency_button)
                                .setTicker("App Alert")
                                .setContentTitle("Your alert has been received.")
                                .setContentText("Emergency App Alert");
                        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(1, b.build());
                    }

                    myCallback.onCallback(count_tran, count);
                    count = 0;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(UserLogin.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception err) {
            Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void init(){

        progressDialog = new ProgressDialog(this);

        mEmail = (EditText)findViewById(R.id.username);
        mPassword = (EditText)findViewById(R.id.password);

        mSignUp = (TextView)findViewById(R.id.btn1);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserLogin.this, UserSignUp.class);
                startActivity(intent);
                finish();
            }
        });

        mLogin = (Button)findViewById(R.id.btn);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(user)){
                    Toast.makeText(UserLogin.this, "Please enter E-mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(UserLogin.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Logging in...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(UserLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference ref = database.child("user");
                            final List<String> areas = new ArrayList<String>();
                            Query phoneQuery = ref.orderByChild("uid").equalTo(firebaseAuth.getUid());
                            phoneQuery.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                @Override
                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                    for(com.google.firebase.database.DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                        String testtest = singleSnapshot.child("status").getValue(String.class);
                                        areas.add(testtest);
                                    }

                                    String allItems = "";
                                    for(String str : areas){
                                        allItems = str;
                                    }

                                    if(allItems.trim().toString().equals("A")){
                                        finish();
                                        startActivity(new Intent(UserLogin.this, Home.class));
                                    } else if(allItems.trim().toString().equals("C")){
                                        Toast.makeText(UserLogin.this, "Your account is canceled by the administrator", Toast.LENGTH_SHORT).show();
                                    } else{
                                        Toast.makeText(UserLogin.this, "Your account is not yet confirm by the administrator", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(UserLogin.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(UserLogin.this, "E-mail and Password didn't match", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
    }
}