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


public class LoginActivity extends Activity {
    private Intent signUpIntent;
    private TextView textView;

    private String username, password;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        textView = new TextView(this);


    }


    /** Called when the user clicks the Send button */
    public void login(View view) {
        // Do something in response to button
        EditText editUsername = (EditText) findViewById(R.id.edit_username);
        EditText editPassword = (EditText) findViewById(R.id.edit_password);
        username = editUsername.getText().toString();
        password = editPassword.getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Intent returnIntent = new Intent();
                    setResult(200, returnIntent);
                    finish();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
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
