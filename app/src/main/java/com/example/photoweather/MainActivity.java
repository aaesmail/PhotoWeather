package com.example.photoweather;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import com.example.photoweather.models.Photo;
import com.example.photoweather.models.Weather;
import com.example.photoweather.ui.main.fragments.history.HistoryViewModel;
import com.example.photoweather.ui.main.fragments.weatherinfo.WeatherInfoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.widget.Toast;

import com.example.photoweather.ui.main.fragments.SectionsPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Ali Adel
 * <p>
 * Main activity that holds fragments and handels dispatching to take photo and sharing it
 */
public class MainActivity extends AppCompatActivity {

    // image capture code to dispatch request
    private static final int IMAGE_CAPTURE_CODE = 1001;
    // image description
    private static final String IMAGE_DESCRIPTION = "PhotoWeather";
    // image file name to be stored into gallery
    private String mImageFileName;
    // image file Uri that holds location of image
    private Uri mImageFileUri;
    // time stamp used in image name to make it unique
    private String mImageTimeStamp;
    // Current weather that user selected
    private LiveData<Weather> mCurrentWeather;

    // view model to hold history of images
    private HistoryViewModel mHistoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout
        setContentView(R.layout.activity_main);
        // get adapter to holds fragments and allow switching between them
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // get reference to floating action button that takes pictures
        FloatingActionButton fab = findViewById(R.id.fab);

        // button in top bar that deletes all pictures
        // set it's listener
        findViewById(R.id.delete_all_button).setOnClickListener(v -> confirmDelete());

        // get history view model
        mHistoryViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

        // get weather info view model to track weather info
        WeatherInfoViewModel weatherInfoViewModel = ViewModelProviders.of(this).get(WeatherInfoViewModel.class);
        mCurrentWeather = weatherInfoViewModel.getWeather();

        // once floating action button is pressed check to take photo or not
        fab.setOnClickListener(view -> takePhoto());
    }

    /**
     * check if there is valid weather info first
     * if no info available then tell user to pick city first
     * if info available then dispatch intent to take photo
     */
    private void takePhoto() {

        if (mCurrentWeather.getValue() == null) {
            Toast.makeText(this, getString(R.string.pick_city), Toast.LENGTH_SHORT).show();
            return;
        }

        dispatchTakePictureIntent();
    }


    /**
     * make intent to take picture and start it to get result back
     */
    private void dispatchTakePictureIntent() {
        // get content values to store info in it
        ContentValues contentValues = new ContentValues();
        // Create an image file name unique by using time stamp
        mImageTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        mImageFileName = "JPEG_" + mImageTimeStamp + "_";

        // put image file name and description
        contentValues.put(MediaStore.Images.Media.TITLE, mImageFileName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, IMAGE_DESCRIPTION);
        // make uri of image to store image in when camera takes picture
        try {
            mImageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            // make new intent to take picture and put file to store photo in
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        } catch (Exception e) {
            // if failed to create image file
            Toast.makeText(this, getString(R.string.cant_create_image), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * wait for response of camera that takes picture
     *
     * @param requestCode code that is sent to camera is returned in here
     * @param resultCode  code if operation was successful or not
     * @param data        returned from intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if image code and result is ok then edit image, store it and share it
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            // edit image
            Bitmap imageEdited = editImage(mImageFileUri);
            // insert new image in gallery and local DB
            String imageUrl = storeImage(imageEdited);
            // share image with Facebook or Twitter or any medium
            shareImage(imageUrl);
        } else {
            // else clean up resources by deleting image file where image was gonna be stored
            try {
                getContentResolver().delete(mImageFileUri, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Edit image here and store it
     *
     * @param imageUri location of image in Uri to get it and use it in editing
     */
    private Bitmap editImage(Uri imageUri) {

        // initialize result image
        Bitmap result = null;

        try {
            // get source image
            Bitmap src = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // set result on source
            result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

            // make canvas on result to edit it
            Canvas canvas = new Canvas(result);
            // draw source image on canvas first
            canvas.drawBitmap(src, 0, 0, null);
            // make paint to draw on image
            Paint paint = new Paint();

            // get dominant color in image
            int dominantColor = getDominantColor(result);
            // then reverse it to get visible color text on image
            int dominantColorReversed = reverseColor(dominantColor);

            // set color of text to use reverse of dominant color
            paint.setColor(dominantColorReversed);

            // set alpha of text and text size
            paint.setAlpha(255);
            paint.setTextSize(getResources().getInteger(R.integer.text_size));
            // activate anti alias for better quality
            paint.setAntiAlias(true);
            // don't underline text
            paint.setUnderlineText(false);

            // draw text on screen in 3 lines
            // first line is city
            canvas.drawText(mCurrentWeather.getValue().getCity(), 50, 50, paint);
            // second line is description of weather
            canvas.drawText(mCurrentWeather.getValue().getDescription(), 50, 150, paint);
            // third line is temperature of weather
            canvas.drawText(getString(R.string.temperature_format,
                    String.format(Locale.US, "%.2f",
                            mCurrentWeather.getValue().getTemperature())),
                    50, 250, paint);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // return result image
        return result;
    }

    /**
     * Store image and remove old 1
     *
     * @param result image to store
     * @return url of location of new image stored
     */
    private String storeImage(Bitmap result) {

        // delete old image first
        try {
            getContentResolver().delete(mImageFileUri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if result image had problem and is null then return
        if (result == null) {
            return null;
        }

        // store result image in gallery and get it's url
        String url = null;
        try {
            url = MediaStore.Images.Media.insertImage(getContentResolver(),
                    result, mImageFileName, IMAGE_DESCRIPTION);

            // create photo object
            Photo photo = new Photo(url, mImageTimeStamp);

            // store it in local DB
            mHistoryViewModel.insert(photo);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.cant_create_image), Toast.LENGTH_SHORT).show();
        }

        // location of new image
        return url;
    }


    /**
     * share image with Facebook or twitter
     *
     * @param path image file name with extension
     */
    private void shareImage(String path) {
        // there was problem storing image so don't share anything
        if (path == null) {
            Toast.makeText(this, getString(R.string.error_image), Toast.LENGTH_SHORT).show();
            return;
        }

        // set intent to share image
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        // make Uri of image path
        Uri screenshotUri = Uri.parse(path);

        // tell type of image and put image data in intent
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share image"));
    }

    /**
     * get Dominant color from image
     *
     * @param bitmap image to get dominant color from
     * @return dominant color in image
     */
    private int getDominantColor(Bitmap bitmap) {
        return Palette.from(bitmap).generate().getDominantColor(Color.parseColor("#00FFFF"));
    }


    /**
     * Helper method to inverse color given
     *
     * @param color to inverse
     * @return color inversed
     * A = A
     * R = 255 - R
     * G = 255 - G
     * B = 255 - B
     */
    private int reverseColor(int color) {
        int a = Color.alpha(color);
        int r = 255 - Color.red(color);
        int g = 255 - Color.green(color);
        int b = 255 - Color.blue(color);
        return Color.argb(a,
                Math.max(r, 0),
                Math.max(g, 0),
                Math.max(b, 0));
    }


    /**
     * helper method to delete all images in DB and gallery
     */
    private void deleteAllImages() {
        // delete from repository
        mHistoryViewModel.deleteAllPhotos();
    }

    /**
     * Generate Dialog box to confirm deleting of all images
     */
    private void confirmDelete() {
        // build alert dialog first
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set message in dialog
        builder.setMessage(getString(R.string.delete_all));

        // set positive button to delete all images
        builder.setPositiveButton(getString(R.string.delete_positive),
                (dialog, which) -> deleteAllImages());

        // set negative button to do nothing
        builder.setNegativeButton(getString(R.string.delete_negative), (dialog, which) -> {
        });

        // create dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}