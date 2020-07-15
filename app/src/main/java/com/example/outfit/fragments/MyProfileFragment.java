package com.example.outfit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.outfit.R;
import com.example.outfit.activities.LoginActivity;
import com.example.outfit.helpers.QueryPosts;
import com.parse.ParseFile;
import com.parse.ParseUser;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends ProfileFragment {

    public static final String TAG = "MyProfileFragment";
    Button btnLogout;

    public MyProfileFragment() {
        user = ParseUser.getCurrentUser();
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

        //adapter = new PostsAdapter(getContext(), posts);
        rvPosts.setAdapter(adapter);
        Log.i(TAG, "onViewCreated: " + rvPosts.toString());

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(R.dimen.span_count, 1);
        rvPosts.setLayoutManager(layoutManager);

        ParseFile profileImage = user.getParseFile("profileImage");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl())
                    .transform(new RoundedCornersTransformation(CORNER_RADIUS, CROP_MARGIN)).into(ivProfileImage);
        }

        tvUsername.setText(user.getUsername());

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
    }

    private void goLoginActivity() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        getActivity().finish();
        startActivity(i);
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts(user);
    }

}