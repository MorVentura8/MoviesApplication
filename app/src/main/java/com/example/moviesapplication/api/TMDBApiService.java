package com.example.moviesapplication.api;

import android.util.Log;

import com.example.moviesapplication.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TMDBApiService {
    private static final String API_KEY = "c05d3fb06c8b3e9f9e3349eaabda8a11";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private final OkHttpClient client = new OkHttpClient();

    public interface MoviesCallback {
        void onSuccess(List<Movie> movies);
        void onError(String message);
    }

    public void getPopularMovies(MoviesCallback callback) {
        String url = BASE_URL + "/movie/popular?api_key=" + API_KEY;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Server error: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray results = jsonObject.getJSONArray("results");
                    List<Movie> movies = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject movieJson = results.getJSONObject(i);
                        Movie movie = new Movie(
                                movieJson.getString("title"),
                                movieJson.getString("poster_path"),
                                movieJson.getDouble("vote_average"),
                                movieJson.getInt("id")
                        );
                        movies.add(movie);
                    }

                    callback.onSuccess(movies);
                } catch (JSONException e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }
}