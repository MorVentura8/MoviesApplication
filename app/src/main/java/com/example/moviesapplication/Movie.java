package com.example.moviesapplication;

import com.google.firebase.database.PropertyName;

public class Movie {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double voteAverage;
    private boolean isFavorite;

    public Movie() {
    }

    public Movie(int id, String title, String overview, String posterPath, String backdropPath, 
                String releaseDate, double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.isFavorite = false;
    }

    @PropertyName("id")
    public int getId() { return id; }
    
    @PropertyName("id")
    public void setId(int id) { this.id = id; }

    @PropertyName("title")
    public String getTitle() { return title; }
    
    @PropertyName("title")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("overview")
    public String getOverview() { return overview; }
    
    @PropertyName("overview")
    public void setOverview(String overview) { this.overview = overview; }

    @PropertyName("poster_path")
    public String getPosterPath() { 
        return posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : null;
    }
    
    @PropertyName("poster_path")
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    @PropertyName("backdrop_path")
    public String getBackdropPath() { 
        return backdropPath != null ? "https://image.tmdb.org/t/p/w500" + backdropPath : null;
    }
    
    @PropertyName("backdrop_path")
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    @PropertyName("release_date")
    public String getReleaseDate() { return releaseDate; }
    
    @PropertyName("release_date")
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    @PropertyName("vote_average")
    public double getVoteAverage() { return voteAverage; }
    
    @PropertyName("vote_average")
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }

    @PropertyName("favorite")
    public boolean isFavorite() { return isFavorite; }
    
    @PropertyName("favorite")
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
} 