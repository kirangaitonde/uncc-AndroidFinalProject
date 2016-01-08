package com.example.kgaitonde.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button users, albums, inbox;
    private boolean new_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set user for push notification

        ParseUser u = ParseUser.getCurrentUser();

        if (getIntent().getExtras() != null) {
            new_user=getIntent().getExtras().getBoolean("NEWUSER");
        }

        Log.d("demo", "sending notification to all users!");


        if(!u.getBoolean("disablePush")){
            ParseInstallation pi = ParseInstallation.getCurrentInstallation();
            pi.put("currentUser", u);
            //pi.saveInBackground();
            pi.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        if (new_user) {

                            // send push
                            ParseQuery pushQuery = ParseInstallation.getQuery();
                            pushQuery.whereNotEqualTo("currentUser", ParseUser.getCurrentUser());
                            Log.d("demo", "sending parse notification");

                            ParsePush push = new ParsePush();
                            //push.setQuery(pushQuery);
                            push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "registered to this app!");
                            push.sendInBackground();
                        }

                    } else {
                        //error
                    }
                }
            });
        }



        users = (Button) findViewById(R.id.buttonUsers);
        albums = (Button) findViewById(R.id.buttonAlbums);
        inbox = (Button) findViewById(R.id.buttonMessages);


        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("isPrivate", false);
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(final List<ParseUser> allPublicUsers, ParseException e) {
                        if (e == null) {
                            // remove current user and users already shared with users
                            final List<ParseUser> relevantUsers = new ArrayList<ParseUser>();
                            for(ParseUser u : allPublicUsers){
                                //already shared users to be removed
                                if(!u.equals(ParseUser.getCurrentUser())){
                                    relevantUsers.add(u);
                                }
                            }
                            //create characterSequence of names
                            CharSequence[] relevantUsersNames = new CharSequence[relevantUsers.size()];
                            for(int i=0;i<relevantUsers.size();i++){
                                relevantUsersNames[i]= relevantUsers.get(i).getString("firstName")+" "+relevantUsers.get(i).getString("lastName");
                            }

                            // dialog to show all users apart from current user
                            final AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Select User To View:");
                            builder.setItems(relevantUsersNames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // code to dispay user profile with selected user

                                    Intent intent = new Intent(MainActivity.this, UserProfileViewActivity.class);
                                    intent.putExtra("USERNAME", relevantUsers.get(which).getUsername());
                                    startActivity(intent);


                                }
                            });
                            final  AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            // Something went wrong.
                        }
                    }
                });
            }
        });



        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(intent);
            }
        });

        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageInboxActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

        if (id == R.id.action_showProfile) {
            // code for  profile management
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_signOut) {
            // code for sign out
            ParseUser.logOut();
            ParseInstallation pi = ParseInstallation.getCurrentInstallation();
            pi.remove("currentUser");
            pi.saveInBackground();

            Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
            Intent intent1=new Intent(MainActivity.this,SigninActivity.class);
            startActivity(intent1);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
