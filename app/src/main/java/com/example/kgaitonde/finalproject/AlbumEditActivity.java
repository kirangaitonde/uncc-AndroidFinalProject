package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AlbumEditActivity extends AppCompatActivity {

    private EditText albumName;
    private CheckBox isPrivate;
    private Button edit,cancel;
    private String albumId;
    private ParseObject album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_edit);

        albumName = (EditText) findViewById(R.id.editTextAlbumEditName);
        isPrivate = (CheckBox) findViewById(R.id.checkBoxAlbumEditPrivate);
        edit = (Button) findViewById(R.id.buttonEditAlbum);
        cancel = (Button) findViewById(R.id.buttonEditAlbumCancel);

        if (getIntent().getExtras() != null) {
            albumId=getIntent().getExtras().getString("ALBUM_ID");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
        query.whereEqualTo("objectId", albumId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if(objects.size()!=0){
                        for(ParseObject o : objects){
                            album=o;
                            albumName.setText(album.getString("name"));
                            if(album.getBoolean("isPrivate")){
                                isPrivate.setChecked(true);
                            }
                        }
                    }

                } else {
                    Log.d("demo", "errror");
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (albumName.getText().toString().isEmpty()) {
                    Toast.makeText(AlbumEditActivity.this, "Alubum name required!", Toast.LENGTH_SHORT).show();
                } else {
                    album.put("name", albumName.getText().toString());
                    if (isPrivate.isChecked()) {
                        album.put("isPrivate", true);
                    } else {
                        album.put("isPrivate", false);
                    }
                    album.saveInBackground();

                    Toast.makeText(AlbumEditActivity.this, "Album Edited", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AlbumEditActivity.this, AlbumActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(AlbumEditActivity.this, AlbumActivity.class);
                startActivity(intent);
            }
        });
    }


}
