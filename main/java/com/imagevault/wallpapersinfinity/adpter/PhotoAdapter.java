package com.imagevault.wallpapersinfinity.adpter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.activity.FavoriteWallActivity;
import com.imagevault.wallpapersinfinity.activity.PhotoDetailActivity;
import com.imagevault.wallpapersinfinity.model.FavoritesManager;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<PexelsResponse.Photo> photoList;
    private final Context context;

    public PhotoAdapter(List<PexelsResponse.Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }


    @Override

    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PexelsResponse.Photo photo = photoList.get(position);

        // Show progress bar before loading
        holder.progressBar.setVisibility(View.VISIBLE);

        // Load image with Glide
        Glide.with(context)
                .load(photo.getSrc().getOriginal())
                .error(R.drawable.noimg) // Show placeholder on error
                .override(600, 600) // Resize for better performance
                .centerCrop()

                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache for faster future loads
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // Hide on failure
                        return false; // Allow Glide to handle error
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // Hide when image is ready
                        return false; // Allow Glide to handle the resource
                    }
                })
                .into(holder.photoImage);
        updateFavoriteIcon(holder, photo);

        // Toggle favorite icon
        holder.favoriteIcon.setOnClickListener(v -> {
            if (FavoritesManager.getFavorites().contains(photo)) {
                FavoritesManager.removeFavorite(photo, context);
                Log.d("PhotoAdapter", "Removed from favorites: " + photo.getSrc().getOriginal());
            } else {
                FavoritesManager.addFavorite(photo, context);
                Log.d("PhotoAdapter", "Added to favorites: " + photo.getSrc().getOriginal());
            }
            updateFavoriteIcon(holder, photo); // Update icon immediately
        });

        // Open photo details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoDetailActivity.class);
            intent.putExtra("photo", photo); // Parcelable implementation required
            intent.putParcelableArrayListExtra("photoList", new ArrayList<>(photoList));
            intent.putExtra("initialPosition", position);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }
    public static void updateFavoriteIcon(@NonNull PhotoViewHolder holder, PexelsResponse.Photo photo) {
        if (FavoritesManager.getFavorites().contains(photo)) {
            holder.favoriteIcon.setImageResource(R.drawable.redheart); // Filled favorite icon
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.favorit); // Border favorite icon
        }
    }


    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;
        ImageView favoriteIcon;
        ProgressBar progressBar;
        boolean isFavorite = false;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.imageView);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
