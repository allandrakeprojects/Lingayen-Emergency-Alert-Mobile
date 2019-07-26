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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class SecondFragment extends Fragment {


    ImageView first,second,third,fourth,fifth,sixth;
    ListView listview;
    TextView tt1,tt2,num1,num2;
    DataBaseKo mydb;

    ArrayList<const_Contacts> contact ;
    Cursor cursor, cursor2, cursorMsg, cursorDtl;
    String name, phonenumber ;
    public static String message;
    public static final int RequestPermissionCode  = 1 ;
    public static String status;
    public static Dialog dialog;

    int number_of_clicks = 0;
    boolean thread_started = false;
    final int DELAY_BETWEEN_CLICKS_IN_MILLISECONDS = 250;

    public SecondFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        init(view);
        return view;
    }

    private void init(View view){

        tt1 = (TextView)view.findViewById(R.id.tt1);
        num1 = (TextView)view.findViewById(R.id.num1);

        listview = new ListView(getActivity());

        contact = new ArrayList<const_Contacts>();

        mydb = new DataBaseKo(getActivity());

        fifth = (ImageView)view.findViewById(R.id.fifth);

        EnableRuntimePermission();

        fifth.setOnTouchListener(new View.OnTouchListener() {

            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        fifth.setImageDrawable(getResources().getDrawable(R.drawable.cont, getActivity().getApplicationContext().getTheme()));
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        fifth.setImageDrawable(getResources().getDrawable(R.drawable.cont_shadow, getActivity().getApplicationContext().getTheme()));
                    }
                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                        fifth.setImageDrawable(getResources().getDrawable(R.drawable.cont_shadow, getActivity().getApplicationContext().getTheme()));
                    }
                } else {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        fifth.setImageDrawable(getResources().getDrawable(R.drawable.cont));
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        fifth.setImageDrawable(getResources().getDrawable(R.drawable.cont_shadow));
                    }
                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                        fifth.setImageDrawable(getResources().getDrawable(R.drawable.cont_shadow));
                    }
                }

                return false;
            }
        });
        fifth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ++number_of_clicks;
                if(!thread_started){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            thread_started = true;
                            try {
                                Thread.sleep(DELAY_BETWEEN_CLICKS_IN_MILLISECONDS);
                                if(number_of_clicks == 1){
                                    cursorDtl = mydb.getUser();
                                    cursorDtl.moveToNext();
                                    message = cursorDtl.getString(1)+" from "+cursorDtl.getString(2)+", "+cursorDtl.getString(3)+" have an emergency. Here is the current location http://www.google.com/maps/place/"+FirstFragment.latLng.latitude+","+FirstFragment.latLng.longitude;
                                    sendMessage();

                                } else if(number_of_clicks == 2){
                                    cursor = getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
                                    contact.clear();
                                    while (cursor.moveToNext()) {
                                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                        phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        const_Contacts s = new const_Contacts(name,phonenumber);
                                        contact.add(s);
                                    }
                                    cursor.close();

                                    status = "first";
                                    ContactList adapter = new ContactList(getActivity(), contact);
                                    listview.setAdapter(adapter);
                                    Searcher();

                                }
                                number_of_clicks = 0;
                                thread_started = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }




            }
        });

        refresh();
    }

    private void sendMessage() {
        cursorMsg = mydb.getContact();
        Log.d(TAG, "sendMessage: "+message);

        String una="",pangalawa="";
        boolean zxc = false;

        //ALERT: angel 18 female has an Emergency. Here is the current location:
        SmsManager smsManager = SmsManager.getDefault();
        while(cursorMsg.moveToNext()){


            if(!cursorMsg.getString(2).equals("")) {

                zxc = true;
                una = cursorMsg.getString(2);
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

                Log.d(TAG, "sendMessage: "+cursorMsg.getString(2));
                smsManager.sendTextMessage(cursorMsg.getString(2), null, message, sentPI, deliveredPI);

            }


        }
        if(zxc) {
            ContentValues values = new ContentValues();
            values.put("address", una);
            values.put("body", message);
            getActivity().getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/sent"), values);
            zxc=false;
        }
    }

    private void refresh() {
        cursor2 = mydb.getContact();
        if(cursor2.getCount() > 0){
            while (cursor2.moveToNext()){

                if(cursor2.getString(3).equals("first")) {
                    tt1.setText(cursor2.getString(1));
                    num1.setText(cursor2.getString(2));
                }
            }
        }
    }

    void Searcher(){
        //dialog
        final LinearLayout lll = new LinearLayout(getActivity());
        lll.setOrientation(LinearLayout.VERTICAL);
        lll.addView(listview);
        runOnUiThread(new Runnable() {
            public void run() {
                dialog = new Dialog(getActivity());
                dialog.setContentView(lll);
                dialog.setTitle("Search List");
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                lp.x = 100; // The new position of the X coordinates
                lp.y = 100; // The new position of the Y coordinates
                lp.width = -1;
                lp.height = -2;
                lp.alpha = 1.0f; // Transparency
                dialog.setCancelable(true);
                dialogWindow.setAttributes(lp);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        lll.removeAllViews();
                        refresh();
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        lll.removeAllViews();
                        refresh();
                    }
                });
            }
        });
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(),
                android.Manifest.permission.READ_CONTACTS) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.SEND_SMS))
        {

            Toast.makeText(getActivity(),"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    android.Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(),"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}
