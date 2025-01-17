package com.imagevault.wallpapersinfinity.interfacee;

import com.imagevault.wallpapersinfinity.model.PexelsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
public interface PexelsApi {
    @GET("search")
    Call<PexelsResponse> searchPhotos(
            @Query("query") String category,          // Category (e.g., "nature", "technology")
            @Query("per_page") int perPage,          // Number of photos per page
            @Query("page") int page,                 // Page number
            @Header("Authorization") String apiKey   // API Key for Authorization
    );
}
