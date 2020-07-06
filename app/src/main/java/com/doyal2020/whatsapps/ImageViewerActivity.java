package com.doyal2020.whatsapps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView=findViewById(R.id.image_viewID);

        imageUri=getIntent().getStringExtra("url");

        Picasso.get().load(imageUri).into(imageView);

    }
}
