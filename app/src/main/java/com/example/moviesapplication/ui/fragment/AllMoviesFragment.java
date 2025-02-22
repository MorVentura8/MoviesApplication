package com.example.moviesapplication.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviesapplication.R;
import com.example.moviesapplication.ui.adapter.MovieAdapter;
import com.example.moviesapplication.data.api.TMDBService;
import com.example.moviesapplication.data.model.Movie;
import com.example.moviesapplication.data.model.MovieResponse;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllMoviesFragment extends Fragment {
    private static final String API_KEY = "c05d3fb06c8b3e9f9e3349eaabda8a11";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    
    private RecyclerView recyclerView;
    private ProgressBar loadingProgressBar;
    private MovieAdapter movieAdapter;
    private TMDBService tmdbService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_movies, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);
        
        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        tmdbService = retrofit.create(TMDBService.class);
        
        // Load movies
        loadMovies();
        
        return view;
    }
    
    private void loadMovies() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        
        tmdbService.getPopularMovies(API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                loadingProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    movieAdapter.updateMovies(response.body().getMovies());
                } else {
                    showError("Error loading movies");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
} 