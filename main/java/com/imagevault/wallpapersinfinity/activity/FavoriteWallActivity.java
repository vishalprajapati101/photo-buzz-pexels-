package com.imagevault.wallpapersinfinity.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.adpter.PhotoAdapter;
import com.imagevault.wallpapersinfinity.model.FavoritesManager;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;

import java.util.List;

public class FavoriteWallActivity extends AppCompatActivity {

    private RecyclerView favoriteRecyclerView;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_wall);
        ImageView image = findViewById(R.id.favButton);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // Initialize views
        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);

        // Get favorite photos
        List<PexelsResponse.Photo> favoritePhotos = FavoritesManager.getFavorites();

        // Handle empty state
        if (favoritePhotos.isEmpty()) {
            Toast.makeText(this, "No favorites added yet!", Toast.LENGTH_SHORT).show();
        }

        // Set up RecyclerView
        setupRecyclerView(favoritePhotos);
    }

    private void setupRecyclerView(List<PexelsResponse.Photo> photos) {
        photoAdapter = new PhotoAdapter(photos, this);
        favoriteRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        favoriteRecyclerView.setAdapter(photoAdapter);
    }
}
