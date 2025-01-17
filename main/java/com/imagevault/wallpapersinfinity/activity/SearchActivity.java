package com.imagevault.wallpapersinfinity.activity;

import static com.imagevault.wallpapersinfinity.adpter.PhotoAdapter.updateFavoriteIcon;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.adpter.PhotoAdapter;
import com.imagevault.wallpapersinfinity.interfacee.PexelsApi;
import com.imagevault.wallpapersinfinity.model.FavoritesManager;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;
import com.imagevault.wallpapersinfinity.model.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<PexelsResponse.Photo> photoList;
    private ProgressBar progressBar;
    private SearchView searchView;
    private final String API_KEY = "urZXSRPP3UQRZm6gpDu8pqMi5aDjq1BJXsIEFuxlo1dJcP5L8NGs6x67"; // Store in BuildConfig

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FavoritesManager.init(this);

        initializeUI();
        setupRecyclerView();
        setupSearchView();
        fetchPhotosByCategories();
    }

    private void initializeUI() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);

        ImageView backImage = findViewById(R.id.backsearch);
        backImage.setOnClickListener(v -> finish()); // Finishes the current activity and returns to the previous one
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setInitialPrefetchItemCount(4); // Prefetch 8 items
        recyclerView.setLayoutManager(layoutManager);

        photoList = new ArrayList<>();
        adapter = new PhotoAdapter(photoList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        // Set up SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchPhotosByQuery(query); // Fetch photos based on user query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchPhotosByCategories(); // Reset to categories if query is empty
                } else {
                    fetchPhotosByQuery(newText); // Dynamically fetch results
                }
                return true;
            }
        });
    }

    private void fetchPhotosByCategories() {
        List<String> categories = Arrays.asList("nature", "technology", "animals", "sports", "gym");
        AtomicInteger pendingCalls = new AtomicInteger(categories.size()); // Track pending calls

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        PexelsApi api = RetrofitClient.getClient().create(PexelsApi.class);

        for (String category : categories) {
            Call<PexelsResponse> call = api.searchPhotos(category, 15, 1, API_KEY);
            call.enqueue(new Callback<PexelsResponse>() {
                @Override
                public void onResponse(Call<PexelsResponse> call, Response<PexelsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        photoList.addAll(response.body().getPhotos());
                        adapter.notifyDataSetChanged();
                    }
                    // Decrement pending calls and hide progress if done
                    checkPendingCalls(pendingCalls.decrementAndGet());
                }

                @Override
                public void onFailure(Call<PexelsResponse> call, Throwable t) {
                    Toast.makeText(SearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Decrement pending calls and hide progress if done
                    checkPendingCalls(pendingCalls.decrementAndGet());
                }
            });
        }
    }

    private void fetchPhotosByQuery(String query) {
        AtomicInteger pendingCalls = new AtomicInteger(1); // Only one call for the query

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        PexelsApi api = RetrofitClient.getClient().create(PexelsApi.class);

        Call<PexelsResponse> call = api.searchPhotos(query, 15, 1, API_KEY);
        call.enqueue(new Callback<PexelsResponse>() {
            @Override
            public void onResponse(Call<PexelsResponse> call, Response<PexelsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    photoList.clear(); // Clear previous list
                    photoList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SearchActivity.this, "No results for: " + query, Toast.LENGTH_SHORT).show();
                }
                // Decrement pending calls and hide progress if done
                checkPendingCalls(pendingCalls.decrementAndGet());
            }

            @Override
            public void onFailure(Call<PexelsResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Decrement pending calls and hide progress if done
                checkPendingCalls(pendingCalls.decrementAndGet());
            }
        });
    }

    private void checkPendingCalls(int pendingCalls) {
        if (pendingCalls == 0) {
            // All calls completed
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the adapter when coming back to this activity to ensure the correct favorite icon is shown
        adapter.notifyDataSetChanged();
    }
}
