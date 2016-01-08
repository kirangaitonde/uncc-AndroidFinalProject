package com.example.kgaitonde.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserProfileViewActivity extends AppCompatActivity implements RVAdapter.IData {
    TextView name, gender, email;
    ImageView profileAvatar;
    String username;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    ParseUser user;
    private Set<ParseObject> viewableAlbums;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        if (getIntent().getExtras() != null) {

            username=getIntent().getExtras().getString("USERNAME");
        }
        name = (TextView) findViewById(R.id.textViewUserProfileName);
        email = (TextView) findViewById(R.id.textViewUserProfileEmail);
        gender = (TextView) findViewById(R.id.textViewUserProfileGender);
        profileAvatar = (ImageView) findViewById(R.id.imageViewUserProfileAvatar);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser u : objects){
                        user=u;
                        name.setText("Name :"+u.getString("firstName")+" "+u.getString("lastName"));
                        email.setText("Email:"+u.getString("email"));
                        gender.setText("Gender:"+u.getString("gender"));

                        ParseFile profilePic = u.getParseFile("profilePic");
                        if(profilePic!=null){
                            profilePic.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        profileAvatar.setImageBitmap(bitmap);

                                    } else {
                                        //error
                                    }
                                }
                            });

                        }else{
                            profileAvatar.setBackgroundResource(R.drawable.default_avatar);
                        }

                        // find out albums of the user
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                        query.whereEqualTo("createdBy", user);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(final List<ParseObject> userAlbums, ParseException e) {
                                if (e == null) {
                                    //setAlbumView(userAlbums);
                                    viewableAlbums = new HashSet<ParseObject>();

                                    // add all public albums
                                    for(ParseObject ua : userAlbums){
                                        if(ua.getBoolean("isPrivate")==false){
                                            viewableAlbums.add(ua);
                                        }
                                    }

                                    //add all shared albums
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("AlbumSharedUsers");
                                    query.whereEqualTo("sharedWith", ParseUser.getCurrentUser());
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> sharedAlbums, ParseException e) {
                                            if (e == null) {
                                                final List<String> sharedAlbumIds = new ArrayList<String>();
                                                for(ParseObject a : sharedAlbums){
                                                    sharedAlbumIds.add(a.getString("albumId"));
                                                }
                                                // select shared albums from allAlbums

                                                // 1) add all shared albums
                                                for(ParseObject b : userAlbums){
                                                    if(sharedAlbumIds.contains(b.getObjectId())){
                                                        viewableAlbums.add(b);
                                                    }
                                                }

                                                List<ParseObject> all = new ArrayList<>();
                                                all.addAll(viewableAlbums);
                                                setAlbumView(all);


                                            } else {
                                                Log.d("exception", e.toString());
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("exception", e.toString());
                                }
                            }
                        });

                    }
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    public void setAlbumView(List<ParseObject> albums){
        rv = (RecyclerView)findViewById(R.id.rvUserProileAlbums);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(albums, this, this);
        rv.setAdapter(adapter);
    }


}
