package com.example.outfit.fragments;

import android.os.Bundle;
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
import com.parse.ParseFile;
import com.parse.ParseUser;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileFragment extends PostsFragment {

    public static final String TAG = "ProfileFragment";
    public static final int CORNER_RADIUS = 150; // corner radius, higher value = more rounded
    public static final int CROP_MARGIN = 10; // crop margin, set to 0 for corners with no crop

    protected ImageView ivProfileImage;
    protected TextView tvUsername;
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

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(R.dimen.span_count, 1);
        rvPosts.setLayoutManager(layoutManager);

        tvUsername.setText(user.getUsername());

        ParseFile profileImage = user.getParseFile("profileImage");
        Glide.with(getContext()).load(profileImage.getUrl())
                .transform(new RoundedCornersTransformation(CORNER_RADIUS, CROP_MARGIN)).into(ivProfileImage);
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts(user);
    }
}
