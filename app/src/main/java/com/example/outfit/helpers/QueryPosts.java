package com.example.outfit.helpers;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.AnyThread;
import androidx.annotation.BinderThread;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.recyclerview.widget.RecyclerView;

import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.fragments.PostsFragment;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryPosts extends PostsFragment {
    // set the number of posts to query at a single time
    public static final int LIMIT = 2;
    public static final String TAG = "QueryPosts";
    public static int begin, end;

    // query posts from the parse database
    // assertion
    // annotate with background thread

    public static void queryPosts(@Nullable final Author author) {

        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        allPosts = new ArrayList<>(posts);
                    }
                });
            }
        }).start();
    }

    // Append the next page of data into the adapter
    public static void loadNextData(long page, final boolean loadSearch, @Nullable final String search,
                                    @Nullable Author author) {
        // specify the class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(LIMIT);
        query.whereLessThan(Post.KEY_CREATED_AT, oldestCreatedAt);
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
                    if (i == posts.size() - 1) {
                        oldestCreatedAt = posts.get(i).getCreatedAt();
                    }
                }
                int postsNumber = allPosts.size();
                allPosts.addAll(posts);
                if (!loadSearch) {
                    adapter.addAll(posts);
                } else {
                    List<Post> searchedPosts = new ArrayList<>();
                    for (int i = postsNumber; i < allPosts.size(); i++) {
                        if (allPosts.get(i).getTags().contains(search.toLowerCase())) {
                            searchedPosts.add(allPosts.get(i));
                        }
                    }
                    adapter.addAll(searchedPosts);
                }

                swipeContainer.setRefreshing(false);

            }
        });
    }

    public static void queryTrendingPosts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // specify the class to query
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_AUTHOR);
                query.setLimit(3);
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

                            // keep track of the oldest post queried
                            // upon load more request, load posts only older than this date.
                            if (i == posts.size() - 1) {
                                oldestCreatedAt = posts.get(i).getCreatedAt();
                            }
                        }
                        adapter.clear();
                        adapter.addAll(posts);
                        swipeContainer.setRefreshing(false);
                        allPosts = new ArrayList<>(posts);
                    }
                });
            }
        }).start();
    }

    public static void queryNearbyPosts(Activity activity) {
        final ParseGeoPoint currentUserLocation = SavePost.saveCurrentUserLocation(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {

                // specify the class to query
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_AUTHOR);

                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting posts.", e);
                            return;
                        }
                        for (int i = 0; i < posts.size(); i++) {
                            ParseGeoPoint loc = posts.get(i).getLocation();
                            double distance = loc.distanceInKilometersTo(currentUserLocation);
                            posts.get(i).setDistanceToCurrent(distance);
                        }

                        quickSort(posts, 0, posts.size() - 1);
                        begin = 0;
                        end = 10;
                        adapter.clear();
                        if (posts.size() > end) {
                            adapter.addAll(posts.subList(begin, end));
                        } else if (posts.size() > begin){
                            adapter.addAll(posts.subList(begin, posts.size()));
                        }
                        swipeContainer.setRefreshing(false);
                        allNearbyPosts = new ArrayList<>(posts);
                    }
                });
            }
        }).start();
    }

    public static void getNearbyPosts() {
        adapter.clear();
        begin = 0;
        end = 10;
        if (allNearbyPosts.size() > end) {
            adapter.addAll(allNearbyPosts.subList(begin, end));
        } else if (allNearbyPosts.size() > begin){
            adapter.addAll(allNearbyPosts.subList(begin, allNearbyPosts.size()));
        }
    }

    public static void loadNextDataForNearby() {
        begin += 10;
        end += 10;
        if (allNearbyPosts.size() > end) {
            adapter.addAll(allNearbyPosts.subList(begin, end));
        } else if (allNearbyPosts.size() > begin){
            adapter.addAll(allNearbyPosts.subList(begin, allNearbyPosts.size()));
        }
    }

    static int partition(List<Post> posts, int begin, int end) {
        int pivot = end;

        int counter = begin;
        for (int i = begin; i < end; i++) {
            if (posts.get(i).getDistanceToCurrent() < posts.get(pivot).getDistanceToCurrent()) {
                Post temp = posts.get(counter);
                posts.set(counter, posts.get(i));
                posts.set(i, temp);
                counter++;
            }
        }
        Post temp = posts.get(pivot);
        posts.set(pivot, posts.get(counter));
        posts.set(counter, temp);

        return counter;
    }

    public static void quickSort(List<Post> posts, int begin, int end) {
        if (end <= begin) return;
        int pivot = partition(posts, begin, end);
        quickSort(posts, begin, pivot-1);
        quickSort(posts, pivot+1, end);
    }
}
