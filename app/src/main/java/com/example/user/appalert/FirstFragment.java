package com.example.user.appalert;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

public class FirstFragment extends Fragment {

    RelativeLayout layout1;
    ListView listview;
    TextView tt1,num1;
    DataBaseKo mydb;

    DatabaseReference databaseReference,myReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    FirebaseApp secondary;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public static LatLng latLng;
    ArrayList<const_Contacts> contact ;
    Cursor cursorMsg, cursorDtl;
    String name;
    public static String message;
    public static final int RequestPermissionCode  = 1 ;
    String activeUser;
    String color;
    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_first, container, false);

        Firebase.setAndroidContext(getActivity().getApplicationContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            auth = FirebaseAuth.getInstance();
            activeUser = user.getUid();
        }

        secondary = FirebaseApp.getInstance("secondary");
        firebaseDatabase = FirebaseDatabase.getInstance(secondary);
        databaseReference = firebaseDatabase.getReference();

        init(view);
        return view;
    }

    private void init(View view){

        listview = new ListView(getActivity());

        contact = new ArrayList<const_Contacts>();

        EnableRuntimePermission();

        //tt1 = (TextView)view.findViewById(R.id.tt1);
        //tt2 = (TextView)view.findViewById(R.id.tt2);
        //num1 = (TextView)view.findViewById(R.id.num1);
        //num2 = (TextView)view.findViewById(R.id.num2);

        mydb = new DataBaseKo(getActivity());

        getLocationPermission();
        getDeviceLocation();

        //refresh();

        layout1 = (RelativeLayout) view.findViewById(R.id.layout1);
        layout1.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialogClass cdd=new CustomDialogClass(getActivity());
                cdd.setCancelable(true);
                cdd.show();

                cdd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        CustomDialogClass.x = 0;
                    }
                });

                cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(CustomDialogClass.x != 0){
                            Cursor g = mydb.getContact();
                            if(g.getCount() != 0) {

                                SendNotification();
                                sendMessage();
                                saveTransac();
                            }
                        }
                    }
                });
            }
        });

    }

    private void sendMessage() {
        color = String.valueOf(CustomDialogClass.x);
        if(color.equals("1"))
            color = "Fire";
        else if(color.equals("2"))
            color = "Flood";
        else if(color.equals("3"))
            color = "Accident";
        else if(color.equals("4"))
            color = "Earthquake";
        else
            color = "Crime";
        cursorDtl = mydb.getUser();
        cursorDtl.moveToNext();
        message = "I have an Emergency! I am "+cursorDtl.getString(1)+" from "+cursorDtl.getString(2)+", "+cursorDtl.getString(3)+", I need help for "+ color +". Current location: http://www.google.com/maps/place/"+latLng.latitude+","+latLng.longitude;
        Log.d(TAG, "sendMessage: "+message);

        //ALERT: angel 18 female has an Emergency. Here is the current location:
        SmsManager smsManager = SmsManager.getDefault();

                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";
                PendingIntent sentPI = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(getActivity(), 0,new Intent(DELIVERED), 0);
// ---when the SMS has been sent---
                getActivity().registerReceiver(
                        new BroadcastReceiver()
                        {
                            @Override
                            public void onReceive(Context arg0, Intent arg1)
                            {
                                switch(getResultCode())
                                {
                                    case Activity.RESULT_OK:{
                                        Toast.makeText(arg0, "Message sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:{
                                        Toast.makeText(arg0, "Message not sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:{
                                        Toast.makeText(arg0, "Message not sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case SmsManager.RESULT_ERROR_NULL_PDU:{
                                        Toast.makeText(arg0, "Message not sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:{
                                        Toast.makeText(arg0, "Message not sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }, new IntentFilter(SENT));
                // ---when the SMS has been delivered---
                getActivity().registerReceiver(
                        new BroadcastReceiver()
                        {

                            @Override
                            public void onReceive(Context arg0,Intent arg1)
                            {
                                switch(getResultCode())
                                {
                                    case Activity.RESULT_OK:{
                                        Toast.makeText(arg0, "Message sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case Activity.RESULT_CANCELED:{
                                        Toast.makeText(arg0, "Message not sent!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }, new IntentFilter(DELIVERED));

                Log.d(TAG, "sendMessage: "+Home.number);
                smsManager.sendTextMessage(Home.number, null, "ALERT: " + message, sentPI, deliveredPI);

        ContentValues values = new ContentValues();
        values.put("address", Home.number);
        values.put("body","ALERT: "+message);
        getActivity().getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/sent"), values);

    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(),
                Manifest.permission.READ_CONTACTS) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.SEND_SMS))
        {

            Toast.makeText(getActivity(),"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS}, RequestPermissionCode);

        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            //Log.d(TAG, latLng.toString());

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: Security Exception: "+e.getMessage());
        }

    }

    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
            } else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                }
            }
        }

        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(),"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void SendNotification() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    String send_email = Home.captain_uid;

                    Log.d(TAG, "QWEQWEQWEQWEQWEQWEQWEWQE: "+send_email);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YjcyMjhmYzktZWQzYS00OGIwLWIyZTMtNTg3NDE2MTY2MmEw");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"0202a9b6-8812-492a-a39f-514a7316e33b\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User Id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Emergency!\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    private void saveTransac(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        String read = "false";

        dateToStr = dateToStr.replace(" ","");
        dateToStr = dateToStr.replace(":","");
        dateToStr = dateToStr.replace("-","");
        dateToStr = dateToStr.substring(0,12);

        String id = databaseReference.child("transaction").push().getKey();
        const_Transaction trans = new const_Transaction(id,activeUser,Home.captain_uid,message,cursorDtl.getString(1),color,Long.parseLong(dateToStr),read,Long.parseLong(dateToStr)*-1);
        databaseReference.child("transaction").child(Home.captain_uid).child(id).setValue(trans);
    }

}
