package com.intellectualcloud.intellectualcloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.intellectualcloud.intellectualcloud.model.post;

public class ReceiveLink extends AppCompatActivity {
    TextView tvforTitle, tvForCon, tvfordec;
    ImageView ivforpic;
    FloatingActionButton sharenot;
    String deeplink, pt = "title", pc = "content", pd = "description", pi = "url";

    DatabaseReference db;
    private static final String DB_URL = "https://intellectualcloud-5fe7b.firebaseio.com/Dlinks";
    String fbkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_link);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvForCon = findViewById(R.id.mconforR);
        tvforTitle = findViewById(R.id.mtitleforR);
        tvfordec = findViewById(R.id.mdescforR);

        ivforpic = findViewById(R.id.ivforR);
        sharenot = findViewById(R.id.shareforR);
        sharenot.setEnabled(false);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                        }


                        deeplink = deepLink.toString();
                        Toast.makeText(ReceiveLink.this, "" + deepLink, Toast.LENGTH_SHORT).show();


                        String a[] = deeplink.split("seperator");
                        // Toast.makeText(ReceiveLink.this, "first->"+a[1], Toast.LENGTH_SHORT).show();

                        // Toast.makeText(ReceiveLink.this, "1->" + a[1], Toast.LENGTH_SHORT).show();
                        fbkey = a[1];

                        getcontentfromdb(fbkey);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("hey", "getDynamicLink:onFailure", e);
                    }
                });


        sharenot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gcreatedynamiclink();
            }
        });

    }

    private void getcontentfromdb(final String key) {
        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                post d = dataSnapshot.child(key).getValue(post.class);

                fun(d);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void fun(post d) {


        pd = d.getPost_description();
        pt = d.getPost_title();
        pc = d.getPost_content();
        pi = d.getImg_path();
        tvForCon.setText(pc);
        tvforTitle.setText(pt);
        tvfordec.setText(pd);


        if (pi != null)
            PicassoClient.downloadimg(getApplicationContext(), pi, ivforpic);
        sharenot.setEnabled(true);

    }

    private void gcreatedynamiclink() {

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(deeplink))
                .setDynamicLinkDomain("xb6hq.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.intellectualcloud.intellectualcloud")
                                .build())

                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(pt)
                                .setDescription(pd)
                                .setImageUrl(Uri.parse(pi))
                                .build())


                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            String a = shortLink.toString();
                            // Toast.makeText(Detailed.this, "Inside" + a, Toast.LENGTH_SHORT).show();

                            shareit(a);
                            Uri flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            // Error
                            // ...
                            // Toast.makeText(Detailed.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void shareit(String msg) {
        if (msg != null) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Did you know " + pt + "? \n" +
                            msg);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else
            Toast.makeText(this, "NO", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, Home.class));
    }

}

