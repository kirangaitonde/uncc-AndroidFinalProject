package com.example.kgaitonde.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kgaitonde on 12/11/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AlbumViewHolder>{

    List<ParseObject> albums;
    Context context;
    IData activity;
    CharSequence[] albumOptionsPrivate = {"Edit Settings","Share Album", "See Shared People List", "Delete Album" };
    CharSequence[] albumOptionsPublic = {"Edit Settings","Delete Album" };

    RVAdapter(List<ParseObject> albums, Context context, IData activity){
        this.albums = albums;
        this.context = context;
        this.activity=activity;
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_item_layout, viewGroup, false);
        AlbumViewHolder avh = new AlbumViewHolder(v);
        return avh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder albumViewHolder, final int i) {
        albumViewHolder.albumName.setText(albums.get(i).getString("name"));

        ParseUser createdBy = (ParseUser)albums.get(i).get("createdBy");
        if(ParseUser.getCurrentUser().equals(createdBy)){
            albumViewHolder.albumOwner.setText("Owner : You");
            albumViewHolder.albumOptions.setImageResource(R.drawable.stack_icon);

            // listener on options
            albumViewHolder.albumOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Options", Toast.LENGTH_SHORT).show();

                    if(albums.get(i).getBoolean("isPrivate")){ // private album
                        final AlertDialog.Builder builder =new AlertDialog.Builder(context);
                        builder.setTitle("Select Options:");
                        builder.setItems(albumOptionsPrivate, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: //"Edit Settings",
                                        //Toast.makeText(context, "Options", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, AlbumEditActivity.class);
                                        intent.putExtra("ALBUM_ID",albums.get(i).getObjectId());
                                        context.startActivity(intent);
                                        break;

                                    case 1: //"Share Album",
                                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                                        query.findInBackground(new FindCallback<ParseUser>() {
                                            public void done(final List<ParseUser> allUsers, ParseException e) {
                                                if (e == null) {
                                                    // The query was successful.

                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("AlbumSharedUsers");
                                                    query.whereEqualTo("albumId", albums.get(i).getObjectId());
                                                    query.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                            if (e == null) {
                                                                final List<ParseUser> usersAlreadySharedWith = new ArrayList<ParseUser>();

                                                                final List<ParseUser> relevantUsers = new ArrayList<ParseUser>();


                                                                for(ParseObject u : objects){
                                                                    // get users from objects
                                                                    ParseUser user = (ParseUser)u.get("sharedWith");
                                                                    usersAlreadySharedWith.add(user);
                                                                }

                                                                // remove current user and users already shared with users
                                                                for(ParseUser u : allUsers){
                                                                    //already shared users to be removed
                                                                    if(!u.equals(ParseUser.getCurrentUser()) && !usersAlreadySharedWith.contains(u)){
                                                                        relevantUsers.add(u);
                                                                    }

                                                                }

                                                                //create characterSequence of names
                                                                CharSequence[] relevantUsersNames = new CharSequence[relevantUsers.size()];
                                                                for(int i=0;i<relevantUsers.size();i++){
                                                                    relevantUsersNames[i]= relevantUsers.get(i).getString("firstName")+" "+relevantUsers.get(i).getString("lastName");
                                                                }


                                                                // dialog to show all user
                                                                final AlertDialog.Builder builder =new AlertDialog.Builder(context);
                                                                builder.setTitle("Select User To Share:");
                                                                builder.setItems(relevantUsersNames, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        // code to share with selected user

                                                                        ParseObject asu = new ParseObject("AlbumSharedUsers");
                                                                        asu.put("albumId", albums.get(i).getObjectId());
                                                                        asu.put("sharedWith", relevantUsers.get(which));
                                                                        asu.saveInBackground();
                                                                        Toast.makeText(context, "Album shared with selected user", Toast.LENGTH_SHORT).show();

                                                                        Log.d("demo", "sending push");

                                                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                                                        pushQuery.whereEqualTo("currentUser", relevantUsers.get(which));

                                                                        ParsePush push = new ParsePush();
                                                                        push.setQuery(pushQuery);
                                                                        push.setMessage(ParseUser.getCurrentUser().getString("firstName")+" " +"shared an album with you!");
                                                                        push.sendInBackground();

                                                                    }
                                                                });
                                                                final  AlertDialog alert = builder.create();
                                                                alert.show();




                                                            } else {
                                                                Log.d("exception", e.toString());
                                                            }
                                                        }
                                                    });


                                                } else {
                                                    // Something went wrong.
                                                }
                                            }
                                        });
                                        break;

                                    case 2: //"See Shared People List",

                                        ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                                        query1.findInBackground(new FindCallback<ParseUser>() {
                                            public void done(final List<ParseUser> allUsers, ParseException e) {
                                                if (e == null) {
                                                    // The query was successful.

                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("AlbumSharedUsers");
                                                    query.whereEqualTo("albumId", albums.get(i).getObjectId());
                                                    query.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                            if (e == null) {
                                                                final List<ParseUser> usersAlreadySharedWith = new ArrayList<ParseUser>();
                                                                for(ParseObject u : objects){
                                                                    // get users from objects
                                                                    ParseUser user = (ParseUser)u.get("sharedWith");
                                                                    usersAlreadySharedWith.add(user);
                                                                }


                                                                //create characterSequence of names
                                                                CharSequence[] sharedUsersNames = new CharSequence[usersAlreadySharedWith.size()];
                                                                for(int i=0;i<usersAlreadySharedWith.size();i++){
                                                                    sharedUsersNames[i]= usersAlreadySharedWith.get(i).getString("firstName")+" "+usersAlreadySharedWith.get(i).getString("lastName");
                                                                }


                                                                // dialog to show all user
                                                                final AlertDialog.Builder builder =new AlertDialog.Builder(context);
                                                                builder.setTitle("Album Shared With:");
                                                                builder.setItems(sharedUsersNames, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        // do nothing


                                                                    }
                                                                });
                                                                final  AlertDialog alert = builder.create();
                                                                alert.show();




                                                            } else {
                                                                Log.d("exception", e.toString());
                                                            }
                                                        }
                                                    });


                                                } else {
                                                    // Something went wrong.
                                                }
                                            }
                                        });

                                        break;

                                    case 3: //"Delete Album"
                                        albums.get(i).deleteInBackground();
                                        albums.remove(i);
                                        activity.setAlbumView(albums);
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
                        builder.setItems(albumOptionsPublic, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: //"Edit Settings",
                                        //Toast.makeText(context, "Options", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, AlbumEditActivity.class);
                                        intent.putExtra("ALBUM_ID",albums.get(i).getObjectId());
                                        context.startActivity(intent);
                                        break;

                                    case 1: //"Delete Album"
                                        albums.get(i).deleteInBackground();
                                        albums.remove(i);
                                        activity.setAlbumView(albums);
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


        }else{
            StringBuilder sb = new StringBuilder("Owner :");
            sb.append(albums.get(i).getString("ownerName"));
            albumViewHolder.albumOwner.setText(sb.toString());
        }

        //if (albums.get(i).get("photos")!= null){
        // set album cover with first pic
        //albumViewHolder.albumCover.setImageResource(persons.get(i).photoId);
        // }

        //listeners

        albumViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumViewActvity.class);
                ParseUser createdBy = (ParseUser)albums.get(i).get("createdBy");

                intent.putExtra("ALBUM_ID", albums.get(i).getObjectId());
                if(ParseUser.getCurrentUser().equals(createdBy)){
                    intent.putExtra("USER_IS_OWNER", true);
                }else{
                    intent.putExtra("USER_IS_OWNER", false);
                }
                context.startActivity(intent);
            }
        });

    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView albumName;
        TextView albumOwner;
        ImageView albumCover, albumOptions;

        AlbumViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            albumName = (TextView)itemView.findViewById(R.id.textViewAlbumName);
            albumOwner = (TextView)itemView.findViewById(R.id.textViewAlbumOwner);
            albumCover = (ImageView)itemView.findViewById(R.id.imageViewAlbumPhoto);
            albumOptions = (ImageView)itemView.findViewById(R.id.imageViewAlbumPhotoOptions);
        }
    }

    static public interface IData{
        public void setAlbumView(List<ParseObject> items);
    }
}