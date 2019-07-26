package com.example.user.appalert;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

//Opens when get started is clicked
public class CustomDialogClass extends Dialog{

public Activity c;
public Dialog d;
public Button yes, no, or, eq, cr;
public static int x = 0;

public CustomDialogClass(Activity a) {
    super(a);
    this.c = a;
}
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main2);


    //Get the widget id from the xml file
    yes = (Button) findViewById(R.id.btn_yes);
    no = (Button) findViewById(R.id.btn_no);
    or = (Button) findViewById(R.id.btn_or);
    eq = (Button) findViewById(R.id.btn_earth);
    cr = (Button) findViewById(R.id.btn_crime);

    //Owner option
    yes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //close this form
            x=1;
            dismiss();
        }
    });

    //Customer option
    no.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //close this form
            x=2;
            dismiss();
        }
    });

    or.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //close this form
            x=3;
            dismiss();
        }
    });

    eq.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            x=4;
            dismiss();
        }
    });

    cr.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            x=5;
            dismiss();
        }
    });
}
}
