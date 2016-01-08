/*InClass07
* * Group 3B
* Kiran Gaitonde
* Praneeth Puli*
* Mark Hooper
* */

package com.example.kgaitonde.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by kgaitonde on 10/5/2015.
 */
public class ItemAlbumViewAdapter extends ArrayAdapter<ParseObject> {

    ArrayList<ParseObject> mData;
    Context mContext;
    int mResource;
    boolean userIsAlbumOwner;


    public ItemAlbumViewAdapter(Context context, int resource, ArrayList<ParseObject> objects, boolean userIsAlbumOwner) {
        super(context, resource, objects);
        this.mContext=context;
        this.mData=objects;
        this.mResource=resource;
        this.userIsAlbumOwner=userIsAlbumOwner;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }

        //CardView cv = (CardView)convertView.findViewById(R.id.cvAlbumView);
        final ImageView photo = (ImageView)convertView.findViewById(R.id.imageViewAlbumPhoto);
        ImageView photoOptions = (ImageView)convertView.findViewById(R.id.imageViewAlbumPhotoOptions);

        ParseFile albumImage = mData.get(position).getParseFile("imageFile");
        if (albumImage != null) {
            albumImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        photo.setImageBitmap(bitmap);

                    } else {
                        //error
                    }
                }
            });
        }
        if (userIsAlbumOwner) {
            photoOptions.setImageResource(R.drawable.stack_icon);
        }
        return convertView;
    }

}
