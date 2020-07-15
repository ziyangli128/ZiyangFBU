package com.example.outfit.helpers;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.outfit.R;
import com.example.outfit.activities.MainActivity;
import com.example.outfit.fragments.ProfileFragment;
import com.example.outfit.models.Post;
import com.parse.ParseUser;

public class ClickListener {

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
                Fragment fragment = new ProfileFragment(post.getAuthor());
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContainer, fragment);
                activity.getSupportFragmentManager().popBackStack();
                ft.addToBackStack("to profile");
                ft.commit();
            }
        });
    }
}
