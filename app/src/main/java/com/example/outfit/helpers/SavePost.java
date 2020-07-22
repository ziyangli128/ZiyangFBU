package com.example.outfit.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.outfit.R;
import com.example.outfit.fragments.ComposeFragment;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

public class SavePost extends ComposeFragment {
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
}
