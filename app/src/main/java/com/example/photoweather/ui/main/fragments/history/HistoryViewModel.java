package com.example.photoweather.ui.main.fragments.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.photoweather.Repository;
import com.example.photoweather.models.Photo;

import java.util.List;

/**
 * @author Ali Adel
 * <p>
 * View model to hold information about history data
 */
public class HistoryViewModel extends AndroidViewModel {

    // reference to repository which fetches data
    private Repository mRepository;
    // reference to all photos in history
    private LiveData<List<Photo>> mAllPhotos;

    /**
     * Constructor to be used by Viewmodleproviders
     *
     * @param application context needed to give to respository
     */
    public HistoryViewModel(@NonNull Application application) {
        super(application);

        // get reference to repository
        mRepository = Repository.getRepositoryInstance(application);
        // get reference to live data to watch it in UI
        mAllPhotos = mRepository.getAllPhotos();
    }

    /**
     * Insert photo in history
     * tell repository to insert it
     *
     * @param photo to be inserted
     */
    public void insert(Photo photo) {
        mRepository.insert(photo);
    }

    /**
     * Delete photo from history
     * tell repository to delete it
     *
     * @param photo to be deleted
     */
    public void delete(Photo photo) {
        mRepository.delete(photo);
    }

    /**
     * Delete all photos from history
     * tell repository to delete all photos
     */
    public void deleteAllPhotos() {
        mRepository.deleteAllPhotos();
    }

    /**
     * @return live data observing all photos
     */
    public LiveData<List<Photo>> getAllPhotos() {
        return this.mAllPhotos;
    }

}