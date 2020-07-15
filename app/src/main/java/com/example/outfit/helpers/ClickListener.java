package com.example.outfit.helpers;

import android.content.Intent;
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
import com.example.outfit.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;

public class ClickListener {

    public static final String KEY_FAVORITES = "favorites";

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
//                    currentUser.removeAll(KEY_FAVORITES, Collections.singleton(post.getObjectId()));
//                    currentUser.saveInBackground();
                    ivFavorite.setSelected(false);
                } else {
//                    currentUser.add("favorites", post.getObjectId());
//                    currentUser.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e != null) {
//                                Log.e(TAG, "done: favorite", e);
//                            }
//                        }
//                    });
                    post.setFavorites(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();

//                    currentUser.setFavorites(post.getObjectId());
//                    currentUser.saveInBackground();
                    ivFavorite.setSelected(true);
                }
            }
        });
    }
}
