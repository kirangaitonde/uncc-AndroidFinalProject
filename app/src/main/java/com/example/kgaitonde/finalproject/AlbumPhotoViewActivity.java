package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumPhotoViewActivity extends AppCompatActivity {
    private ImageView iv;
    private String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo_view);

        iv = (ImageView) findViewById(R.id.imageViewViewPhoto);

        if(getIntent().hasExtra("imageId")) {
          //  Bitmap b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            imageId =getIntent().getExtras().getString("imageId");

           // iv.setImageBitmap(b);
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.whereEqualTo("objectId", imageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if(objects.size()!=0){
                        for (ParseObject o :objects){
                            ParseFile albumImage = o.getParseFile("imageFile");
                            if (albumImage != null) {
                                albumImage.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            iv.setImageBitmap(bitmap);

                                        } else {
                                            //error
                                        }
                                    }
                                });
                            }
                        }
                    }

                } else {
                    Log.d("demo", "errror getting Images");
                }
            }
        });
    }
}
