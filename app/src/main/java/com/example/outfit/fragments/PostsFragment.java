package com.example.outfit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.outfit.activities.MainActivity;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostsFragment extends BaseFragment {

    public static final String TAG = "PostsFragment";
    private TabLayout tabTimeline;
    private SearchView svSearch;
    private ImageView ivGoBack;
    protected static List<Post> allPosts;
    protected static List<Post> searchedPosts;
    public static List<Post> allFollowingPosts;
    public static List<Post> allNearbyPosts;
    public int countFollowing = 0;
    public int countNearby = 0;


    int[] postsPosition;
    int[] followingPostsPosition;
    int[] nearbyPostsPosition;


    public PostsFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), posts);
        queryMyPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts.setAdapter(adapter);

        tabTimeline = view.findViewById(R.id.tabTimeline);
        svSearch = view.findViewById(R.id.svSearch);
        ivGoBack = view.findViewById(R.id.ivGoBack);
        ivGoBack.setVisibility(View.GONE);

        tabTimeline.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        adapter.addAll(allPosts);
                        rvPosts.clearOnScrollListeners();
                        rvPosts.addOnScrollListener(scrollListener);
                        swipeContainer.setOnRefreshListener(onRefreshListener);
                        rvPosts.scrollToPosition(postsPosition[0]);
                        break;
                    case 1:
                        if (((Author) ParseUser.getCurrentUser().getParseObject("author"))
                                .getFollowings() != null) {
                            if (countFollowing == 0) {
                                queryFollowingPosts();
                                countFollowing++;
                            } else {
                                QueryPosts.getFollowingPosts();
                                rvPosts.scrollToPosition(followingPostsPosition[0]);
                            }
                            rvPosts.clearOnScrollListeners();
                            rvPosts.addOnScrollListener(scrollListenerForFollowing);
                            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    Log.i(TAG, "onRefresh for following Posts");
                                    QueryPosts.queryFollowingPosts();
                                }
                            });
                            break;
                        } else {
                            Toast.makeText(getContext(), R.string.no_following, Toast.LENGTH_SHORT).show();
                            break;
                        }

                    case 2:
                        if (countNearby == 0) {
                            queryNearbyPosts(getActivity());
                            countNearby++;
                        } else {
                            QueryPosts.getNearbyPosts();
                            rvPosts.scrollToPosition(nearbyPostsPosition[0]);
                        }
                        rvPosts.clearOnScrollListeners();
                        rvPosts.addOnScrollListener(scrollListenerForNearby);
                        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Log.i(TAG, "onRefresh for nearby Posts");
                                QueryPosts.queryNearbyPosts(getActivity());
                            }
                        });
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                    default:
                        postsPosition = layoutManager.findFirstCompletelyVisibleItemPositions(null);
                        adapter.clear();
                        break;
                    case 1:
                        followingPostsPosition = layoutManager.findFirstCompletelyVisibleItemPositions(null);
                        adapter.clear();
                        break;
                    case 2:
                        nearbyPostsPosition = layoutManager.findFirstCompletelyVisibleItemPositions(null);
                        adapter.clear();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                rvPosts.smoothScrollToPosition(0);
            }
        });
        
        setOnSearchPosts();
        setOnGoBackHome();
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts(null);
    }

    public static void queryFollowingPosts() { QueryPosts.queryFollowingPosts(); }

    public static void queryNearbyPosts(Activity activity) {
        QueryPosts.queryNearbyPosts(activity);

    }
    
    private void setOnSearchPosts() {

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                Log.i(TAG, "onQueryTextSubmit: submit search request: " + s);

                searchedPosts = new ArrayList<>();
                for (Post post: allPosts) {
                    if (post.getTags().contains(s.toLowerCase())) {
                        searchedPosts.add(post);
                    }
                }

                int postsSize = allPosts.size();
                Log.i(TAG, "onQueryTextSubmit: " + allPosts.size());

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
                            QueryPosts.loadNextData(page, true, s, null);
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

    private void setOnGoBackHome() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                adapter.addAll(allPosts);
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
}