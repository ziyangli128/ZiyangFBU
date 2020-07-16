package com.example.outfit.helpers;

import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.Nullable;

import com.example.outfit.fragments.PostsFragment;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class QueryPosts extends PostsFragment {
    // set the number of posts to query at a single time
    public static final int LIMIT = 20;

    // query posts from the parse database
    public static void queryPosts(@Nullable Author author) {
        // specify the class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        if (author != null) {
            query.whereEqualTo(Post.KEY_AUTHOR, author);
        }

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.", e);
                    return;
                }
                for (int i = 0; i < posts.size(); i++) {
                    Log.i(TAG, "Post: " + posts.get(i).getDescription()
                            + ", username: " + posts.get(i).getAuthorUsername());

                    // keep track of the oldest post queried
                    // upon load more request, load posts only older than this date.
                    if (i == posts.size() - 1) {
                        oldestCreatedAt = posts.get(i).getCreatedAt();
                    }
                }
                adapter.clear();
                adapter.addAll(posts);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    // Append the next page of data into the adapter
    public static void loadNextData(long page) {
        // specify the class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(LIMIT);
        query.whereLessThan(Post.KEY_CREATED_AT, oldestCreatedAt);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.", e);
                    return;
                }
                for (int i = 0; i < posts.size(); i++) {
                    Log.i(TAG, "Post: " + posts.get(i).getDescription()
                            + ", username: " + posts.get(i).getAuthorUsername());
                    if (i == posts.size() - 1) {
                        oldestCreatedAt = posts.get(i).getCreatedAt();
                    }
                }
                adapter.addAll(posts);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
