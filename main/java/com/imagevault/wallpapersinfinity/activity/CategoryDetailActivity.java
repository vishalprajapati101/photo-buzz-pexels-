package com.imagevault.wallpapersinfinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.adpter.PhotoAdapter;
import com.imagevault.wallpapersinfinity.interfacee.PexelsApi;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;
import com.imagevault.wallpapersinfinity.model.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<PexelsResponse.Photo> photoList;
    private String categoryName;
    private final String API_KEY = "you api key"; // Store this in BuildConfig
    private ProgressBar progressBar; // Declare ProgressBar
    private TextView toolbarTitle; // TextView for the title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        ImageView image = findViewById(R.id.backcat);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // Set up the toolbar as the action bar

        // Enable back button in toolbar

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCategoryDetail);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar
        toolbarTitle = findViewById(R.id.toolbarTitle); // Initialize TextView for title

        // Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize photo list and adapter
        photoList = new ArrayList<>();
        adapter = new PhotoAdapter(photoList, this);
        recyclerView.setAdapter(adapter);

        // Get category data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            categoryName = intent.getStringExtra("category_name");
            String categoryImageUrl = intent.getStringExtra("category_image_url");

            // Set the category title in the TextView
            toolbarTitle.setText(categoryName);

            // Fetch photos for the selected category
            fetchPhotosByCategory(categoryName);
        }
    }

    private void fetchPhotosByCategory(String category) {
        // Show ProgressBar while data is loading
        progressBar.setVisibility(View.VISIBLE);

        PexelsApi api = RetrofitClient.getClient().create(PexelsApi.class);

        Call<PexelsResponse> call = api.searchPhotos(category, 15, 1, API_KEY);
        call.enqueue(new Callback<PexelsResponse>() {
            @Override
            public void onResponse(Call<PexelsResponse> call, Response<PexelsResponse> response) {
                // Hide ProgressBar once data is fetched
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    photoList.clear(); // Clear the list for new results
                    photoList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged(); // Notify adapter about the new data
                } else {
                    Toast.makeText(CategoryDetailActivity.this, "No results for: " + category, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PexelsResponse> call, Throwable t) {
                // Hide ProgressBar in case of failure
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CategoryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle the back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handles the back button press
        return true;
    }
}
