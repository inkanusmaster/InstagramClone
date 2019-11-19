package com.example.parseproject;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    int checkScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        checkScore = Integer.parseInt(Objects.requireNonNull(object.getString("score")));
                        if (checkScore > 40) {
                            checkScore += 20;
                            object.put("score", String.valueOf(checkScore));
                            object.saveInBackground();
                        }

                        Log.i("USERNAME", object.getString("username"));
                        Log.i("SCORE", object.getString("score"));
                    }
                }
            }
        });


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
