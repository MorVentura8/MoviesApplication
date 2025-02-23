package com.example.moviesapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.Map;

public interface TMDBApiService {
    @GET("movie/now_playing")
    Call<Map<String, Object>> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Map<String, Object>> getMovieDetails(
        @Path("movie_id") int movieId,
        @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/videos")
    Call<Map<String, Object>> getMovieVideos(
        @Path("movie_id") int movieId,
        @Query("api_key") String apiKey
    );
} 