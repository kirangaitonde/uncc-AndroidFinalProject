package com.example.kgaitonde.finalproject;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.List;

public class MessageReplyActivity extends AppCompatActivity {


    TextView subject, body;
    ImageView messagePhoto;
    Button reply, cancel;
    String message_id;
    ParseObject rMessage;
    private static final int MESSAGE_REPLY_IMAGE_SELECT = 202;
    private Uri picURI;
    private Bitmap bitmap;
    private ParseFile file;
    private boolean imageSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reply);

        subject = (TextView) findViewById(R.id.editTextReplyMessageSubject);
        body = (TextView) findViewById(R.id.editTextReplyMessageBody);
        messagePhoto = (ImageView) findViewById(R.id.imageViewReplyMessageImage);
        reply = (Button) findViewById(R.id.buttonReplyMessageSend);
        cancel = (Button) findViewById(R.id.buttonReplyMessageCancel);


        if (getIntent().getExtras() != null) {
            message_id=getIntent().getExtras().getString("MESSAGE_REPLY_ID");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.whereEqualTo("objectId", message_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject m : objects) {
                        rMessage = m;
                        subject.setText("Subject: " + "Re: " + m.getString("subject"));
                    }

                } else {
                    Log.d("demo", "errror getting Images");
                }
            }
        });

        messagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, MESSAGE_REPLY_IMAGE_SELECT);

            }
        });


        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subject.getText().toString().isEmpty() || body.getText().toString().isEmpty()) {
                    Toast.makeText(MessageReplyActivity.this, "All feilds are required!", Toast.LENGTH_SHORT).show();

                } else {
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
                                    message.put("subject", "Re: " + rMessage.getString("subject"));
                                    message.put("body", body.getText().toString());
                                    message.put("from", ParseUser.getCurrentUser());
                                    message.put("senderName",ParseUser.getCurrentUser().get("firstName")+""+ParseUser.getCurrentUser().get("lastName"));
                                    message.put("to", rMessage.get("from"));
                                    message.put("isRead", false);
                                    message.put("photo", file);
                                    message.saveInBackground();
                                    Toast.makeText(MessageReplyActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                                    ParseQuery pushQuery = ParseInstallation.getQuery();
                                    pushQuery.whereEqualTo("currentUser", rMessage.get("from"));

                                    ParsePush push = new ParsePush();
                                    push.setQuery(pushQuery);
                                    push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "sent you a reply!");
                                    push.sendInBackground();
                                    finish();

                                } else  {
                                    //error
                                }
                            }
                        });

                    }else{
                        ParseObject message = new ParseObject("Messages");
                        message.put("subject", "Re: " + rMessage.getString("subject"));
                        message.put("body", body.getText().toString());
                        message.put("from", ParseUser.getCurrentUser());
                        message.put("senderName",ParseUser.getCurrentUser().get("firstName")+""+ParseUser.getCurrentUser().get("lastName"));
                        message.put("to", rMessage.get("from"));
                        message.put("isRead", false);
                        message.saveInBackground();
                        Toast.makeText(MessageReplyActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                        ParseQuery pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereEqualTo("currentUser", rMessage.get("from"));

                        ParsePush push = new ParsePush();
                        push.setQuery(pushQuery);
                        push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "sent you a reply!");
                        push.sendInBackground();


                        finish();
                    }
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
        if (requestCode == MESSAGE_REPLY_IMAGE_SELECT ) {
            if (resultCode == RESULT_OK) {
                picURI = data.getData();
                imageSelected = true;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),picURI);
                    messagePhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
