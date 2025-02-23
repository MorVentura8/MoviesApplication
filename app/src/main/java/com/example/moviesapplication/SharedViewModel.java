package com.example.moviesapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> removedFavoriteId = new MutableLiveData<>();

    public void setRemovedFavorite(int movieId) {
        removedFavoriteId.setValue(movieId);
    }

    public LiveData<Integer> getRemovedFavoriteId() {
        return removedFavoriteId;
    }
} 