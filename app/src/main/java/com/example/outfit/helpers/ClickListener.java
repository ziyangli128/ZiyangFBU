package com.example.outfit.helpers;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.outfit.R;
import com.example.outfit.activities.MainActivity;
import com.example.outfit.fragments.MyProfileFragment;
import com.example.outfit.fragments.ProfileFragment;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;

public class ClickListener {

    public static final String KEY_FAVORITES = "collection";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWINGS = "followings";

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
                    fragment = new ProfileFragment(post.getAuthor().getParseUser("user"));
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

    public static void setbtnFollowClickListener(final Post post, final Button btnfollow, final String TAG) {
        btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");
                Author author = (Author) post.getAuthor();
                ArrayList followers = ((ArrayList) author.get(KEY_FOLLOWERS));
                Log.i(TAG, "onClick: follow this post author");
                if (followers != null && followers.contains(currentUser.getObjectId())) {
                    currentUser.removeAll(KEY_FOLLOWINGS, Collections.singleton(post.getAuthor().getObjectId()));
                    currentUser.saveInBackground();

                    author.removeAll(KEY_FOLLOWERS, Collections.singleton(currentUser.getObjectId()));
                    author.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "done: error", e);
                            }
                            Log.i(TAG, "done: ");
                        }
                    });
                    btnfollow.setText(R.string.follow);
                } else {
                    currentUser.add(KEY_FOLLOWINGS, post.getAuthor().getObjectId());
                    currentUser.saveInBackground();
                    Log.i(TAG, "onClick: " + post.getAuthorUsername());
                    Log.i(TAG, "onClick: " + currentUser.getObjectId());
                    author.add(KEY_FOLLOWERS, currentUser.getObjectId());
                    author.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "done: error", e);
                            }
                            Log.i(TAG, "done: ");
                        }
                    });
                    btnfollow.setText(R.string.unfollow);
                }
            }
        });
    }
}
