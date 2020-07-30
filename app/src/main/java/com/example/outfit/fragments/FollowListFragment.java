package com.example.outfit.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.outfit.R;
import com.example.outfit.adapters.FollowListAdapter;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.databinding.FragmentAddTagBinding;
import com.example.outfit.databinding.FragmentFollowingsBinding;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowListFragment extends DialogFragment {

    public static final String TAG = "FollowListFragment";

    public FollowListAdapter adapter;
    private RecyclerView rvFollowList;
    private LinearLayoutManager layoutManager;
    private List<Author> users;
    private ArrayList userIDs;

    public FollowListFragment() {
        // Empty constructor is required for DialogFragment
    }

    public FollowListFragment(ArrayList userIDs) {
        this.userIDs = userIDs;
    }

    public static FollowListFragment newInstance(String title, ArrayList userIDs) {
        FollowListFragment frag = new FollowListFragment(userIDs);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFollowList = view.findViewById(R.id.rvFollowList);
        layoutManager = new LinearLayoutManager(getContext());
        rvFollowList.setLayoutManager(layoutManager);
        users = new ArrayList<>();

        adapter = new FollowListAdapter(getContext(), users);
        queryFollowList();

        rvFollowList.setAdapter(adapter);


    }

    // query posts from the parse database
    public void queryFollowList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // specify the class to query
                ParseQuery<Author> query = ParseQuery.getQuery(Author.class);
                //query.include(Post.KEY_AUTHOR);
                //query.addDescendingOrder(Post.KEY_CREATED_AT);

                query.findInBackground(new FindCallback<Author>() {
                    @Override
                    public void done(List<Author> authors, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting followers.", e);
                            return;
                        }
                        for (int i = 0; i < authors.size(); i++) {
                            if (userIDs.contains(authors.get(i).getObjectId())) {
                                users.add(authors.get(i));
                            }
                        }
                        Log.i(TAG, "done: " + users.size());
                        Log.i(TAG, "done: " + adapter.getItemCount());
                        adapter.addAll(new ArrayList<Author>());
                    }
                });
            }
        }).start();
    }
}