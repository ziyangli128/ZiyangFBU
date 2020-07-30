package com.example.outfit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.databinding.FragmentProfileBinding;
import com.example.outfit.helpers.ClickListener;
import com.example.outfit.helpers.EndlessRecyclerViewScrollListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final int CORNER_RADIUS = 150; // corner radius, higher value = more rounded
    public static final int CROP_MARGIN = 10; // crop margin, set to 0 for corners with no crop
    public static final String KEY_FAVORITES = "collection";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWINGS = "followings";
    public static final int SPAN_COUNT = 2;

    protected ImageView ivProfileImage;
    protected TextView tvUsername;
    protected TextView tvFollowingNum;
    protected TextView tvFollowerNum;
    protected TextView tvFavoritesNum;
    private Button btnFollow;
    protected LinearLayout llFollowings;
    protected LinearLayout llFollowers;
    private FragmentProfileBinding binding;
    protected RecyclerView rvProfilePosts;
    protected PostsAdapter profileAdapter;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected static SwipeRefreshLayout swipeContainer;
    protected SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    protected List<Post> posts;

    protected Author author;

    public ProfileFragment() {}

    public ProfileFragment(Author author) {
        this.author = author;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        profileAdapter = new PostsAdapter(getContext(), posts);
        queryMyPosts();
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

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFollowingNum = view.findViewById(R.id.tvFollowingNum);
        tvFollowerNum = view.findViewById(R.id.tvFollowerNum);
        tvFavoritesNum = view.findViewById(R.id.tvFavoritesNum);
        btnFollow = view.findViewById(R.id.btnFollow);
        llFollowings = view.findViewById(R.id.llFollowings);
        llFollowers = view.findViewById(R.id.llFollowers);

        rvProfilePosts = view.findViewById(R.id.rvProfilePosts);
        rvProfilePosts.setAdapter(profileAdapter);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(SPAN_COUNT, 1);
        rvProfilePosts.setLayoutManager(layoutManager);
        // Retain an instance to call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "onLoadMore for profile posts!");
                QueryPosts.loadNextData(page, false, null, author,
                        profileAdapter, swipeContainer);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvProfilePosts.addOnScrollListener(scrollListener);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: fetching new data!");
                profileAdapter.clear();
                queryMyPosts();
            }
        };
        swipeContainer.setOnRefreshListener(onRefreshListener);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        tvUsername.setText(author.getUsername());
        if (author.getFollowings() != null) {
            tvFollowingNum.setText((author.getFollowings()).size() + "");
        }
        if (((author.getFollowers()) != null)) {
            tvFollowerNum.setText((author.getFollowers()).size()+ "");
        }
        if ((author.getFavorites()) != null) {
            tvFavoritesNum.setText((author.getFavorites()).size() + "");
        }

        ParseFile profileImage = author.getParseFile("profileImage");
        Glide.with(getContext()).load(profileImage.getUrl())
                .transform(new RoundedCornersTransformation(CORNER_RADIUS, CROP_MARGIN)).into(ivProfileImage);

        if (btnFollow != null) {
            ArrayList followers = ((ArrayList) author.get(KEY_FOLLOWERS));
            if (followers != null && followers.contains(ParseUser.getCurrentUser().getParseObject("author").getObjectId())) {
                binding.btnFollow.setText(R.string.unfollow);
            } else {
                binding.btnFollow.setText(R.string.follow);
            }

            ClickListener.setbtnFollowClickListener(author, binding.btnFollow, TAG);
        }

        llFollowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(author.getFollowings());
            }
        });

        llFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(author.getFollowers());
            }
        });
    }

    public void queryMyPosts() {
        QueryPosts.queryPosts(author, profileAdapter, swipeContainer);
    }

    public void updatePosts(Post post, int position) {
        posts.remove(position);
        posts.add(position, post);
        profileAdapter.notifyDataSetChanged();
    }

    protected void showEditDialog(ArrayList userIDs) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FollowListFragment followListFragment =
                FollowListFragment.newInstance("some title", userIDs);
        // SETS the target fragment for use later when sending results
        // addTagFragment.setTargetFragment(ComposeFragment.this, 300);
        followListFragment.show(fm, "fragment_follow_list");
    }
}
