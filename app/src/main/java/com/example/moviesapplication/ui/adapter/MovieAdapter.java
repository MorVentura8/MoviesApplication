package com.example.moviesapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviesapplication.R;
import com.example.moviesapplication.data.model.Movie;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView;
        private final TextView ratingTextView;
        private final TextView releaseDateTextView;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.moviePoster);
            titleTextView = itemView.findViewById(R.id.movieTitle);
            ratingTextView = itemView.findViewById(R.id.movieRating);
            releaseDateTextView = itemView.findViewById(R.id.movieReleaseDate);
        }

        void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(String.format("%.1f", movie.getRating()));
            releaseDateTextView.setText(movie.getReleaseDate());

            Glide.with(itemView.getContext())
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(posterImageView);
        }
    }
} 