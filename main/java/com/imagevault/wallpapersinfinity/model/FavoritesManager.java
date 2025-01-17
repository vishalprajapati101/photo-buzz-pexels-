package com.imagevault.wallpapersinfinity.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String PREF_NAME = "favorites_pref";
    private static final String KEY_FAVORITES = "favorites";
    private static List<PexelsResponse.Photo> favorites = new ArrayList<>();

    public static void init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_FAVORITES, null);
        if (json != null) {
            Type type = new TypeToken<List<PexelsResponse.Photo>>() {}.getType();
            favorites = new Gson().fromJson(json, type);
        }
    }

    public static void addFavorite(PexelsResponse.Photo photo, Context context) {
        if (!favorites.contains(photo)) {
            favorites.add(photo);
            saveFavorites(context);
        }
    }

    public static void removeFavorite(PexelsResponse.Photo photo, Context context) {
        favorites.remove(photo);
        saveFavorites(context);
    }

    public static List<PexelsResponse.Photo> getFavorites() {
        return new ArrayList<>(favorites);
    }

    private static void saveFavorites(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(favorites);
        editor.putString(KEY_FAVORITES, json);
        editor.apply();
    }
}
