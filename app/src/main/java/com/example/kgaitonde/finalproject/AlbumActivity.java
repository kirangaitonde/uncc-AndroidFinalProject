package com.example.kgaitonde.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumActivity extends AppCompatActivity implements RVAdapter.IData {

    private RecyclerView rv;
    private ListView listView;
    private LinearLayoutManager llm;
    private Set<ParseObject> allAccesible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        allAccesible = new HashSet<ParseObject>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> allAlbums, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("AlbumSharedUsers");
                    query.whereEqualTo("sharedWith", ParseUser.getCurrentUser());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> sharedAlbums, ParseException e) {
                            if (e == null) {
                                final List<String> sharedAlbumIds = new ArrayList<String>();
                                for (ParseObject a : sharedAlbums) {
                                    sharedAlbumIds.add(a.getString("albumId"));
                                }
                                // select shared albums from allAlbums

                                // 1) add all shared albums
                                for (ParseObject b : allAlbums) {
                                    if (sharedAlbumIds.contains(b.getObjectId())) {
                                        allAccesible.add(b);
                                    }

                                }

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                                query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> myAlbums, ParseException e) {
                                        if (e == null) {
                                            if (myAlbums.size() != 0) {
                                                for (ParseObject c : myAlbums) {
                                                    //2) add my albums
                                                    allAccesible.add(c);
                                                }
                                            }
                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                                            query.whereEqualTo("isPrivate", false);
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> publicAlbums, ParseException e) {
                                                    if (e == null) {
                                                        //3) add all public albums albums
                                                        for (ParseObject d : publicAlbums) {
                                                            allAccesible.add(d);
                                                        }
                                                        List<ParseObject> all = new ArrayList<>();
                                                        all.addAll(allAccesible);
                                                        setAlbumView(all);

                                                    } else {
                                                        Log.d("exception", e.toString());
                                                    }
                                                }
                                            });


                                        } else {
                                            Log.d("exception", e.toString());
                                        }
                                    }
                                });


                            } else {
                                Log.d("exception", e.toString());
                            }
                        }
                    });


                } else {
                    Log.d("exception", e.toString());
                }
            }
        });



    }

    // using list view
   /* public void setAlbumView(ArrayList <ParseObject> albums){
        // set view for main activity
        //Log.d("kiran", albums.toString());
        listView = (ListView) findViewById(R.id.listViewAlbum);
        ItemAdapter adapter = new ItemAdapter(this, R.layout.album_item_layout,albums) ;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //code to preview activity

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                return true;
            }
        });

    }
*/
  // recycler view


     public void setAlbumView(List<ParseObject> albums){
        rv = (RecyclerView)findViewById(R.id.rv);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(albums, this, this);
        rv.setAdapter(adapter);
    }


    public List<ParseObject> getAllAccesibleAlbums(){
        List<ParseObject> allAlbums = new ArrayList<>();



        return allAlbums;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_createAlbum) {
            finish();
            Intent intent = new Intent(AlbumActivity.this, AlbumCreateActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_allAlbums) { // owned by me, public albums, shared with me

            allAccesible = new HashSet<ParseObject>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> allAlbums, ParseException e) {
                    if (e == null) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("AlbumSharedUsers");
                        query.whereEqualTo("sharedWith", ParseUser.getCurrentUser());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> sharedAlbums, ParseException e) {
                                if (e == null) {
                                    final List<String> sharedAlbumIds = new ArrayList<String>();
                                    for(ParseObject a : sharedAlbums){
                                        sharedAlbumIds.add(a.getString("albumId"));
                                    }
                                    // select shared albums from allAlbums

                                  // 1) add all shared albums
                                    for(ParseObject b : allAlbums){
                                        if(sharedAlbumIds.contains(b.getObjectId())){
                                            allAccesible.add(b);
                                        }

                                    }

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                                    query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> myAlbums, ParseException e) {
                                            if (e == null) {
                                                if(myAlbums.size()!=0){
                                                    for(ParseObject c : myAlbums){
                                                        //2) add my albums
                                                        allAccesible.add(c);
                                                    }
                                                }
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
                                                query.whereEqualTo("isPrivate", false);
                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> publicAlbums, ParseException e) {
                                                        if (e == null) {
                                                            //3) add all public albums albums
                                                            for(ParseObject d : publicAlbums){
                                                                allAccesible.add(d);
                                                            }
                                                         List<ParseObject> all = new ArrayList<>();
                                                            all.addAll(allAccesible);
                                                            setAlbumView(all);

                                                        } else {
                                                            Log.d("exception", e.toString());
                                                        }
                                                    }
                                                });


                                            } else {
                                                Log.d("exception", e.toString());
                                            }
                                        }
                                    });



                                } else {
                                    Log.d("exception", e.toString());
                                }
                            }
                        });




                    } else {
                        Log.d("exception", e.toString());
                    }
                }
            });



            return true;
        }
        if (id == R.id.action_ownedByMe) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
            query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        setAlbumView(objects);
                    } else {
                        Log.d("exception", e.toString());
                    }
                }
            });

            return true;
        }
        if (id == R.id.action_sharedWithMe) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> allAlbums, ParseException e) {
                    if (e == null) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("AlbumSharedUsers");
                        query.whereEqualTo("sharedWith", ParseUser.getCurrentUser());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> sharedAlbums, ParseException e) {
                                if (e == null) {
                                    final List<String> sharedAlbumIds = new ArrayList<String>();
                                    for (ParseObject a : sharedAlbums) {
                                        sharedAlbumIds.add(a.getString("albumId"));
                                    }

                                    List<ParseObject> sharedWithMe = new ArrayList<>();
                                    // select shared albums from allAlbums
                                    for (ParseObject b : allAlbums) {
                                        if (sharedAlbumIds.contains(b.getObjectId())) {
                                            sharedWithMe.add(b);
                                        }

                                    }

                                    setAlbumView(sharedWithMe);

                                } else {
                                    Log.d("exception", e.toString());
                                }
                            }
                        });


                    } else {
                        Log.d("exception", e.toString());
                    }
                }
            });

            return true;
        }

        if (id == R.id.action_publicAlbums) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Albums");
            query.whereEqualTo("isPrivate", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        setAlbumView(objects);
                    } else {
                        Log.d("exception", e.toString());
                    }
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
