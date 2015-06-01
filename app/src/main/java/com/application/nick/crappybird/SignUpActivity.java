package com.application.nick.crappybird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;



public class SignUpActivity extends Activity {

    private TextView textView;

    private String username, email, password1, password2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sign_up);
        textView = new TextView(this);


    }


    /** Called when the user clicks the Send button */
    public void signUp(View view) {
        // Do something in response to button
        EditText editUsername = (EditText) findViewById(R.id.edit_username);
        EditText editEmail = (EditText) findViewById(R.id.edit_email);
        EditText editPassword1 = (EditText) findViewById(R.id.edit_password1);
        EditText editPassword2 = (EditText) findViewById(R.id.edit_password2);

        username = editUsername.getText().toString();
        email = editEmail.getText().toString();
        password1 = editPassword1.getText().toString();
        password2 = editPassword2.getText().toString();

        if((username.length() >= 3 && username.length() <= 16) && password1.equals(password2) && password1.length() >= 6) {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password1);
            user.setEmail(email);

            //add highscore field
            user.put("highScore", 0);

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Intent returnIntent = new Intent();
                        setResult(200, returnIntent);
                        finish();
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong

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
        } else { //if passwords don't match
            Context context = getApplicationContext();
            CharSequence text = "An error occurred. Please try again.";
            if(username.length() < 3 || username.length() > 12) {
                text = "Username length must be between 3 and 16 characters";
            } else if(!password1.equals(password2)) {
                text = "Passwords do not match";
            } else if(password1.length() < 6) {
                text = "Password must be at least 6 characters";
            }
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

}
