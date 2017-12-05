package com.intellectualcloud.intellectualcloud;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowNotification extends AppCompatActivity {

    TextView tvforTitle, tvForCon;
    ImageView ivforpic;
    FloatingActionButton sharenot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();


        final String title = i.getStringExtra("title");
        String body = i.getStringExtra("body");

        String url = i.getStringExtra("picture_url");
        getSupportActionBar().setTitle(title);

        tvForCon = findViewById(R.id.mconn);
        tvforTitle = findViewById(R.id.mtitlen);
        ivforpic = findViewById(R.id.ivpicture);
        sharenot = findViewById(R.id.shareNot);

        tvforTitle.setText(title);
        tvForCon.setText(body);
        PicassoClient.downloadimg(this, url, ivforpic);

        sharenot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check this article! \n" + title + "\n By " + getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });
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
