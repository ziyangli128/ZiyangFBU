package com.example.outfit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    public static final String TAG = "BaseFragment";
    public static final int SPAN_COUNT = 2;

    protected static PostsAdapter adapter;
    protected GridLayoutManager layoutManager;
    protected List<Post> posts;
    protected RecyclerView rvPosts;
    protected SearchView svSearch;
    protected ImageView ivGoBack;
    protected static List<Post> allPosts;
    protected static List<Post> searchedPosts;
    public static List<Post> allFollowingPosts;
    public static List<Post> allNearbyPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected EndlessRecyclerViewScrollListener scrollListenerForFollowing;
    protected EndlessRecyclerViewScrollListener scrollListenerForNearby;
    protected SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    protected static Date oldestCreatedAt;

    protected NestedScrollView svPosts;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        rvPosts.setLayoutManager(layoutManager);
        svSearch = view.findViewById(R.id.svSearch);
        ivGoBack = view.findViewById(R.id.ivGoBack);
        ivGoBack.setVisibility(View.GONE);
        svPosts = view.findViewById(R.id.svPosts);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: fetching new data!");
                adapter.clear();
                queryMyPosts();
            }
        };
        swipeContainer.setOnRefreshListener(onRefreshListener);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Retain an instance to call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "onLoadMore!");
                QueryPosts.loadNextData(page, false, null, null, adapter, swipeContainer);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);
        Log.i(TAG, "queryMyPosts: " + swipeContainer.toString());

    }

    public abstract void queryMyPosts();

    public SwipeRefreshLayout getSwipeContainer() {
        return swipeContainer;
    }

    public void updatePosts(Post post, int position) {

    }
}
