package com.intellectualcloud.intellectualcloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.intellectualcloud.intellectualcloud.model.post;
import com.squareup.picasso.Picasso;

public class Detailed extends AppCompatActivity {

    TextView tvmtitle, tvmcon, tvmdesc;
    ImageView iv;
    FloatingActionButton btn_share;
    String pt, pc, pd, pi;
    post pfb;
    DatabaseReference db;
    private static final String DB_URL = "https://intellectualcloud-5fe7b.firebaseio.com/Dlinks";


    /* pt -post title,pd - post desc, pc - post content,pi - post img */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        Gson gson = new Gson();
        String data = getIntent().getExtras().getString("obj", "fool");
        post post;
        tvmcon = findViewById(R.id.mcon);
        tvmdesc = findViewById(R.id.mdesc);
        tvmtitle = findViewById(R.id.mtitle);
        iv = findViewById(R.id.imgview);
        btn_share = findViewById(R.id.share);

        if (!data.equals("fool")) {
            post = gson.fromJson(data, post.class);

            pfb = post;

            pt = post.getPost_title();
            tvmtitle.setText(pt);
            pd = post.getPost_description();
            tvmdesc.setText(pd);
            pc = post.getPost_content();
            tvmcon.setText(pc);
            pi = post.getImg_path();

            drawimg(pi);
        }
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharelink();
            }
        });

    }


    private void sharelink() {


        gcreatedynamiclink(getid());

    }

    private String getid() {
        String id = db.push().getKey();
        db.child(id).setValue(pfb);
        return id;
    }


    private void gcreatedynamiclink(String id) {


        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://github.com/" +getString(R.string.seperator) +id))
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

                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            final String a = shortLink.toString();

                            View parentLayout = findViewById(R.id.detailed);
                            Snackbar.make(parentLayout, "Share...", Snackbar.LENGTH_LONG)
                                    .setAction("Select", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            shareit(a);

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.white))
                                    .show();


//  Toast.makeText(Detailed.this, "Sharing..." + a, Toast.LENGTH_SHORT).show();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            // Error
                            // ...
                            //Toast.makeText(Detailed.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void drawimg(String url) {
        if (url != null && url.length() > 0) {
            Picasso.with(this).load(url).placeholder(R.drawable.placeholder).into(iv);

        } else {
            Picasso.with(this).load(R.drawable.placeholder).into(iv);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        //  startActivity(new Intent(this, Home.class));
    }
}
