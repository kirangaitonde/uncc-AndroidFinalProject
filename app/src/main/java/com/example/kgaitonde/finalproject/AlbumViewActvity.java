package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumViewActvity extends AppCompatActivity implements RVAlbumViewAdapter.IData {

    private String albumId;
    private ParseObject album;
    //private ArrayList<ParseObject> albumPhotos;
    List<Bitmap> albumImages;

    private RecyclerView rv;
    private GridLayoutManager glm;
    private boolean user_is_owner;
    private static final int ADD_PHOTO = 103;
    private ParseFile file;

    private Bitmap bitmap;

    private GridView gridView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_album_actvity);

        if (getIntent().getExtras() != null) {
            user_is_owner = getIntent().getExtras().getBoolean("USER_IS_OWNER");
            albumId=getIntent().getExtras().getString("ALBUM_ID");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.whereEqualTo("belongsToAlbum", albumId);
        query.whereEqualTo("isApproved", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                        //albumPhotos = (ArrayList<ParseObject>)objects;
                        setViewAlbumView(objects, false);

                } else {
                    Log.d("demo", "errror getting Images");
                }
            }
        });
    }

    // using list view
  /* public void setViewAlbumView(final ArrayList <ParseObject> photos){

        gridView = (GridView) findViewById(R.id.gridViewAlbumView);
        ItemAlbumViewAdapter adapter = new ItemAlbumViewAdapter (this, R.layout.album_view_item_layoout, photos, user_is_owner) ;
        gridView.setAdapter(adapter);

       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //code to preview activity

               Intent intent = new Intent(AlbumViewActvity.this, AlbumPhotoViewActivity.class);
               //ByteArrayOutputStream bs = new ByteArrayOutputStream();
               // bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
               intent.putExtra("imageId", photos.get(position).getObjectId());
               startActivity(intent);
           }



       });

    }
*/

    //using recycler view
    public void setViewAlbumView(List<ParseObject> photos, boolean forModeration){
        rv = (RecyclerView)findViewById(R.id.rvAlbumView);
        glm = new GridLayoutManager(this,2);
        rv.setLayoutManager(glm);
        RVAlbumViewAdapter adapter = new RVAlbumViewAdapter(photos, this, user_is_owner, this, forModeration);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(user_is_owner){
            getMenuInflater().inflate(R.menu.menu_view_album_owner_actvity, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_view_album_actvity, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addPhoto) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, ADD_PHOTO);
            return true;
        }

        if (id == R.id.action_moderatePhotos) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
            query.whereEqualTo("belongsToAlbum", albumId);
            query.whereEqualTo("isApproved", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                            setViewAlbumView(objects, true);

                    } else {
                        Log.d("demo", "errror getting Images");
                    }
                }
            });
            return true;
        }

        if (id == R.id.action_goBackToAlbum) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
            query.whereEqualTo("belongsToAlbum", albumId);
            query.whereEqualTo("isApproved", true);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                            //albumPhotos = (ArrayList<ParseObject>)objects;
                            setViewAlbumView(objects, false);

                    } else {
                        Log.d("demo", "errror getting Images");
                    }
                }
            });
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri picURI = data.getData();
                // just upload the image to parse and associate with album
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),picURI);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapBytes = stream.toByteArray();
                    file  = new ParseFile("photo.png", bitmapBytes);
                   // profileAvatar.setImageBitmap(bitmap);
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject image = new ParseObject("Images");
                                image.put("imageFile", file);
                                image.put("belongsToAlbum", albumId);
                                if(user_is_owner){
                                    image.put("isApproved", true);
                                }else{
                                    image.put("isApproved", false);
                                }
                                image.put("addedBy", ParseUser.getCurrentUser());
                                image.saveInBackground();

                                if(!user_is_owner){

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                                    query.whereEqualTo("objectId", albumId);
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {

                                                for (ParseObject a : objects){
                                                    ParseQuery pushQuery = ParseInstallation.getQuery();
                                                    pushQuery.whereEqualTo("currentUser", a.get("createdBy"));

                                                    ParsePush push = new ParsePush();
                                                    push.setQuery(pushQuery);
                                                    push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "submitted a photo to your album!");
                                                    push.sendInBackground();
                                                    Toast.makeText(AlbumViewActvity.this, "Your photo will appear in the album once approved by the album owner", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Log.d("demo", "errror getting Images");
                                            }
                                        }
                                    });
                                }


                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
                                query.whereEqualTo("belongsToAlbum", albumId);
                                query.whereEqualTo("isApproved", true);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                                setViewAlbumView(objects, false);
                                        } else {
                                            Log.d("demo", "errror getting Images");
                                        }
                                    }
                                });
                            } else  {
                                //error
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
