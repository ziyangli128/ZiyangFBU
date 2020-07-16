package com.example.outfit.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.outfit.R;
import com.example.outfit.helpers.SavePost;
import com.example.outfit.databinding.FragmentComposeBinding;
import com.example.outfit.models.Author;
import com.parse.ParseUser;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 44;
    public static String photoFileName = "photo.jpg";
    protected static File photoFile;

    protected static FragmentComposeBinding binding;

    public ComposeFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);
        // layout of fragment is stored in a special property called root
        View view = binding.getRoot();
        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + getActivity().toString());
                //LaunchCamera.launchCamera(getContext(), getActivity());
                launchCamera();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.etTitle.getText().toString();
                String description = binding.etDescription.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(),
                            R.string.title_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || binding.ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(),
                            R.string.image_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");
                SavePost.savePost(description, title, currentUser, photoFile, getContext(), getActivity());
            }
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.outfit", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //ImageView ivPostImage = (ImageView) findViewById(R.id.ivPostImage);
                binding.ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), R.string.picture_not_taken, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i(TAG, "onActivityResult: " + requestCode);
        }
    }
}