package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView firstName, lastName, email, gender;
    private ImageView profileAvatar;
    private CheckBox privateProfile, disablePush;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = (TextView) findViewById(R.id.textViewProfileFirstName);
        lastName = (TextView) findViewById(R.id.textViewProfileLastName);
        email = (TextView) findViewById(R.id.textViewProfileEmail);
        gender = (TextView) findViewById(R.id.textViewUserProfileGender);
        profileAvatar = (ImageView) findViewById(R.id.imageViewUserProfileAvatar);
        privateProfile = (CheckBox) findViewById(R.id.checkBoxProfilePrivate);
        disablePush = (CheckBox) findViewById(R.id.checkBoxProfilePush);

        //privateProfile.setEnabled(false);
        //disablePush.setEnabled(false);

        ParseUser currentUser = ParseUser.getCurrentUser();


        firstName.setText("First Name :"+currentUser.getString("firstName"));
        lastName.setText("Last Name :"+currentUser.getString("lastName"));
        email.setText("Email Id:"+currentUser.getString("username"));
        gender.setText("Gender :"+currentUser.getString("gender"));

        if(currentUser.getBoolean("isPrivate")){
            privateProfile.setChecked(true);
        }
        if(currentUser.getBoolean("disablePush")){
            disablePush.setChecked(true);
        }

        ParseFile profilePic = currentUser.getParseFile("profilePic");
        if(profilePic!=null){
            profilePic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e==null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
                        profileAvatar.setImageBitmap(bitmap);

                    }else{
                        //error
                    }
                }
            });

        }else{
            profileAvatar.setBackgroundResource(R.drawable.default_avatar);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_profile) {
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
