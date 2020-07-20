package com.example.outfit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostsFragment extends BaseFragment {

    public static final String TAG = "PostsFragment";
    private TabLayout tabTimeline;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), posts);
        rvPosts = view.findViewById(R.id.rvPosts);
        //adapter = new PostsAdapter(getContext(), posts);
        rvPosts.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(SPAN_COUNT, 1);
        rvPosts.setLayoutManager(layoutManager);

        tabTimeline = view.findViewById(R.id.tabTimeline);

        queryMyPosts();
        tabTimeline.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        queryMyPosts();
                        break;
                    case 1:
                        queryTrendingPosts();
                        break;
                    case 2:
                        queryNearbyPosts();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        adapter.clear();
                        break;
                    case 1:
                        adapter.clear();
                        break;
                    case 2:
                        adapter.clear();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // set default selection view
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts(null);
    }

    public void queryTrendingPosts() {

    }

    public void queryNearbyPosts() {

    }
}