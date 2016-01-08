/*InClass07
* * Group 3B
* Kiran Gaitonde
* Praneeth Puli*
* Mark Hooper
* */

package com.example.kgaitonde.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by kgaitonde on 10/5/2015.
 */
public class ItemAdapter extends ArrayAdapter<ParseObject> {

    ArrayList<ParseObject> mData;
    Context mContext;
    int mResource;


    public ItemAdapter(Context context, int resource, ArrayList<ParseObject> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.mData=objects;
        this.mResource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }


        TextView albumName = (TextView)convertView.findViewById(R.id.textViewAlbumName);
        TextView albumOwner = (TextView)convertView.findViewById(R.id.textViewAlbumOwner);
        ImageView albumCover = (ImageView)convertView.findViewById(R.id.imageViewAlbumPhoto);
        ImageView albumOptions = (ImageView)convertView.findViewById(R.id.imageViewAlbumPhotoOptions);


        albumName.setText(mData.get(position).getString("name"));
        ParseUser createdBy = (ParseUser)mData.get(position).get("createdBy");
        if(ParseUser.getCurrentUser().equals(createdBy)){
            albumOwner.setText("Owner : You");
            albumOptions.setImageResource(R.drawable.stack_icon);

        }else{
            StringBuilder sb = new StringBuilder("Owner :");
            sb.append(mData.get(position).getString("ownerName"));
            albumOwner.setText(sb.toString());
        }
        return convertView;
    }

}
