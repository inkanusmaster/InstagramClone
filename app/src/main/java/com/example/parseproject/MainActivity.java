package com.example.parseproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    String username, password;
    EditText usernameEditText, passwordEditText;
    ConstraintLayout backgroundLayout;
    ImageView logoImageView;

    // po to implementujemy onkeylistener, żeby po kliknięciu w enter na klawiaturze...
    //... LOGOWALO USERA
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            login(view);
        }
        return false;
    }

    // po to implementujemy onclicklistener żeby po kliknięciu w background czy w logo, chowała się klawiatura. A oto metoda onclick
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backgroundLayout || view.getId() == R.id.logoImageView) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //te 2 linijki chowają klawę
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }
    }

    // otwiera drugą aktywność z listą userów. To będzie po zalogowaniu
    public void showUserList(){
        Intent intentUserListActivity = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intentUserListActivity);
    }

    public void login(View view) { // linijka niżej tolowercase bo moge sie zalogować jako Fury czy fury to jest to samo..
        ParseUser.logInInBackground(String.valueOf(usernameEditText.getText()).toLowerCase(), String.valueOf(passwordEditText.getText()), new LogInCallback() {
            @SuppressLint("ShowToast")
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(MainActivity.this, "LOGIN SUCCESS!", Toast.LENGTH_SHORT).show();
                    showUserList();

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
            username = username.toLowerCase(); // zapisujemy w bazie małymi literami!
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", username);
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        Toast.makeText(MainActivity.this, "USERNAME EXISTS!", Toast.LENGTH_SHORT).show();
                    } else {
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
        backgroundLayout = findViewById(R.id.backgroundLayout);
        logoImageView = findViewById(R.id.logoImageView);
        logoImageView.setOnClickListener(this); //żeby po kliknięciu klawiatura się chowała to implementujemy to
        backgroundLayout.setOnClickListener(this); //j.w.

        // jeśli już jesteśmy zalogowani (po uruchomieniu aplikacji) to też pokazuje listę userów
//        if(ParseUser.getCurrentUser() != null){
//            showUserList();
//        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
