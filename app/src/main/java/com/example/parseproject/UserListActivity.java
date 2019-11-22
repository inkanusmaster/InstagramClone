package com.example.parseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        final ListView usersListView = findViewById(R.id.usersListView);
        final ArrayList<String> usersArrayList = new ArrayList<>();
        final ArrayAdapter<String> usersArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,usersArrayList);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername()); // nie chcemy wyświetlić swojego usera
//        query.whereExists("username"); // wszyscy userzy
        query.addAscendingOrder("username"); // sortujemy rosnaco po username
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null){
                    if (objects.size() > 0){
                        for (ParseUser user : objects){ // lecimy po objektach ParseUser i dodajemy username z getUsername do arraylist
                            usersArrayList.add(user.getUsername());
                        }
                            usersListView.setAdapter(usersArrayAdapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}
