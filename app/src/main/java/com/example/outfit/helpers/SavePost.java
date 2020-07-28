package com.example.outfit.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.outfit.R;
import com.example.outfit.fragments.ComposeFragment;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

public class SavePost extends ComposeFragment {
    private static final int REQUEST_LOCATION = 1;
    static LocationManager locationManager;


    public static void savePost(final String description, String title, String brand, ArrayList tags,
                                Author currentUser, @Nullable File photoFile, @Nullable ParseFile galleryFile, final Context context,
                                final FragmentActivity activity) {
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setBrand(brand);
        if (photoFile != null) {
            post.setImage(new ParseFile(photoFile));
        } else {
            post.setImage(galleryFile);
        }
        Log.i(TAG, "savePost: ");

        post.setAuthor(currentUser);
        post.setLikes("default");
        post.setFavorites("default");

        for (Object tag: tags) {
            post.setTags((String)tag);
        }
        ParseGeoPoint currentLocation = saveCurrentUserLocation(activity);
        post.setLocation(currentLocation);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with saving post.", e);
                    Toast.makeText(context,
                            R.string.error_saving, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post save was successful!");
                binding.etDescription.setText("");
                binding.ivPostImage.setImageResource(0);
                Fragment fragment = new com.example.outfit.fragments.PostsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });
    }

    public static ParseGeoPoint saveCurrentUserLocation(Activity activity) {
        ParseGeoPoint currentUserLocation = null;
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            // getting last know user's location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // checking if the location is null
            if(location != null){
                currentUserLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            }
            else {
                Log.i(TAG, "saveCurrentUserLocation: cannot get current location.");
            }
        }

        return currentUserLocation;
    }
}
