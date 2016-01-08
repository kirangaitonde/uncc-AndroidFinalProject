package com.example.kgaitonde.finalproject;

import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MessageViewActivity extends AppCompatActivity  {

    TextView subject,body;
    ImageView messagePhoto;
    Button reply, cancel;
    String message_id;
    ParseObject message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        subject = (TextView) findViewById(R.id.textViewMessageSubject);
        body = (TextView) findViewById(R.id.textViewMessageBody);
        messagePhoto = (ImageView) findViewById(R.id.imageViewMessageViewImage);
        reply = (Button) findViewById(R.id.buttonMessageViewReply);
        cancel = (Button) findViewById(R.id.buttonMessageViewCancel);


        if (getIntent().getExtras() != null) {
            message_id=getIntent().getExtras().getString("MESSAGE_ID");
        }

        Log.d("demo", message_id);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.whereEqualTo("objectId", message_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for(ParseObject m: objects){
                        Log.d("demo", message_id);
                        message = m;

                        m.put("isRead",true);
                        m.saveInBackground();

                        subject.setText("Subject :"+m.getString("subject"));
                        body.setText(m.getString("body"));

                        ParseFile photo = m.getParseFile("photo");
                        if(photo!=null){
                            photo.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e==null){
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        messagePhoto.setImageBitmap(bitmap);

                                    }else{
                                        //error
                                    }
                                }
                            });

                        }else{
                            messagePhoto.setBackgroundResource(R.drawable.default_avatar);
                        }

                    }



                } else {
                    Log.d("demo", "errror getting Images");
                }
            }
        });





        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageViewActivity.this, MessageReplyActivity.class);
                intent.putExtra("MESSAGE_REPLY_ID", message.getObjectId());
                startActivity(intent);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageViewActivity.this, MessageInboxActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }




}
