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
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by kgaitonde on 12/11/2015.
 */
public class RVAlbumViewAdapter extends RecyclerView.Adapter<RVAlbumViewAdapter.AlbumViewHolder>{

    List<ParseObject> photos;
    Context context;
    boolean userIsAlbumOwner;
    CharSequence[] imageOptions = {"Delete Photo" };
    CharSequence[] imageOptionsModeration = {"Approve" , "Reject"};
    IData activity;
    boolean forModeration;

    RVAlbumViewAdapter(List<ParseObject> photos, Context context, boolean userIsAlbumOwner, IData activity, boolean forModeration){
        this.photos = photos;
        this.context = context;
        this.userIsAlbumOwner=userIsAlbumOwner;
        this.activity=activity;
        this.forModeration=forModeration;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_view_item_layoout, viewGroup, false);
        AlbumViewHolder avh = new AlbumViewHolder(v);
        return avh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder albumViewHolder, final int i) {

        ParseFile albumImage = photos.get(i).getParseFile("imageFile");
        if (albumImage != null) {
            albumImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        albumViewHolder.photo.setImageBitmap(bitmap);

                    } else {
                        //error
                    }
                }
            });
        }

        if (userIsAlbumOwner) {
            albumViewHolder.photoOptions.setImageResource(R.drawable.stack_icon);
            albumViewHolder.photoOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //image options
                    //Toast.makeText(context, "Options", Toast.LENGTH_SHORT).show();

                    if(forModeration){
                        final AlertDialog.Builder builder =new AlertDialog.Builder(context);
                        builder.setTitle("Select Options:");
                        builder.setItems(imageOptionsModeration, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case 0: //"Approve Photo"
                                        photos.get(i).put("isApproved", true);
                                        photos.get(i).saveInBackground();

                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("currentUser", photos.get(i).get("addedBy"));

                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
                                        push.setMessage(ParseUser.getCurrentUser().getString("firstName") + " " + "has approved your photo submitted to  his album!");
                                        push.sendInBackground();


                                        photos.remove(i);
                                        activity.setViewAlbumView(photos, true);
                                        break;

                                    case 1: //"Reject Photo"
                                        photos.get(i).deleteInBackground();
                                        photos.remove(i);
                                        activity.setViewAlbumView(photos, true);
                                        break;

                                    default:
                                        break;

                                }
                            }
                        });
                        final  AlertDialog alert = builder.create();
                        alert.show();

                    }else{
                        final AlertDialog.Builder builder =new AlertDialog.Builder(context);
                        builder.setTitle("Select Options:");
                        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case 0: //"Delete Photo"
                                        photos.get(i).deleteInBackground();
                                        photos.remove(i);
                                        activity.setViewAlbumView(photos, false);
                                        break;

                                    default:
                                        break;

                                }
                            }
                        });
                        final  AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            });
        }

        albumViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFile albumImage = photos.get(i).getParseFile("imageFile");
                if (albumImage != null) {
                    albumImage.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {

                                Intent intent = new Intent(context, AlbumPhotoViewActivity.class);
                                //ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                // bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                intent.putExtra("imageId", photos.get(i).getObjectId());
                                context.startActivity(intent);

                            } else {
                                //error
                            }
                        }
                    });
                }
            }
        });

    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView photo, photoOptions;

        AlbumViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvAlbumView);
            photo = (ImageView)itemView.findViewById(R.id.imageViewAlbumPhoto);
            photoOptions = (ImageView)itemView.findViewById(R.id.imageViewAlbumPhotoOptions);
        }


    }

    static public interface IData{
        public void setViewAlbumView(List<ParseObject> items, boolean forModeration);
    }

}