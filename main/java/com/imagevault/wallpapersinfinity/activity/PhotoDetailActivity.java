package com.imagevault.wallpapersinfinity.activity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.adpter.PhotoPagerAdapter;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PhotoDetailActivity extends AppCompatActivity {
    private ImageView favoriteIcon, shareIcon, downloadIcon, setWallpaperIcon;
    private ViewPager2 viewPager2;
    private List<PexelsResponse.Photo> photoList;
    private PhotoPagerAdapter photoPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // Initialize ImageView elements

        ImageView image= findViewById(R.id.backImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Get the photo list passed via Intent
        Intent intent = getIntent();
        photoList = intent.getParcelableArrayListExtra("photoList");

        // Get the selected position
        int initialPosition = intent.getIntExtra("initialPosition", 0);

        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setOffscreenPageLimit(2); // This will preload 2 pages before and after the current page
        viewPager2.setLayerType(View.LAYER_TYPE_NONE, null);

        // Set the adapter for ViewPager2
        photoPagerAdapter = new PhotoPagerAdapter(photoList, this);
        viewPager2.setAdapter(photoPagerAdapter);


        viewPager2.setCurrentItem(initialPosition);



    }
}




