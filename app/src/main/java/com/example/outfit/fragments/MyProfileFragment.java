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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.outfit.R;
import com.example.outfit.activities.LoginActivity;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Author;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends ProfileFragment {

    public static final String TAG = "MyProfileFragment";
    private Button btnLogout;
    private TabLayout tabMyPosts;

    public MyProfileFragment() {
        try {
            author = (Author) ParseUser.getCurrentUser().getParseObject("author").fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

        tabMyPosts = view.findViewById(R.id.tabMyPosts);
        tabMyPosts.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        rvProfilePosts.smoothScrollToPosition(0);
                        queryMyPosts();
                        swipeContainer.setEnabled(true);
                        rvProfilePosts.addOnScrollListener(scrollListener);
                        break;
                    case 1:
                        rvProfilePosts.clearOnScrollListeners();
                        rvProfilePosts.smoothScrollToPosition(0);
                        QueryPosts.queryFavoritePosts(profileAdapter);
                        swipeContainer.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        profileAdapter.clear();
                        break;
                    case 1:
                        profileAdapter.clear();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                rvProfilePosts.smoothScrollToPosition(0);
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        getActivity().finish();
        startActivity(i);
    }
}