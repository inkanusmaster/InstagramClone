package com.example.parseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Objects;

public class UserFeedActivity extends AppCompatActivity {

    LinearLayout feedLinearLayout; // nasz linearlayout będzie trzymał obrazki
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        feedLinearLayout = findViewById(R.id.feedLinearLayout);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username"); // username bo parametr username nazwany został przesłany do intentu
        setTitle(username+"'s photos");

        // będziemy brali imaż usera
        ParseQuery<ParseObject> query = new ParseQuery<>("Image");
        query.whereEqualTo("username", username); // usera konkretnego szukamy. Bierzemy wartość z przesłanego intent (pamiętasz get(i)?)
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        ParseFile file = (ParseFile) object.get("image"); // parsujemy pliczek do file, ale musimy go zaraz jeszcze ściągnąć
                        assert file != null;
                        file.getDataInBackground(new GetDataCallback() { // pobieramy pliczek
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) { // jeśli nie ma błędu i data nie jest puste
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); // tutaj pobieramy, dekodujemy, i parametry
                                    imageView = new ImageView(getApplicationContext()); // obrazki będzie trzymał
                                    imageView.setLayoutParams(new ViewGroup.MarginLayoutParams( // parametry naszego imageview co to będzie trzymał obrazki
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));
                                    imageView.setImageBitmap(bitmap); // wrzucamy w imageview nasz obrazek
                                    feedLinearLayout.addView(imageView); // dodajemy imageview do linearlayout
                                } else {
                                    assert e != null;
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}