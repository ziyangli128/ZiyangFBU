package com.example.outfit.helpers;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.outfit.R;
import com.example.outfit.activities.MainActivity;
import com.example.outfit.fragments.MyProfileFragment;
import com.example.outfit.fragments.ProfileFragment;
import com.example.outfit.models.Post;
import com.parse.ParseUser;

import java.util.Collections;

public class ClickListener {

    public static final String KEY_FAVORITES = "collection";

    public static void setIvLikeClickListener(final Post post, final ImageView ivLike, final String TAG) {
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + post.getLikes());
                if (post.getLikes().contains(ParseUser.getCurrentUser().getObjectId())) {
                    post.removeLikes(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();
                    ivLike.setSelected(false);
                } else {
                    post.setLikes(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();
                    ivLike.setSelected(true);
                }
            }
        });
    }

    public static void setIvProfileClickListener(final Post post, final ImageView ivProfile,
                                                 final String TAG) {
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) view.getContext();
                Fragment fragment;
                if (post.getAuthor() == ParseUser.getCurrentUser()) {
                    fragment = new MyProfileFragment();
                } else {
                    fragment = new ProfileFragment(post.getAuthor());
                }
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContainer, fragment);
                activity.getSupportFragmentManager().popBackStack();
                ft.addToBackStack("to profile");
                ft.commit();
            }
        });
    }

    public static void setIvFavoriteClickListener(final Post post, final ImageView ivFavorite, final String TAG) {
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                Log.i(TAG, "onClick: set favorite" + post.getFavorites());
                if (post.getFavorites().contains(ParseUser.getCurrentUser().getObjectId())) {
                    post.removeFavorites(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();
                    currentUser.removeAll(KEY_FAVORITES, Collections.singleton(post.getObjectId()));
                    currentUser.saveInBackground();
                    ivFavorite.setSelected(false);
                } else {
                    currentUser.add(KEY_FAVORITES, post.getObjectId());
                    currentUser.saveInBackground();
                    post.setFavorites(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();
                    ivFavorite.setSelected(true);
                }
            }
        });
    }
}
