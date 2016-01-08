package com.example.kgaitonde.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private ToggleButton gender;
    private ImageView avatar;
    private CheckBox privateProfile, disablePush;
    private Button btSignup, btCancel;
    private static final int IMAGE_SELECT = 101;
    private Uri picURI;
    private Bitmap bitmap;
    private ParseFile file;
    private boolean imageSelected = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etFirstName= (EditText) findViewById(R.id.editTextFirstName);
        etLastName= (EditText) findViewById(R.id.editTextLastName);
        etEmail= (EditText) findViewById(R.id.editTextEmail);
        etPassword= (EditText) findViewById(R.id.editTextPassword);
        etConfirmPassword= (EditText) findViewById(R.id.editTextPasswordConfirm);
        gender = (ToggleButton) findViewById(R.id.toggleButtonGender);
        avatar = (ImageView) findViewById(R.id.imageViewAvatar);
        privateProfile = (CheckBox) findViewById(R.id.checkBoxPrivate);
        disablePush = (CheckBox) findViewById(R.id.checkBoxPush);
        btSignup= (Button) findViewById(R.id.buttonSignup);
        btCancel= (Button) findViewById(R.id.buttonCancelSignup);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, IMAGE_SELECT);

            }
        });


        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etFirstName.getText().toString().isEmpty() || etLastName.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() ||
                        etPassword.getText().toString().isEmpty()||etConfirmPassword.getText().toString().isEmpty()){
                    Toast.makeText(SignupActivity.this, "All the details are required!", Toast.LENGTH_SHORT).show();
                }else if(! etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    Toast.makeText(SignupActivity.this, "Password did not match!", Toast.LENGTH_SHORT).show();
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
                                    ParseUser user = new ParseUser();
                                    user.setEmail(etEmail.getText().toString());
                                    user.setUsername(etEmail.getText().toString());
                                    user.setPassword(etPassword.getText().toString());
                                    user.put("firstName", etFirstName.getText().toString());
                                    user.put("lastName", etLastName.getText().toString());
                                    if (gender.isChecked()) {
                                        user.put("gender", gender.getTextOn().toString());
                                    } else {
                                        user.put("gender", gender.getTextOff().toString());
                                    }
                                    if (privateProfile.isChecked()) {
                                        user.put("isPrivate", true);
                                    } else {
                                        user.put("isPrivate", false);
                                    }
                                    if (disablePush.isChecked()) {
                                        user.put("disablePush", true);
                                    } else {
                                        user.put("disablePush", false);
                                    }
                                    user.put("profilePic", file);
                                    user.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(SignupActivity.this, "Sign Up successful", Toast.LENGTH_SHORT).show();

                                                //send push to all users to notify new signup


                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                intent.putExtra("NEWUSER", true);
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            } else  {
                                //error
                            }
                        }
                    });

                    }else{
                        ParseUser user = new ParseUser();
                        user.setEmail(etEmail.getText().toString());
                        user.setUsername(etEmail.getText().toString());
                        user.setPassword(etPassword.getText().toString());
                        user.put("firstName", etFirstName.getText().toString());
                        user.put("lastName", etLastName.getText().toString());
                        if (gender.isChecked()) {
                            user.put("gender", gender.getTextOn().toString());
                        } else {
                            user.put("gender", gender.getTextOff().toString());
                        }
                        if (privateProfile.isChecked()) {
                            user.put("isPrivate", true);
                        } else {
                            user.put("isPrivate", false);
                        }
                        if (disablePush.isChecked()) {
                            user.put("disablePush", true);
                        } else {
                            user.put("disablePush", false);
                        }
                        //user.put("profilePic", null);
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(SignupActivity.this, "Sign Up successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_SELECT) {
            if (resultCode == RESULT_OK) {
                picURI = data.getData();
                imageSelected = true;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),picURI);
                    avatar.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
