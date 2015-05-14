package com.application.nick.crappybird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPasswordActivity extends Activity {
    private TextView textView;

    private String email;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_forgot_password);
        textView = new TextView(this);


    }


    /** Called when the user clicks the Send button */
    public void recover(View view) {
        // Do something in response to button
        EditText editEmail = (EditText) findViewById(R.id.edit_email);
        email = editEmail.getText().toString();

        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // An email was successfully sent with reset instructions.
                    Context context = getApplicationContext();
                    CharSequence text = "Please check your email for password reset instructions";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    finish();
                } else {
                    // Something went wrong. Look at the ParseException to see what's up.
                    e.printStackTrace();

                    String eString = e.toString();
                    eString = eString.substring(eString.indexOf(":") + 2);

                    Context context = getApplicationContext();
                    CharSequence text = eString;
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });



    }
}
