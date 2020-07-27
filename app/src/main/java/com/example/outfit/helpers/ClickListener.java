package com.example.outfit.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.outfit.R;
import com.example.outfit.activities.DetailActivity;
import com.example.outfit.activities.MainActivity;
import com.example.outfit.activities.MapsActivity;
import com.example.outfit.fragments.MyProfileFragment;
import com.example.outfit.fragments.ProfileFragment;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.ParseUser;
import com.plattysoft.leonids.ParticleSystem;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickListener {

    public static final String KEY_FAVORITES = "collection";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWINGS = "followings";

    public static void setIvLikeClickListener(final Post post, final View ivLike, final String TAG) {
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
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment;
                if (post.getAuthor() == ParseUser.getCurrentUser()) {
                    fragment = new MyProfileFragment();
                } else {
                    fragment = new ProfileFragment((Author) post.getAuthor());
                }
                Log.i(TAG, "onClick: " + activity.getSupportFragmentManager().getBackStackEntryCount());

                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContainer, fragment);
                ft.addToBackStack("to profile");
                ft.commit();
            }
        });
    }

    public static void setIvFavoriteClickListener(final Post post, final View ivFavorite, final String TAG) {
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

    public static void setbtnFollowClickListener(final Author author, final Button btnfollow, final String TAG) {
        btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");
                //Author author = (Author) post.getAuthor();
                ArrayList followers = ((ArrayList) author.get(KEY_FOLLOWERS));
                Log.i(TAG, "onClick: follow this post author");
                if (followers != null && followers.contains(currentUser.getObjectId())) {
                    currentUser.removeAll(KEY_FOLLOWINGS, Collections.singleton(author.getObjectId()));
                    currentUser.saveInBackground();

                    author.removeAll(KEY_FOLLOWERS, Collections.singleton(currentUser.getObjectId()));
                    author.saveInBackground();
                    btnfollow.setText(R.string.follow);
                } else {
                    currentUser.add(KEY_FOLLOWINGS, author.getObjectId());
                    currentUser.saveInBackground();
                    Log.i(TAG, "onClick: " + author.getUsername());
                    author.add(KEY_FOLLOWERS, currentUser.getObjectId());
                    author.saveInBackground();
                    btnfollow.setText(R.string.unfollow);
                }
            }
        });
    }

    public static void setFindStoresClickListener(final Context context, final Button btnMap, final String TAG) {
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: btnMap");
                Intent i = new Intent(context, MapsActivity.class);
                ((Activity) context).startActivity(i);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setDoubleTapListener(final ImageView ivImage, final Context context, final View ivLike,
                                            final Post post, final String TAG, final int position,
                                            @Nullable final List<Post> posts,
                                            final int REQUEST_CODE) {
        ivImage.setOnTouchListener(new OnTapListener(context) {

            @Override
            public void onDoubleTap(MotionEvent e) {
                Log.i(TAG, "onClick: " + post.getLikes());
                if (post.getLikes().contains(ParseUser.getCurrentUser().getObjectId())) {
                    post.removeLikes(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();
                    ivLike.setSelected(false);
                } else {
                    post.setLikes(ParseUser.getCurrentUser().getObjectId());
                    post.saveInBackground();
                    ivLike.setSelected(true);

                    new ParticleSystem((Activity) context, 80,
                            R.drawable.ic_heart_active, 10000)
                            .setSpeedModuleAndAngleRange(0.5f, 0.5f, 0, 360)
                            .setRotationSpeed(144)
                            .setAcceleration(0.00005f, 90)
                            .oneShot(ivImage, 16);
                }


            }
            
            public void onSingleTapConfirmed(MotionEvent e) {
                if (position != -1) {
                    Log.i(TAG, "onClick: " + TAG);
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the post clicked on
                        Post post = posts.get(position);
                        Intent i = new Intent(context, DetailActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        i.putExtra("post", Parcels.wrap(post));
                        i.putExtra("position", position);
                        ((Activity) context).startActivityForResult(i, REQUEST_CODE);
                    }
                }

            }

        });
    }
}
