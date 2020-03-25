package com.example.photoweather.ui.main.fragments.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoweather.R;
import com.example.photoweather.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ali Adel
 * <p>
 * Adapter to use by recycler view to display photos
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    // list of photos first make empty list
    private List<Photo> photos = new ArrayList<>();
    // on item click listener to be implemented by fragment that uses this adapter
    private OnPhotoListener mOnPhotoListener;

    /**
     * Constructor to hold reference to listener
     *
     * @param onPhotoListener interface implemented by whoever calls this adapter to handle on item
     *                        click events
     */
    public PhotoAdapter(OnPhotoListener onPhotoListener) {
        this.mOnPhotoListener = onPhotoListener;
    }

    /**
     * Photo holder to be used by adapter
     */
    class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // reference to image view
        private ImageView mImageView;
        // reference to listener to call in case of item click
        private OnPhotoListener mOnPhotoListener;

        /**
         * @param itemView        view that inflates with holder
         * @param onPhotoListener listener to item click event
         */
        public PhotoHolder(@NonNull View itemView, OnPhotoListener onPhotoListener) {
            super(itemView);

            // set listener
            this.mOnPhotoListener = onPhotoListener;
            // get reference to image view
            mImageView = itemView.findViewById(R.id.image_item);
            // set item clicked to invoke onClick here
            itemView.setOnClickListener(this);
        }

        /**
         * onItem clicked and delegate it to listener who wants to know if item clicked
         *
         * @param v reference to view clicked
         */
        @Override
        public void onClick(View v) {
            mOnPhotoListener.onPhotoClick(getAdapterPosition());
        }
    }

    /**
     * inflates layout to display as item of recycler view and set its listener
     */
    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new PhotoHolder(itemView, mOnPhotoListener);
    }

    /**
     * when item instantiated get photo object and load image using picasso into view
     */
    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        Photo currentPhoto = photos.get(position);

        Picasso.get().load(currentPhoto.getUrl()).into(holder.mImageView);
    }

    /**
     * @return size of list to be displayed
     */
    @Override
    public int getItemCount() {
        return photos.size();
    }

    /**
     * Update list of photos to display
     *
     * @param photos list of photos to replace current list to display
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;

        // tell recycler view to update view based on that list changed
        notifyDataSetChanged();
    }

    /**
     * Interface to be implemented by whoever uses this adapter to respond to item click event
     */
    public interface OnPhotoListener {
        void onPhotoClick(int position);
    }
}

