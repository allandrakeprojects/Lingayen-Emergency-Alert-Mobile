package com.example.user.appalert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.Map;

public class Home extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "NICKEL SAN JUAN";
    //Widgets
    DrawerLayout dl;
    ImageView avatar,iv1,iv2,iv3,iv0,iv4;
    TextView title;

    FirebaseAuth authen;
    FirebaseApp secondary;

    GoogleApiClient googleApiClient;

    DataBaseKo mydb;

    DatabaseReference databaseReference, databaseReference2;
    FirebaseDatabase firebaseDatabase;

    public static String barangay_name,uid,number;
    public static String captain_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GpsOn();
        createNetErrorDialog();

        Firebase.setAndroidContext(getApplicationContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            authen = FirebaseAuth.getInstance();
            uid = user.getUid();
        }

        secondary = FirebaseApp.getInstance("secondary");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference2 = FirebaseDatabase.getInstance(secondary).getReference();

        mydb = new DataBaseKo(this);

        Cursor reader = mydb.getUser();
        init();
        Log.d("HAHAHAHA", "onCreate: "+reader.getCount());
        if(reader.getCount() == 0){
            title.setText("Account Settings");
            FourthFragment ff = new FourthFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
            Home.this.getFragmentManager().popBackStack();
            dl.closeDrawer(0x03);
        }
    }

    private void init(){

        title = (TextView)findViewById(R.id.title);

        dl = (DrawerLayout)findViewById(R.id.drawer_layout);

        iv0 = (ImageView)findViewById(R.id.iv0);
        iv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor reader = mydb.getUser();
                if(reader.getCount() == 0){
                    Toast.makeText(Home.this, "Fill up this form first!", Toast.LENGTH_SHORT).show();
                    title.setText("Account Settings");
                    FourthFragment ff = new FourthFragment();
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
                    Home.this.getFragmentManager().popBackStack();
                    dl.closeDrawer(0x03);
                } else {
                    title.setText("Emergency Alert App");
                    FirstFragment ff = new FirstFragment();
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
                    Home.this.getFragmentManager().popBackStack();
                    dl.closeDrawer(0x03);
                }
            }
        });

        iv1 = (ImageView)findViewById(R.id.iv1);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Emergency contact Person");
                SecondFragment ff = new SecondFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
                Home.this.getFragmentManager().popBackStack();
                dl.closeDrawer(0x03);
            }
        });
        iv2 = (ImageView)findViewById(R.id.iv2);
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Account Settings");
                FourthFragment ff = new FourthFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
                Home.this.getFragmentManager().popBackStack();
                dl.closeDrawer(0x03);
            }
        });
        iv3 = (ImageView)findViewById(R.id.iv3);
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("How to use");
                ThirdFragment ff = new ThirdFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
                Home.this.getFragmentManager().popBackStack();
                dl.closeDrawer(0x03);
            }
        });
        iv4 = (ImageView)findViewById(R.id.iv4);
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(Home.this);
                alert.setCancelable(false);
                alert.setTitle("Sign Out")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                authen.signOut();
                                Intent intent = new Intent(Home.this, UserLogin.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        avatar = (ImageView)findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dl.isDrawerOpen(GravityCompat.START))
                    dl.closeDrawer(0x03);
                else
                    dl.openDrawer((int)GravityCompat.START);
            }
        });
        avatar.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    avatar.setColorFilter(Color.argb(50, 0, 0, 0));
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    avatar.setColorFilter(Color.argb(0, 0, 0, 0));
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        avatar.setColorFilter(Color.argb(0, 0, 0, 0));
                    }
                }
                return false;
            }
        });

        //FirstFragment ff = new FirstFragment();
        //android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
        //Home.this.getFragmentManager().popBackStack();

        FirstFragment ff = new FirstFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, ff).commit();
        Home.this.getFragmentManager().popBackStack();

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child("user").orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<?, ?> value = (Map<?, ?>) child.getValue();
                    //Try name value
                    barangay_name = value.get("bname").toString();
                    Log.d(TAG, "BARANGAY NAME!!!!: "+barangay_name);

                    databaseReference2.child("info").orderByChild("bname").equalTo(barangay_name).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()) {
                                Map<?, ?> value = (Map<?, ?>) child.getValue();
                                //Try name value
                                captain_uid = value.get("uid").toString();
                                number = value.get("cnum").toString();
                                mydb.updateContact("2","Barangay Captain",number,"second");
                                Log.d(TAG, "onDataChange: "+number);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void GpsOn(){
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(Home.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createNetErrorDialog() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need internet connection for this app. Please turn on mobile network or Wi-Fi in Settings.")
                    .setTitle("Unable to connect")
                    .setCancelable(false)
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(i);
                                }
                            }
                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Home.this.finish();
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
