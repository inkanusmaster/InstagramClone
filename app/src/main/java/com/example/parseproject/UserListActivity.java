package com.example.parseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    @Override // otrzymujemy dane z drugiego intentu tą metodą
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                // lokacja naszego zaznaczonego pliku
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Toast.makeText(this, "IMAGE SELECTED!", Toast.LENGTH_SHORT).show();

                // I JEDZIEMY. WYSYLAMY DO PARSA ZAZNACZONY OBRAZ
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // kompresujemy do png, jakości 100 i wysyłamy do stream
                byte[] byteArray = stream.toByteArray(); // robimy bytearray i wrzucamy tam stream.
                ParseFile file = new ParseFile("imagetest.png", byteArray); // parsujemy pliczek, nadajemy mu nazwę image.png. Odczytujemy go z bytearray

                ParseObject object = new ParseObject("Image"); // robimy nową klasę Image gdzie będą przechowywane obrazy
                object.put("image", file); // wrzucamy zparsowany plik z bytearray do utworzonej kolumny o nazwie image
                object.put("username", ParseUser.getCurrentUser().getUsername()); // wrzucamy do kolumny username info czyj to plik
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(UserListActivity.this,"IMAGE UPLOADED!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserListActivity.this,"IMAGE NOT UPLOADED! SOMETHING WENT WRONG!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getPhoto() {
        // robimy intent do pobierania foteczek i startujemy startactivityforresult (to służy do przekazania danych z powrotem z intentu drugiego do pierwszego)
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

//
//    @Override // obsługa cholernego pozwolenia na galerię chyba przy pierwszym odpaleniu
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getPhoto();
//            }
//        } // PAMIĘTAJ O PERMISSIONS  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/> W ANDROID MANIFEST
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            // po odpaleniu kolejnym sprawdza czy jest permission
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPhoto();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        final ListView usersListView = findViewById(R.id.usersListView);
        final ArrayList<String> usersArrayList = new ArrayList<>();
        final ArrayAdapter<String> usersArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersArrayList);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername()); // nie chcemy wyświetlić swojego usera
//        query.whereExists("username"); // wszyscy userzy
        query.addAscendingOrder("username"); // sortujemy rosnaco po username
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) { // lecimy po objektach ParseUser i dodajemy username z getUsername do arraylist
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
