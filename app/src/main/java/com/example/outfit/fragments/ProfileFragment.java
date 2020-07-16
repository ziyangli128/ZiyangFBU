package com.example.outfit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.outfit.R;
import com.example.outfit.databinding.FragmentProfileBinding;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Author;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileFragment extends BaseFragment {

    public static final String TAG = "ProfileFragment";
    public static final int CORNER_RADIUS = 150; // corner radius, higher value = more rounded
    public static final int CROP_MARGIN = 10; // crop margin, set to 0 for corners with no crop
    public static final String KEY_FAVORITES = "collection";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWINGS = "followings";

    protected ImageView ivProfileImage;
    protected TextView tvUsername;
    protected TextView tvFollowingNum;
    protected TextView tvFollowerNum;
    protected TextView tvFavoritesNum;
    private FragmentProfileBinding binding;

    protected ParseUser user;

    public ProfileFragment() {}

    public ProfileFragment(ParseUser user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        // layout of fragment is stored in a special property called root
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts.setAdapter(adapter);

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFollowingNum = view.findViewById(R.id.tvFollowingNum);
        tvFollowerNum = view.findViewById(R.id.tvFollowerNum);
        tvFavoritesNum = view.findViewById(R.id.tvFavoritesNum);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(SPAN_COUNT, 1);
        rvPosts.setLayoutManager(layoutManager);

        tvUsername.setText(user.getUsername());
        try {
            Author author = user.getParseObject("author").fetchIfNeeded();
            if (author.getFollowings() != null) {
                tvFollowingNum.setText((author.getFollowings()).size() + "");
            }
            if (((author.getFollowers()) != null)) {
                tvFollowerNum.setText((author.getFollowers()).size()+ "");
            }
            if ((author.getFavorites()) != null) {
                tvFavoritesNum.setText((author.getFavorites()).size() + "");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        ParseFile profileImage = user.getParseFile("profileImage");
        Glide.with(getContext()).load(profileImage.getUrl())
                .transform(new RoundedCornersTransformation(CORNER_RADIUS, CROP_MARGIN)).into(ivProfileImage);

        queryMyPosts();
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts((Author) user.getParseObject("author"));
    }
}
