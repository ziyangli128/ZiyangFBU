package com.example.outfit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Post;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import java.util.ArrayList;
import java.util.List;

public class FollowingPostsFragment extends BaseFragment {

    private PostsAdapter followingAdapter;
    private List<Post> posts;
    protected SwipeRefreshLayout.OnRefreshListener onRefreshListenerFollowing;

    public FollowingPostsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        followingAdapter = new PostsAdapter(getContext(), posts);
        queryMyPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts.setAdapter(followingAdapter);
        MaterialViewPagerHelper.registerScrollView(getContext(), svPosts);
        MaterialViewPagerHelper.registerRecyclerView(getContext(), rvPosts);

        scrollListenerForFollowing = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore for following posts!");
                QueryPosts.loadNextDataForFollowing(followingAdapter, false, null);
            }
        };
        rvPosts.clearOnScrollListeners();
        rvPosts.addOnScrollListener(scrollListenerForFollowing);

        // Setup refresh listener which triggers new data loading
        onRefreshListenerFollowing = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: fetching new data!");
                followingAdapter.clear();
                queryMyPosts();
            }
        };
        swipeContainer.setOnRefreshListener(onRefreshListenerFollowing);

        setOnSearchPosts(followingAdapter);
        setOnGoBackHome(followingAdapter, scrollListenerForFollowing);
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryFollowingPosts(followingAdapter, swipeContainer);
    }

    private void setOnSearchPosts(final PostsAdapter adapter) {

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                Log.i(TAG, "onQueryTextSubmit: submit search request: " + s);

                searchedPosts = new ArrayList<>();
                for (Post post: allFollowingPosts) {
                    if (post.getTags().contains(s.toLowerCase())) {
                        searchedPosts.add(post);
                    }
                }

                int postsSize = allFollowingPosts.size();
                Log.i(TAG, "onQueryTextSubmit: " + allFollowingPosts.size());

                if (searchedPosts.isEmpty()) {
                    Toast.makeText(getContext(), R.string.empty_search, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.clear();
                    adapter.addAll(searchedPosts);
                    Log.i(TAG, "onQueryTextSubmit: " + adapter.getItemCount());

                    // Retain an instance to call `resetState()` for fresh searches
                    final EndlessRecyclerViewScrollListener scrollListener2 = new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                            // Triggered only when new data needs to be appended to the list
                            // Add whatever code is needed to append new items to the bottom of the list
                            Log.i(TAG, "onLoadMore for search!");
                            QueryPosts.loadNextDataForFollowing(followingAdapter, true, s);
                        }
                    };

                    // Adds the scroll listener to RecyclerView
                    rvPosts.clearOnScrollListeners();
                    rvPosts.addOnScrollListener(scrollListener2);

                    swipeContainer.setOnRefreshListener(null);
                    ivGoBack.setVisibility(View.VISIBLE);
                }
                svSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    private void setOnGoBackHome(final PostsAdapter adapter,
                                 final EndlessRecyclerViewScrollListener scrollListener) {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                adapter.addAll(allFollowingPosts);
                svSearch.setQuery(null, true);
                ivGoBack.setVisibility(View.GONE);
                rvPosts.clearOnScrollListeners();
                // Setup refresh listener which triggers new data loading
                swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh: fetching new data!");
                        adapter.clear();
                        queryMyPosts();
                    }
                });
                rvPosts.addOnScrollListener(scrollListener);
            }
        });
    }

    @Override
    public void updatePosts(Post post, int position) {
        Log.i(TAG, "updatePosts: ");
        posts.remove(position);
        posts.add(position, post);
        followingAdapter.notifyDataSetChanged();
    }
}
