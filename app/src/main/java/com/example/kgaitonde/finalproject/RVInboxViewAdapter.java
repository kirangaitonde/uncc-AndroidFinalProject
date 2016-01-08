package com.example.kgaitonde.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by kgaitonde on 12/11/2015.
 */
public class RVInboxViewAdapter extends RecyclerView.Adapter<RVInboxViewAdapter.AlbumViewHolder>{

    List<ParseObject> messages;
    Context context;

    CharSequence[] messageOptions = {"Delete Message" };
    IData activity;



    RVInboxViewAdapter(List<ParseObject> messages, Context context, IData activity) {
        this.messages = messages;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_message_item_layout, viewGroup, false);
        AlbumViewHolder avh = new AlbumViewHolder(v);
        return avh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder albumViewHolder, final int i) {

        albumViewHolder.subject.setText(messages.get(i).getString("subject"));
        albumViewHolder.sender.setText("From:"+" "+messages.get(i).getString("senderName"));
        albumViewHolder.timeSent.setText(messages.get(i).getCreatedAt().toString());

        if(messages.get(i).getBoolean("isRead")){
            albumViewHolder.read.setText("Read");
        }else{
            albumViewHolder.read.setText("Unread");        }

        albumViewHolder.mOptions.setImageResource(R.drawable.stack_icon);

        albumViewHolder.mOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("Select Options:");
                builder.setItems(messageOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //"Delete Photo"
                                messages.get(i).deleteInBackground();
                                messages.remove(i);
                                activity.setInboxView(messages);
                                break;

                            default:
                                break;

                        }
                    }
                });
                final  AlertDialog alert = builder.create();
                alert.show();
            }
        });

        albumViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageViewActivity.class);
                intent.putExtra("MESSAGE_ID", messages.get(i).getObjectId());
                context.startActivity(intent);
            }
        });



    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView subject, sender, timeSent, read;
        ImageView  mOptions;

        AlbumViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvInboxView);
            subject = (TextView)itemView.findViewById(R.id.textViewItemMessageSubject);
            sender = (TextView)itemView.findViewById(R.id.textViewItemMessageSender);
            timeSent = (TextView)itemView.findViewById(R.id.textViewItemMessageTime);
            read = (TextView)itemView.findViewById(R.id.textViewItemMessageRead);
            mOptions = (ImageView)itemView.findViewById(R.id.imageViewItemMessageOptions);
        }

    }

    static public interface IData{
        public void setInboxView(List<ParseObject> items);
    }



}