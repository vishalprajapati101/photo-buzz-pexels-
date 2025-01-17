package com.imagevault.wallpapersinfinity.adpter;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.imagevault.wallpapersinfinity.R;
import com.imagevault.wallpapersinfinity.model.FavoritesManager;
import com.imagevault.wallpapersinfinity.model.PexelsResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PhotoPagerAdapter extends RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder> {

    private List<PexelsResponse.Photo> photoList;
    private Context context;

    public PhotoPagerAdapter(List<PexelsResponse.Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PexelsResponse.Photo photo = photoList.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(photo.getSrc().getOriginal())

                .error(R.drawable.noimg)
                .fitCenter()
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar when image is ready
                        return false;
                    }
                })
                .into(holder.photoImage);

        // Set click listeners for actions
        holder.favoriteIcon.setOnClickListener(v -> {

            if (FavoritesManager.getFavorites().contains(photo)) {
                // If it's already a favorite, remove it
                FavoritesManager.removeFavorite(photo, context);
                Log.d("PhotoAdapter", "Removed from favorites: " + photo.getSrc().getOriginal());
            } else {
                // If it's not a favorite, add it
                FavoritesManager.addFavorite(photo, context);
                Log.d("PhotoAdapter", "Added to favorites: " + photo.getSrc().getOriginal());
            }
            updateFavoriteIcon(holder, photo); // Update icon immediately
        });

        holder.shareIcon.setOnClickListener(v -> {
            // Share image functionality
            shareImage(photo.getSrc().getOriginal());
        });

        holder.downloadIcon.setOnClickListener(v -> {
            // Download image functionality
            downloadImage(photo.getSrc().getOriginal());
        });

        holder.setWallpaperIcon.setOnClickListener(v -> {
            // Set wallpaper functionality
            setWallpaper(photo.getSrc().getOriginal());
        });
    }
    private void updateFavoriteIcon(PhotoViewHolder holder, PexelsResponse.Photo photo) {
        if (FavoritesManager.getFavorites().contains(photo)) {
            holder.favoriteIcon.setImageResource(R.drawable.redheart); // Replace with your "filled" favorite icon
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.favorit); // Replace with your "border" favorite icon
        }
    }


    @Override
    public int getItemCount() {
        return photoList.size();
    }

    private void shareImage(String imageUrl) {
        Toast.makeText(context, "Preparing image for sharing...", Toast.LENGTH_SHORT).show();
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            File cachePath = new File(context.getCacheDir(), "images");
                            cachePath.mkdirs(); // Create folder if not exists
                            File file = new File(cachePath, "shared_image.png");
                            FileOutputStream stream = new FileOutputStream(file); // Overwrite if exists
                            resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.close();

                            Uri imageUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Toast.makeText(context, "Image is ready for sharing!", Toast.LENGTH_SHORT).show();

                            context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup if required
                    }
                });
    }

    private void downloadImage(String imageUrl) {

        Toast.makeText(context, "Starting download...", Toast.LENGTH_SHORT).show();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Using DownloadManager
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(imageUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Downloading Image");
            request.setDescription("Downloading image to device");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
            downloadManager.enqueue(request);
            Toast.makeText(context, "Image is being downloaded. Check notifications for progress.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Feature not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void setWallpaper(String imageUrl) {
        Toast.makeText(context, "Setting wallpaper, please wait...", Toast.LENGTH_SHORT).show();

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        try {
                            wallpaperManager.setBitmap(resource);
                            Toast.makeText(context, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup if required
                    }
                });
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage, favoriteIcon, shareIcon, downloadIcon, setWallpaperIcon;

        ProgressBar progressBar;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.imageView);
            favoriteIcon = itemView.findViewById(R.id.favoriteIconn);
            shareIcon = itemView.findViewById(R.id.shareIconn);
            downloadIcon = itemView.findViewById(R.id.downloadIconn);
            progressBar = itemView.findViewById(R.id.progressforview);
            setWallpaperIcon = itemView.findViewById(R.id.setWallpaperIconn);
        }
    }
}
