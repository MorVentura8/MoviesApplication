package com.example.moviesapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;
import java.util.Map;

public class MovieDetailsFragment extends Fragment {
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private TMDBApiService apiService;
    private ImageView backdropImageView;
    private TextView titleTextView, releaseDateTextView, ratingTextView, overviewTextView;
    private YouTubePlayerView youTubePlayerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        setupViews(view);
        setupRetrofit();
        loadMovieDetails();

        return view;
    }

    private void setupViews(View view) {
        view.setBackgroundResource(android.R.color.background_dark);
        backdropImageView = view.findViewById(R.id.iv_movie_backdrop);
        titleTextView = view.findViewById(R.id.tv_movie_title);
        releaseDateTextView = view.findViewById(R.id.tv_release_date);
        ratingTextView = view.findViewById(R.id.tv_rating);
        overviewTextView = view.findViewById(R.id.tv_overview);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);

        // Set text colors to white for better visibility
        titleTextView.setTextColor(android.graphics.Color.WHITE);
        releaseDateTextView.setTextColor(android.graphics.Color.WHITE);
        ratingTextView.setTextColor(android.graphics.Color.parseColor("#FFEB3B")); // Yellow color
        overviewTextView.setTextColor(android.graphics.Color.WHITE);

        getLifecycle().addObserver(youTubePlayerView);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(TMDBApiService.class);
    }

    private void loadMovieDetails() {
        if (getArguments() == null) return;
        int movieId = getArguments().getInt("movieId", -1);
        if (movieId == -1) return;

        // Load movie details
        apiService.getMovieDetails(movieId, BuildConfig.TMDB_API_KEY)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Object>> call,
                                         @NonNull Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayMovieDetails(response.body());
                        } else {
                            showError("Error loading movie details");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                        showError("Network error: " + t.getMessage());
                    }
                });

        // Load movie videos (for trailer)
        apiService.getMovieVideos(movieId, BuildConfig.TMDB_API_KEY)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Object>> call,
                                         @NonNull Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            processVideosResponse(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                        showError("Error loading trailer");
                    }
                });
    }

    private void displayMovieDetails(Map<String, Object> movieData) {
        String title = (String) movieData.get("title");
        String releaseDate = (String) movieData.get("release_date");
        String overview = (String) movieData.get("overview");
        String backdropPath = (String) movieData.get("backdrop_path");
        double voteAverage = ((Number) movieData.get("vote_average")).doubleValue();

        titleTextView.setText(title);
        releaseDateTextView.setText(String.format("Release Date: %s", releaseDate));
        ratingTextView.setText(String.format("Rating: %.1f", voteAverage));
        overviewTextView.setText(overview);

        if (backdropPath != null) {
            String imageUrl = "https://image.tmdb.org/t/p/w500" + backdropPath;
            Glide.with(this)
                    .load(imageUrl)
                    .into(backdropImageView);
        }
    }

    private void processVideosResponse(Map<String, Object> response) {
        List<?> results = (List<?>) response.get("results");
        if (results != null && !results.isEmpty()) {
            for (Object result : results) {
                if (result instanceof Map) {
                    Map<?, ?> videoData = (Map<?, ?>) result;
                    String type = (String) videoData.get("type");
                    String site = (String) videoData.get("site");
                    
                    if ("Trailer".equals(type) && "YouTube".equals(site)) {
                        String videoKey = (String) videoData.get("key");
                        loadYouTubeVideo(videoKey);
                        break;
                    }
                }
            }
        }
    }

    private void loadYouTubeVideo(String videoId) {
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        youTubePlayerView.release();
    }
} 