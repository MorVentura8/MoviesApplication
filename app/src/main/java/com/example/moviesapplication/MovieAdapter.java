package com.example.moviesapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private final OnMovieClickListener movieClickListener;
    private final OnFavoriteClickListener favoriteClickListener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Movie movie);
    }

    public MovieAdapter(List<Movie> movies, OnMovieClickListener movieClickListener,
                       OnFavoriteClickListener favoriteClickListener) {
        this.movies = movies;
        this.movieClickListener = movieClickListener;
        this.favoriteClickListener = favoriteClickListener;
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
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView;
        private final TextView releaseDateTextView;
        private final TextView ratingTextView;
        private final ImageView favoriteImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.iv_movie_poster);
            titleTextView = itemView.findViewById(R.id.tv_movie_title);
            releaseDateTextView = itemView.findViewById(R.id.tv_release_date);
            ratingTextView = itemView.findViewById(R.id.tv_rating);
            favoriteImageView = itemView.findViewById(R.id.iv_favorite);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    movieClickListener.onMovieClick(movies.get(position));
                }
            });

            favoriteImageView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie movie = movies.get(position);
                    movie.setFavorite(!movie.isFavorite());
                    favoriteClickListener.onFavoriteClick(movie);
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
            releaseDateTextView.setText(movie.getReleaseDate());
            ratingTextView.setText(String.format("Rating: %.1f", movie.getVoteAverage()));

            Glide.with(itemView)
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.dark_background)
                    .error(R.drawable.dark_background)
                    .into(posterImageView);

            favoriteImageView.setImageResource(
                    movie.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );
        }
    }
} 