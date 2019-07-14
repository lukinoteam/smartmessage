package com.example.scratchmessage;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewer extends AppCompatActivity {

    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imgView = findViewById(R.id.imgView);

        Intent intentData = getIntent();
        String uri = intentData.getStringExtra("uri");
        Picasso.get().load(Uri.parse(uri)).into(imgView);
    }
}
