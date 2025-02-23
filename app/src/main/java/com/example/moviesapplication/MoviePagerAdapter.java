package com.example.moviesapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MoviePagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public MoviePagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment based on position
        return position == 0 ? new MoviesListFragment() : new FavoritesListFragment();
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
} 