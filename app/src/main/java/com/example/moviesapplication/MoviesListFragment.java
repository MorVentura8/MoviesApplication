package com.example.moviesapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MoviesListFragment extends Fragment implements MovieAdapter.OnMovieClickListener, MovieAdapter.OnFavoriteClickListener {
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private TMDBApiService apiService;
    private List<Movie> movies = new ArrayList<>();
    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setupViews(view);
        setupRetrofit();
        loadMovies();
        observeFavoriteRemovals();

        return view;
    }

    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.rv_movies);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(movies, this, this);
        recyclerView.setAdapter(movieAdapter);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(TMDBApiService.class);
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getNowPlayingMovies(BuildConfig.TMDB_API_KEY)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Object>> call,
                                         @NonNull Response<Map<String, Object>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            processMovieResponse(response.body());
                        } else {
                            showError("Error loading movies");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void processMovieResponse(Map<String, Object> response) {
        List<?> results = (List<?>) response.get("results");
        if (results != null) {
            movies.clear();
            for (Object result : results) {
                if (result instanceof Map) {
                    Map<?, ?> movieData = (Map<?, ?>) result;
                    Movie movie = new Movie(
                            ((Number) movieData.get("id")).intValue(),
                            (String) movieData.get("title"),
                            (String) movieData.get("overview"),
                            (String) movieData.get("poster_path"),
                            (String) movieData.get("backdrop_path"),
                            (String) movieData.get("release_date"),
                            ((Number) movieData.get("vote_average")).doubleValue()
                    );
                    movies.add(movie);
                    checkFavoriteStatus(movie);
                }
            }
            movieAdapter.notifyDataSetChanged();
        }
    }

    private void checkFavoriteStatus(Movie movie) {
        Users currentUser = FirebaseAuthManager.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference favRef = FirebaseDatabase.getInstance()
                    .getReference("favorites")
                    .child(currentUser.getUserName())
                    .child(String.valueOf(movie.getId()));
            
            favRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    movie.setFavorite(true);
                    movieAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        Bundle args = new Bundle();
        args.putInt("movieId", movie.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_allMoviesFragment_to_movieDetailsFragment, args);
    }

    @Override
    public void onFavoriteClick(Movie movie) {
        Users currentUser = FirebaseAuthManager.getCurrentUser();
        if (currentUser == null) {
            showError("Please log in to manage favorites");
            return;
        }

        try {
            DatabaseReference favRef = FirebaseDatabase.getInstance()
                    .getReference("favorites")
                    .child(currentUser.getUserName())
                    .child(String.valueOf(movie.getId()));

            // Check current state in Firebase before updating
            favRef.get().addOnSuccessListener(snapshot -> {
                boolean isCurrentlyFavorite = snapshot.exists();
                
                if (!isCurrentlyFavorite) {
                    // Add to favorites
                    favRef.setValue(movie)
                            .addOnSuccessListener(aVoid -> {
                                movie.setFavorite(true);
                                movieAdapter.notifyDataSetChanged();
                                showError("Added to favorites");
                            })
                            .addOnFailureListener(e -> {
                                showError("Error adding to favorites: " + e.getMessage());
                                movie.setFavorite(false);
                                movieAdapter.notifyDataSetChanged();
                            });
                } else {
                    // Remove from favorites
                    favRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                movie.setFavorite(false);
                                movieAdapter.notifyDataSetChanged();
                                showError("Removed from favorites");
                            })
                            .addOnFailureListener(e -> {
                                showError("Error removing from favorites: " + e.getMessage());
                                movie.setFavorite(true);
                                movieAdapter.notifyDataSetChanged();
                            });
                }
            }).addOnFailureListener(e -> showError("Error checking favorite status: " + e.getMessage()));
        } catch (Exception e) {
            showError("Error connecting to Firebase. Please try again.");
            e.printStackTrace();
        }
    }

    private void observeFavoriteRemovals() {
        sharedViewModel.getRemovedFavoriteId().observe(getViewLifecycleOwner(), movieId -> {
            if (movieId != null) {
                for (Movie movie : movies) {
                    if (movie.getId() == movieId) {
                        movie.setFavorite(false);
                        movieAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }
} 