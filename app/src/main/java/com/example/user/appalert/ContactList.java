package com.example.user.appalert;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Mansaii on 1/14/2018.
 */

public class ContactList extends ArrayAdapter<const_Contacts> {

    DataBaseKo mydb;
    private Activity context;
    private List<const_Contacts> services;

    public ContactList(@NonNull Activity context, List<const_Contacts> services) {
        super(context, R.layout.contact_list, services);
        this.context = context;
        this.services = services;
    }

    public View getView(int pos, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        mydb = new DataBaseKo(context);
        final View listViewItem = inflater.inflate(R.layout.contact_list, null, true);

        final TextView name = (TextView)listViewItem.findViewById(R.id.name);
        final TextView number = (TextView)listViewItem.findViewById(R.id.number);
        LinearLayout ll1 = (LinearLayout)listViewItem.findViewById(R.id.ll1);

        final const_Contacts service = services.get(pos);
        name.setText(service.getName());
        number.setText(service.getNumber().toString());

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SecondFragment.status.equals("first")){

                    mydb.updateContact("1",name.getText().toString(),number.getText().toString(),"first");

                }
                SecondFragment.dialog.dismiss();
//
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";
                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,new Intent(DELIVERED), 0);
// ---when the SMS has been sent---
                context.registerReceiver(
                        new BroadcastReceiver()
                        {
                            @Override
                            public void onReceive(Context arg0, Intent arg1)
                            {
                                switch(getResultCode())
                                {
                                    case Activity.RESULT_OK:

                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        break;
                                }
                            }
                        }, new IntentFilter(SENT));
                // ---when the SMS has been delivered---
                context.registerReceiver(
                        new BroadcastReceiver()
                        {

                            @Override
                            public void onReceive(Context arg0,Intent arg1)
                            {
                                switch(getResultCode())
                                {
                                    case Activity.RESULT_OK:
                                        break;
                                    case Activity.RESULT_CANCELED:
                                        break;
                                }
                            }
                        }, new IntentFilter(DELIVERED));

                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(number.getText().toString(), null,
                        "Good day this message is sent to inform you that I put your number on my list of emergency contact person",
                        sentPI, deliveredPI);

//
                ContentValues values = new ContentValues();
                values.put("address", number.getText().toString());
                values.put("body", "Good day this message is sent to inform you that I put your number on my list of emergency contact person");
                context.getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/sent"), values);

            }
        });

        return  listViewItem;
    }
}
