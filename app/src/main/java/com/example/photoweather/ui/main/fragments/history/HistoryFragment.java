package com.example.photoweather.ui.main.fragments.history;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoweather.R;
import com.example.photoweather.models.Photo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ali Adel
 * <p>
 * Fragment to show photos history and display it to user
 * implements interface specified by photo adapter to handle on item clicks
 */
public class HistoryFragment extends Fragment implements PhotoAdapter.OnPhotoListener {

    // factor to set number of images to display in grid view
    private static final int IMAGE_NUMBER_FACTOR = 100;

    // holds view model reference to update data from
    private HistoryViewModel mHistoryViewModel;
    // reference to recycler view
    private RecyclerView mRecyclerView;
    // reference to empty view shown when no photos to display
    private View mEmptyView;
    // reference to full image size view
    private ImageView mFullImageView;
    // container that holds full image
    private View mFullImageContainer;
    // know if currently in full image mode or not
    private boolean mFullImage;
    // holds current photo clicked
    private Photo mCurrentPhoto;

    // holds list of photos that show in view
    private List<Photo> mPhotos;
    // holds adapter that shows photos
    private PhotoAdapter mPhotoAdapter;

    /**
     * get reference to view model
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHistoryViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
    }

    /**
     * inflate view & get reference to items in view
     *
     * @return layout inflated to display on screen
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // get history fragment to display
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        // override on key click listener to know if back key is pressed
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            // if back key is pressed and it is up action and in full image mode then remove image
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && mFullImage
                    && event.getAction() == KeyEvent.ACTION_UP) {

                // show recycler view and remove full image and empty view
                mRecyclerView.setVisibility(View.VISIBLE);
                mFullImageContainer.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                // no longer in full image mode to false
                mFullImage = false;
                // true so no other will handle this event
                return true;
            }
            // events not fulfilled so let other handle event
            return false;
        });

        // first no current photo is selected
        mCurrentPhoto = null;

        // get reference to recyclerView, FullImageView, it's container and empty view
        mRecyclerView = root.findViewById(R.id.recycler_view);
        mFullImageView = root.findViewById(R.id.full_image_view);
        mFullImageContainer = root.findViewById(R.id.full_image_container);
        mEmptyView = root.findViewById(R.id.empty_view);

        // set delete image button shown with FullImageView to call confirm delete
        root.findViewById(R.id.delete_image_button).setOnClickListener(v -> confirmDelete());

        // first set empty view and hide others as data is not yet fetched from View Model
        mRecyclerView.setVisibility(View.GONE);
        mFullImageContainer.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);

        // get screen width in DP so change number of images dynamically based on screen size
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // set layout manager of recycler view to grid layout
        // and set it's width to a variable that changes with screen size
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),
                ((int) dpWidth / IMAGE_NUMBER_FACTOR)));
        // tell recycler view that it's constant to improve performance
        mRecyclerView.setHasFixedSize(true);

        // get adapter and give it to recycler view to use to display photos
        mPhotoAdapter = new PhotoAdapter(this);
        mRecyclerView.setAdapter(mPhotoAdapter);

        // track change in list of photos in repository and update UI when data changes
        mHistoryViewModel.getAllPhotos().observe(getViewLifecycleOwner(), this::updateUi);

        return root;
    }

    /**
     * Updates UI according to data changed
     *
     * @param photos list with new data according to change in repository
     */
    private void updateUi(List<Photo> photos) {

        // first make sure that all images still exist in gallery and clean up any images not found
        if (photos != null) {
            for (Photo photo : photos) {
                Picasso.get().load(photo.getUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        mHistoryViewModel.delete(photo);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }

        // set local list to new list
        mPhotos = photos;
        // if data changed then full screen mode is false
        mFullImage = false;

        // set adapter data set to new list of photos
        if (photos != null)
            mPhotoAdapter.setPhotos(photos);
        else
            // if it is null put empty list
            mPhotoAdapter.setPhotos(new ArrayList<>());

        // if there are no photos then show empty view
        if (photos == null || photos.size() < 1) {
            mRecyclerView.setVisibility(View.GONE);
            mFullImageContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            // if there are photos show recycler view
            mRecyclerView.setVisibility(View.VISIBLE);
            mFullImageContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Implement interface of photo adapter to get notified when an item is clicked
     *
     * @param position position of item in list
     */
    @Override
    public void onPhotoClick(int position) {

        // if there are no photos then return
        if (mPhotos == null || mPhotos.size() < 1)
            return;

        // show full image mode
        mRecyclerView.setVisibility(View.GONE);
        mFullImageContainer.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);

        // load photo into image view to show in full image mode
        mCurrentPhoto = mPhotos.get(position);
        Picasso.get().load(mCurrentPhoto.getUrl()).into(mFullImageView);
        // set to true to know currently in full image mode
        // so properly handle back press
        mFullImage = true;
    }

    /**
     * shows confirm delete image dialog and if yes is pressed proceed to delete image
     */
    private void confirmDelete() {
        // First build alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // set message to delete photo confirmation
        builder.setMessage(getString(R.string.delete_photo));

        // set positive button and if pressed then proceed to delete photo
        builder.setPositiveButton(getString(R.string.delete_positive), (dialog, which) -> deletePhoto());

        // set negative button and if pressed do nothing
        builder.setNegativeButton(getString(R.string.delete_negative), (dialog, which) -> {
        });

        // build the dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Helper method to delete photo which is currently displayed in full mode
     */
    private void deletePhoto() {
        // if no current photo is null just return
        if (mCurrentPhoto == null)
            return;

        // tell view model to delete image
        mHistoryViewModel.delete(mCurrentPhoto);
    }

}