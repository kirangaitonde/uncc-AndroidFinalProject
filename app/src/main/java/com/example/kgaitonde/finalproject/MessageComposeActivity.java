package com.example.kgaitonde.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageComposeActivity extends AppCompatActivity {

    EditText subject,body;
    ImageView photo;
    Button send, cancel;
    private static final int MESSAGE_IMAGE_SELECT = 201;
    private Uri picURI;
    private Bitmap bitmap;
    private ParseFile file;
    private boolean imageSelected = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_compose);

        subject = (EditText) findViewById(R.id.editTextComposeMessageSubject);
        body = (EditText) findViewById(R.id.editTextComposeMessageBody);
        photo = (ImageView) findViewById(R.id.imageViewComposeMessageImage);
        send = (Button) findViewById(R.id.buttonComposeMessageSend);
        cancel = (Button) findViewById(R.id.buttonComposeMessageCancel);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, MESSAGE_IMAGE_SELECT);

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(subject.getText().toString().isEmpty() || body.getText().toString().isEmpty() ){
                    Toast.makeText(MessageComposeActivity.this, "All feilds are required!", Toast.LENGTH_SHORT).show();
                }else {
                    //code to send message
                    Log.d("demo", ParseUser.getCurrentUser().getObjectId());

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("isPrivate",false);
                    query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(final List<ParseUser> users, ParseException e) {
                            if (e == null) {
                                // The query was successful.

                                Log.d("demo", users.size()+"");

                                CharSequence[] usersNames = new CharSequence[users.size()];
                                for(int i=0;i<users.size();i++){
                                    usersNames[i]= users.get(i).getString("firstName")+" "+users.get(i).getString("lastName");
                                }

                                // dialog to show all user
                                final AlertDialog.Builder builder =new AlertDialog.Builder(MessageComposeActivity.this);
                                builder.setTitle("Message Sent to :");
                                builder.setItems(usersNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, final int which) {

                                        if(imageSelected == true){
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] bitmapBytes = stream.toByteArray();
                                            file  = new ParseFile("messageImage.png", bitmapBytes);
                                            file.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        ParseObject message = new ParseObject("Messages");
                                                        message.put("subject", subject.getText().toString());
                                                        message.put("body", body.getText().toString());
                                                        message.put("from", ParseUser.getCurrentUser());
                                                        message.put("senderName",ParseUser.getCurrentUser().get("firstName")+""+ParseUser.getCurrentUser().get("lastName"));
                                                        message.put("to", users.get(which) );
                                                        message.put("isRead", false);
                                                        message.put("photo", file);
                                                        message.saveInBackground();
                                                        Toast.makeText(MessageComposeActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                                        pushQuery.whereEqualTo("currentUser", users.get(which));

                                                        ParsePush push = new ParsePush();
                                                        push.setQuery(pushQuery);
                                                        push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "sent you a message!");
                                                        push.sendInBackground();


                                                        finish();

                                                    } else  {
                                                        //error
                                                    }
                                                }
                                            });

                                        }else{
                                            ParseObject message = new ParseObject("Messages");
                                            message.put("subject", subject.getText().toString());
                                            message.put("body", body.getText().toString());
                                            message.put("from", ParseUser.getCurrentUser());
                                            message.put("senderName",ParseUser.getCurrentUser().get("firstName")+""+ParseUser.getCurrentUser().get("lastName"));
                                            message.put("to", users.get(which) );
                                            message.put("isRead", false);
                                            message.saveInBackground();
                                            Toast.makeText(MessageComposeActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                                            ParseQuery pushQuery = ParseInstallation.getQuery();
                                            pushQuery.whereEqualTo("currentUser", users.get(which));

                                            ParsePush push = new ParsePush();
                                            push.setQuery(pushQuery);
                                            push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "sent you a message!");
                                            push.sendInBackground();

                                            finish();
                                        }
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
        if (requestCode == MESSAGE_IMAGE_SELECT ) {
            if (resultCode == RESULT_OK) {
                picURI = data.getData();
                imageSelected = true;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),picURI);
                    photo.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }



}
