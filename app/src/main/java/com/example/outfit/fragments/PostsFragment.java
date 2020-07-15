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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.databinding.FragmentPostsBinding;
import com.example.outfit.models.Post;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostsFragment extends BaseFragment {

    public static final String TAG = "PostsFragment";

//    protected static PostsAdapter adapter;
//    protected List<Post> posts;
//    protected RecyclerView rvPosts;
//    protected static SwipeRefreshLayout swipeContainer;
//    private EndlessRecyclerViewScrollListener scrollListener;
//    protected static Date oldestCreatedAt;

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

        rvPosts = view.findViewById(R.id.rvPosts);
        adapter = new PostsAdapter(getContext(), posts);
        rvPosts.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(SPAN_COUNT, 1);
        rvPosts.setLayoutManager(layoutManager);

//        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.i(TAG, "onRefresh: fetching new data!");
//                queryMyPosts();
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);

//        queryMyPosts();

//        // Retain an instance to call `resetState()` for fresh searches
//        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                Log.i(TAG, "onLoadMore!");
//                QueryPosts.loadNextData(page);
//            }
//        };
//        // Adds the scroll listener to RecyclerView
//        rvPosts.addOnScrollListener(scrollListener);
        
        
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Log.i(TAG, "onActivityResult: " + resultCode);
        if (resultCode == RESULT_OK) {
            // Get data from the Intent (tweet)
            //Post post = Parcels.unwrap(data.getParcelableExtra("post"));
            // Update the Recycler View with the new tweet
            // Modify data source of tweets
            Log.i(TAG, "onActivityResult: finished");
            // Update the adapter
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts(null);
    }
}