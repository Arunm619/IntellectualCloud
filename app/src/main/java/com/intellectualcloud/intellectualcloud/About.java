package com.intellectualcloud.intellectualcloud;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.intellectualcloud.intellectualcloud.model.post;

public class About extends AppCompatActivity implements View.OnClickListener {
    private static final String DB_URL = "https://intellectualcloud-5fe7b.firebaseio.com/";
    private DatabaseReference db;
    Button btn_shareapp, btn_moreinfo;
    String link;//githubl;
    Button btn_call, btn_mail;
    ListView lvforfeatures;
    TextView tv_teamcon, tv_teamdesc, tv_title;
    ImageView iv_teamimg;
    private String DB_URLforAbout = "https://intellectualcloud-5fe7b.firebaseio.com/About";
    FirebaseClient firebaseClient;
    private String DB_URLforfeatures =
            "https://intellectualcloud-5fe7b.firebaseio.com/Primaryfeatures";
    RelativeLayout rlaboutl;


    private Boolean isFabOpen = false;
    private FloatingActionButton fab, teamfab, contactfab;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About Us");

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollv);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        iv_teamimg = findViewById(R.id.teampic);
        tv_teamcon = findViewById(R.id.teamcon);
        tv_teamdesc = findViewById(R.id.teamdesc);
        lvforfeatures = findViewById(R.id.lv_forfeatures);
        rlaboutl = findViewById(R.id.aboutrl);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        contactfab = (FloatingActionButton) findViewById(R.id.contacusfab);
        teamfab = (FloatingActionButton) findViewById(R.id.teamfab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        fab.setOnClickListener(this);
        teamfab.setOnClickListener(this);
        contactfab.setOnClickListener(this);


        btn_moreinfo = findViewById(R.id.Moreinfo);
        btn_shareapp = findViewById(R.id.Shareapp);


        btn_shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareapp();
            }
        });

        btn_moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitwebsite();
            }
        });
        abouttheteam();

        firebaseClient = new FirebaseClient(this, DB_URLforfeatures, lvforfeatures, "");
        firebaseClient.refreshdata();
    }

    private void shareapp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out Cloud Intellectual App at: https://play.google.com/store/apps/details?id=com.intellectualcloud.intellectualcloud");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void visitwebsite() {
        String url = "http://www.myintellecutalcloud.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    private void abouttheteam() {

        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URLforAbout);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post p = dataSnapshot.getValue(post.class);
                setteamdetails(p);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setteamdetails(post p) {
        tv_teamdesc.setText(p.getPost_description());
        tv_teamcon.setText(p.getPost_content());
        PicassoClient.downloadimg(this, p.getImg_path(), iv_teamimg);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        // startActivity(new Intent(this, Home.class));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.teamfab:
                //    Toast.makeText(this, "team fab", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(About.this, Employees.class));
                break;
            case R.id.contacusfab:
                //  Toast.makeText(this, "contactfab", Toast.LENGTH_SHORT).show();
                makealert();
                animateFAB();


                break;
        }
    }

    private void makealert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(About.this);
        builder.setTitle("Contact Us")
                .setMessage("Just send us your questions or concerns by starting a new case" +
                        " and we will give you the help you need.")
                .setPositiveButton("Mail", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mail();
                    }
                })
                .setNegativeButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callnumber();

                    }
                })
                .setIcon(R.drawable.mobile)
                .show();
    }

    private void mail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "myintellectualcloud@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Write the subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }

    private void callnumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:9940245619"));
        startActivity(intent);

    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            contactfab.startAnimation(fab_close);
            teamfab.startAnimation(fab_close);
            contactfab.setClickable(false);
            teamfab.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            contactfab.startAnimation(fab_open);
            teamfab.startAnimation(fab_open);
            contactfab.setClickable(true);
            teamfab.setClickable(true);
            isFabOpen = true;

        }
    }

}
