package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText firstName, lastName;
    private ToggleButton gender;
    private ImageView profileAvatar;
    private CheckBox privateProfile, disablePush;
    private Button submit, cancel;
    private static final int IMAGE_EDIT = 102;
    private Uri picURI;
    private Bitmap bitmap;
    private ParseFile file;
    private boolean imageSelected = false;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = (EditText) findViewById(R.id.editTextEditFirstName);
        lastName = (EditText) findViewById(R.id.editTextEditLastName);
        gender = (ToggleButton) findViewById(R.id.toggleButtonEditGender);
        profileAvatar = (ImageView) findViewById(R.id.imageViewEditAvatar);
        privateProfile = (CheckBox) findViewById(R.id.checkBoxEditPrivate);
        disablePush = (CheckBox) findViewById(R.id.checkBoxEditPush);
        submit = (Button) findViewById(R.id.buttonEditSubmit);
        cancel = (Button) findViewById(R.id.buttonEditCancel);

        currentUser = ParseUser.getCurrentUser();

        firstName.setText(currentUser.getString("firstName"));
        lastName.setText(currentUser.getString("lastName"));
        if(currentUser.getString("gender").equals("Female")){
            gender.setChecked(true);
        }
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
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profileAvatar.setImageBitmap(bitmap);

                    }else{
                        //error
                    }
                }
            });

        }else{
            profileAvatar.setBackgroundResource(R.drawable.default_avatar);
        }

        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, IMAGE_EDIT);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()){
                    Toast.makeText(ProfileEditActivity.this, "All the details are required!", Toast.LENGTH_SHORT).show();
                }else{
                    if(imageSelected == true){
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] bitmapBytes = stream.toByteArray();
                        file  = new ParseFile("avatar.jpg", bitmapBytes);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    currentUser.put("firstName", firstName.getText().toString());
                                    currentUser.put("lastName", lastName.getText().toString());
                                    if (gender.isChecked()) {
                                        currentUser.put("gender", gender.getTextOn().toString());
                                    } else {
                                        currentUser.put("gender", gender.getTextOff().toString());
                                    }
                                    if (privateProfile.isChecked()) {
                                        currentUser.put("isPrivate", true);
                                    } else {
                                        currentUser.put("isPrivate", false);
                                    }
                                    if (disablePush.isChecked()) {
                                        currentUser.put("disablePush", true);
                                    } else {
                                        currentUser.put("disablePush", false);
                                    }
                                    currentUser.put("profilePic", file);
                                    currentUser.saveInBackground();
                                } else  {
                                    //error
                                }
                            }
                        });

                    }else{
                        currentUser.put("firstName", firstName.getText().toString());
                        currentUser.put("lastName", lastName.getText().toString());
                        if (gender.isChecked()) {
                            currentUser.put("gender", gender.getTextOn().toString());
                        } else {
                            currentUser.put("gender", gender.getTextOff().toString());
                        }
                        if (privateProfile.isChecked()) {
                            currentUser.put("isPrivate", true);
                        } else {
                            currentUser.put("isPrivate", false);
                        }
                        if (disablePush.isChecked()) {
                            currentUser.put("disablePush", true);
                        } else {
                            currentUser.put("disablePush", false);
                        }
                        //user.put("profilePic", null);
                        currentUser.saveInBackground();
                    }
                    Toast.makeText(ProfileEditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_EDIT) {
            if (resultCode == RESULT_OK) {
                picURI = data.getData();
                imageSelected = true;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),picURI);
                    profileAvatar.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
