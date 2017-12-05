package com.intellectualcloud.intellectualcloud;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.intellectualcloud.intellectualcloud.model.post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.iwgang.countdownview.CountdownView;

public class Home extends AppCompatActivity {
    private static final String DB_URL = "https://intellectualcloud-5fe7b.firebaseio.com/PostDetails";
    private DatabaseReference db;
    private ProgressBar progressBar;
    RelativeLayout relativeLayout_home;
    FirebaseClient firebaseClient;
    CardView cvforcountdown, cvforbigday;
    Button checkwebsite;
    String releaseDate;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        checkfornotification();

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        ListView listViewfeed = findViewById(R.id.lv_feed);
        //db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://intellectualcloud-5fe7b.firebaseio.com/PostDetails");
        relativeLayout_home = findViewById(R.id.rl_home);

        db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://intellectualcloud-5fe7b.firebaseio.com/");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                releaseDate = dataSnapshot.child("ReleaseDate").getValue(String.class);
                /* Toast.makeText(Home.this, "" + releaseDate, Toast.LENGTH_SHORT).show(); */
                countDownStart(releaseDate);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        firebaseClient = new FirebaseClient(this, DB_URL, listViewfeed, "reverse");
        firebaseClient.refreshdata();
        //    progressBar.setVisibility(View.GONE);


        listViewfeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                post post = (post) adapterView.getItemAtPosition(i);
                Gson gson = new Gson();
                String objstring = gson.toJson(post);
                Intent intent = new Intent(Home.this, Detailed.class);
                intent.putExtra("obj", objstring);
                //  finish();
                startActivity(intent);


            }
        });

        initUI();
        checkwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitwebsite();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 2560);


    }

    private void visitwebsite() {
        String url = "http://www.myintellecutalcloud.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    private void checkfornotification() {
        if (getIntent().getExtras() != null) {
            Intent intent = new Intent(this, ShowNotification.class);

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                //  Toast.makeText(this, key + value, Toast.LENGTH_SHORT).show();

                if (key.equals("picture_url"))
                    intent.putExtra("picture_url", value);
                if (key.equals("title"))
                    intent.putExtra("title", value);
                if (key.equals("body"))
                    intent.putExtra("body", value);

            }
            startActivity(intent);
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void initUI() {
        cvforcountdown = findViewById(R.id.cvforcountdown);
        cvforbigday = findViewById(R.id.cvforbigday);
        cvforbigday.setVisibility(View.GONE);
        cvforcountdown.setVisibility(View.GONE);
        checkwebsite = findViewById(R.id.websitebtn);
        checkwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "website woohoo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void countDownStart(final String releaseDate) {

//        tv_countdown.setText(releaseDate);

        //String myDate = "2017/12/04 10:00:00";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cvforcountdown.setVisibility(View.VISIBLE);
        long answer = date.getTime() - new Date().getTime();
        CountdownView mCvCountdownViewTest4 = (CountdownView) findViewById(R.id.cv_countdownViewTest4);
        mCvCountdownViewTest4.start(answer);

        if (answer <= 0) {
            cvforcountdown.setVisibility(View.GONE);
            cvforbigday.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.aboutus) {

            Intent intent = new Intent(Home.this, About.class);
            startActivity(intent);
            //  finish();

        } else if (id == R.id.privacypolicy) {

            Intent intent = new Intent(Home.this, PrivacyPolicy.class);
            startActivity(intent);
            //  finish();
        } else if (id == R.id.admin) {

            final EditText taskEditText = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Admin Panel")
                    .setMessage("Enter Password:")
                    .setView(taskEditText)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (taskEditText.getText().toString().equals("9940")) {

                                startActivity(new Intent(Home.this, Admin.class));
                                finish();
                            } else
                                Toast.makeText(Home.this, "Password Incorrect. Self Destruct in 2 more Attempts.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();


        }


        return super.onOptionsItemSelected(item);

    }
}