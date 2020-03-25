package com.example.photoweather;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.photoweather.models.Photo;

import java.util.List;

/**
 * @author Ali Adel
 * <p>
 * Interface of DB to use Room to auto generate DB operations
 */
@Dao
public interface PhotoDao {

    /**
     * @param photo to insert in DB
     */
    @Insert
    void insert(Photo photo);

    /**
     * @param photo to delete from DB
     */
    @Delete
    void delete(Photo photo);

    /**
     * Delete all photos from DB
     */
    @Query("DELETE FROM photo_table")
    void deleteAllPhotos();

    /**
     * @return whole photos table as live data to observe and know if any change in DB happened
     */
    @Query("SELECT * FROM photo_table ORDER BY timeStamp DESC")
    LiveData<List<Photo>> getAllPhotos();
}
