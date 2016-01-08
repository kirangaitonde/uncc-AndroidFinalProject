package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class AlbumCreateActivity extends AppCompatActivity {
    private EditText albumName;
    private CheckBox isPrivate;
    private Button submit,cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        albumName = (EditText) findViewById(R.id.editTextAlbumEditName);
        isPrivate = (CheckBox) findViewById(R.id.checkBoxAlbumCreatePrivate);
        submit = (Button) findViewById(R.id.buttonCreateAlbum);
        cancel = (Button) findViewById(R.id.buttonCreateAlbumCancel);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(albumName.getText().toString().isEmpty()){
                    Toast.makeText(AlbumCreateActivity.this, "Alubum name required!", Toast.LENGTH_SHORT).show();
                }else {
                    ParseObject album = new ParseObject("Albums");
                    album.put("name", albumName.getText().toString());
                    if (isPrivate.isChecked()) {
                        album.put("isPrivate", true);
                    } else {
                        album.put("isPrivate", false);
                    }
                    album.put("createdBy",ParseUser.getCurrentUser());
                    album.put("ownerName",ParseUser.getCurrentUser().getString("firstName")+" "+ParseUser.getCurrentUser().getString("lastName"));
                    album.saveInBackground();

                    Toast.makeText(AlbumCreateActivity.this, "Album Created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AlbumCreateActivity.this, AlbumActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(AlbumCreateActivity.this, AlbumActivity.class);
                startActivity(intent);
            }
        });
    }

}
