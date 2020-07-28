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
import com.example.outfit.fragments.BaseFragment;
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

public class QueryPosts extends BaseFragment {
    // set the number of posts to query at a single time
    public static final int LIMIT = 6;
    public static final String TAG = "QueryPosts";
    public static int begin = 0;
    public static int end = 15;
    public static Date oldestFollowingPost;
    public static Date oldestFavoritesPost;
    public static Date oldestMyPostCreatedAt;

    // query posts from the parse database
    public static void queryPosts(@Nullable final Author author, final PostsAdapter postsAdapter) {

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
                                if (author != null) {
                                    oldestMyPostCreatedAt = posts.get(i).getCreatedAt();
                                } else {
                                    oldestCreatedAt = posts.get(i).getCreatedAt();
                                    allPosts = new ArrayList<>(posts);
                                }
                            }
                        }
                        postsAdapter.clear();
                        postsAdapter.addAll(posts);
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    // Append the next page of data into the adapter
    public static void loadNextData(long page, final boolean loadSearch,
                                    @Nullable final String search, @Nullable final Author author,
                                    final PostsAdapter postsAdapter) {
        // specify the class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        if (author != null) {
            query.whereEqualTo(Post.KEY_AUTHOR, author);
            query.whereLessThan(Post.KEY_CREATED_AT, oldestMyPostCreatedAt);
        } else {
            query.whereLessThan(Post.KEY_CREATED_AT, oldestCreatedAt);
        }

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.", e);
                    return;
                }
                int postsNumber = allPosts.size();
                for (int i = 0; i < posts.size(); i++) {
                    Log.i(TAG, "Post: " + posts.get(i).getDescription()
                            + ", username: " + posts.get(i).getAuthorUsername());
                    if (i == posts.size() - 1) {
                        if (author != null) {
                            oldestMyPostCreatedAt = posts.get(i).getCreatedAt();
                        } else {
                            oldestCreatedAt = posts.get(i).getCreatedAt();
                            allPosts.addAll(posts);
                        }
                    }
                }

                if (!loadSearch) {
                    postsAdapter.addAll(posts);
                } else {
                    List<Post> searchedPosts = new ArrayList<>();
                    Log.i(TAG, "done: " + postsNumber);
                    Log.i(TAG, "done: " + allPosts.size());
                    for (int i = postsNumber; i < allPosts.size(); i++) {
                        if (allPosts.get(i).getTags().contains(search.toLowerCase())) {
                            Log.i(TAG, "done: " + allPosts.get(i).getTitle());
                            searchedPosts.add(allPosts.get(i));
                        }
                    }
                    postsAdapter.addAll(searchedPosts);
                }
            }
        });
    }

    public static void queryFollowingPosts(final PostsAdapter adapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // specify the class to query
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_AUTHOR);
                query.setLimit(LIMIT);
                query.addDescendingOrder(Post.KEY_CREATED_AT);

                final Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");

                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        List<Post> followingPosts = new ArrayList<>();
                        if (e != null) {
                            Log.e(TAG, "Issue with getting following's posts.", e);
                            return;
                        }
                        for (int i = 0; i < posts.size(); i++) {
                            Log.i(TAG, "Post: " + posts.get(i).getDescription()
                                    + ", username: " + posts.get(i).getAuthorUsername());
                            if (currentUser.getFollowings()
                                    .contains(posts.get(i).getAuthor().getObjectId())) {
                                Log.i(TAG, "Post: " + posts.get(i).getDescription()
                                        + ", username: " + posts.get(i).getAuthorUsername());

                                followingPosts.add(posts.get(i));
                            }
                            // keep track of the oldest post queried
                            // upon load more request, load posts only older than this date.
                            if (i == posts.size() - 1) {
                                oldestFollowingPost = posts.get(i).getCreatedAt();
                            }
                        }
                        if (followingPosts.isEmpty() && !posts.isEmpty()) {
                            loadNextDataForFollowing(adapter, false, null);
                        }
                        adapter.clear();
                        adapter.addAll(followingPosts);
                        swipeContainer.setRefreshing(false);
                        allFollowingPosts = new ArrayList<>(followingPosts);
                    }
                });
            }
        }).start();
    }

    public static void loadNextDataForFollowing(final PostsAdapter adapter, final boolean loadSearch,
                                                @Nullable final String search) {
        // specify the class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(LIMIT);
        query.whereLessThan(Post.KEY_CREATED_AT, oldestFollowingPost);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        final Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                List<Post> followingPosts = new ArrayList<>();
                if (e != null) {
                    Log.e(TAG, "Issue with getting following posts.", e);
                    return;
                }
                int postsNumber = allFollowingPosts.size();
                for (int i = 0; i < posts.size(); i++) {
                    Log.i(TAG, "Post: " + posts.get(i).getDescription()
                            + ", username: " + posts.get(i).getAuthorUsername());
                    if (currentUser.getFollowings()
                            .contains(posts.get(i).getAuthor().getObjectId())) {
                        Log.i(TAG, "Post: " + posts.get(i).getDescription()
                                + ", username: " + posts.get(i).getAuthorUsername());

                        followingPosts.add(posts.get(i));
                    }
                    // keep track of the oldest post queried
                    // upon load more request, load posts only older than this date.
                    if (i == posts.size() - 1) {
                        oldestFollowingPost = posts.get(i).getCreatedAt();
                    }
                }
                if (followingPosts.isEmpty() && !posts.isEmpty()) {
                    loadNextDataForFollowing(adapter, false, null);
                }
                swipeContainer.setRefreshing(false);
                allFollowingPosts.addAll(followingPosts);

                if (!loadSearch) {
                    adapter.addAll(followingPosts);
                } else {
                    List<Post> searchedPosts = new ArrayList<>();
                    for (int i = postsNumber; i < allFollowingPosts.size(); i++) {
                        if (allFollowingPosts.get(i).getTags().contains(search.toLowerCase())) {
                            Log.i(TAG, "done: " + allFollowingPosts.get(i).getTitle());
                            searchedPosts.add(allFollowingPosts.get(i));
                        }
                    }
                    adapter.addAll(searchedPosts);
                }
            }
        });
    }

    public static void queryNearbyPosts(Activity activity, final PostsAdapter adapter) {
        final ParseGeoPoint currentUserLocation = SavePost.saveCurrentUserLocation(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {

                // specify the class to query
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_AUTHOR);

                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> nearbyPosts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting posts.", e);
                            return;
                        }
                        for (int i = 0; i < nearbyPosts.size(); i++) {
                            ParseGeoPoint loc = nearbyPosts.get(i).getLocation();
                            double distance = loc.distanceInKilometersTo(currentUserLocation);
                            nearbyPosts.get(i).setDistanceToCurrent(distance);
                        }

                        quickSort(nearbyPosts, 0, nearbyPosts.size() - 1);
                        adapter.clear();
                        if (nearbyPosts.size() > end) {
                            adapter.addAll(nearbyPosts.subList(begin, end));
                        } else if (nearbyPosts.size() > begin){
                            adapter.addAll(nearbyPosts.subList(begin, nearbyPosts.size()));
                        }
                        swipeContainer.setRefreshing(false);
                        allNearbyPosts = new ArrayList<>(nearbyPosts);
                    }
                });
            }
        }).start();
    }

    public static void loadNextDataForNearby(PostsAdapter adapter) {
        begin = begin + 15;
        end = end + 15;
        Log.i(TAG, "loadNextDataForNearby: ");
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

    public static void queryFavoritePosts(PostsAdapter postsAdapter) {
        Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");
        ArrayList favorites = currentUser.getFavorites();
        List<Post> favoritePosts = new ArrayList<>();
        if (favorites != null) {
            for (int i = 0; i < allPosts.size(); i++) {
                for (int j = 0; j < favorites.size(); j++) {
                    if (allPosts.get(i).getObjectId().equals(favorites.get(j).toString())) {
                        favoritePosts.add(allPosts.get(i));
                    }
                }
            }
            postsAdapter.clear();
            postsAdapter.addAll(favoritePosts);
            oldestFavoritesPost = oldestCreatedAt;
            swipeContainer.setRefreshing(false);
            if (favoritePosts.size() != favorites.size()) {
                loadNextDataForFavorites(postsAdapter);
            }
        }

    }

    public static void loadNextDataForFavorites(final PostsAdapter postsAdapter) {
        // specify the class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(LIMIT);
        query.whereLessThan(Post.KEY_CREATED_AT, oldestFavoritesPost);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                List<Post> favoritePosts = new ArrayList<>();
                if (e != null) {
                    Log.e(TAG, "Issue with getting favorite posts.", e);
                    return;
                }
                for (int i = 0; i < posts.size(); i++) {
                    Log.i(TAG, "Post: " + posts.get(i).getDescription()
                            + ", username: " + posts.get(i).getAuthorUsername());

                    // keep track of the oldest post queried
                    // upon load more request, load posts only older than this date.
                    if (i == posts.size() - 1) {
                        oldestFavoritesPost = posts.get(i).getCreatedAt();
                    }
                }

                Author currentUser = (Author) ParseUser.getCurrentUser().getParseObject("author");
                ArrayList favorites = currentUser.getFavorites();
                for (int i = 0; i < posts.size(); i++) {
                    for (int j = 0; j < favorites.size(); j++) {
                        if (posts.get(i).getObjectId().equals(favorites.get(j).toString())) {
                            Log.i(TAG, "queryFavoritePosts: " + posts.get(i).getTitle());
                            favoritePosts.add(posts.get(i));
                        }
                    }
                }
                if (favorites.size() != favoritePosts.size() && !posts.isEmpty()) {
                    loadNextDataForFavorites(postsAdapter);
                }
                postsAdapter.addAll(favoritePosts);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void queryMyPosts() {

    }
}
