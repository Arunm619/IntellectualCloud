package com.intellectualcloud.intellectualcloud;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intellectualcloud.intellectualcloud.model.post;

import java.util.ArrayList;
import java.util.Collections;

public class FirebaseClient {

    Context c;
    String DB_URL;
    ListView listView;
    ArrayList<post> postslist = new ArrayList<>();
    CustomAdapter customAdapter;
    DatabaseReference db;
    String order;

    public FirebaseClient(Context c, String DB_URL, ListView listView, String order) {
        this.c = c;
        this.DB_URL = DB_URL;
        this.listView = listView;
        this.order = order;


        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
    }

    public void refreshdata() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getupdates(DataSnapshot dataSnapshot) {


        post d = dataSnapshot.getValue(post.class);

        postslist.add(d);


        if (postslist.size() > 0) {

            if (order.equals("reverse"))
                Collections.reverse(postslist);
            customAdapter = new CustomAdapter(c, postslist);
            listView.setAdapter(customAdapter);
            Utility.setDynamicHeight(listView);

        } else {
            Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
        }
    }

}