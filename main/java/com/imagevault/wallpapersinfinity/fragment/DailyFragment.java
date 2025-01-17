package com.imagevault.wallpapersinfinity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<PexelsResponse.Photo> photoList;
    private final String API_KEY = "your api key"; // Store this in BuildConfig

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_daily, container, false);
        FavoritesManager.init(getContext());

        // Initialize views
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerViewdaily);

        // Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10); // Cache 10 items for smooth scrolling

        // Set up GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setInitialPrefetchItemCount(6); // Prefetch 6 items
        recyclerView.setLayoutManager(layoutManager);

        // Initialize photo list and adapter
        photoList = new ArrayList<>();
        adapter = new PhotoAdapter(photoList, getContext());
        recyclerView.setAdapter(adapter);
        List<String> categories = Arrays.asList("Sunshine", "River", "Mountain", "Forest", "Sky", "Ocean",
                "Believe", "Strength", "Courage", "Persist", "Focus", "Achieve", "Grateful",
                "Optimistic", "Kind", "Hopeful", "Peace", "Dream", "Success", "Grow", "Vision",
                "Ambition", "Passion");

        String randomCategory = getRandomCategory(categories);

        // Fetch photos by categories initially
        fetchPhotosByCategory(randomCategory);

        return rootView;
    }

    private void fetchPhotosByCategory(String category) {
        AtomicInteger pendingCalls = new AtomicInteger(1); // Only one call for the random category

        // Show progress bar while loading data
        progressBar.setVisibility(View.VISIBLE);

        PexelsApi api = RetrofitClient.getClient().create(PexelsApi.class);

        Call<PexelsResponse> call = api.searchPhotos(category, 15, 1, API_KEY);
        call.enqueue(new Callback<PexelsResponse>() {
            @Override
            public void onResponse(Call<PexelsResponse> call, Response<PexelsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    photoList.clear(); // Clear the list for new results
                    photoList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged(); // Notify adapter about the new data
                } else {
                    Toast.makeText(getContext(), "No results for: " + category, Toast.LENGTH_SHORT).show();
                }

                // Decrement pending calls and hide progress if done
                checkPendingCalls(pendingCalls.decrementAndGet());
            }

            @Override
            public void onFailure(Call<PexelsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                checkPendingCalls(pendingCalls.decrementAndGet());
            }
        });
    }
    private String getRandomCategory(List<String> categories) {
        Random random = new Random();
        int randomIndex = random.nextInt(categories.size());

        return categories.get(randomIndex);
    }

    private void checkPendingCalls(int pendingCalls) {
        if (pendingCalls == 0) {
            // All calls completed, hide the progress bar
            progressBar.setVisibility(View.GONE);
        }
    }
}
