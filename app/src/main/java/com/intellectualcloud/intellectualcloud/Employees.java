package com.intellectualcloud.intellectualcloud;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.intellectualcloud.intellectualcloud.model.post;

public class Employees extends AppCompatActivity {

    ListView lvforworkers;

    FirebaseClient firebaseClient;
    private String DB_URLforworkers = "https://intellectualcloud-5fe7b.firebaseio.com/Team";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Team Intellect");


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollvforteam);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        lvforworkers = findViewById(R.id.lv_forworkers);


        firebaseClient = new FirebaseClient(this, DB_URLforworkers, lvforworkers, "");

        firebaseClient.refreshdata();
        lvforworkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                post post = (post) adapterView.getItemAtPosition(i);
                final String[] link = {post.getPost_content()};

                View parentLayout = findViewById(R.id.theteam);
                Snackbar.make(parentLayout, "Want to see my works? Check My Github page! ", Snackbar.LENGTH_LONG)
                        .setAction("Yeah!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent a = new Intent(Intent.ACTION_VIEW);
                                if (!link[0].startsWith("http://") && !link[0].startsWith("https://"))
                                    link[0] = "http://" + link[0];
                                a.setData(Uri.parse(link[0]));
                                startActivity(a);

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.white))
                        .show();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
