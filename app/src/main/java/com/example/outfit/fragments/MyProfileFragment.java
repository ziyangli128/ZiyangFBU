package com.example.outfit.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.outfit.R;
import com.example.outfit.activities.LoginActivity;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Author;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends ProfileFragment {

    public static final String TAG = "MyProfileFragment";
    public final static int PICK_PHOTO_CODE = 1046;
    private Button btnLogout;
    private TabLayout tabMyPosts;
    private FloatingActionButton fbAddProfile;
    private static ParseFile galleryFile;

    public MyProfileFragment() {
        try {
            author = (Author) ParseUser.getCurrentUser().getParseObject("author").fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: button logout");
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(getContext(), "Logout failed!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    goLoginActivity();
                }
            }
        });

        tabMyPosts = view.findViewById(R.id.tabMyPosts);
        tabMyPosts.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        rvProfilePosts.smoothScrollToPosition(0);
                        queryMyPosts();
                        swipeContainer.setEnabled(true);
                        rvProfilePosts.addOnScrollListener(scrollListener);
                        break;
                    case 1:
                        rvProfilePosts.clearOnScrollListeners();
                        rvProfilePosts.smoothScrollToPosition(0);
                        QueryPosts.queryFavoritePosts(profileAdapter, swipeContainer);
                        swipeContainer.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        profileAdapter.clear();
                        break;
                    case 1:
                        profileAdapter.clear();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                rvProfilePosts.smoothScrollToPosition(0);
            }
        });

        fbAddProfile = view.findViewById(R.id.fbAddProfile);
        fbAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery(view);
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        getActivity().finish();
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: sfsfsd " + profileAdapter.getItemCount() + " " + posts.size());
    }

    // Trigger gallery selection for a photo
    public void launchGallery(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            galleryFile = new ParseFile("image_profile.jpg", byteArray);
            author.setProfileImage(galleryFile);
            author.saveInBackground();
        } else {
            Log.i(TAG, "onActivityResult: " + requestCode);
        }
    }
}