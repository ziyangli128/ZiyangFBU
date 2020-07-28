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

public class NearbyPostsFragment extends BaseFragment {

    private PostsAdapter nearbyAdapter;
    private List<Post> posts;

    public NearbyPostsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        nearbyAdapter = new PostsAdapter(getContext(), posts);
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

        rvPosts.setAdapter(nearbyAdapter);
        MaterialViewPagerHelper.registerScrollView(getContext(), svPosts);

        scrollListenerForNearby = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore for nearby posts!");
                QueryPosts.loadNextDataForNearby(nearbyAdapter);
            }
        };
        rvPosts.clearOnScrollListeners();
        rvPosts.addOnScrollListener(scrollListenerForNearby);

        setOnSearchPosts(nearbyAdapter);
        setOnGoBackHome(nearbyAdapter, scrollListenerForNearby);
        swipeContainer.setEnabled(false);
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryNearbyPosts(getActivity(), nearbyAdapter);
    }

    private void setOnSearchPosts(final PostsAdapter adapter) {

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                Log.i(TAG, "onQueryTextSubmit: submit search request: " + s);

                searchedPosts = new ArrayList<>();
                for (Post post: allNearbyPosts) {
                    if (post.getTags().contains(s.toLowerCase())) {
                        searchedPosts.add(post);
                    }
                }

                int postsSize = allNearbyPosts.size();
                Log.i(TAG, "onQueryTextSubmit: " + allNearbyPosts.size());

                if (searchedPosts.isEmpty()) {
                    Toast.makeText(getContext(), R.string.empty_search, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.clear();
                    adapter.addAll(searchedPosts);
                    Log.i(TAG, "onQueryTextSubmit: " + adapter.getItemCount());

                    rvPosts.clearOnScrollListeners();

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
                adapter.addAll(allNearbyPosts);
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
        nearbyAdapter.notifyDataSetChanged();
    }
}
