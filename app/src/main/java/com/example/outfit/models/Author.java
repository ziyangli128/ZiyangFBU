package com.example.outfit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;


/* Class that contains non-login information of a user in order to write to them
 */
@ParseClassName("Author")
public class Author extends ParseObject {

    public static final String KEY_FAVORITES = "collection";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWINGS = "followings";
    public static final String KEY_USERNAME = "username";

    public ArrayList getFavorites() {
        return (ArrayList) get(KEY_FAVORITES);
    }

    public void setFavorites(String userId) {
        add(KEY_FAVORITES, userId);
    }

    public void removeFavorites(String userId) {
        removeAll(KEY_FAVORITES, Collections.singleton(userId));
    }

    public ArrayList getFollowers() {
        return (ArrayList) get(KEY_FOLLOWERS);
    }

    public void setFollowers(String userId) {
        add(KEY_FOLLOWERS, userId);
    }

    public void removeFollowers(String userId) {
        removeAll(KEY_FOLLOWERS, Collections.singleton(userId));
    }

    public ArrayList getFollowings() {
        return (ArrayList) get(KEY_FOLLOWINGS);
    }

    public void setFollowings(String userId) {
        add(KEY_FOLLOWINGS, userId);
    }

    public void removeFollowings(String userId) {
        removeAll(KEY_FOLLOWINGS, Collections.singleton(userId));
    }

    public String getUsername() {
        return (String) get(KEY_USERNAME);
    }

    public void setUsername(String username) {
        put(KEY_USERNAME, username);
    }
}
