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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.outfit.activities.MainActivity;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.models.Post;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostsFragment extends BaseFragment {

    public static final String TAG = "PostsFragment";
    private TabLayout tabTimeline;
    private SearchView svSearch;
    private PostsAdapter searchAdapter;
    private ImageView ivGoBack;

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
        rvPosts.setAdapter(adapter);

        tabTimeline = view.findViewById(R.id.tabTimeline);
        svSearch = view.findViewById(R.id.svSearch);
        ivGoBack = view.findViewById(R.id.ivGoBack);
        ivGoBack.setVisibility(View.GONE);

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
                        searchAdapter.clear();
                        queryTrendingPosts();
                        break;
                    case 2:
                        searchAdapter.clear();
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
        
        setOnSearchPosts();
        setOnGoBackHome();
    }

    @Override
    public void queryMyPosts() {
        QueryPosts.queryPosts(null);
    }

    public void queryTrendingPosts() {

    }

    public void queryNearbyPosts() {

    }
    
    private void setOnSearchPosts() {
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i(TAG, "onQueryTextSubmit: submit search request: " + s);
                List<Post> searchedPosts = new ArrayList<>();
                for (Post post: posts) {
                    if (post.getTags().contains(s.toLowerCase())) {
                        searchedPosts.add(post);
                    }
                }
                if (searchedPosts.isEmpty()) {
                    Toast.makeText(getContext(), R.string.empty_search, Toast.LENGTH_SHORT).show();
                } else {
                    searchAdapter = new PostsAdapter(getContext(), searchedPosts);
                    Log.i(TAG, "onQueryTextSubmit: " + searchAdapter.getItemCount());
                    rvPosts.setAdapter(searchAdapter);
                }
                svSearch.clearFocus();
                ivGoBack.setVisibility(View.VISIBLE);
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
                rvPosts.setAdapter(adapter);
                ivGoBack.setVisibility(View.GONE);
            }
        });
    }
}