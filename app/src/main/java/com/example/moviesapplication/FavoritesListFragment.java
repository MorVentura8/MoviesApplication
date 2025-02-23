package com.example.moviesapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FavoritesListFragment extends Fragment implements MovieAdapter.OnMovieClickListener, MovieAdapter.OnFavoriteClickListener {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private List<Movie> favoriteMovies = new ArrayList<>();
    private DatabaseReference databaseReference;
    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setupViews(view);
        setupFirebase();
        loadFavorites();

        return view;
    }

    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.rv_movies);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);

        if (emptyView == null) {
            // Create empty view if it doesn't exist in layout
            emptyView = new TextView(requireContext());
            emptyView.setText("No favorites found");
            emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((ViewGroup) recyclerView.getParent()).addView(emptyView);
            emptyView.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(favoriteMovies, this, this);
        recyclerView.setAdapter(movieAdapter);
    }

    private void setupFirebase() {
        try {
            Users currentUser = FirebaseAuthManager.getCurrentUser();
            if (currentUser == null) {
                showEmptyView("Please log in to view favorites");
                return;
            }
            
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("favorites")
                    .child(currentUser.getUserName());
        } catch (Exception e) {
            showError("Error setting up Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFavorites() {
        if (databaseReference == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                favoriteMovies.clear();
                
                try {
                    for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                        try {
                            // Get individual fields and construct Movie object
                            int id = movieSnapshot.child("id").getValue(Integer.class);
                            String title = movieSnapshot.child("title").getValue(String.class);
                            String overview = movieSnapshot.child("overview").getValue(String.class);
                            String posterPath = movieSnapshot.child("poster_path").getValue(String.class);
                            String backdropPath = movieSnapshot.child("backdrop_path").getValue(String.class);
                            String releaseDate = movieSnapshot.child("release_date").getValue(String.class);
                            Double voteAverage = movieSnapshot.child("vote_average").getValue(Double.class);

                            if (id != 0 && title != null) {
                                Movie movie = new Movie(
                                    id, 
                                    title, 
                                    overview != null ? overview : "", 
                                    posterPath, 
                                    backdropPath,
                                    releaseDate != null ? releaseDate : "",
                                    voteAverage != null ? voteAverage : 0.0
                                );
                                movie.setFavorite(true);
                                favoriteMovies.add(movie);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Continue to next movie if one fails
                            continue;
                        }
                    }
                    movieAdapter.notifyDataSetChanged();
                    updateEmptyView();
                } catch (Exception e) {
                    showError("Error loading favorites: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                showError("Error loading favorites: " + error.getMessage());
            }
        });
    }

    private void updateEmptyView() {
        if (favoriteMovies.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No favorites found");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText(message);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("movieId", movie.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_allMoviesFragment_to_movieDetailsFragment, bundle);
        } catch (Exception e) {
            showError("Error navigating to movie details");
            e.printStackTrace();
        }
    }

    @Override
    public void onFavoriteClick(Movie movie) {
        if (databaseReference == null) {
            showError("Please log in to manage favorites");
            return;
        }

        try {
            // Remove from Firebase
            databaseReference.child(String.valueOf(movie.getId())).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Remove from local list
                        favoriteMovies.remove(movie);
                        movieAdapter.notifyDataSetChanged();
                        updateEmptyView();
                        // Notify other fragments about the removal
                        sharedViewModel.setRemovedFavorite(movie.getId());
                        showError("Removed from favorites");
                    })
                    .addOnFailureListener(e -> {
                        showError("Error removing from favorites");
                    });
        } catch (Exception e) {
            showError("Error updating favorites: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 