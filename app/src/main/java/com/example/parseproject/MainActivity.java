package com.example.parseproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    String username, password;
    EditText usernameEditText, passwordEditText;
    Button loginButton, signUpButton;

    public void login(View view) {
        ParseUser.logInInBackground(String.valueOf(usernameEditText.getText()), String.valueOf(passwordEditText.getText()), new LogInCallback() {
            @SuppressLint("ShowToast")
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(MainActivity.this, "LOGIN SUCCESS!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "INVALID CREDENTIALS!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void signup(View view) {
        username = String.valueOf(usernameEditText.getText());
        password = String.valueOf(passwordEditText.getText());
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "USERNAME OR PASSWORD CANNOT BE EMPTY!", Toast.LENGTH_SHORT).show();
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("User"); //ZMIEN PARESEOBJECT NA PARSEUSER!!!!
            query.whereEqualTo("username",username);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e==null){
                        Toast.makeText(MainActivity.this, "USERNAME EXISTS!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ParseUser user = new ParseUser();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(MainActivity.this, "USER CREATED SUCCESSFULY!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "USER NOT CREATED! SOMETHING WENT WRONG!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);


//
//        ParseUser.logInInBackground("fury", "qweasdzxc", new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (user != null) {
//                    Log.i("Success!", "We logged in");
//                } else {
//                    Log.i("LOGIN FAILED", "INVALID CREDENTIALS");
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        ParseUser.logOut();
//
//        if (ParseUser.getCurrentUser() != null) {
//            Log.i("Signed In", ParseUser.getCurrentUser().getUsername());
//        } else {
//            Log.i("Not luck", "Not signed in");
//        }
//
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
